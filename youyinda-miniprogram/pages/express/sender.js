const app = getApp();

Page({
  data: {
    senderAddress: null,
    receiverAddress: null
  },

  onLoad() {},

  selectSender() {
    wx.navigateTo({
      url: '/pages/user/address/list?select=1&type=sender'
    });
  },

  selectReceiver() {
    wx.navigateTo({
      url: '/pages/user/address/list?select=1&type=receiver'
    });
  },

  exchangeAddress() {
    const { senderAddress, receiverAddress } = this.data;
    this.setData({
      senderAddress: receiverAddress,
      receiverAddress: senderAddress
    });
  },

  goToPackage() {
    const app = getApp();
    if (!app.globalData.flowState) app.globalData.flowState = {};
    app.globalData.flowState.senderAddress = this.data.senderAddress;
    app.globalData.flowState.receiverAddress = this.data.receiverAddress;
    // 写入旧 Storage key 以兼容其他模块
    wx.setStorageSync('senderAddress', this.data.senderAddress);
    wx.setStorageSync('receiverAddress', this.data.receiverAddress);
    wx.navigateTo({
      url: '/pages/express/package'
    });
  }
});
