package com.youyinda.controller;

import com.youyinda.common.BusinessException;
import com.youyinda.common.enums.ErrorCodeEnum;
import com.youyinda.dto.ExpressOrderDTO;
import com.youyinda.entity.OrderMain;
import com.youyinda.entity.UserAddress;
import com.youyinda.service.OrderMainService;
import com.youyinda.service.UserAddressService;
import com.youyinda.util.JwtUtil;
import com.youyinda.vo.ExpressOrderVO;
import com.youyinda.vo.UserAddressVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 快递订单控制器
 * 处理快递订单相关接口
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/express/order")
public class ExpressOrderController {

    @Autowired
    private OrderMainService orderMainService;

    @Autowired
    private UserAddressService userAddressService;

    /**
     * 创建快递订单
     * @param expressOrderDTO 快递订单请求参数
     * @return 订单ID
     */
    @PostMapping("/create")
    public com.youyinda.common.R<Long> createOrder(@Valid @RequestBody ExpressOrderDTO expressOrderDTO) {
        try {
            // 获取当前用户ID
            Long userId = JwtUtil.getUserIdFromToken();
            if (userId == null) {
                throw new BusinessException(ErrorCodeEnum.UNAUTHORIZED, "未登录");
            }

            // 验证寄件地址是否存在
            UserAddress fromAddress = userAddressService.getById(expressOrderDTO.getFromAddressId());
            if (fromAddress == null || !fromAddress.getUserId().equals(userId)) {
                throw new BusinessException(ErrorCodeEnum.ADDRESS_NOT_FOUND, "寄件地址不存在");
            }

            // 验证收件地址是否存在
            UserAddress toAddress = userAddressService.getById(expressOrderDTO.getToAddressId());
            if (toAddress == null || !toAddress.getUserId().equals(userId)) {
                throw new BusinessException(ErrorCodeEnum.ADDRESS_NOT_FOUND, "收件地址不存在");
            }

            // 计算订单总价（这里简化处理，实际应该根据价格引擎计算）
            BigDecimal totalPrice = new BigDecimal(12); // 假设首重价格为12元
            if (expressOrderDTO.getWeight() > 1) {
                // 续重每公斤5元
                int extraWeight = (int) Math.ceil(expressOrderDTO.getWeight() - 1);
                totalPrice = totalPrice.add(new BigDecimal(5).multiply(new BigDecimal(extraWeight)));
            }

            // 创建订单主表
            OrderMain orderMain = new OrderMain();
            orderMain.setUserId(userId);
            orderMain.setOrderType(2); // 2-快递订单
            orderMain.setOrderNo(orderMainService.generateOrderNo());
            orderMain.setTotalPrice(totalPrice.doubleValue());
            orderMain.setStatus(1); // 1-待支付
            orderMain.setPayStatus(1); // 1-未支付
            orderMain.setAddressId(expressOrderDTO.getToAddressId()); // 收件地址ID
            orderMain.setFromAddressId(expressOrderDTO.getFromAddressId()); // 寄件地址ID
            orderMain.setCourier(expressOrderDTO.getCourier());
            orderMain.setWeight(expressOrderDTO.getWeight());
            orderMain.setGoodsType(expressOrderDTO.getGoodsType());
            orderMain.setRemark(expressOrderDTO.getRemark());
            orderMainService.save(orderMain);

            return com.youyinda.common.R.success(orderMain.getId());
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("创建快递订单失败: {}", e.getMessage(), e);
            throw new BusinessException(ErrorCodeEnum.ORDER_CREATE_FAIL, "创建订单失败");
        }
    }

    /**
     * 获取快递订单列表
     * @return 订单列表
     */
    @GetMapping("/list")
    public com.youyinda.common.R<List<ExpressOrderVO>> getOrderList() {
        try {
            // 获取当前用户ID
            Long userId = JwtUtil.getUserIdFromToken();
            if (userId == null) {
                throw new BusinessException(ErrorCodeEnum.UNAUTHORIZED, "未登录");
            }

            // 查询用户的快递订单
            List<OrderMain> orderList = orderMainService.getByUserIdAndType(userId, 2);
            List<ExpressOrderVO> result = new ArrayList<>();

            for (OrderMain order : orderList) {
                ExpressOrderVO orderVO = new ExpressOrderVO();
                BeanUtils.copyProperties(order, orderVO);

                // 获取寄件地址
                if (order.getFromAddressId() != null) {
                    UserAddress fromAddress = userAddressService.getById(order.getFromAddressId());
                    if (fromAddress != null) {
                        UserAddressVO fromAddressVO = new UserAddressVO();
                        BeanUtils.copyProperties(fromAddress, fromAddressVO);
                        // 手机号脱敏
                        if (fromAddress.getPhone() != null && fromAddress.getPhone().length() >= 11) {
                            fromAddressVO.setPhone(fromAddress.getPhone().substring(0, 3) + "****" + fromAddress.getPhone().substring(7));
                        }
                        orderVO.setFromAddress(fromAddressVO);
                    }
                }

                // 获取收件地址
                if (order.getAddressId() != null) {
                    UserAddress toAddress = userAddressService.getById(order.getAddressId());
                    if (toAddress != null) {
                        UserAddressVO toAddressVO = new UserAddressVO();
                        BeanUtils.copyProperties(toAddress, toAddressVO);
                        // 手机号脱敏
                        if (toAddress.getPhone() != null && toAddress.getPhone().length() >= 11) {
                            toAddressVO.setPhone(toAddress.getPhone().substring(0, 3) + "****" + toAddress.getPhone().substring(7));
                        }
                        orderVO.setToAddress(toAddressVO);
                    }
                }

                // 转换状态为中文
                orderVO.setStatus(getExpressOrderStatusText(order.getStatus()));
                orderVO.setPayStatus(getPayStatusText(order.getPayStatus()));

                result.add(orderVO);
            }

            return com.youyinda.common.R.success(result);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("获取快递订单列表失败: {}", e.getMessage(), e);
            throw new BusinessException(ErrorCodeEnum.SYSTEM_ERROR, "获取订单列表失败");
        }
    }

