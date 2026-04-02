package com.youyinda.controller;

import com.youyinda.common.BusinessException;
import com.youyinda.common.enums.ErrorCodeEnum;
import com.youyinda.dto.PrintOrderDTO;
import com.youyinda.entity.OrderMain;
import com.youyinda.entity.OrderDetail;
import com.youyinda.entity.UserAddress;
import com.youyinda.service.OrderMainService;
import com.youyinda.service.OrderDetailService;
import com.youyinda.service.UserAddressService;
import com.youyinda.util.JwtUtil;
import com.youyinda.vo.PrintOrderVO;
import com.youyinda.vo.PrintOrderDetailVO;
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
 * 打印订单控制器
 * 处理打印订单相关接口
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/print/order")
public class PrintOrderController {

    @Autowired
    private OrderMainService orderMainService;

    @Autowired
    private OrderDetailService orderDetailService;

    @Autowired
    private UserAddressService userAddressService;

    /**
     * 创建打印订单
     * @param printOrderDTO 打印订单请求参数
     * @return 订单ID
     */
    @PostMapping("/create")
    public com.youyinda.common.R<Long> createOrder(@Valid @RequestBody PrintOrderDTO printOrderDTO) {
        try {
            // 获取当前用户ID
            Long userId = JwtUtil.getUserIdFromToken();
            if (userId == null) {
                throw new BusinessException(ErrorCodeEnum.UNAUTHORIZED, "未登录");
            }

            // 验证地址是否存在
            UserAddress address = userAddressService.getById(printOrderDTO.getAddressId());
            if (address == null || !address.getUserId().equals(userId)) {
                throw new BusinessException(ErrorCodeEnum.ADDRESS_NOT_FOUND, "地址不存在");
            }

            // 计算订单总价（这里简化处理，实际应该根据价格引擎计算）
            BigDecimal totalPrice = BigDecimal.ZERO;
            for (com.youyinda.dto.PrintFileDTO file : printOrderDTO.getFiles()) {
                // 假设每页价格为0.5元
                BigDecimal filePrice = new BigDecimal(0.5)
                        .multiply(new BigDecimal(file.getPages()))
                        .multiply(new BigDecimal(file.getCopies()));
                totalPrice = totalPrice.add(filePrice);
            }

            // 创建订单主表
            OrderMain orderMain = new OrderMain();
            orderMain.setUserId(userId);
            orderMain.setOrderType(1); // 1-打印订单
            orderMain.setOrderNo(orderMainService.generateOrderNo());
            orderMain.setTotalPrice(totalPrice.doubleValue());
            orderMain.setStatus(1); // 1-待支付
            orderMain.setPayStatus(1); // 1-未支付
            orderMain.setAddressId(printOrderDTO.getAddressId());
            orderMain.setRemark(printOrderDTO.getRemark());
            orderMainService.save(orderMain);

            // 创建订单详情
            for (com.youyinda.dto.PrintFileDTO file : printOrderDTO.getFiles()) {
                OrderDetail orderDetail = new OrderDetail();
                orderDetail.setOrderId(orderMain.getId());
                orderDetail.setFileName(file.getFileName());
                orderDetail.setFileUrl(file.getFileUrl());
                orderDetail.setFileType(file.getFileType());
                orderDetail.setFileSize(file.getFileSize());
                orderDetail.setPrintPages(file.getPages());
                orderDetail.setPrintCopies(file.getCopies());
                orderDetail.setPaperType(printOrderDTO.getPaperType());
                orderDetail.setColorType(printOrderDTO.getColorType());
                orderDetail.setPrintSide(printOrderDTO.getPrintSide());
                orderDetail.setBindingType(printOrderDTO.getBindingType());
                orderDetail.setValueAddedService(printOrderDTO.getValueAddedService());
                // 计算详情小计
                BigDecimal unitPrice = new BigDecimal(0.5);
                BigDecimal subtotal = unitPrice
                        .multiply(new BigDecimal(file.getPages()))
                        .multiply(new BigDecimal(file.getCopies()));
                orderDetail.setUnitPrice(unitPrice.doubleValue());
                orderDetail.setSubtotal(subtotal.doubleValue());
                orderDetailService.save(orderDetail);
            }

            return com.youyinda.common.R.success(orderMain.getId());
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("创建打印订单失败: {}", e.getMessage(), e);
            throw new BusinessException(ErrorCodeEnum.ORDER_CREATE_FAIL, "创建订单失败");
        }
    }

