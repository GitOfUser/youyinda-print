const app = getApp();
const priceUtil = require('../../utils/price');

Page({
  data: {
    colorType: 1,
    duplex: 1,
    paperSize: 'A4',
    copies: 1,
    basePrice: 1.00,
    colorPrice: 0,
    totalPrice: 1.00,
    fileList: []
  },

  onLoad() {
    const fileList = wx.getStorageSync('printFiles') || [];
    this.setData({ fileList });
    this.calculatePrice();
  },

  selectColor(e) {
    this.setData({
      colorType: parseInt(e.currentTarget.dataset.type)
    });
    this.calculatePrice();
  },

  selectDuplex(e) {
    this.setData({
      duplex: parseInt(e.currentTarget.dataset.type)
    });
    this.calculatePrice();
  },

  selectPaper(e) {
    this.setData({
      paperSize: e.currentTarget.dataset.size
    });
    this.calculatePrice();
  },

  decreaseQuantity() {
    if (this.data.copies > 1) {
      this.setData({
        copies: this.data.copies - 1
      });
      this.calculatePrice();
    }
  },

  increaseQuantity() {
    this.setData({
      copies: this.data.copies + 1
    });
    this.calculatePrice();
  },

  calculatePrice() {
    const { colorType, duplex, paperSize, copies, fileList } = this.data;
    const pageCount = fileList.length * 10;
    
    const price = priceUtil.calcPrintPrice({
      pageCount,
      colorType,
      duplex,
      paperSize,
      copies
    });

    this.setData({
      basePrice: price.basePrice.toFixed(2),
      colorPrice: price.colorPrice.toFixed(2),
      totalPrice: price.totalPrice.toFixed(2)
    });
  },

  goToConfirm() {
    const printConfig = {
      colorType: this.data.colorType,
      duplex: this.data.duplex,
      paperSize: this.data.paperSize,
      copies: this.data.copies,
      totalPrice: this.data.totalPrice
    };
    wx.setStorageSync('printConfig', printConfig);
    wx.navigateTo({
      url: '/pages/print/confirm'
    });
  }
});
