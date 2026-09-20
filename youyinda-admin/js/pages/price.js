window.PricePage = {
    name: 'price',
    data() {
        return {
            activeTab: 'print',
            // ===== 打印价格 =====
            printLoading: false,
            printList: [],
            printTotal: 0,
            printQuery: { pageNum: 1, pageSize: 10, paperType: '', colorType: '' },
            printDialogVisible: false,
            printForm: { paperType: 'A4', colorType: '黑白', singleDouble: 'single', basePrice: 0, minPrice: 0, profitRatio: 0, status: 1, isActive: 1 },
            // ===== 快递价格 =====
            expressLoading: false,
            expressList: [],
            expressTotal: 0,
            expressQuery: { pageNum: 1, pageSize: 10, courier: '' },
            expressDialogVisible: false,
            expressForm: { courier: '', firstWeight: 1, firstPrice: 0, continueWeight: 1, continuePrice: 0, weightCeiling: 50, minProfit: 0, profitRatio: 0, isActive: 1 },
            // ===== 盈利规则 =====
            ruleLoading: false,
            ruleList: [],
            ruleTotal: 0,
            ruleQuery: { pageNum: 1, pageSize: 10, businessType: '', ruleCode: '' },
            ruleDialogVisible: false,
            ruleForm: { ruleName: '', ruleCode: '', businessType: 'print', sceneCode: 'DEFAULT', ruleType: 'global', profitType: 'percent', profitValue: 0, isEnabled: 1, priority: 10, description: '' }
        };
    },
    mounted() {
        this.loadPrintPrices();
        this.loadExpressPrices();
        this.loadRules();
    },
    methods: {
        formatMoney(v) {
            return v === null || v === undefined ? '0.00' : Number(v).toFixed(2);
        },
        // ==================== 打印价格 ====================
        async loadPrintPrices() {
            this.printLoading = true;
            try {
                const res = await AdminAPI.getPrintPriceList({
                    pageNum: this.printQuery.pageNum,
                    pageSize: this.printQuery.pageSize,
                    paperType: this.printQuery.paperType || undefined,
                    colorType: this.printQuery.colorType || undefined
                });
                this.printList = res.records || [];
                this.printTotal = res.total || 0;
            } catch (e) {
                console.error('load print prices failed:', e);
            } finally {
                this.printLoading = false;
            }
        },
        printSearch() {
            this.printQuery.pageNum = 1;
            this.loadPrintPrices();
        },
        printReset() {
            this.printQuery = { pageNum: 1, pageSize: 10, paperType: '', colorType: '' };
            this.loadPrintPrices();
        },
        printPageChange(page) {
            this.printQuery.pageNum = page;
            this.loadPrintPrices();
        },
        openPrintDialog(row) {
            this.printForm = row ? { ...row } : { paperType: 'A4', colorType: '黑白', singleDouble: 'single', basePrice: 0, minPrice: 0, profitRatio: 0, status: 1, isActive: 1 };
            this.printDialogVisible = true;
        },
        async savePrint() {
            try {
                if (this.printForm.id) {
                    await AdminAPI.updatePrintPrice(this.printForm);
                } else {
                    await AdminAPI.createPrintPrice(this.printForm);
                }
                ElMessage.success('保存成功');
                this.printDialogVisible = false;
                this.loadPrintPrices();
            } catch (e) {
                console.error('save print price failed:', e);
            }
        },
        async deletePrint(row) {
            try {
                await ElMessageBox.confirm(`确定删除该打印价格（${row.paperType} ${row.colorType} ${row.singleDouble === 'double' ? '双面' : '单面'}）吗？`, '删除确认', { type: 'warning' });
            } catch (e) {
                return;
            }
            try {
                await AdminAPI.deletePrintPrice(row.id);
                ElMessage.success('删除成功');
                this.loadPrintPrices();
            } catch (e) {
                console.error('delete print price failed:', e);
            }
        },
        // ==================== 快递价格 ====================
        async loadExpressPrices() {
            this.expressLoading = true;
            try {
                const res = await AdminAPI.getExpressPriceList({
                    pageNum: this.expressQuery.pageNum,
                    pageSize: this.expressQuery.pageSize,
                    courier: this.expressQuery.courier || undefined
                });
                this.expressList = res.records || [];
                this.expressTotal = res.total || 0;
            } catch (e) {
                console.error('load express prices failed:', e);
            } finally {
                this.expressLoading = false;
            }
        },
        expressSearch() {
            this.expressQuery.pageNum = 1;
            this.loadExpressPrices();
        },
        expressReset() {
            this.expressQuery = { pageNum: 1, pageSize: 10, courier: '' };
            this.loadExpressPrices();
        },
        expressPageChange(page) {
            this.expressQuery.pageNum = page;
            this.loadExpressPrices();
        },
        openExpressDialog(row) {
            this.expressForm = row ? { ...row } : { courier: '', firstWeight: 1, firstPrice: 0, continueWeight: 1, continuePrice: 0, weightCeiling: 50, minProfit: 0, profitRatio: 0, isActive: 1 };
            this.expressDialogVisible = true;
        },
        async saveExpress() {
            try {
                if (this.expressForm.id) {
                    await AdminAPI.updateExpressPrice(this.expressForm);
                } else {
                    await AdminAPI.createExpressPrice(this.expressForm);
                }
                ElMessage.success('保存成功');
                this.expressDialogVisible = false;
                this.loadExpressPrices();
            } catch (e) {
                console.error('save express price failed:', e);
            }
        },
        async deleteExpress(row) {
            try {
                await ElMessageBox.confirm(`确定删除「${row.courier}」的快递价格配置吗？`, '删除确认', { type: 'warning' });
            } catch (e) {
                return;
            }
            try {
                await AdminAPI.deleteExpressPrice(row.id);
                ElMessage.success('删除成功');
                this.loadExpressPrices();
            } catch (e) {
                console.error('delete express price failed:', e);
            }
        },
        // ==================== 盈利规则 ====================
        async loadRules() {
            this.ruleLoading = true;
            try {
                const res = await AdminAPI.getProfitRuleList({
                    pageNum: this.ruleQuery.pageNum,
                    pageSize: this.ruleQuery.pageSize,
                    businessType: this.ruleQuery.businessType || undefined,
                    ruleCode: this.ruleQuery.ruleCode || undefined
                });
                this.ruleList = res.records || [];
                this.ruleTotal = res.total || 0;
            } catch (e) {
                console.error('load rules failed:', e);
            } finally {
                this.ruleLoading = false;
            }
        },
        ruleSearch() {
            this.ruleQuery.pageNum = 1;
            this.loadRules();
        },
        ruleReset() {
            this.ruleQuery = { pageNum: 1, pageSize: 10, businessType: '', ruleCode: '' };
            this.loadRules();
        },
        rulePageChange(page) {
            this.ruleQuery.pageNum = page;
            this.loadRules();
        },
        openRuleDialog(row) {
            this.ruleForm = row ? { ...row } : { ruleName: '', ruleCode: '', businessType: 'print', sceneCode: 'DEFAULT', ruleType: 'global', profitType: 'percent', profitValue: 0, isEnabled: 1, priority: 10, description: '' };
            this.ruleDialogVisible = true;
        },
        async saveRule() {
            try {
                if (this.ruleForm.id) {
                    await AdminAPI.updateProfitRule(this.ruleForm);
                } else {
                    await AdminAPI.createProfitRule(this.ruleForm);
                }
                ElMessage.success('保存成功');
                this.ruleDialogVisible = false;
                this.loadRules();
            } catch (e) {
                console.error('save rule failed:', e);
            }
        },
        async deleteRule(row) {
            try {
                await ElMessageBox.confirm(`确定删除盈利规则「${row.ruleName}」吗？`, '删除确认', { type: 'warning' });
            } catch (e) {
                return;
            }
            try {
                await AdminAPI.deleteProfitRule(row.id);
                ElMessage.success('删除成功');
                this.loadRules();
            } catch (e) {
                console.error('delete rule failed:', e);
            }
        }
    },
    template: `
    <div class="page-container">
        <div class="page-header">
            <h2>价格与盈利配置</h2>
        </div>
        <el-tabs v-model="activeTab">
            <el-tab-pane label="打印价格" name="print">
                <div class="filter-bar">
                    <el-select v-model="printQuery.paperType" placeholder="纸张类型" clearable style="width: 120px;">
                        <el-option label="A4" value="A4" /><el-option label="A3" value="A3" />
                    </el-select>
                    <el-select v-model="printQuery.colorType" placeholder="颜色" clearable style="width: 120px;">
                        <el-option label="黑白" value="黑白" /><el-option label="彩色" value="彩色" />
                    </el-select>
                    <el-button type="primary" @click="printSearch">查询</el-button>
                    <el-button @click="printReset">重置</el-button>
                    <el-button type="primary" plain style="float: right;" @click="openPrintDialog()">新增打印价格</el-button>
                </div>
                <div class="table-card">
                    <el-table v-loading="printLoading" :data="printList" stripe>
                        <el-table-column prop="paperType" label="纸张" width="80" />
                        <el-table-column prop="colorType" label="颜色" width="90" />
                        <el-table-column label="单双面" width="90">
                            <template #default="{ row }">{{ row.singleDouble === 'double' ? '双面' : '单面' }}</template>
                        </el-table-column>
                        <el-table-column label="基础价" width="110">
                            <template #default="{ row }">¥{{ formatMoney(row.basePrice) }}</template>
                        </el-table-column>
                        <el-table-column label="最低价" width="110">
                            <template #default="{ row }">¥{{ formatMoney(row.minPrice) }}</template>
                        </el-table-column>
                        <el-table-column label="盈利比例" width="100">
                            <template #default="{ row }">{{ row.profitRatio }}%</template>
                        </el-table-column>
                        <el-table-column label="状态" width="90">
                            <template #default="{ row }">
                                <el-tag size="small" :type="Number(row.isActive) === 1 ? 'success' : 'info'">{{ Number(row.isActive) === 1 ? '启用' : '停用' }}</el-tag>
                            </template>
                        </el-table-column>
                        <el-table-column label="操作" width="150" fixed="right">
                            <template #default="{ row }">
                                <el-button link type="primary" size="small" @click="openPrintDialog(row)">编辑</el-button>
                                <el-button link type="danger" size="small" @click="deletePrint(row)">删除</el-button>
                            </template>
                        </el-table-column>
                    </el-table>
                    <div class="pagination-wrap">
                        <el-pagination background layout="total, prev, pager, next" :total="printTotal" :current-page="printQuery.pageNum" :page-size="printQuery.pageSize" @current-change="printPageChange" />
                    </div>
                </div>
            </el-tab-pane>

            <el-tab-pane label="快递价格" name="express">
                <div class="filter-bar">
                    <el-input v-model="expressQuery.courier" placeholder="快递公司" clearable style="width: 200px;" @keyup.enter="expressSearch" />
                    <el-button type="primary" @click="expressSearch">查询</el-button>
                    <el-button @click="expressReset">重置</el-button>
                    <el-button type="primary" plain style="float: right;" @click="openExpressDialog()">新增快递价格</el-button>
                </div>
                <div class="table-card">
                    <el-table v-loading="expressLoading" :data="expressList" stripe>
                        <el-table-column prop="courier" label="快递公司" min-width="120" />
                        <el-table-column prop="fromProvince" label="始发地" width="100" />
                        <el-table-column prop="toProvince" label="目的地" width="100" />
                        <el-table-column label="首重(kg)" width="90">
                            <template #default="{ row }">{{ row.firstWeight }}</template>
                        </el-table-column>
                        <el-table-column label="首重价" width="90">
                            <template #default="{ row }">¥{{ formatMoney(row.firstPrice) }}</template>
                        </el-table-column>
                        <el-table-column label="续重(kg)" width="90">
                            <template #default="{ row }">{{ row.continueWeight }}</template>
                        </el-table-column>
                        <el-table-column label="续重价" width="90">
                            <template #default="{ row }">¥{{ formatMoney(row.continuePrice) }}</template>
                        </el-table-column>
                        <el-table-column label="状态" width="90">
                            <template #default="{ row }">
                                <el-tag size="small" :type="Number(row.isActive) === 1 ? 'success' : 'info'">{{ Number(row.isActive) === 1 ? '启用' : '停用' }}</el-tag>
                            </template>
                        </el-table-column>
                        <el-table-column label="操作" width="150" fixed="right">
                            <template #default="{ row }">
                                <el-button link type="primary" size="small" @click="openExpressDialog(row)">编辑</el-button>
                                <el-button link type="danger" size="small" @click="deleteExpress(row)">删除</el-button>
                            </template>
                        </el-table-column>
                    </el-table>
                    <div class="pagination-wrap">
                        <el-pagination background layout="total, prev, pager, next" :total="expressTotal" :current-page="expressQuery.pageNum" :page-size="expressQuery.pageSize" @current-change="expressPageChange" />
                    </div>
                </div>
            </el-tab-pane>

            <el-tab-pane label="盈利规则" name="rule">
                <div class="filter-bar">
                    <el-select v-model="ruleQuery.businessType" placeholder="业务类型" clearable style="width: 140px;">
                        <el-option label="打印" value="print" /><el-option label="快递" value="express" />
                    </el-select>
                    <el-input v-model="ruleQuery.ruleCode" placeholder="规则编码" clearable style="width: 200px;" @keyup.enter="ruleSearch" />
                    <el-button type="primary" @click="ruleSearch">查询</el-button>
                    <el-button @click="ruleReset">重置</el-button>
                    <el-button type="primary" plain style="float: right;" @click="openRuleDialog()">新增规则</el-button>
                </div>
                <div class="table-card">
                    <el-table v-loading="ruleLoading" :data="ruleList" stripe>
                        <el-table-column prop="ruleName" label="规则名称" min-width="160" />
                        <el-table-column prop="ruleCode" label="规则编码" min-width="170" />
                        <el-table-column label="业务类型" width="100">
                            <template #default="{ row }">{{ row.businessType === 'express' ? '快递' : '打印' }}</template>
                        </el-table-column>
                        <el-table-column label="盈利方式" width="110">
                            <template #default="{ row }">{{ row.profitType === 'fixed' ? '固定金额' : '百分比' }}</template>
                        </el-table-column>
                        <el-table-column label="盈利值" width="100">
                            <template #default="{ row }">{{ row.profitValue }}{{ row.profitType === 'fixed' ? ' 元' : '%' }}</template>
                        </el-table-column>
                        <el-table-column label="优先级" width="90">
                            <template #default="{ row }">{{ row.priority }}</template>
                        </el-table-column>
                        <el-table-column label="状态" width="90">
                            <template #default="{ row }">
                                <el-tag size="small" :type="Number(row.isEnabled) === 1 ? 'success' : 'info'">{{ Number(row.isEnabled) === 1 ? '启用' : '停用' }}</el-tag>
                            </template>
                        </el-table-column>
                        <el-table-column label="操作" width="150" fixed="right">
                            <template #default="{ row }">
                                <el-button link type="primary" size="small" @click="openRuleDialog(row)">编辑</el-button>
                                <el-button link type="danger" size="small" @click="deleteRule(row)">删除</el-button>
                            </template>
                        </el-table-column>
                    </el-table>
                    <div class="pagination-wrap">
                        <el-pagination background layout="total, prev, pager, next" :total="ruleTotal" :current-page="ruleQuery.pageNum" :page-size="ruleQuery.pageSize" @current-change="rulePageChange" />
                    </div>
                </div>
            </el-tab-pane>
        </el-tabs>

        <el-dialog v-model="printDialogVisible" title="打印价格" width="560px">
            <el-form label-width="90px">
                <el-form-item label="纸张类型">
                    <el-select v-model="printForm.paperType" style="width: 100%;"><el-option label="A4" value="A4" /><el-option label="A3" value="A3" /></el-select>
                </el-form-item>
                <el-form-item label="颜色">
                    <el-select v-model="printForm.colorType" style="width: 100%;"><el-option label="黑白" value="黑白" /><el-option label="彩色" value="彩色" /></el-select>
                </el-form-item>
                <el-form-item label="单双面">
                    <el-select v-model="printForm.singleDouble" style="width: 100%;"><el-option label="单面" value="single" /><el-option label="双面" value="double" /></el-select>
                </el-form-item>
                <el-form-item label="基础价(元)">
                    <el-input-number v-model="printForm.basePrice" :min="0" :precision="2" :step="0.1" style="width: 100%;" />
                </el-form-item>
                <el-form-item label="最低价(元)">
                    <el-input-number v-model="printForm.minPrice" :min="0" :precision="2" :step="0.1" style="width: 100%;" />
                </el-form-item>
                <el-form-item label="盈利比例(%)">
                    <el-input-number v-model="printForm.profitRatio" :min="0" :precision="2" :step="0.5" style="width: 100%;" />
                </el-form-item>
                <el-form-item label="是否启用">
                    <el-switch v-model="printForm.isActive" :active-value="1" :inactive-value="0" />
                </el-form-item>
            </el-form>
            <template #footer>
                <el-button @click="printDialogVisible = false">取消</el-button>
                <el-button type="primary" @click="savePrint">保存</el-button>
            </template>
        </el-dialog>

        <el-dialog v-model="expressDialogVisible" title="快递价格" width="560px">
            <el-form label-width="90px">
                <el-form-item label="快递公司">
                    <el-input v-model="expressForm.courier" placeholder="如：顺丰速运" />
                </el-form-item>
                <el-form-item label="始发地">
                    <el-input v-model="expressForm.fromProvince" placeholder="如：广东" />
                </el-form-item>
                <el-form-item label="目的地">
                    <el-input v-model="expressForm.toProvince" placeholder="如：全国" />
                </el-form-item>
                <el-form-item label="首重(kg)">
                    <el-input-number v-model="expressForm.firstWeight" :min="0" :precision="2" :step="0.5" style="width: 100%;" />
                </el-form-item>
                <el-form-item label="首重价(元)">
                    <el-input-number v-model="expressForm.firstPrice" :min="0" :precision="2" :step="0.5" style="width: 100%;" />
                </el-form-item>
                <el-form-item label="续重(kg)">
                    <el-input-number v-model="expressForm.continueWeight" :min="0" :precision="2" :step="0.5" style="width: 100%;" />
                </el-form-item>
                <el-form-item label="续重价(元)">
                    <el-input-number v-model="expressForm.continuePrice" :min="0" :precision="2" :step="0.5" style="width: 100%;" />
                </el-form-item>
                <el-form-item label="是否启用">
                    <el-switch v-model="expressForm.isActive" :active-value="1" :inactive-value="0" />
                </el-form-item>
            </el-form>
            <template #footer>
                <el-button @click="expressDialogVisible = false">取消</el-button>
                <el-button type="primary" @click="saveExpress">保存</el-button>
            </template>
        </el-dialog>

        <el-dialog v-model="ruleDialogVisible" title="盈利规则" width="560px">
            <el-form label-width="100px">
                <el-form-item label="规则名称">
                    <el-input v-model="ruleForm.ruleName" placeholder="如：打印服务盈利规则" />
                </el-form-item>
                <el-form-item label="规则编码">
                    <el-input v-model="ruleForm.ruleCode" placeholder="如：PRINT_GLOBAL_PROFIT" />
                </el-form-item>
                <el-form-item label="业务类型">
                    <el-select v-model="ruleForm.businessType" style="width: 100%;"><el-option label="打印" value="print" /><el-option label="快递" value="express" /></el-select>
                </el-form-item>
                <el-form-item label="盈利方式">
                    <el-select v-model="ruleForm.profitType" style="width: 100%;"><el-option label="百分比" value="percent" /><el-option label="固定金额" value="fixed" /></el-select>
                </el-form-item>
                <el-form-item label="盈利值">
                    <el-input-number v-model="ruleForm.profitValue" :min="0" :precision="2" :step="0.5" style="width: 100%;" />
                </el-form-item>
                <el-form-item label="优先级">
                    <el-input-number v-model="ruleForm.priority" :min="1" :step="1" style="width: 100%;" />
                </el-form-item>
                <el-form-item label="是否启用">
                    <el-switch v-model="ruleForm.isEnabled" :active-value="1" :inactive-value="0" />
                </el-form-item>
                <el-form-item label="描述">
                    <el-input v-model="ruleForm.description" type="textarea" :rows="2" />
                </el-form-item>
            </el-form>
            <template #footer>
                <el-button @click="ruleDialogVisible = false">取消</el-button>
                <el-button type="primary" @click="saveRule">保存</el-button>
            </template>
        </el-dialog>
    </div>
    `
};
