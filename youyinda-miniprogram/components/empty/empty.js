/**
 * 空状态组件
 */
Component({
  properties: {
    // 图标（emoji或iconfont）
    icon: {
      type: String,
      value: '📭'
    },
    // 自定义图片URL
    image: {
      type: String,
      value: ''
    },
    // 标题
    title: {
      type: String,
      value: '暂无数据'
    },
    // 描述
    desc: {
      type: String,
      value: ''
    },
    // 是否显示按钮
    showBtn: {
      type: Boolean,
      value: false
    },
    // 按钮文字
    btnText: {
      type: String,
      value: '去逛逛'
    }
  },

  methods: {
    /**
     * 按钮点击事件
     */
    onBtnTap() {
      this.triggerEvent('click');
    }
  }
});
