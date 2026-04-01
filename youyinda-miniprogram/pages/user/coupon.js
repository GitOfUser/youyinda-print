const request = require('../../utils/request');

Page({
  data: {
    currentTab: 0,
    couponList: []
  },

  onLoad() {
    this.loadCouponList();
  },

  switchTab(e) {
    this.setData({
      currentTab: parseInt(e.currentTarget.dataset.index)
    });
    this.loadCouponList();
  },

  loadCouponList() {
    wx.showLoading({
      title: '加载中...'
    });

    request.get('/user/coupon/list', {
      status: this.data.currentTab
    }).then(res => {
      wx.hideLoading();
      this.setData({
        couponList: res.list || []
      });
    }).catch(err => {
      wx.hideLoading();
      wx.showToast({
        title: err.message || '加载失败',
        icon: 'none'
      });
    });
  }
});