    /**
     * 获取打印订单列表
     * @return 订单列表
     */
    @GetMapping("/list")
    public com.youyinda.common.R<List<PrintOrderVO>> getOrderList() {
        try {
            // 获取当前用户ID
            Long userId = JwtUtil.getUserIdFromToken();
            if (userId == null) {
                throw new BusinessException(ErrorCodeEnum.UNAUTHORIZED, "未登录");
            }

            // 查询用户的打印订单
            List<OrderMain> orderList = orderMainService.getByUserIdAndType(userId, 1);
            List<PrintOrderVO> result = new ArrayList<>();

            for (OrderMain order : orderList) {
                PrintOrderVO orderVO = new PrintOrderVO();
                BeanUtils.copyProperties(order, orderVO);

                // 获取订单详情
                List<OrderDetail> details = orderDetailService.getByOrderId(order.getId());
                List<PrintOrderDetailVO> detailVOs = new ArrayList<>();
                for (OrderDetail detail : details) {
                    PrintOrderDetailVO detailVO = new PrintOrderDetailVO();
                    BeanUtils.copyProperties(detail, detailVO);
                    detailVOs.add(detailVO);
                }
                orderVO.setDetails(detailVOs);

                // 获取收货地址
                if (order.getAddressId() != null) {
                    UserAddress address = userAddressService.getById(order.getAddressId());
                    if (address != null) {
                        UserAddressVO addressVO = new UserAddressVO();
                        BeanUtils.copyProperties(address, addressVO);
                        // 手机号脱敏
                        if (address.getPhone() != null && address.getPhone().length() >= 11) {
                            addressVO.setPhone(address.getPhone().substring(0, 3) + "****" + address.getPhone().substring(7));
                        }
                        orderVO.setAddress(addressVO);
                    }
                }

                // 转换状态为中文
                orderVO.setStatus(getOrderStatusText(order.getStatus()));
                orderVO.setPayStatus(getPayStatusText(order.getPayStatus()));

                result.add(orderVO);
            }

            return com.youyinda.common.R.success(result);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("获取打印订单列表失败: {}", e.getMessage(), e);
            throw new BusinessException(ErrorCodeEnum.SYSTEM_ERROR, "获取订单列表失败");
        }
    }

    /**
     * 获取打印订单详情
     * @param orderId 订单ID
     * @return 订单详情
     */
    @GetMapping("/detail/{orderId}")
    public com.youyinda.common.R<PrintOrderVO> getOrderDetail(@PathVariable Long orderId) {
        try {
            // 获取当前用户ID
            Long userId = JwtUtil.getUserIdFromToken();
            if (userId == null) {
                throw new BusinessException(ErrorCodeEnum.UNAUTHORIZED, "未登录");
            }

            // 查询订单
            OrderMain order = orderMainService.getById(orderId);
            if (order == null || !order.getUserId().equals(userId)) {
                throw new BusinessException(ErrorCodeEnum.ORDER_NOT_FOUND, "订单不存在");
            }

            PrintOrderVO orderVO = new PrintOrderVO();
            BeanUtils.copyProperties(order, orderVO);

            // 获取订单详情
            List<OrderDetail> details = orderDetailService.getByOrderId(order.getId());
            List<PrintOrderDetailVO> detailVOs = new ArrayList<>();
            for (OrderDetail detail : details) {
                PrintOrderDetailVO detailVO = new PrintOrderDetailVO();
                BeanUtils.copyProperties(detail, detailVO);
                detailVOs.add(detailVO);
            }
            orderVO.setDetails(detailVOs);

            // 获取收货地址
            if (order.getAddressId() != null) {
                UserAddress address = userAddressService.getById(order.getAddressId());
                if (address != null) {
                    UserAddressVO addressVO = new UserAddressVO();
                    BeanUtils.copyProperties(address, addressVO);
                    orderVO.setAddress(addressVO);
                }
            }

            // 转换状态为中文
            orderVO.setStatus(getOrderStatusText(order.getStatus()));
            orderVO.setPayStatus(getPayStatusText(order.getPayStatus()));

            return com.youyinda.common.R.success(orderVO);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("获取打印订单详情失败: {}", e.getMessage(), e);
            throw new BusinessException(ErrorCodeEnum.SYSTEM_ERROR, "获取订单详情失败");
        }
    }

    /**
     * 取消打印订单
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
            if (order == null || !order.getUserId().equals(userId)) {
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
            log.error("取消打印订单失败: {}", e.getMessage(), e);
            throw new BusinessException(ErrorCodeEnum.ORDER_CANCEL_FAIL, "取消订单失败");
        }
    }

    /**
     * 获取订单状态文本
     */
    private String getOrderStatusText(Integer status) {
        switch (status) {
            case 1: return "待支付";
            case 2: return "待打印";
            case 3: return "待发货";
            case 4: return "待收货";
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
