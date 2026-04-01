const request = require('../../utils/request');
const priceUtil = require('../../utils/price');

Page({
  data: {
    companyList: [],
    selectedCompany: null
  },

  onLoad() {
    this.loadCompanyList();
  },

  loadCompanyList() {
    const packageInfo = wx.getStorageSync('packageInfo') || {};
    const senderAddress = wx.getStorageSync('senderAddress') || {};
    const receiverAddress = wx.getStorageSync('receiverAddress') || {};

    const mockCompanies = [
      { id: 1, name: '顺丰速运', desc: '时效快，服务好', price: priceUtil.calcExpressPrice(1, 12).toFixed(2), logo: '' },
      { id: 2, name: '中通快递', desc: '性价比高', price: priceUtil.calcExpressPrice(1, 8).toFixed(2), logo: '' },
      { id: 3, name: '圆通速递', desc: '网点多', price: priceUtil.calcExpressPrice(1, 7.5).toFixed(2), logo: '' },
      { id: 4, name: '韵达快递', desc: '价格实惠', price: priceUtil.calcExpressPrice(1, 7).toFixed(2), logo: '' }
    ];

    this.setData({
      companyList: mockCompanies
    });
  },

  selectCompany(e) {
    this.setData({
      selectedCompany: e.currentTarget.dataset.item
    });
  },

  goToConfirm() {
    wx.setStorageSync('selectedCompany', this.data.selectedCompany);
    wx.navigateTo({
      url: '/pages/express/confirm'
    });
  }
});
