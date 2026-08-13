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
        // TODO: 应调用腾讯地图逆地理编码接口解析经纬度获取省市区
        // 当前方案：使用用户选择的地址信息，让用户手动补充省市区
        const address = res.address || '';
        
        // 简单解析：尝试从地址中提取省市信息（实际应使用地图SDK）
        let province = this.data.form.province;
        let city = this.data.form.city;
        let district = this.data.form.district;
        
        if (address) {
          // 如果地址包含已知城市信息，尝试提取
          const cityMatch = address.match(/(.*?市)/);
          if (cityMatch) {
            city = cityMatch[1];
          }
          
          const provinceMatch = address.match(/(.*?省)/);
          if (provinceMatch) {
            province = provinceMatch[1];
          }
        }
        
        this.setData({
          'form.province': province,
          'form.city': city,
          'form.district': district,
          'form.detail': address || this.data.form.detail
        });
        
        if (!province || !city) {
          wx.showToast({
            title: '请手动选择或输入省市区',
            icon: 'none',
            duration: 2000
          });
        }
      },
      fail: (err) => {
        console.error('选择位置失败:', err);
        wx.showToast({
          title: '选择位置失败，请手动输入',
          icon: 'none'
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
