package com.youyinda.controller;

import com.youyinda.common.BusinessException;
import com.youyinda.common.R;
import com.youyinda.dto.LoginDTO;
import com.youyinda.dto.PasswordLoginDTO;
import com.youyinda.dto.PhoneDTO;
import com.youyinda.dto.PhoneLoginDTO;
import com.youyinda.dto.RegisterDTO;
import com.youyinda.dto.SendSmsCodeDTO;
import com.youyinda.entity.User;
import com.youyinda.service.SmsService;
import com.youyinda.service.UserService;
import com.youyinda.util.JwtUtil;
import com.youyinda.util.WechatUtil;
import com.youyinda.vo.LoginVO;
import com.youyinda.vo.UserInfoVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * 认证控制器
 */
@RestController
@RequestMapping("/v1/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private WechatUtil wechatUtil;

    @Autowired
    private SmsService smsService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${sms.expire-seconds:300}")
    private long smsExpireSeconds;

    @Value("${sms.dev-mode:true}")
    private boolean smsDevMode;

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
     * 发送短信验证码
     * @param sendSmsCodeDTO 发送验证码请求参数
     * @return 发送结果（dev-mode 下 data 返回 devCode 便于联调）
     */
    @PostMapping("/send-sms-code")
    public R<Map<String, Object>> sendSmsCode(@Valid @RequestBody SendSmsCodeDTO sendSmsCodeDTO) {
        String phone = sendSmsCodeDTO.getPhone();
        String key = "sms:code:" + phone;

        // 60秒内重复发送拦截：已有验证码且剩余有效期大于(总有效期-60)秒，视为60秒内刚发送过
        Long ttl = stringRedisTemplate.getExpire(key, TimeUnit.SECONDS);
        if (ttl != null && ttl > smsExpireSeconds - 60) {
            throw new BusinessException(400, "验证码发送过于频繁，请稍后再试");
        }

        // 生成6位随机验证码
        String code = String.format("%06d", ThreadLocalRandom.current().nextInt(1000000));

        // 存入Redis，过期时间取sms.expire-seconds（默认300秒）
        stringRedisTemplate.opsForValue().set(key, code, smsExpireSeconds, TimeUnit.SECONDS);

        // 发送短信
        smsService.sendSmsCode(phone, code);

        Map<String, Object> data = null;
        if (smsDevMode) {
            data = new HashMap<>();
            data.put("devCode", code);
        }
        return R.success(data);
    }

    /**
     * 手机号验证码登录
     * @param phoneLoginDTO 手机号验证码登录请求参数
     * @return 登录结果
     */
    @PostMapping("/phone-login")
    public R<LoginVO> phoneLogin(@Valid @RequestBody PhoneLoginDTO phoneLoginDTO) {
        String phone = phoneLoginDTO.getPhone();
        String code = phoneLoginDTO.getCode();
        String key = "sms:code:" + phone;

        // 校验验证码
        String cachedCode = stringRedisTemplate.opsForValue().get(key);
        if (cachedCode == null || !cachedCode.equals(code)) {
            throw new BusinessException(400, "验证码错误或已过期");
        }

        // 验证码使用后立即删除（一次性）
        stringRedisTemplate.delete(key);

        // 按手机号查询用户，不存在则新建
        User user = userService.getByPhone(phone);
        if (user == null) {
            user = new User();
            user.setPhone(phone);
            user.setNickName("用户" + phone.substring(phone.length() - 4));
            user.setLastLoginTime(new Date());
            user.setCreateTime(new Date());
            user.setUpdateTime(new Date());
            user.setIsDelete(0);
            userService.save(user);
        } else {
            user.setLastLoginTime(new Date());
            user.setUpdateTime(new Date());
            userService.updateById(user);
        }

        // 手机号登录无openid，openid传空字符串（jjwt不允许null claim）
        String token = jwtUtil.generateToken(user.getId(), "");

        // 构建返回结果
        LoginVO loginVO = new LoginVO();
        loginVO.setToken(token);
        loginVO.setOpenid(null);

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
     * 手机号密码注册
     * @param registerDTO 注册请求参数
     * @return 注册结果（注册成功即登录）
     */
    @PostMapping("/register")
    public R<LoginVO> register(@Valid @RequestBody RegisterDTO registerDTO) {
        String phone = registerDTO.getPhone();
        String password = registerDTO.getPassword();

        // 按手机号查询用户
        User user = userService.getByPhone(phone);
        if (user == null) {
            // 用户不存在，新建用户
            user = new User();
            user.setPhone(phone);
            user.setNickName("用户" + phone.substring(phone.length() - 4));
            user.setPassword(passwordEncoder.encode(password));
            user.setLastLoginTime(new Date());
            user.setCreateTime(new Date());
            user.setUpdateTime(new Date());
            user.setIsDelete(0);
            userService.save(user);
        } else if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            // 用户已注册过密码
            throw new BusinessException(400, "该手机号已注册");
        } else {
            // 老微信用户无密码，补设加密密码（相当于绑定手机号）
            user.setPassword(passwordEncoder.encode(password));
            user.setLastLoginTime(new Date());
            user.setUpdateTime(new Date());
            userService.updateById(user);
        }

        // 注册成功直接签发token，登录即完成（手机号注册无openid，传空字符串）
        String token = jwtUtil.generateToken(user.getId(), "");

        // 构建返回结果
        LoginVO loginVO = new LoginVO();
        loginVO.setToken(token);
        loginVO.setOpenid(null);

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
     * 手机号密码登录
     * @param passwordLoginDTO 密码登录请求参数
     * @return 登录结果
     */
    @PostMapping("/password-login")
    public R<LoginVO> passwordLogin(@Valid @RequestBody PasswordLoginDTO passwordLoginDTO) {
        String phone = passwordLoginDTO.getPhone();
        String password = passwordLoginDTO.getPassword();

        // 按手机号查询用户
        User user = userService.getByPhone(phone);
        if (user == null) {
            throw new BusinessException(400, "该手机号未注册，请先注册");
        }
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            throw new BusinessException(400, "该账号未设置密码，请先注册");
        }
        // 校验密码
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BusinessException(400, "手机号或密码错误");
        }

        // 更新最后登录时间
        user.setLastLoginTime(new Date());
        user.setUpdateTime(new Date());
        userService.updateById(user);

        // 手机号登录无openid，openid传空字符串（jjwt不允许null claim）
        String token = jwtUtil.generateToken(user.getId(), "");

        // 构建返回结果
        LoginVO loginVO = new LoginVO();
        loginVO.setToken(token);
        loginVO.setOpenid(null);

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
