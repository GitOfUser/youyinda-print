AdminApp.component('price-base', {
    template: `
        <div>
            <div class="page-header">
                <h2 class="page-title">基础价格配置</h2>
                <el-button type="primary" @click="addPrice">新增价格</el-button>
            </div>

            <el-tabs v-model="activeTab">
                <el-tab-pane label="打印价格" name="print">
                    <div class="card">
                        <el-table :data="printPrices" style="width: 100%" border>
                            <el-table-column prop="name" label="规格名称" width="150" />
                            <el-table-column prop="paperSize" label="纸张" width="100" />
                            <el-table-column prop="color" label="色彩" width="100" />
                            <el-table-column prop="singleSide" label="单/双面" width="100" />
                            <el-table-column prop="price" label="单价(元/页)" width="120">
                                <template #default="{row}">¥{{ row.price }}</template>
                            </el-table-column>
                            <el-table-column prop="binding" label="装订费" width="120">
                                <template #default="{row}">¥{{ row.binding }}</template>
                            </el-table-column>
                            <el-table-column label="操作" width="150">
                                <template #default="{row, $index}">
                                    <el-button type="primary" link size="small" @click="editPrice(row, $index)">编辑</el-button>
                                    <el-button type="danger" link size="small" @click="deletePrice($index)">删除</el-button>
                                </template>
                            </el-table-column>
                        </el-table>
                    </div>
                </el-tab-pane>
                <el-tab-pane label="快递价格" name="express">
                    <div class="card">
                        <el-table :data="expressPrices" style="width: 100%" border>
                            <el-table-column prop="company" label="快递公司" width="150" />
                            <el-table-column prop="firstWeight" label="首重价格(元/kg)" width="150">
                                <template #default="{row}">¥{{ row.firstWeight }}</template>
                            </el-table-column>
                            <el-table-column prop="addWeight" label="续重价格(元/kg)" width="150">
                                <template #default="{row}">¥{{ row.addWeight }}</template>
                            </el-table-column>
                            <el-table-column prop="estimate" label="预计时效" width="120" />
                            <el-table-column label="操作" width="150">
                                <template #default="{row, $index}">
                                    <el-button type="primary" link size="small" @click="editPrice(row, $index)">编辑</el-button>
                                </template>
                            </el-table-column>
                        </el-table>
                    </div>
                </el-tab-pane>
            </el-tabs>

            <el-dialog v-model="dialogVisible" :title="editIndex >= 0 ? '编辑价格' : '新增价格'" width="500px">
                <el-form :model="form" label-width="100px">
                    <el-form-item label="规格名称" v-if="activeTab==='print'">
                        <el-input v-model="form.name" />
                    </el-form-item>
                    <el-form-item label="快递公司" v-if="activeTab==='express'">
                        <el-input v-model="form.company" />
                    </el-form-item>
                    <el-form-item label="价格" v-if="activeTab==='print'">
                        <el-input-number v-model="form.price" :min="0" :precision="2" />
                    </el-form-item>
                    <el-form-item label="首重价格" v-if="activeTab==='express'">
                        <el-input-number v-model="form.firstWeight" :min="0" :precision="2" />
                    </el-form-item>
                    <el-form-item label="续重价格" v-if="activeTab==='express'">
                        <el-input-number v-model="form.addWeight" :min="0" :precision="2" />
                    </el-form-item>
                </el-form>
                <template #footer>
                    <el-button @click="dialogVisible = false">取消</el-button>
                    <el-button type="primary" @click="savePrice">保存</el-button>
                </template>
            </el-dialog>
        </div>
    `,
    data() {
        return {
            activeTab: 'print',
            dialogVisible: false,
            editIndex: -1,
            form: {},
            printPrices: [
                { name: 'A4黑白单面', paperSize: 'A4', color: '黑白', singleSide: '单面', price: 0.10, binding: 2.00 },
                { name: 'A4黑白双面', paperSize: 'A4', color: '黑白', singleSide: '双面', price: 0.15, binding: 2.00 },
                { name: 'A4彩色单面', paperSize: 'A4', color: '彩色', singleSide: '单面', price: 0.50, binding: 2.00 },
                { name: 'A3黑白单面', paperSize: 'A3', color: '黑白', singleSide: '单面', price: 0.20, binding: 3.00 }
            ],
            expressPrices: [
                { company: '顺丰速运', firstWeight: 12.00, addWeight: 2.00, estimate: '1-2天' },
                { company: '京东快递', firstWeight: 10.00, addWeight: 1.50, estimate: '1-2天' },
                { company: '中通快递', firstWeight: 8.00, addWeight: 1.00, estimate: '2-3天' },
                { company: '圆通速递', firstWeight: 8.00, addWeight: 1.00, estimate: '2-3天' }
            ]
        };
    },
    methods: {
        addPrice() {
            this.editIndex = -1;
            this.form = this.activeTab === 'print' ? { name: '', price: 0.10 } : { company: '', firstWeight: 8, addWeight: 1 };
            this.dialogVisible = true;
        },
        editPrice(row, index) {
            this.editIndex = index;
            this.form = { ...row };
            this.dialogVisible = true;
        },
        savePrice() {
            if (this.editIndex >= 0) {
                const list = this.activeTab === 'print' ? this.printPrices : this.expressPrices;
                list[this.editIndex] = { ...this.form };
                ElementPlus.ElMessage.success('修改成功');
            } else {
                const list = this.activeTab === 'print' ? this.printPrices : this.expressPrices;
                list.push({ ...this.form });
                ElementPlus.ElMessage.success('添加成功');
            }
            this.dialogVisible = false;
        },
        deletePrice(index) {
            ElementPlus.ElMessageBox.confirm('确定删除该价格配置？', '提示', { type: 'warning' }).then(() => {
                this.printPrices.splice(index, 1);
                ElementPlus.ElMessage.success('删除成功');
            });
        }
    }
});

