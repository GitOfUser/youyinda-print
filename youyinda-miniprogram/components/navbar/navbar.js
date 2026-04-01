/**
 * 自定义导航栏组件
 */
Component({
  options: {
    multipleSlots: true
  },

  properties: {
    // 标题
    title: {
      type: String,
      value: ''
    },
    // 是否显示返回按钮
    showBack: {
      type: Boolean,
      value: true
    },
    // 背景颜色
    bgColor: {
      type: String,
      value: '#FF7D00'
    },
    // 文字颜色
    textColor: {
      type: String,
      value: '#FFFFFF'
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
    /**
     * 初始化导航栏高度
     */
    initNavBar() {
      const systemInfo = wx.getSystemInfoSync();
      const menuButtonInfo = wx.getMenuButtonBoundingClientRect();
      
      // 状态栏高度
      const statusBarHeight = systemInfo.statusBarHeight;
      
      // 导航栏高度 = (菜单按钮顶部位置 - 状态栏高度) * 2 + 菜单按钮高度
      const navBarHeight = (menuButtonInfo.top - statusBarHeight) * 2 + menuButtonInfo.height;
      
      this.setData({
        statusBarHeight: statusBarHeight,
        navBarHeight: navBarHeight
      });
    },

    /**
     * 返回按钮点击
     */
    onBack() {
      // 触发返回事件
      this.triggerEvent('back');
      
      // 默认返回上一页
      const pages = getCurrentPages();
      if (pages.length > 1) {
        wx.navigateBack();
      } else {
        wx.switchTab({
          url: '/pages/index/index'
        });
      }
    }
  }
});
