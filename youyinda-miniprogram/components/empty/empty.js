/**
 * 空状态组件
 */
Component({
  options: {
    styleIsolation: 'apply-shared'
  },

  properties: {
    icon: {
      type: String,
      value: 'package'
    },
    image: {
      type: String,
      value: ''
    },
    title: {
      type: String,
      value: '暂无数据'
    },
    desc: {
      type: String,
      value: ''
    },
    showBtn: {
      type: Boolean,
      value: false
    },
    btnText: {
      type: String,
      value: '去逛逛'
    },
    btnType: {
      type: String,
      value: 'primary'
    },
    fixed: {
      type: Boolean,
      value: false
    }
  },

  methods: {
    onBtnTap() {
      this.triggerEvent('click');
    }
  }
});
