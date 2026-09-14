/**
 * 优印达 - SVG 图标组件
 * 用法：<icon name="printer" size="{{40}}" color="#4F46E5" />
 * size 单位 rpx；color 支持任意 CSS 色值
 */
const icons = require('./icons');

/* UTF-8 安全的 base64 编码（小程序环境无 btoa） */
const B64_CHARS = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/';
function base64Encode(str) {
  const bytes = [];
  const encoded = encodeURIComponent(str);
  for (let i = 0; i < encoded.length; i++) {
    if (encoded[i] === '%') {
      bytes.push(parseInt(encoded.substr(i + 1, 2), 16));
      i += 2;
    } else {
      bytes.push(encoded.charCodeAt(i));
    }
  }
  let out = '';
  for (let i = 0; i < bytes.length; i += 3) {
    const b0 = bytes[i];
    const b1 = i + 1 < bytes.length ? bytes[i + 1] : null;
    const b2 = i + 2 < bytes.length ? bytes[i + 2] : null;
    out += B64_CHARS[b0 >> 2];
    out += B64_CHARS[((b0 & 3) << 4) | (b1 === null ? 0 : b1 >> 4)];
    out += b1 === null ? '=' : B64_CHARS[((b1 & 15) << 2) | (b2 === null ? 0 : b2 >> 6)];
    out += b2 === null ? '=' : B64_CHARS[b2 & 63];
  }
  return out;
}

Component({
  externalClasses: ['ext-class'],

  properties: {
    name: { type: String, value: '' },
    size: { type: Number, value: 40 },
    color: { type: String, value: '#475569' },
    strokeWidth: { type: Number, value: 2 }
  },

  data: {
    src: ''
  },

  observers: {
    'name, color, strokeWidth': function () {
      this.buildIcon();
    }
  },

  lifetimes: {
    attached() {
      this.buildIcon();
    }
  },

  methods: {
    buildIcon() {
      const { name, color, strokeWidth } = this.data;
      const body = icons[name] || icons.dot;
      const svg =
        '<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="' +
        color + '" stroke-width="' + strokeWidth + '" stroke-linecap="round" stroke-linejoin="round">' +
        body + '</svg>';
      this.setData({ src: 'data:image/svg+xml;base64,' + base64Encode(svg) });
    }
  }
});
