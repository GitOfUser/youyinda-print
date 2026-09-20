window.CouponPage = {
    name: 'coupon',
    data() {
        return {
            loading: false,
            couponList: [],
            total: 0,
            query: { pageNum: 1, pageSize: 10, couponName: '' },
            dialogVisible: false,
            form: {
                couponName: '',
                couponType: 'cash',
                couponValue: 0,
                minAmount: 0,
                totalCount: 100,
                remainCount: 100,
                perLimit: 1,
                startTime: null,
                endTime: null,
                status: 1
            }
        };
    },
    mounted() {
        this.loadCoupons();
    },
    methods: {
        formatMoney(v) {
            return v === null || v === undefined ? '0.00' : Number(v).toFixed(2);
        },
        formatDate(v) {
            if (!v) return '-';
            return String(v).replace('T', ' ').slice(0, 19);
        },
        async loadCoupons() {
            this.loading = true;
            try {
                const res = await AdminAPI.getCouponList({
                    pageNum: this.query.pageNum,
                    pageSize: this.query.pageSize,
                    couponName: this.query.couponName || undefined
                });
                this.couponList = res.records || [];
                this.total = res.total || 0;
            } catch (e) {
                console.error('load coupons failed:', e);
            } finally {
                this.loading = false;
            }
        },
        handleSearch() {
            this.query.pageNum = 1;
            this.loadCoupons();
        },
        handleReset() {
            this.query = { pageNum: 1, pageSize: 10, couponName: '' };
            this.loadCoupons();
        },
        handlePageChange(page) {
            this.query.pageNum = page;
            this.loadCoupons();
        },
        handleSizeChange(size) {
            this.query.pageSize = size;
            this.query.pageNum = 1;
            this.loadCoupons();
        },
        openDialog(row) {
            this.form = row ? {
                ...row,
                startTime: row.startTime ? String(row.startTime).replace('T', ' ') : null,
                endTime: row.endTime ? String(row.endTime).replace('T', ' ') : null
            } : {
                couponName: '',
                couponType: 'cash',
                couponValue: 0,
                minAmount: 0,
                totalCount: 100,
                remainCount: 100,
                perLimit: 1,
                startTime: null,
                endTime: null,
                status: 1
            };
            this.dialogVisible = true;
        },
        async saveCoupon() {
            if (!this.form.couponName) {
                ElMessage.warning('请输入优惠券名称');
                return;
            }
            try {
                if (this.form.id) {
                    await AdminAPI.updateCoupon(this.form);
                } else {
                    await AdminAPI.createCoupon(this.form);
                }
                ElMessage.success('保存成功');
                this.dialogVisible = false;
                this.loadCoupons();
            } catch (e) {
                console.error('save coupon failed:', e);
            }
        },
        async deleteCoupon(row) {
            try {
                await ElMessageBox.confirm(`确定删除优惠券「${row.couponName}」吗？`, '删除确认', { type: 'warning' });
            } catch (e) {
                return;
            }
            try {
                await AdminAPI.deleteCoupon(row.id);
                ElMessage.success('删除成功');
                this.loadCoupons();
            } catch (e) {
                console.error('delete coupon failed:', e);
            }
        }
    },
    template: `
    <div class="page-container">
        <div class="page-header">
            <h2>优惠券管理</h2>
            <el-button type="primary" plain @click="openDialog()">
                <el-icon><Plus /></el-icon>&nbsp;新增优惠券
            </el-button>
        </div>

        <div class="filter-bar">
            <el-input v-model="query.couponName" placeholder="优惠券名称" clearable style="width: 220px;" @keyup.enter="handleSearch" />
            <el-button type="primary" @click="handleSearch">
                <el-icon><Search /></el-icon>&nbsp;查询
            </el-button>
            <el-button @click="handleReset">重置</el-button>
        </div>

        <div class="table-card">
            <el-table v-loading="loading" :data="couponList" stripe style="width: 100%">
                <el-table-column prop="couponName" label="优惠券名称" min-width="150" />
                <el-table-column label="类型" width="100">
                    <template #default="{ row }">
                        <el-tag size="small" :type="row.couponType === 'discount' ? 'warning' : 'primary'">
                            {{ row.couponType === 'discount' ? '折扣券' : '立减券' }}
                        </el-tag>
                    </template>
                </el-table-column>
                <el-table-column label="优惠" width="100">
                    <template #default="{ row }">{{ row.couponType === 'discount' ? (row.couponValue + '折') : ('¥' + formatMoney(row.couponValue)) }}</template>
                </el-table-column>
                <el-table-column label="满减门槛" width="110">
                    <template #default="{ row }">满 ¥{{ formatMoney(row.minAmount) }}</template>
                </el-table-column>
                <el-table-column label="发放/剩余" width="120">
                    <template #default="{ row }">{{ row.totalCount }} / {{ row.remainCount }}</template>
                </el-table-column>
                <el-table-column label="每人限领" width="90">
                    <template #default="{ row }">{{ row.perLimit }}</template>
                </el-table-column>
                <el-table-column label="有效期至" min-width="160">
                    <template #default="{ row }">{{ formatDate(row.endTime) }}</template>
                </el-table-column>
                <el-table-column label="状态" width="90">
                    <template #default="{ row }">
                        <el-tag size="small" :type="Number(row.status) === 1 ? 'success' : 'info'">{{ Number(row.status) === 1 ? '启用' : '停用' }}</el-tag>
                    </template>
                </el-table-column>
                <el-table-column label="操作" width="150" fixed="right">
                    <template #default="{ row }">
                        <el-button link type="primary" size="small" @click="openDialog(row)">编辑</el-button>
                        <el-button link type="danger" size="small" @click="deleteCoupon(row)">删除</el-button>
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

        <el-dialog v-model="dialogVisible" title="优惠券" width="560px">
            <el-form label-width="100px">
                <el-form-item label="优惠券名称">
                    <el-input v-model="form.couponName" placeholder="如：新用户立减券" />
                </el-form-item>
                <el-form-item label="券类型">
                    <el-radio-group v-model="form.couponType">
                        <el-radio value="cash">立减券</el-radio>
                        <el-radio value="discount">折扣券</el-radio>
                    </el-radio-group>
                </el-form-item>
                <el-form-item :label="form.couponType === 'discount' ? '折扣(如0.9)' : '优惠金额(元)'">
                    <el-input-number v-model="form.couponValue" :min="0" :precision="2" :step="0.1" style="width: 100%;" />
                </el-form-item>
                <el-form-item label="满减门槛(元)">
                    <el-input-number v-model="form.minAmount" :min="0" :precision="2" :step="1" style="width: 100%;" />
                </el-form-item>
                <el-form-item label="发行总量">
                    <el-input-number v-model="form.totalCount" :min="1" :step="100" style="width: 100%;" />
                </el-form-item>
                <el-form-item label="剩余数量">
                    <el-input-number v-model="form.remainCount" :min="0" :step="10" style="width: 100%;" />
                </el-form-item>
                <el-form-item label="每人限领">
                    <el-input-number v-model="form.perLimit" :min="1" :step="1" style="width: 100%;" />
                </el-form-item>
                <el-form-item label="生效时间">
                    <el-date-picker v-model="form.startTime" type="datetime" placeholder="选择生效时间" style="width: 100%;" value-format="YYYY-MM-DD HH:mm:ss" />
                </el-form-item>
                <el-form-item label="结束时间">
                    <el-date-picker v-model="form.endTime" type="datetime" placeholder="选择结束时间" style="width: 100%;" value-format="YYYY-MM-DD HH:mm:ss" />
                </el-form-item>
                <el-form-item label="是否启用">
                    <el-switch v-model="form.status" :active-value="1" :inactive-value="0" />
                </el-form-item>
            </el-form>
            <template #footer>
                <el-button @click="dialogVisible = false">取消</el-button>
                <el-button type="primary" @click="saveCoupon">保存</el-button>
            </template>
        </el-dialog>
    </div>
    `
};
