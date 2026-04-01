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
    wx.setStorageSync('senderAddress', this.data.senderAddress);
    wx.setStorageSync('receiverAddress', this.data.receiverAddress);
    wx.navigateTo({
      url: '/pages/express/package'
    });
  }
});
