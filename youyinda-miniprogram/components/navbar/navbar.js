/**
 * 自定义导航栏组件
 */
Component({
  options: {
    multipleSlots: true,
    styleIsolation: 'apply-shared'
  },

  properties: {
    title: {
      type: String,
      value: ''
    },
    showBack: {
      type: Boolean,
      value: true
    },
    showHome: {
      type: Boolean,
      value: false
    },
    bgColor: {
      type: String,
      value: '#FFFFFF'
    },
    textColor: {
      type: String,
      value: '#1D2129'
    },
    backBg: {
      type: String,
      value: 'rgba(0,0,0,0.05)'
    },
    backType: {
      type: String,
      value: 'default'
    },
    transparent: {
      type: Boolean,
      value: false
    }
  },

  data: {
    statusBarHeight: 20,
    navBarHeight: 44
  },

  lifetimes: {
    attached() {
      this.initNavBar();
    }
  },

  methods: {
    initNavBar() {
      const systemInfo = wx.getSystemInfoSync();
      const menuButtonInfo = wx.getMenuButtonBoundingClientRect();
      
      const statusBarHeight = systemInfo.statusBarHeight;
      const navBarHeight = (menuButtonInfo.top - statusBarHeight) * 2 + menuButtonInfo.height;
      
      this.setData({
        statusBarHeight: statusBarHeight,
        navBarHeight: navBarHeight
      });
    },

    onBack() {
      this.triggerEvent('back');
      
      const pages = getCurrentPages();
      if (pages.length > 1) {
        wx.navigateBack();
      } else {
        wx.switchTab({
          url: '/pages/index/index'
        });
      }
    },

    onHome() {
      this.triggerEvent('home');
      wx.switchTab({
        url: '/pages/index/index'
      });
    }
  }
});
