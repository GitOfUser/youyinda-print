const app = getApp();
const request = require('../../utils/request');

Page({
  data: {
    senderAddress: null,
    receiverAddress: null,
    packageInfo: null,
    selectedCompany: null,
    coupon: null,
    finalPrice: 0
  },

  onLoad() {
    const senderAddress = wx.getStorageSync('senderAddress');
    const receiverAddress = wx.getStorageSync('receiverAddress');
    const packageInfo = wx.getStorageSync('packageInfo');
    const selectedCompany = wx.getStorageSync('selectedCompany');

    this.setData({
      senderAddress,
      receiverAddress,
      packageInfo,
      selectedCompany,
      finalPrice: parseFloat(selectedCompany.price)
    });
  },

  selectCoupon() {
    wx.showToast({
      title: '优惠券功能开发中',
      icon: 'none'
    });
  },

  createOrder() {
    wx.showLoading({
      title: '创建订单中...'
    });

    const orderData = {
      senderAddress: this.data.senderAddress,
      receiverAddress: this.data.receiverAddress,
      packageInfo: this.data.packageInfo,
      selectedCompany: this.data.selectedCompany,
      couponId: this.data.coupon?.id,
      totalAmount: this.data.finalPrice
    };

    request.post('/express/order/create', orderData).then(res => {
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
      orderType: 2
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
