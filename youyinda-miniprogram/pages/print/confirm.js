const app = getApp();
const request = require('../../utils/request');
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
    items: [],
    cartIds: [],
    totalBase: '0.00',
    totalColor: '0.00',
    totalBinding: '0.00',
    finalPrice: '0.00',
    address: null,
    coupon: null,
    agreed: false,
    remark: '',
    availableCoupons: 0,
    canSubmit: false
  },

  onLoad(options) {
    const ids = (options.ids || '').split(',').filter(Boolean);
    this.setData({ cartIds: ids });
    this.loadItems(ids);
  },

  onShow() {
    // 地址选择返回后重新计算可提交状态
    this.updateCanSubmit();
  },

  loadItems(ids) {
    const cart = getCart();
    const items = (cart.items || [])
      .filter(it => ids.indexOf(it.cartId) >= 0)
      .map(it => ({
        cartId: it.cartId,
        name: it.name,
        size: it.size,
        fileUrl: it.fileUrl,
        fileId: it.fileId,
        config: it.config || {},
        bindingName: priceUtil.BINDING_NAMES[(it.config || {}).binding] || '无装订'
      }));
    this.setData({ items });
    this.calcSummary();
    this.updateCanSubmit();
  },

  calcSummary() {
    let totalBase = 0;
    let totalColor = 0;
    let totalBinding = 0;
    let finalPrice = 0;
    this.data.items.forEach(it => {
      const c = it.config || {};
      const copies = c.copies || 1;
      totalBase += Number(c.basePrice || 0) * copies;
      totalColor += Number(c.colorPrice || 0) * copies;
      totalBinding += Number(c.bindingPrice || 0) * copies;
      finalPrice += Number(c.totalPrice || 0);
    });
    this.setData({
      totalBase: totalBase.toFixed(2),
      totalColor: totalColor.toFixed(2),
      totalBinding: totalBinding.toFixed(2),
      finalPrice: finalPrice.toFixed(2)
    });
  },

  updateCanSubmit() {
    this.setData({
      canSubmit: !!this.data.address && this.data.agreed
    });
  },

  goBack() {
    wx.navigateBack();
  },

  selectAddress() {
    wx.navigateTo({
      url: '/pages/user/address/list?select=1'
    });
  },

  onRemarkInput(e) {
    this.setData({ remark: e.detail.value });
  },

  toggleAgreement() {
    this.setData({ agreed: !this.data.agreed });
    this.updateCanSubmit();
  },

  selectCoupon() {
    wx.showToast({ title: '暂无可用优惠券', icon: 'none' });
  },

  createOrder() {
    if (!this.data.address) {
      wx.showToast({ title: '请选择收货地址', icon: 'none' });
      return;
    }
    if (!this.data.agreed) {
      wx.showToast({ title: '请先阅读并同意服务协议', icon: 'none' });
      return;
    }
    if (this.data.items.length === 0) {
      wx.showToast({ title: '订单文件为空', icon: 'none' });
      return;
    }

    wx.showLoading({ title: '创建订单中...' });

    const orderData = {
      addressId: this.data.address.id,
      couponId: this.data.coupon ? this.data.coupon.id : null,
      totalAmount: parseFloat(this.data.finalPrice),
      remark: this.data.remark,
      items: this.data.items.map(it => ({
        fileId: it.fileId,
        fileUrl: it.fileUrl,
        fileName: it.name,
        fileType: null,
        fileSize: null,
        paperType: it.config.paperSize,
        colorType: String(it.config.colorType),
        singleDouble: it.config.duplex === 2 ? 'double' : 'single',
        copies: it.config.copies || 1,
        quantity: 10,
        bindingType: it.config.binding,
        specJson: null
      }))
    };

    request.post('/print/order/create', orderData).then(res => {
      wx.hideLoading();
      this.payOrder(res);
    }).catch(err => {
      wx.hideLoading();
      wx.showToast({ title: err.message || '创建订单失败', icon: 'none' });
    });
  },

  payOrder(orderId) {
    request.post('/wx-pay/create', { orderId, orderType: 1 }).then(payParams => {
      wx.requestPayment({
        ...payParams,
        success: () => {
          this.removeSettledItems();
          wx.showToast({ title: '支付成功', icon: 'success' });
          setTimeout(() => {
            wx.reLaunch({ url: '/pages/order/list' });
          }, 1500);
        },
        fail: () => {
          wx.showToast({ title: '支付取消', icon: 'none' });
        }
      });
    }).catch(err => {
      wx.showToast({ title: err.message || '支付失败', icon: 'none' });
    });
  },

  /**
   * 下单支付成功后，从购物车移除本次已结算项并持久化
   */
  removeSettledItems() {
    const cart = getCart();
    const ids = this.data.cartIds;
    cart.items = (cart.items || []).filter(it => ids.indexOf(it.cartId) < 0);
    saveCart(cart);
  }
});
