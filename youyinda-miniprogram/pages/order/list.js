const request = require('../../utils/request');
const util = require('../../utils/util');

Page({
  data: {
    currentTab: 0,
    currentStatus: -1,
    orderList: []
  },

  onLoad() {
    this.loadOrderList();
  },

  onShow() {
    this.loadOrderList();
  },

  switchTab(e) {
    this.setData({
      currentTab: parseInt(e.currentTarget.dataset.index)
    });
    this.loadOrderList();
  },

  switchStatus(e) {
    this.setData({
      currentStatus: parseInt(e.currentTarget.dataset.status)
    });
    this.loadOrderList();
  },

  loadOrderList() {
    wx.showLoading({
      title: '加载中...'
    });

    const params = {
      orderType: this.data.currentTab === 0 ? null : this.data.currentTab,
      status: this.data.currentStatus === -1 ? null : this.data.currentStatus
    };

    request.get('/order/list', params).then(res => {
      wx.hideLoading();
      const orderList = (res.list || []).map(item => ({
        ...item,
        statusClass: this.getStatusClass(item.status),
        statusText: this.getStatusText(item.status),
        createTime: util.formatTime(item.createTime),
        canPay: item.status === 0,
        canCancel: item.status === 0,
        canTrack: item.orderType === 2 && item.status >= 2
      }));
      this.setData({ orderList });
    }).catch(err => {
      wx.hideLoading();
      wx.showToast({
        title: err.message || '加载失败',
        icon: 'none'
      });
    });
  },

  getStatusClass(status) {
    if (status === 0) return 'pending';
    if (status === 3) return 'success';
    return 'processing';
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

  goToDetail(e) {
    wx.navigateTo({
      url: `/pages/order/detail?id=${e.currentTarget.dataset.id}`
    });
  },

  payOrder(e) {
    const orderId = e.currentTarget.dataset.id;
    request.post('/wx-pay/create', {
      orderId: orderId
    }).then(payParams => {
      wx.requestPayment({
        ...payParams,
        success: () => {
          wx.showToast({
            title: '支付成功',
            icon: 'success'
          });
          setTimeout(() => {
            this.loadOrderList();
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

  cancelOrder(e) {
    wx.showModal({
      title: '提示',
      content: '确定要取消订单吗？',
      success: (res) => {
        if (res.confirm) {
          request.post('/order/cancel', {
            orderId: e.currentTarget.dataset.id
          }).then(() => {
            wx.showToast({
              title: '取消成功',
              icon: 'success'
            });
            setTimeout(() => {
              this.loadOrderList();
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

  goToTrack(e) {
    wx.navigateTo({
      url: `/pages/express/track?orderId=${e.currentTarget.dataset.id}`
    });
  }
});
