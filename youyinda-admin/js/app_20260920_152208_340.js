// 页面组件注册：将 pages/*.js 中定义的全局对象注册为 Vue 组件
// 简易 v-chart 封装（echarts 已全局引入，未用 vue-echarts 库）
AdminApp.component('v-chart', {
    props: {
        option: { type: Object, default: null },
        autoresize: { type: Boolean, default: false }
    },
    template: '<div ref="el" style="width:100%;height:100%;"></div>',
    mounted() {
        if (typeof echarts !== 'undefined') {
            this.chart = echarts.init(this.$refs.el);
            if (this.option) {
                this.chart.setOption(this.option);
            }
        }
    },
    watch: {
        option: {
            deep: true,
            handler(val) {
                if (this.chart && val) {
                    this.chart.setOption(val);
                }
            }
        }
    },
    beforeUnmount() {
        if (this.chart) {
            this.chart.dispose();
            this.chart = null;
        }
    }
});

// 单页面组件（dashboard / orders / users / coupon / sys-config）
AdminApp.component('dashboard', window.DashboardPage);
AdminApp.component('orders', window.OrdersPage);
AdminApp.component('users', window.UsersPage);
AdminApp.component('coupon', window.CouponPage);
AdminApp.component('sys-config', window.SysConfigPage);

// 价格配置：price-base 默认打印价格 tab，profit-rule 默认盈利规则 tab
const makePriceComponent = (defaultTab, compName) => {
    const baseData = typeof window.PricePage.data === 'function' ? window.PricePage.data() : {};
    return {
        ...window.PricePage,
        name: compName,
        data() {
            return { ...baseData, activeTab: defaultTab };
        }
    };
};

AdminApp.component('price-base', makePriceComponent('print', 'price-base'));
AdminApp.component('profit-rule', makePriceComponent('rule', 'profit-rule'));

AdminApp.mount('#app');
