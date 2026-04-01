const app = getApp();
const request = require('../../utils/request');

Page({
  data: {
    fileList: [],
    config: {},
    address: null,
    coupon: null,
    finalPrice: 0
  },

  onLoad() {
    const fileList = wx.getStorageSync('printFiles') || [];
    const config = wx.getStorageSync('printConfig') || {};
    this.setData({
      fileList,
      config,
      finalPrice: parseFloat(config.totalPrice || 0)
    });
  },

  selectAddress() {
    wx.navigateTo({
      url: '/pages/user/address/list?select=1'
    });
  },

  selectCoupon() {
    wx.showToast({
      title: '优惠券功能开发中',
      icon: 'none'
    });
  },

  createOrder() {
    if (!this.data.address) {
      wx.showToast({
        title: '请选择收货地址',
        icon: 'none'
      });
      return;
    }

    wx.showLoading({
      title: '创建订单中...'
    });

    const orderData = {
      fileList: this.data.fileList,
      config: this.data.config,
      address: this.data.address,
      couponId: this.data.coupon?.id,
      totalAmount: this.data.finalPrice
    };

    request.post('/print/order/create', orderData).then(res => {
      wx.hideLoading();
      this.payOrder(res.orderId);
    }).catch(err => {
      wx.hideLoading();
      wx.showToast({
        title: err.message || '创建订单失败',
        icon: 'none'
      });
    });
  },

  payOrder(orderId) {
    request.post('/wx-pay/create', {
      orderId: orderId,
      orderType: 1
    }).then(payParams => {
      wx.requestPayment({
        ...payParams,
        success: () => {
          wx.showToast({
            title: '支付成功',
            icon: 'success'
          });
          setTimeout(() => {
            wx.reLaunch({
              url: '/pages/order/list'
            });
          }, 1500);
        },
        fail: () => {
          wx.showToast({
            title: '支付取消',
            icon: 'none'
          });
        }
      });
    }).catch(err => {
      wx.showToast({
        title: err.message || '支付失败',
        icon: 'none'
      });
    });
  }
});
