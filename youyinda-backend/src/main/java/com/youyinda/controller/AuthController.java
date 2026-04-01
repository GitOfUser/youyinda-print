package com.youyinda.controller;

import com.youyinda.common.BusinessException;
import com.youyinda.common.R;
import com.youyinda.dto.LoginDTO;
import com.youyinda.dto.PhoneDTO;
import com.youyinda.entity.User;
import com.youyinda.service.UserService;
import com.youyinda.util.JwtUtil;
import com.youyinda.util.WechatUtil;
import com.youyinda.vo.LoginVO;
import com.youyinda.vo.UserInfoVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Date;
import java.util.Map;

/**
 * 认证控制器
 */
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private WechatUtil wechatUtil;

    /**
     * 微信小程序登录
     * @param loginDTO 登录请求参数
     * @return 登录结果
     */
    @PostMapping("/login")
    public R<LoginVO> login(@Valid @RequestBody LoginDTO loginDTO) {
        // 调用微信登录接口获取openid和sessionKey
        Map<String, Object> wxResult = wechatUtil.wxLogin(loginDTO.getCode());
        if (wxResult == null || wxResult.get("openid") == null) {
            throw new BusinessException(400, "微信登录失败");
        }

        String openid = wxResult.get("openid").toString();

        // 查询用户是否存在
        User user = userService.getByOpenid(openid);
        if (user == null) {
            // 新增用户
            user = new User();
            user.setOpenid(openid);
            user.setLastLoginTime(new Date());
            user.setCreateTime(new Date());
            user.setUpdateTime(new Date());
            user.setIsDelete(0);

            // 更新用户信息
            if (loginDTO.getUserInfo() != null) {
                user.setNickName(loginDTO.getUserInfo().getNickName());
                user.setAvatarUrl(loginDTO.getUserInfo().getAvatarUrl());
                user.setGender(loginDTO.getUserInfo().getGender());
                user.setCity(loginDTO.getUserInfo().getCity());
                user.setProvince(loginDTO.getUserInfo().getProvince());
                user.setCountry(loginDTO.getUserInfo().getCountry());
                user.setLanguage(loginDTO.getUserInfo().getLanguage());
            }

            userService.save(user);
        } else {
            // 更新用户信息
            if (loginDTO.getUserInfo() != null) {
                user.setNickName(loginDTO.getUserInfo().getNickName());
                user.setAvatarUrl(loginDTO.getUserInfo().getAvatarUrl());
                user.setGender(loginDTO.getUserInfo().getGender());
                user.setCity(loginDTO.getUserInfo().getCity());
                user.setProvince(loginDTO.getUserInfo().getProvince());
                user.setCountry(loginDTO.getUserInfo().getCountry());
                user.setLanguage(loginDTO.getUserInfo().getLanguage());
            }
            user.setLastLoginTime(new Date());
            user.setUpdateTime(new Date());
            userService.updateById(user);
        }

        // 生成token
        String token = jwtUtil.generateToken(user.getId(), openid);

        // 构建返回结果
        LoginVO loginVO = new LoginVO();
        loginVO.setToken(token);
        loginVO.setOpenid(openid);

        UserInfoVO userInfoVO = new UserInfoVO();
        BeanUtils.copyProperties(user, userInfoVO);
        // 手机号脱敏
        if (user.getPhone() != null && user.getPhone().length() >= 11) {
            userInfoVO.setPhone(user.getPhone().substring(0, 3) + "****" + user.getPhone().substring(7));
        }
        loginVO.setUserInfo(userInfoVO);

        return R.success(loginVO);
    }

    /**
     * 更新手机号
     * @param phoneDTO 手机号更新请求参数
     * @return 更新结果
     */
    @PostMapping("/update-phone")
    public R<?> updatePhone(@Valid @RequestBody PhoneDTO phoneDTO) {
        // 解密手机号
        String phone = wechatUtil.decryptPhoneNumber(phoneDTO.getEncryptedData(), phoneDTO.getIv(), phoneDTO.getSessionKey());
        if (phone == null) {
            throw new BusinessException(400, "手机号解密失败");
        }

        // 获取当前用户ID
        Long userId = JwtUtil.getUserIdFromToken();
        if (userId == null) {
            throw new BusinessException(401, "未登录");
        }

        // 更新用户手机号
        User user = userService.getById(userId);
        if (user == null) {
            throw new BusinessException(400, "用户不存在");
        }

        user.setPhone(phone);
        user.setUpdateTime(new Date());
        userService.updateById(user);

        return R.success();
    }
}
