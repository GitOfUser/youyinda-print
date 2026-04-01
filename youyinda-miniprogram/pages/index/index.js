const app = getApp();
const request = require('../../utils/request');

Page({
  data: {},

  onLoad() {
    if (!app.globalData.isLogin) {
      app.login().catch(() => {});
    }
  },

  goToPrint() {
    app.ensureLogin().then(() => {
      wx.navigateTo({
        url: '/pages/print/upload'
      });
    }).catch(() => {});
  },

  goToExpress() {
    app.ensureLogin().then(() => {
      wx.navigateTo({
        url: '/pages/express/sender'
      });
    }).catch(() => {});
  },

  goToOrderList() {
    wx.switchTab({
      url: '/pages/order/list'
    });
  },

  goToAddress() {
    app.ensureLogin().then(() => {
      wx.navigateTo({
        url: '/pages/user/address/list'
      });
    }).catch(() => {});
  },

  goToCoupon() {
    app.ensureLogin().then(() => {
      wx.navigateTo({
        url: '/pages/user/coupon'
      });
    }).catch(() => {});
  },

  goToHelp() {
    wx.navigateTo({
      url: '/pages/user/help'
    });
  }
});
