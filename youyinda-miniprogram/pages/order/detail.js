const request = require('../../utils/request');
const util = require('../../utils/util');

Page({
  data: {
    orderId: null,
    orderInfo: null
  },

  onLoad(options) {
    if (options.id) {
      this.setData({ orderId: options.id });
      this.loadOrderDetail();
    }
  },

  loadOrderDetail() {
    wx.showLoading({
      title: '加载中...'
    });

    request.get('/order/detail', {
      orderId: this.data.orderId
    }).then(res => {
      wx.hideLoading();
      const orderInfo = {
        ...res,
        statusIcon: this.getStatusIcon(res.status),
        statusText: this.getStatusText(res.status),
        statusDesc: this.getStatusDesc(res.status),
        createTime: util.formatTime(res.createTime),
        payTime: res.payTime ? util.formatTime(res.payTime) : '',
        canPay: res.status === 0,
        canCancel: res.status === 0,
        canTrack: res.orderType === 2 && res.status >= 2
      };
      this.setData({ orderInfo });
    }).catch(err => {
      wx.hideLoading();
      wx.showToast({
        title: err.message || '加载失败',
        icon: 'none'
      });
    });
  },

  getStatusIcon(status) {
    const iconMap = {
      0: '⏰',
      1: '📦',
      2: '🚚',
      3: '✅',
      4: '❌'
    };
    return iconMap[status] || '📋';
  },

  getStatusText(status) {
    const statusMap = {
      0: '待付款',
      1: '待发货',
      2: '运输中',
      3: '已完成',
      4: '已取消'
    };
    return statusMap[status] || '未知';
  },

  getStatusDesc(status) {
    const descMap = {
      0: '请尽快完成支付',
      1: '商家正在准备中',
      2: '商品正在配送中',
      3: '订单已完成，感谢您的使用',
      4: '订单已取消'
    };
    return descMap[status] || '';
  },

  payOrder() {
    request.post('/wx-pay/create', {
      orderId: this.data.orderId
    }).then(payParams => {
      wx.requestPayment({
        ...payParams,
        success: () => {
          wx.showToast({
            title: '支付成功',
            icon: 'success'
          });
          setTimeout(() => {
            this.loadOrderDetail();
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
  },

  cancelOrder() {
    wx.showModal({
      title: '提示',
      content: '确定要取消订单吗？',
      success: (res) => {
        if (res.confirm) {
          request.post('/order/cancel', {
            orderId: this.data.orderId
          }).then(() => {
            wx.showToast({
              title: '取消成功',
              icon: 'success'
            });
            setTimeout(() => {
              this.loadOrderDetail();
            }, 1500);
          }).catch(err => {
            wx.showToast({
              title: err.message || '取消失败',
              icon: 'none'
            });
          });
        }
      }
    });
  },

  goToTrack() {
    wx.navigateTo({
      url: `/pages/express/track?orderId=${this.data.orderId}`
    });
  }
});
