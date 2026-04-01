const request = require('../../../utils/request');

Page({
  data: {
    addressId: null,
    form: {
      name: '',
      phone: '',
      province: '',
      city: '',
      district: '',
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
      this.setData({
        form: {
          name: res.name || '',
          phone: res.phone || '',
          province: res.province || '',
          city: res.city || '',
          district: res.district || '',
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
      [`form.${field}`]: e.detail.value
    });
  },

  onSwitchChange(e) {
    this.setData({
      'form.isDefault': e.detail.value
    });
  },

  selectRegion() {
    wx.chooseLocation({
      success: (res) => {
        this.setData({
          'form.province': '广东省',
          'form.city': '深圳市',
          'form.district': '南山区',
          'form.detail': res.address || this.data.form.detail
        });
      }
    });
  },

  saveAddress() {
    const { name, phone, province, detail } = this.data.form;

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

    if (!province) {
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

    const url = this.data.addressId ? '/user/address/update' : '/user/address/add';
    const data = this.data.addressId ? {
      id: this.data.addressId,
      ...this.data.form
    } : this.data.form;

    request.post(url, data).then(() => {
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
