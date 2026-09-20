window.DashboardPage = {
    name: 'dashboard',
    data() {
        return {
            loading: false,
            stats: {
                orderStats: { totalOrders: 0, todayOrders: 0, pendingOrders: 0, completedOrders: 0 },
                revenueStats: { totalRevenue: 0, todayRevenue: 0, totalProfit: 0, todayProfit: 0 },
                userStats: { totalUsers: 0, todayNewUsers: 0, weekNewUsers: 0, monthNewUsers: 0 },
                businessRatio: { printRatio: 0, expressRatio: 0 },
                trendData: { dates: [], orderCounts: [], revenues: [] }
            },
            recentOrders: [],
            chartRange: 7,
            trendOptions: {},
            pieOptions: {}
        };
    },
    computed: {
        orderCompletionRate() {
            const total = this.stats.orderStats.totalOrders || 0;
            const completed = this.stats.orderStats.completedOrders || 0;
            return total > 0 ? Math.round(completed / total * 100) : 0;
        },
        orderTypeText() {
            return (t) => (Number(t) === 2 ? '快递' : '打印');
        },
        orderStatusText() {
            return (s) => this.statusMap[Number(s)] || '未知';
        }
    },
    created() {
        this.statusMap = { 1: '待支付', 2: '已支付', 3: '打印中', 4: '已打印', 5: '已完成', 6: '已取消', 7: '已完成', 9: '退款中', 10: '已退款' };
    },
    mounted() {
        this.loadStats();
        this.loadRecentOrders();
    },
    methods: {
        formatMoney(v) {
            return v === null || v === undefined ? '0.00' : Number(v).toFixed(2);
        },
        formatDate(v) {
            if (!v) return '-';
            return String(v).replace('T', ' ').slice(0, 16);
        },
        async loadStats() {
            this.loading = true;
            try {
                const stats = await AdminAPI.getDashboardStats();
                Object.assign(this.stats, stats);
                this.renderTrend();
                this.renderPie();
            } catch (e) {
                console.error('load stats failed:', e);
            } finally {
                this.loading = false;
            }
        },
        async changeRange(days) {
            this.chartRange = days;
            try {
                const trend = await AdminAPI.getDashboardChart(days);
                this.stats.trendData = trend;
                this.renderTrend();
            } catch (e) {
                console.error('load chart failed:', e);
            }
        },
        renderTrend() {
            const td = this.stats.trendData || {};
            const dates = td.dates || [];
            const orders = td.orderCounts || [];
            const revenues = td.revenues || [];
            this.trendOptions = {
                tooltip: { trigger: 'axis' },
                legend: { data: ['订单量', '营收'] },
                grid: { left: 40, right: 50, top: 40, bottom: 30 },
                xAxis: { type: 'category', data: dates },
                yAxis: [
                    { type: 'value', name: '订单量' },
                    { type: 'value', name: '营收(元)' }
                ],
                series: [
                    { name: '订单量', type: 'line', smooth: true, data: orders, itemStyle: { color: '#FF7D00' }, areaStyle: { opacity: 0.1 } },
                    { name: '营收', type: 'bar', yAxisIndex: 1, data: revenues, itemStyle: { color: '#3B82F6', borderRadius: [4, 4, 0, 0] } }
                ]
            };
        },
        renderPie() {
            const ratio = this.stats.businessRatio || {};
            this.pieOptions = {
                tooltip: { trigger: 'item', formatter: '{b}: {c}%' },
                legend: { bottom: 0 },
                series: [{
                    type: 'pie',
                    radius: ['45%', '70%'],
                    center: ['50%', '45%'],
                    data: [
                        { value: Number(ratio.printRatio) || 0, name: '打印业务', itemStyle: { color: '#FF7D00' } },
                        { value: Number(ratio.expressRatio) || 0, name: '快递业务', itemStyle: { color: '#3B82F6' } }
                    ],
                    label: { formatter: '{b}\n{c}%' }
                }]
            };
        },
        async loadRecentOrders() {
            try {
                this.recentOrders = await AdminAPI.getRecentOrders();
            } catch (e) {
                console.error('load recent orders failed:', e);
            }
        }
    },
    template: `
    <div class="page-container">
        <div class="page-header">
            <h2>数据仪表盘</h2>
            <div class="header-actions">
                <el-radio-group v-model="chartRange" size="small" @change="changeRange">
                    <el-radio-button :label="7">近7天</el-radio-button>
                    <el-radio-button :label="30">近30天</el-radio-button>
                </el-radio-group>
            </div>
        </div>

        <div v-loading="loading" class="stat-cards">
            <div class="stat-card">
                <div class="stat-icon" style="background: #FFF3E8; color: #FF7D00;">
                    <el-icon><DataLine /></el-icon>
                </div>
                <div>
                    <div class="stat-label">今日订单</div>
                    <div class="stat-value">{{ stats.orderStats.todayOrders }}</div>
                    <div class="stat-trend">累计订单 {{ stats.orderStats.totalOrders }}</div>
                </div>
            </div>
            <div class="stat-card">
                <div class="stat-icon" style="background: #E8F0FE; color: #3B82F6;">
                    <el-icon><TrendCharts /></el-icon>
                </div>
                <div>
                    <div class="stat-label">今日营收</div>
                    <div class="stat-value">¥{{ formatMoney(stats.revenueStats.todayRevenue) }}</div>
                    <div class="stat-trend">累计营收 ¥{{ formatMoney(stats.revenueStats.totalRevenue) }}</div>
                </div>
            </div>
            <div class="stat-card">
                <div class="stat-icon" style="background: #E6F7F2; color: #10B981;">
                    <el-icon><User /></el-icon>
                </div>
                <div>
                    <div class="stat-label">今日新增用户</div>
                    <div class="stat-value">{{ stats.userStats.todayNewUsers }}</div>
                    <div class="stat-trend">累计用户 {{ stats.userStats.totalUsers }}</div>
                </div>
            </div>
            <div class="stat-card">
                <div class="stat-icon" style="background: #F3E8FF; color: #8B5CF6;">
                    <el-icon><Finished /></el-icon>
                </div>
                <div>
                    <div class="stat-label">订单完成率</div>
                    <div class="stat-value">{{ orderCompletionRate }}%</div>
                    <div class="stat-trend">已完成 {{ stats.orderStats.completedOrders }} / 待处理 {{ stats.orderStats.pendingOrders }}</div>
                </div>
            </div>
        </div>

        <div class="dashboard-charts">
            <div class="chart-card chart-card-wide">
                <div class="card-title">订单与营收趋势</div>
                <div style="height: 300px;">
                    <v-chart v-if="trendOptions.series" :option="trendOptions" autoresize />
                </div>
            </div>
            <div class="chart-card">
                <div class="card-title">业务占比</div>
                <div style="height: 300px;">
                    <v-chart v-if="pieOptions.series" :option="pieOptions" autoresize />
                </div>
            </div>
        </div>

        <div class="table-card">
            <div class="card-title">最近订单</div>
            <el-table :data="recentOrders" stripe style="width: 100%">
                <el-table-column prop="orderNo" label="订单号" min-width="160" />
                <el-table-column label="类型" width="80">
                    <template #default="{ row }">{{ orderTypeText(row.orderType) }}</template>
                </el-table-column>
                <el-table-column prop="nickname" label="用户" min-width="100" />
                <el-table-column label="状态" width="100">
                    <template #default="{ row }">
                        <el-tag size="small" :type="Number(row.status) === 5 || Number(row.status) === 7 ? 'success' : (Number(row.status) === 6 ? 'danger' : 'warning')">
                            {{ orderStatusText(row.status) }}
                        </el-tag>
                    </template>
                </el-table-column>
                <el-table-column label="金额" width="120">
                    <template #default="{ row }">¥{{ formatMoney(row.actualPrice) }}</template>
                </el-table-column>
                <el-table-column label="下单时间" min-width="150">
                    <template #default="{ row }">{{ formatDate(row.createTime) }}</template>
                </el-table-column>
            </el-table>
        </div>
    </div>
    `
};