    /**
     * 获取快递订单详情
     * @param orderId 订单ID
     * @return 订单详情
     */
    @GetMapping("/detail/{orderId}")
    public com.youyinda.common.R<ExpressOrderVO> getOrderDetail(@PathVariable Long orderId) {
        try {
            // 获取当前用户ID
            Long userId = JwtUtil.getUserIdFromToken();
            if (userId == null) {
                throw new BusinessException(ErrorCodeEnum.UNAUTHORIZED, "未登录");
            }

            // 查询订单
            OrderMain order = orderMainService.getById(orderId);
            if (order == null || !order.getUserId().equals(userId) || order.getOrderType() != 2) {
                throw new BusinessException(ErrorCodeEnum.ORDER_NOT_FOUND, "订单不存在");
            }

            ExpressOrderVO orderVO = new ExpressOrderVO();
            BeanUtils.copyProperties(order, orderVO);

            // 获取寄件地址
            if (order.getFromAddressId() != null) {
                UserAddress fromAddress = userAddressService.getById(order.getFromAddressId());
                if (fromAddress != null) {
                    UserAddressVO fromAddressVO = new UserAddressVO();
                    BeanUtils.copyProperties(fromAddress, fromAddressVO);
                    orderVO.setFromAddress(fromAddressVO);
                }
            }

            // 获取收件地址
            if (order.getAddressId() != null) {
                UserAddress toAddress = userAddressService.getById(order.getAddressId());
                if (toAddress != null) {
                    UserAddressVO toAddressVO = new UserAddressVO();
                    BeanUtils.copyProperties(toAddress, toAddressVO);
                    orderVO.setToAddress(toAddressVO);
                }
            }

            // 转换状态为中文
            orderVO.setStatus(getExpressOrderStatusText(order.getStatus()));
            orderVO.setPayStatus(getPayStatusText(order.getPayStatus()));

            return com.youyinda.common.R.success(orderVO);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("获取快递订单详情失败: {}", e.getMessage(), e);
            throw new BusinessException(ErrorCodeEnum.SYSTEM_ERROR, "获取订单详情失败");
        }
    }

    /**
     * 取消快递订单
     * @param orderId 订单ID
     * @return 取消结果
     */
    @PostMapping("/cancel/{orderId}")
    public com.youyinda.common.R<?> cancelOrder(@PathVariable Long orderId) {
        try {
            // 获取当前用户ID
            Long userId = JwtUtil.getUserIdFromToken();
            if (userId == null) {
                throw new BusinessException(ErrorCodeEnum.UNAUTHORIZED, "未登录");
            }

            // 查询订单
            OrderMain order = orderMainService.getById(orderId);
            if (order == null || !order.getUserId().equals(userId) || order.getOrderType() != 2) {
                throw new BusinessException(ErrorCodeEnum.ORDER_NOT_FOUND, "订单不存在");
            }

            // 检查订单状态
            if (order.getStatus() != 1) { // 1-待支付
                throw new BusinessException(ErrorCodeEnum.ORDER_STATUS_ERROR, "只有待支付的订单可以取消");
            }

            // 取消订单
            order.setStatus(5); // 5-已取消
            orderMainService.updateById(order);

            return com.youyinda.common.R.success("订单取消成功");
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("取消快递订单失败: {}", e.getMessage(), e);
            throw new BusinessException(ErrorCodeEnum.ORDER_CANCEL_FAIL, "取消订单失败");
        }
    }

    /**
     * 获取快递订单状态文本
     */
    private String getExpressOrderStatusText(Integer status) {
        switch (status) {
            case 1: return "待支付";
            case 2: return "待揽收";
            case 3: return "运输中";
            case 4: return "派送中";
            case 5: return "已取消";
            case 6: return "已完成";
            case 7: return "售后中";
            default: return "未知状态";
        }
    }

    /**
     * 获取支付状态文本
     */
    private String getPayStatusText(Integer payStatus) {
        switch (payStatus) {
            case 1: return "未支付";
            case 2: return "已支付";
            case 3: return "支付失败";
            case 4: return "已退款";
            default: return "未知状态";
        }
    }
}
