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
    }
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