AdminApp.component('profit-rule', {
    template: `
        <div>
            <div class="page-header">
                <h2 class="page-title">盈利规则配置</h2>
            </div>

            <div class="card">
                <el-form :model="rule" label-width="140px" style="max-width: 600px;">
                    <el-divider content-position="left">打印服务盈利</el-divider>
                    <el-form-item label="盈利方式">
                        <el-radio-group v-model="rule.printType">
                            <el-radio value="percent">按比例加价</el-radio>
                            <el-radio value="fixed">固定金额加价</el-radio>
                        </el-radio-group>
                    </el-form-item>
                    <el-form-item :label="rule.printType==='percent' ? '加价比例' : '固定加价'">
                        <el-input-number v-model="rule.printValue" :min="0" :precision="rule.printType==='percent'?2:2" />
                        <span style="margin-left: 8px; color: #86909C;">{{ rule.printType==='percent' ? '%' : '元/页' }}</span>
                    </el-form-item>

                    <el-divider content-position="left">快递服务盈利</el-divider>
                    <el-form-item label="盈利方式">
                        <el-radio-group v-model="rule.expressType">
                            <el-radio value="percent">按比例加价</el-radio>
                            <el-radio value="fixed">固定金额加价</el-radio>
                        </el-radio-group>
                    </el-form-item>
                    <el-form-item :label="rule.expressType==='percent' ? '加价比例' : '固定加价'">
                        <el-input-number v-model="rule.expressValue" :min="0" :precision="2" />
                        <span style="margin-left: 8px; color: #86909C;">{{ rule.expressType==='percent' ? '%' : '元/单' }}</span>
                    </el-form-item>

                    <el-divider content-position="left">其他设置</el-divider>
                    <el-form-item label="最低消费">
                        <el-input-number v-model="rule.minAmount" :min="0" :precision="2" />
                        <span style="margin-left: 8px; color: #86909C;">元</span>
                    </el-form-item>
                    <el-form-item label="免配送费门槛">
                        <el-input-number v-model="rule.freeShipping" :min="0" :precision="2" />
                        <span style="margin-left: 8px; color: #86909C;">元</span>
                    </el-form-item>
                    <el-form-item label="配送费">
                        <el-input-number v-model="rule.shippingFee" :min="0" :precision="2" />
                        <span style="margin-left: 8px; color: #86909C;">元</span>
                    </el-form-item>

                    <el-form-item>
                        <el-button type="primary" @click="saveRule">保存配置</el-button>
                        <el-button>重置</el-button>
                    </el-form-item>
                </el-form>
            </div>

            <div class="card">
                <div class="card-header"><span class="card-title">价格预览示例</span></div>
                <el-alert title="示例：A4黑白打印10页，装订" type="info" :closable="false" style="margin-bottom: 16px;" />
                <el-descriptions :column="2" border>
                    <el-descriptions-item label="基础成本">¥1.00 (0.10元/页 × 10页)</el-descriptions-item>
                    <el-descriptions-item label="盈利加价">¥0.20 (20%)</el-descriptions-item>
                    <el-descriptions-item label="装订费">¥2.00</el-descriptions-item>
                    <el-descriptions-item label="用户售价"><span style="color:#FF7D00;font-weight:700;">¥3.20</span></el-descriptions-item>
                </el-descriptions>
            </div>
        </div>
    `,
    data() {
        return {
            rule: {
                printType: 'percent',
                printValue: 20,
                expressType: 'fixed',
                expressValue: 2,
                minAmount: 5,
                freeShipping: 30,
                shippingFee: 3
            }
        };
    },
    methods: {
        saveRule() {
            ElementPlus.ElMessage.success('配置保存成功');
        }
    }
});
