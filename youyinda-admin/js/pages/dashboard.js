AdminApp.component('dashboard', {
    template: `
        <div>
            <div class="page-header">
                <h2 class="page-title">数据概览</h2>
                <el-date-picker v-model="dateRange" type="daterange" range-separator="至" start-placeholder="开始日期" end-placeholder="结束日期" />
            </div>
            
            <el-row :gutter="24" style="margin-bottom: 24px;">
                <el-col :span="6">
                    <div class="stat-card">
                        <div class="stat-icon orange">📋</div>
                        <div class="stat-info">
                            <div class="stat-label">今日订单</div>
                            <div class="stat-value">{{ stats.todayOrders }}</div>
                            <div class="stat-trend up">↑ 12.5%</div>
                        </div>
                    </div>
                </el-col>
                <el-col :span="6">
                    <div class="stat-card">
                        <div class="stat-icon blue">💰</div>
                        <div class="stat-info">
                            <div class="stat-label">今日收入</div>
                            <div class="stat-value">¥{{ stats.todayRevenue }}</div>
                            <div class="stat-trend up">↑ 8.3%</div>
                        </div>
                    </div>
                </el-col>
                <el-col :span="6">
                    <div class="stat-card">
                        <div class="stat-icon green">👥</div>
                        <div class="stat-info">
                            <div class="stat-label">新增用户</div>
                            <div class="stat-value">{{ stats.newUsers }}</div>
                            <div class="stat-trend down">↓ 3.2%</div>
                        </div>
                    </div>
                </el-col>
                <el-col :span="6">
                    <div class="stat-card">
                        <div class="stat-icon red">📊</div>
                        <div class="stat-info">
                            <div class="stat-label">完成率</div>
                            <div class="stat-value">{{ stats.completionRate }}%</div>
                            <div class="stat-trend up">↑ 2.1%</div>
                        </div>
                    </div>
                </el-col>
            </el-row>

            <el-row :gutter="24">
                <el-col :span="16">
                    <div class="card">
                        <div class="card-header">
                            <span class="card-title">订单趋势</span>
                        </div>
                        <div ref="orderChart" style="height: 320px;"></div>
                    </div>
                </el-col>
                <el-col :span="8">
                    <div class="card">
                        <div class="card-header">
                            <span class="card-title">订单类型分布</span>
                        </div>
                        <div ref="pieChart" style="height: 320px;"></div>
                    </div>
                </el-col>
            </el-row>

            <div class="card">
                <div class="card-header">
                    <span class="card-title">最近订单</span>
                    <el-button type="primary" link @click="$parent.activeMenu='orders'">查看全部</el-button>
                </div>
                <el-table :data="recentOrders" style="width: 100%">
                    <el-table-column prop="orderNo" label="订单号" width="180" />
                    <el-table-column prop="type" label="类型" width="100">
                        <template #default="{row}">
                            <el-tag :type="row.type === '打印' ? 'primary' : 'warning'" size="small">{{ row.type }}</el-tag>
                        </template>
                    </el-table-column>
                    <el-table-column prop="user" label="用户" width="120" />
                    <el-table-column prop="amount" label="金额" width="100">
                        <template #default="{row}">¥{{ row.amount }}</template>
                    </el-table-column>
                    <el-table-column prop="status" label="状态" width="100">
                        <template #default="{row}">
                            <el-tag :type="getStatusType(row.status)" size="small">{{ row.status }}</el-tag>
                        </template>
                    </el-table-column>
                    <el-table-column prop="createTime" label="创建时间" />
                </el-table>
            </div>
        </div>
    `,
    data() {
        return {
            dateRange: [],
            stats: {
                todayOrders: 156,
                todayRevenue: '3,680.50',
                newUsers: 28,
                completionRate: 94.5
            },
            recentOrders: [
                { orderNo: 'YYD202607220001', type: '打印', user: '张三', amount: '12.50', status: '处理中', createTime: '2026-07-22 10:30' },
                { orderNo: 'YYD202607220002', type: '快递', user: '李四', amount: '18.00', status: '待支付', createTime: '2026-07-22 10:25' },
                { orderNo: 'YYD202607220003', type: '打印', user: '王五', amount: '8.00', status: '已完成', createTime: '2026-07-22 10:20' },
                { orderNo: 'YYD202607220004', type: '快递', user: '赵六', amount: '25.00', status: '已完成', createTime: '2026-07-22 10:15' }
            ]
        };
    },
    methods: {
        getStatusType(status) {
            const map = { '待支付': 'warning', '处理中': 'primary', '已完成': 'success', '已取消': 'info' };
            return map[status] || '';
        },
        initCharts() {
            const orderChart = echarts.init(this.$refs.orderChart);
            orderChart.setOption({
                tooltip: { trigger: 'axis' },
                legend: { data: ['打印订单', '快递订单'] },
                grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
                xAxis: { type: 'category', boundaryGap: false, data: ['周一','周二','周三','周四','周五','周六','周日'] },
                yAxis: { type: 'value' },
                series: [
                    { name: '打印订单', type: 'line', smooth: true, itemStyle: {color: '#165DFF'}, areaStyle: {opacity: 0.1}, data: [82, 95, 88, 102, 110, 98, 85] },
                    { name: '快递订单', type: 'line', smooth: true, itemStyle: {color: '#FF7D00'}, areaStyle: {opacity: 0.1}, data: [65, 72, 68, 85, 92, 78, 70] }
                ]
            });

            const pieChart = echarts.init(this.$refs.pieChart);
            pieChart.setOption({
                tooltip: { trigger: 'item' },
                legend: { orient: 'vertical', left: 'left' },
                series: [{
                    type: 'pie', radius: ['40%', '70%'], avoidLabelOverlap: false,
                    itemStyle: { borderRadius: 8, borderColor: '#fff', borderWidth: 2 },
                    label: { show: false },
                    data: [
                        { value: 420, name: '文档打印', itemStyle: {color: '#165DFF'} },
                        { value: 380, name: '快递寄件', itemStyle: {color: '#FF7D00'} },
                        { value: 120, name: '照片打印', itemStyle: {color: '#22C55E'} }
                    ]
                }]
            });

            window.addEventListener('resize', () => {
                orderChart.resize();
                pieChart.resize();
            });
        }
    },
    mounted() {
        this.$nextTick(() => {
            this.initCharts();
        });
    }
});
