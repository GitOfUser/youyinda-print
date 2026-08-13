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
    const totalPages = fileList.length * 10; // 假设每个文件10页
    
    // 转换参数格式以匹配 price.js 的函数签名
    const price = priceUtil.calcPrintPrice({
      totalPages: totalPages,
      isDoubleSide: duplex === 2, // 1=单面, 2=双面
      colorType: colorType === 1 ? 'bw' : 'color', // 1=黑白, 2=彩色
      paperSize: paperSize,
      printType: 'normal',
      isBinding: false
    });

    this.setData({
      basePrice: (price.totalPrice / copies).toFixed(2),
      colorPrice: (colorType === 2 ? price.totalPrice * 0.8 : 0).toFixed(2),
      totalPrice: price.totalPrice.toFixed(2)
    });
  },

  goToConfirm() {
    const app = getApp();
    if (!app.globalData.flowState) app.globalData.flowState = {};
    app.globalData.flowState.printConfig = {
      colorType: this.data.colorType,
      duplex: this.data.duplex,
      paperSize: this.data.paperSize,
      copies: this.data.copies,
      totalPrice: this.data.totalPrice
    };
    // 写入旧 Storage key 以兼容其他模块
    wx.setStorageSync('printConfig', app.globalData.flowState.printConfig);
    wx.navigateTo({
      url: '/pages/print/confirm'
    });
  }
});
