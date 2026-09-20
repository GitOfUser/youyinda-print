window.OrdersPage = {
    name: 'orders',
    data() {
        return {
            loading: false,
            orderList: [],
            total: 0,
            query: {
                pageNum: 1,
                pageSize: 10,
                orderNo: '',
                orderType: null,
                orderStatus: null,
                userId: null
            },
            detailVisible: false,
            detail: {},
            detailLoading: false,
            refundVisible: false,
            refundReason: '',
            refundingId: null,
            statusMap: { 1: '待支付', 2: '已支付', 3: '打印中', 4: '已打印', 5: '已完成', 6: '已取消', 7: '已完成', 9: '退款中', 10: '已退款' },
            statusTagType: { 1: 'warning', 2: 'primary', 3: 'info', 4: 'info', 5: 'success', 6: 'danger', 7: 'success', 9: 'warning', 10: 'info' }
        };
    },
    computed: {
        statusOptions() {
            return Object.entries(this.statusMap).map(([value, label]) => ({ value: Number(value), label }));
        },
        typeOptions() {
            return [
                { value: 1, label: '打印' },
                { value: 2, label: '快递' }
            ];
        }
    },
    mounted() {
        this.loadOrders();
    },
    methods: {
        formatMoney(v) {
            return v === null || v === undefined ? '0.00' : Number(v).toFixed(2);
        },
        formatDate(v) {
            if (!v) return '-';
            return String(v).replace('T', ' ').slice(0, 19);
        },
        statusText(s) {
            return this.statusMap[Number(s)] || '未知';
        },
        tagType(s) {
            return this.statusTagType[Number(s)] || 'info';
        },
        handleSearch() {
            this.query.pageNum = 1;
            this.loadOrders();
        },
        handleReset() {
            this.query = { pageNum: 1, pageSize: 10, orderNo: '', orderType: null, orderStatus: null, userId: null };
            this.loadOrders();
        },
        async loadOrders() {
            this.loading = true;
            try {
                const params = {
                    pageNum: this.query.pageNum,
                    pageSize: this.query.pageSize,
                    orderNo: this.query.orderNo || undefined,
                    orderType: this.query.orderType,
                    orderStatus: this.query.orderStatus,
                    userId: this.query.userId
                };
                const res = await AdminAPI.getOrderList(params);
                this.orderList = res.records || [];
                this.total = res.total || 0;
            } catch (e) {
                console.error('load orders failed:', e);
            } finally {
                this.loading = false;
            }
        },
        handlePageChange(page) {
            this.query.pageNum = page;
            this.loadOrders();
        },
        handleSizeChange(size) {
            this.query.pageSize = size;
            this.query.pageNum = 1;
            this.loadOrders();
        },
        async openDetail(row) {
            this.detailVisible = true;
            this.detailLoading = true;
            this.detail = {};
            try {
                this.detail = await AdminAPI.getOrderDetail(row.id);
            } catch (e) {
                console.error('load order detail failed:', e);
            } finally {
                this.detailLoading = false;
            }
        },
        async handleCancel(row) {
            try {
                await ElMessageBox.confirm(`确定取消订单 ${row.orderNo} 吗？`, '取消订单', { type: 'warning' });
            } catch (e) {
                return;
            }
            try {
                await AdminAPI.updateOrderStatus(row.id, 6);
                ElMessage.success('订单已取消');
                this.loadOrders();
            } catch (e) {
                console.error('cancel order failed:', e);
            }
        },
        async openRefund(row) {
            this.refundingId = row.id;
            this.refundReason = '';
            this.refundVisible = true;
        },
        async submitRefund() {
            if (!this.refundingId) return;
            try {
                await AdminAPI.refundOrder(this.refundingId, this.refundReason || '');
                ElMessage.success('退款申请已提交');
                this.refundVisible = false;
                this.loadOrders();
            } catch (e) {
                console.error('refund order failed:', e);
            }
        }
    },
    template: `
    <div class="page-container">
        <div class="page-header">
            <h2>订单管理</h2>
        </div>

        <div class="filter-bar">
            <el-input v-model="query.orderNo" placeholder="订单号" clearable style="width: 200px;" @keyup.enter="handleSearch" />
            <el-select v-model="query.orderType" placeholder="订单类型" clearable style="width: 130px;">
                <el-option v-for="t in typeOptions" :key="t.value" :label="t.label" :value="t.value" />
            </el-select>
            <el-select v-model="query.orderStatus" placeholder="订单状态" clearable style="width: 130px;">
                <el-option v-for="s in statusOptions" :key="s.value" :label="s.label" :value="s.value" />
            </el-select>
            <el-input v-model="query.userId" placeholder="用户ID" clearable style="width: 120px;" @keyup.enter="handleSearch" />
            <el-button type="primary" @click="handleSearch">
                <el-icon><Search /></el-icon>&nbsp;查询
            </el-button>
            <el-button @click="handleReset">重置</el-button>
        </div>

        <div class="table-card">
            <el-table v-loading="loading" :data="orderList" stripe style="width: 100%">
                <el-table-column prop="orderNo" label="订单号" min-width="170" />
                <el-table-column label="类型" width="80">
                    <template #default="{ row }">
                        <el-tag size="small" :type="Number(row.orderType) === 2 ? 'warning' : 'primary'">{{ Number(row.orderType) === 2 ? '快递' : '打印' }}</el-tag>
                    </template>
                </el-table-column>
                <el-table-column prop="userId" label="用户ID" width="90" />
                <el-table-column label="订单金额" width="120">
                    <template #default="{ row }">¥{{ formatMoney(row.totalPrice) }}</template>
                </el-table-column>
                <el-table-column label="实付金额" width="120">
                    <template #default="{ row }">¥{{ formatMoney(row.actualPrice) }}</template>
                </el-table-column>
                <el-table-column label="状态" width="100">
                    <template #default="{ row }">
                        <el-tag size="small" :type="tagType(row.status)">{{ statusText(row.status) }}</el-tag>
                    </template>
                </el-table-column>
                <el-table-column label="下单时间" min-width="150">
                    <template #default="{ row }">{{ formatDate(row.createTime) }}</template>
                </el-table-column>
                <el-table-column label="操作" width="190" fixed="right">
                    <template #default="{ row }">
                        <el-button link type="primary" size="small" @click="openDetail(row)">详情</el-button>
                        <el-button v-if="Number(row.status) === 9" link type="danger" size="small" @click="openRefund(row)">退款</el-button>
                        <el-button v-if="Number(row.status) !== 5 && Number(row.status) !== 6 && Number(row.status) !== 7 && Number(row.status) !== 10" link type="warning" size="small" @click="handleCancel(row)">取消</el-button>
                    </template>
                </el-table-column>
            </el-table>
            <div class="pagination-wrap">
                <el-pagination
                    background
                    layout="total, sizes, prev, pager, next"
                    :total="total"
                    :current-page="query.pageNum"
                    :page-size="query.pageSize"
                    :page-sizes="[10, 20, 50]"
                    @current-change="handlePageChange"
                    @size-change="handleSizeChange"
                />
            </div>
        </div>

        <el-dialog v-model="detailVisible" title="订单详情" width="720px">
            <div v-loading="detailLoading">
                <el-descriptions :column="2" border v-if="Object.keys(detail).length">
                    <el-descriptions-item label="订单号">{{ detail.orderNo }}</el-descriptions-item>
                    <el-descriptions-item label="类型">{{ Number(detail.orderType) === 2 ? '快递' : '打印' }}</el-descriptions-item>
                    <el-descriptions-item label="用户ID">{{ detail.userId }}</el-descriptions-item>
                    <el-descriptions-item label="状态">
                        <el-tag size="small" :type="tagType(detail.status)">{{ statusText(detail.status) }}</el-tag>
                    </el-descriptions-item>
                    <el-descriptions-item label="订单金额">¥{{ formatMoney(detail.totalPrice) }}</el-descriptions-item>
                    <el-descriptions-item label="实付金额">¥{{ formatMoney(detail.actualPrice) }}</el-descriptions-item>
                    <el-descriptions-item label="支付状态">{{ Number(detail.payStatus) === 1 ? '已支付' : '未支付' }}</el-descriptions-item>
                    <el-descriptions-item label="支付方式">{{ detail.payType ? '微信支付' : '-' }}</el-descriptions-item>
                    <el-descriptions-item label="下单时间">{{ formatDate(detail.createTime) }}</el-descriptions-item>
                    <el-descriptions-item label="支付时间">{{ formatDate(detail.payTime) }}</el-descriptions-item>
                    <el-descriptions-item label="收货人" v-if="detail.receiverName">{{ detail.receiverName }} {{ detail.receiverPhone }}</el-descriptions-item>
                    <el-descriptions-item label="收货地址" :span="2" v-if="detail.receiverAddress">{{ detail.receiverAddress }}</el-descriptions-item>
                </el-descriptions>
                <el-empty v-else-if="!detailLoading" description="暂无详情数据" />
            </div>
        </el-dialog>

        <el-dialog v-model="refundVisible" title="订单退款" width="480px">
            <el-form label-position="top">
                <el-form-item label="退款原因">
                    <el-input v-model="refundReason" type="textarea" :rows="3" placeholder="请输入退款原因（可选）" />
                </el-form-item>
            </el-form>
            <template #footer>
                <el-button @click="refundVisible = false">取消</el-button>
                <el-button type="danger" @click="submitRefund">提交退款</el-button>
            </template>
        </el-dialog>
    </div>
    `
};
