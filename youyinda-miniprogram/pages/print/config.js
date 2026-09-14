const app = getApp();
const priceUtil = require('../../utils/price');
const CART_KEY = 'printCart';

function getCart() {
  return wx.getStorageSync(CART_KEY) || { items: [] };
}

function saveCart(cart) {
  wx.setStorageSync(CART_KEY, cart);
}

Page({
  data: {
    mode: 'single',
    ids: [],
    colorType: 1,
    duplex: 1,
    paperSize: 'A4',
    copies: 1,
    binding: 'none',
    basePrice: '0.00',
    colorPrice: '0.00',
    bindingPrice: '0.00',
    totalPrice: '0.00',
    fileList: []
  },

  onLoad(options) {
    const mode = options.mode === 'batch' ? 'batch' : 'single';
    const cart = getCart();
    let targetItems = [];

    if (mode === 'batch') {
      const ids = (options.ids || '').split(',').filter(Boolean);
      targetItems = (cart.items || []).filter(it => ids.indexOf(it.cartId) >= 0);
      this.setData({ mode: 'batch', ids });
    } else {
      const id = options.id || '';
      const item = (cart.items || []).find(it => it.cartId === id);
      if (item) {
        targetItems = [item];
      }
      this.setData({ mode: 'single', ids: [id] });
    }

    // 回显配置：single 用对应项，batch 以第一个勾选项为准
    const firstConfig = targetItems.length > 0 ? targetItems[0].config : {};
    this.setData({
      fileList: targetItems.map(it => ({ name: it.name, size: it.size })),
      paperSize: firstConfig.paperSize || 'A4',
      colorType: firstConfig.colorType || 1,
      duplex: firstConfig.duplex || 1,
      copies: firstConfig.copies || 1,
      binding: firstConfig.binding || 'none'
    });
    this.calculatePrice();
  },

  selectColor(e) {
    this.setData({ colorType: parseInt(e.currentTarget.dataset.type, 10) });
    this.calculatePrice();
  },

  selectDuplex(e) {
    this.setData({ duplex: parseInt(e.currentTarget.dataset.type, 10) });
    this.calculatePrice();
  },

  selectPaper(e) {
    this.setData({ paperSize: e.currentTarget.dataset.size });
    this.calculatePrice();
  },

  selectBinding(e) {
    this.setData({ binding: e.currentTarget.dataset.type });
    this.calculatePrice();
  },

  decreaseQuantity() {
    if (this.data.copies > 1) {
      this.setData({ copies: this.data.copies - 1 });
      this.calculatePrice();
    }
  },

  increaseQuantity() {
    this.setData({ copies: this.data.copies + 1 });
    this.calculatePrice();
  },

  calculatePrice() {
    const { colorType, duplex, paperSize, copies, binding } = this.data;
    const price = priceUtil.calcItemPrice({ paperSize, colorType, duplex, copies, binding });
    this.setData({
      basePrice: price.basePrice.toFixed(2),
      colorPrice: price.colorPrice.toFixed(2),
      bindingPrice: price.bindingPrice.toFixed(2),
      totalPrice: price.totalPrice.toFixed(2)
    });
  },

  /**
   * 保存配置：single 写回单项，batch 应用到所有勾选项，保存后返回购物车
   */
  saveConfig() {
    const { mode, ids, paperSize, colorType, duplex, copies, binding } = this.data;
    const config = {
      paperSize,
      colorType,
      duplex,
      copies,
      binding
    };
    const price = priceUtil.calcItemPrice(config);
    config.basePrice = price.basePrice;
    config.colorPrice = price.colorPrice;
    config.bindingPrice = price.bindingPrice;
    config.totalPrice = price.totalPrice;

    const cart = getCart();
    const idSet = new Set(ids.filter(Boolean));
    cart.items = (cart.items || []).map(it => {
      if (idSet.has(it.cartId)) {
        return { ...it, config: { ...config } };
      }
      return it;
    });
    saveCart(cart);

    wx.showToast({ title: '配置已保存', icon: 'success' });
    setTimeout(() => {
      wx.navigateBack();
    }, 1200);
  }
});
