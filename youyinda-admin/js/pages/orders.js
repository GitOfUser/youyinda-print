AdminApp.component('orders', {
    template: `
        <div>
            <div class="page-header">
                <h2 class="page-title">订单管理</h2>
                <el-button type="primary" @click="exportOrders">导出订单</el-button>
            </div>

            <div class="filter-bar">
                <el-input v-model="search" placeholder="搜索订单号/用户" style="width: 240px;" clearable />
                <el-select v-model="filterType" placeholder="订单类型" style="width: 140px;" clearable>
                    <el-option label="打印订单" value="print" />
                    <el-option label="快递订单" value="express" />
                </el-select>
                <el-select v-model="filterStatus" placeholder="订单状态" style="width: 140px;" clearable>
                    <el-option label="待支付" value="pending" />
                    <el-option label="处理中" value="processing" />
                    <el-option label="已完成" value="completed" />
                    <el-option label="已取消" value="cancelled" />
                </el-select>
                <el-date-picker v-model="dateRange" type="daterange" range-separator="至" start-placeholder="开始日期" end-placeholder="结束日期" style="width: 260px;" />
                <el-button type="primary">查询</el-button>
                <el-button>重置</el-button>
            </div>

            <div class="card">
                <el-table :data="orderList" style="width: 100%" v-loading="loading">
                    <el-table-column type="selection" width="50" />
                    <el-table-column prop="orderNo" label="订单号" width="180" />
                    <el-table-column prop="type" label="类型" width="100">
                        <template #default="{row}">
                            <el-tag :type="row.type === '打印' ? 'primary' : 'warning'" size="small">{{ row.type }}</el-tag>
                        </template>
                    </el-table-column>
                    <el-table-column prop="user" label="用户" width="120" />
                    <el-table-column prop="phone" label="手机号" width="130" />
                    <el-table-column prop="amount" label="金额" width="100">
                        <template #default="{row}">¥{{ row.amount }}</template>
                    </el-table-column>
                    <el-table-column prop="status" label="状态" width="100">
                        <template #default="{row}">
                            <el-tag :type="getStatusType(row.status)" size="small">{{ row.status }}</el-tag>
                        </template>
                    </el-table-column>
                    <el-table-column prop="createTime" label="创建时间" width="180" />
                    <el-table-column label="操作" width="200" fixed="right">
                        <template #default="{row}">
                            <el-button type="primary" link size="small" @click="viewDetail(row)">详情</el-button>
                            <el-button type="danger" link size="small" @click="cancelOrder(row)" v-if="row.status==='待支付'">取消</el-button>
                        </template>
                    </el-table-column>
                </el-table>
                <div style="margin-top: 20px; text-align: right;">
                    <el-pagination
                        v-model:current-page="currentPage"
                        v-model:page-size="pageSize"
                        :page-sizes="[10, 20, 50]"
                        :total="total"
                        layout="total, sizes, prev, pager, next, jumper"
                        background />
                </div>
            </div>

            <el-dialog v-model="detailVisible" title="订单详情" width="600px">
                <el-descriptions :column="2" border v-if="currentOrder">
                    <el-descriptions-item label="订单号">{{ currentOrder.orderNo }}</el-descriptions-item>
                    <el-descriptions-item label="订单类型">{{ currentOrder.type }}</el-descriptions-item>
                    <el-descriptions-item label="用户">{{ currentOrder.user }}</el-descriptions-item>
                    <el-descriptions-item label="手机号">{{ currentOrder.phone }}</el-descriptions-item>
                    <el-descriptions-item label="金额">¥{{ currentOrder.amount }}</el-descriptions-item>
                    <el-descriptions-item label="状态">{{ currentOrder.status }}</el-descriptions-item>
                    <el-descriptions-item label="创建时间" :span="2">{{ currentOrder.createTime }}</el-descriptions-item>
                </el-descriptions>
                <template #footer>
                    <el-button @click="detailVisible = false">关闭</el-button>
                </template>
            </el-dialog>
        </div>
    `,
    data() {
        return {
            search: '',
            filterType: '',
            filterStatus: '',
            dateRange: [],
            loading: false,
            currentPage: 1,
            pageSize: 10,
            total: 56,
            detailVisible: false,
            currentOrder: null,
            orderList: [
                { orderNo: 'YYD202607220001', type: '打印', user: '张三', phone: '138****1234', amount: '12.50', status: '处理中', createTime: '2026-07-22 10:30:00' },
                { orderNo: 'YYD202607220002', type: '快递', user: '李四', phone: '139****5678', amount: '18.00', status: '待支付', createTime: '2026-07-22 10:25:00' },
                { orderNo: 'YYD202607220003', type: '打印', user: '王五', phone: '137****9012', amount: '8.00', status: '已完成', createTime: '2026-07-22 10:20:00' },
                { orderNo: 'YYD202607220004', type: '快递', user: '赵六', phone: '136****3456', amount: '25.00', status: '已完成', createTime: '2026-07-22 10:15:00' },
                { orderNo: 'YYD202607220005', type: '打印', user: '孙七', phone: '135****7890', amount: '15.00', status: '已取消', createTime: '2026-07-22 10:10:00' }
            ]
        };
    },
    methods: {
        getStatusType(status) {
            const map = { '待支付': 'warning', '处理中': 'primary', '已完成': 'success', '已取消': 'info' };
            return map[status] || '';
        },
        viewDetail(row) {
            this.currentOrder = row;
            this.detailVisible = true;
        },
        cancelOrder(row) {
            ElementPlus.ElMessageBox.confirm('确定要取消该订单吗？', '提示', { type: 'warning' }).then(() => {
                row.status = '已取消';
                ElementPlus.ElMessage.success('订单已取消');
            });
        },
        exportOrders() {
            ElementPlus.ElMessage.success('导出功能开发中');
        }
    }
});
