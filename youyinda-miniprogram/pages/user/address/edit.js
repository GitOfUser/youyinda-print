const request = require('../../../utils/request');

Page({
  data: {
    addressId: null,
    formData: {
      name: '',
      phone: '',
      region: [],
      regionText: '',
      detail: '',
      isDefault: false
    }
  },

  onLoad(options) {
    if (options.id) {
      this.setData({ addressId: options.id });
      this.loadAddressDetail();
    }
  },

  loadAddressDetail() {
    wx.showLoading({
      title: '加载中...'
    });

    request.get('/user/address/detail', {
      id: this.data.addressId
    }).then(res => {
      wx.hideLoading();
      // 回显时把后端返回的 province/city/district 组装回 region 数组与 regionText
      const province = res.province || '';
      const city = res.city || '';
      const district = res.district || '';
      this.setData({
        formData: {
          name: res.name || '',
          phone: res.phone || '',
          region: province ? [province, city, district] : [],
          regionText: province ? `${province}${city}${district}` : '',
          detail: res.detail || '',
          isDefault: res.isDefault || false
        }
      });
    }).catch(err => {
      wx.hideLoading();
      wx.showToast({
        title: err.message || '加载失败',
        icon: 'none'
      });
    });
  },

  onInput(e) {
    const field = e.currentTarget.dataset.field;
    this.setData({
      [`formData.${field}`]: e.detail.value
    });
  },

  onSwitchChange(e) {
    this.setData({
      'formData.isDefault': e.detail.value
    });
  },

  /**
   * 所在地区选择（微信内置 region 三级联动）
   */
  onRegionChange(e) {
    const region = e.detail.value || [];
    this.setData({
      'formData.region': region,
      'formData.regionText': region.join('')
    });
  },

  saveAddress() {
    const { name, phone, region, detail, isDefault } = this.data.formData;

    if (!name) {
      wx.showToast({
        title: '请输入收货人姓名',
        icon: 'none'
      });
      return;
    }

    if (!phone) {
      wx.showToast({
        title: '请输入手机号码',
        icon: 'none'
      });
      return;
    }

    if (!/^1\d{10}$/.test(phone)) {
      wx.showToast({
        title: '请输入正确的手机号码',
        icon: 'none'
      });
      return;
    }

    // 校验省市区已选
    if (!region || region.length < 3) {
      wx.showToast({
        title: '请选择所在地区',
        icon: 'none'
      });
      return;
    }

    if (!detail) {
      wx.showToast({
        title: '请输入详细地址',
        icon: 'none'
      });
      return;
    }

    wx.showLoading({
      title: '保存中...'
    });

    // 提交给后端仍是 province/city/district 三个独立字段，从 region 数组取值
    const payload = {
      name,
      phone,
      province: region[0],
      city: region[1],
      district: region[2],
      detail,
      isDefault
    };
    if (this.data.addressId) {
      payload.id = this.data.addressId;
    }

    const url = this.data.addressId ? '/user/address/update' : '/user/address/add';

    request.post(url, payload).then(() => {
      wx.hideLoading();
      wx.showToast({
        title: '保存成功',
        icon: 'success'
      });
      setTimeout(() => {
        wx.navigateBack();
      }, 1500);
    }).catch(err => {
      wx.hideLoading();
      wx.showToast({
        title: err.message || '保存失败',
        icon: 'none'
      });
    });
  }
});
