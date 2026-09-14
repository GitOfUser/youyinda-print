const request = require('../../utils/request');

Page({
  data: {
    orderId: null,
    orderStatusText: '运输中',
    latestTime: '',
    companyName: '',
    trackingNo: '',
    trackList: []
  },

  onLoad(options) {
    if (options.orderId) {
      this.setData({ orderId: options.orderId });
      this.loadTrackInfo();
    } else {
      wx.showModal({
        title: '提示',
        content: '缺少订单号，无法查询物流信息',
        showCancel: false,
        success: () => {
          wx.navigateBack();
        }
      });
    }
  },

  copyTrackingNo() {
    const no = this.data.trackingNo;
    if (!no) return;
    wx.setClipboardData({
      data: no,
      success: () => {
        wx.showToast({ title: '运单号已复制', icon: 'none' });
      }
    });
  },

  loadTrackInfo() {
    request.get('/express/track', {
      orderId: this.data.orderId
    }).then(res => {
      this.setData({
        orderStatusText: res.statusText || '运输中',
        latestTime: res.latestTime || '',
        companyName: res.companyName || '',
        trackingNo: res.trackingNo || '',
        trackList: res.trackList || []
      });
    }).catch(err => {
      wx.showToast({
        title: err.message || '获取物流信息失败',
        icon: 'none'
      });
    });
  }
});
