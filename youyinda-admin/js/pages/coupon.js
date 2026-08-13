AdminApp.component('coupon', {
    template: `
        <div>
            <div class="page-header">
                <h2 class="page-title">优惠券管理</h2>
                <el-button type="primary" @click="addCoupon">创建优惠券</el-button>
            </div>

            <div class="filter-bar">
                <el-input v-model="search" placeholder="搜索优惠券名称" style="width: 240px;" clearable />
                <el-select v-model="filterStatus" placeholder="状态" style="width: 140px;" clearable>
                    <el-option label="进行中" value="active" />
                    <el-option label="未开始" value="pending" />
                    <el-option label="已过期" value="expired" />
                </el-select>
                <el-button type="primary">查询</el-button>
                <el-button>重置</el-button>
            </div>

            <div class="card">
                <el-table :data="couponList" style="width: 100%">
                    <el-table-column prop="name" label="优惠券名称" min-width="180" />
                    <el-table-column label="面额" width="120">
                        <template #default="{row}">
                            <span v-if="row.type==='reduce'" style="color:#FF7D00;font-weight:700;">¥{{ row.value }}</span>
                            <span v-else style="color:#FF7D00;font-weight:700;">{{ row.value }}折</span>
                        </template>
                    </el-table-column>
                    <el-table-column label="使用门槛" width="120">
                        <template #default="{row}">满{{ row.minAmount }}元可用</template>
                    </el-table-column>
                    <el-table-column prop="total" label="发放数量" width="100" />
                    <el-table-column prop="received" label="已领取" width="100" />
                    <el-table-column prop="used" label="已使用" width="100" />
                    <el-table-column prop="expireTime" label="有效期至" width="180" />
                    <el-table-column prop="status" label="状态" width="100">
                        <template #default="{row}">
                            <el-tag :type="getStatusType(row.status)" size="small">{{ row.status }}</el-tag>
                        </template>
                    </el-table-column>
                    <el-table-column label="操作" width="180" fixed="right">
                        <template #default="{row}">
                            <el-button type="primary" link size="small">编辑</el-button>
                            <el-button type="danger" link size="small" @click="deleteCoupon(row)">删除</el-button>
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

            <el-dialog v-model="dialogVisible" title="创建优惠券" width="500px">
                <el-form :model="form" label-width="100px">
                    <el-form-item label="优惠券名称">
                        <el-input v-model="form.name" placeholder="如：新用户专享券" />
                    </el-form-item>
                    <el-form-item label="优惠券类型">
                        <el-radio-group v-model="form.type">
                            <el-radio value="reduce">满减券</el-radio>
                            <el-radio value="discount">折扣券</el-radio>
                        </el-radio-group>
                    </el-form-item>
                    <el-form-item :label="form.type==='reduce' ? '减免金额' : '折扣'">
                        <el-input-number v-model="form.value" :min="form.type==='reduce'?1:1" :max="form.type==='discount'?9.9:null" :precision="form.type==='discount'?1:0" />
                        <span style="margin-left: 8px;">{{ form.type==='reduce' ? '元' : '折' }}</span>
                    </el-form-item>
                    <el-form-item label="使用门槛">
                        <el-input-number v-model="form.minAmount" :min="0" />
                        <span style="margin-left: 8px;">元</span>
                    </el-form-item>
                    <el-form-item label="发放数量">
                        <el-input-number v-model="form.total" :min="1" />
                        <span style="margin-left: 8px;">张</span>
                    </el-form-item>
                    <el-form-item label="有效期">
                        <el-date-picker v-model="form.expireTime" type="datetime" placeholder="选择过期时间" style="width: 100%;" />
                    </el-form-item>
                </el-form>
                <template #footer>
                    <el-button @click="dialogVisible = false">取消</el-button>
                    <el-button type="primary" @click="saveCoupon">创建</el-button>
                </template>
            </el-dialog>
        </div>
    `,
    data() {
        return {
            search: '',
            filterStatus: '',
            currentPage: 1,
            pageSize: 10,
            total: 24,
            dialogVisible: false,
            form: { type: 'reduce', value: 5, minAmount: 20, total: 100 },
            couponList: [
                { id: 1, name: '新用户专享券', type: 'reduce', value: 5, minAmount: 20, total: 1000, received: 256, used: 89, expireTime: '2026-12-31 23:59', status: '进行中' },
                { id: 2, name: '满30减8券', type: 'reduce', value: 8, minAmount: 30, total: 500, received: 120, used: 45, expireTime: '2026-08-31 23:59', status: '进行中' },
                { id: 3, name: '打印9折券', type: 'discount', value: 9, minAmount: 0, total: 2000, received: 800, used: 320, expireTime: '2026-07-31 23:59', status: '进行中' },
                { id: 4, name: '端午特惠券', type: 'reduce', value: 10, minAmount: 50, total: 300, received: 300, used: 280, expireTime: '2026-06-30 23:59', status: '已过期' }
            ]
        };
    },
    methods: {
        getStatusType(status) {
            const map = { '进行中': 'success', '未开始': 'warning', '已过期': 'info' };
            return map[status] || '';
        },
        addCoupon() {
            this.form = { type: 'reduce', value: 5, minAmount: 20, total: 100 };
            this.dialogVisible = true;
        },
        saveCoupon() {
            ElementPlus.ElMessage.success('优惠券创建成功');
            this.dialogVisible = false;
        },
        deleteCoupon(row) {
            ElementPlus.ElMessageBox.confirm('确定删除该优惠券？', '提示', { type: 'warning' }).then(() => {
                const idx = this.couponList.indexOf(row);
                this.couponList.splice(idx, 1);
                ElementPlus.ElMessage.success('删除成功');
            });
        }
    }
});
