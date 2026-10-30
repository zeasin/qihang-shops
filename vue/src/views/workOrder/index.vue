<template>
  <div class="app-container">
    <!-- 顶部操作条 -->
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-edit-outline" size="mini" @click="openQuick">速记工单</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="openAdd">完整新建</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="info" plain icon="el-icon-upload2" size="mini" @click="openImport">批量导入</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="el-icon-check" size="mini" :disabled="multiple" @click="handleSettle">批量结算</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <!-- 查询 -->
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="订单号" prop="orderNum">
        <el-input v-model="queryParams.orderNum" placeholder="请输入订单号" clearable style="width: 200px" @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="快递单号" prop="waybillCode">
        <el-input v-model="queryParams.waybillCode" placeholder="按订单快递单号查" clearable style="width: 180px" @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="店铺" prop="shopId">
        <el-select v-model="queryParams.shopId" placeholder="全部店铺" clearable filterable style="width: 160px">
          <el-option v-for="s in shopList" :key="s.id" :label="s.name" :value="s.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="类型" prop="type">
        <el-select v-model="queryParams.type" placeholder="全部类型" clearable style="width: 120px">
          <el-option v-for="(t, i) in typeOptions" :key="i" :label="t.label" :value="i + 1" />
        </el-select>
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="全部状态" clearable style="width: 120px">
          <el-option label="待处理" :value="0" />
          <el-option label="已结算" :value="1" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 列表 -->
    <el-table v-loading="loading" :data="workOrderList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="50" align="center" :selectable="row => row.status === 0" />
      <el-table-column label="类型" align="center" width="90">
        <template slot-scope="scope">
          <el-tag :type="typeTag(scope.row.type)" size="small">{{ typeLabel(scope.row.type) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="金额" align="center" prop="amount" width="110">
        <template slot-scope="scope">
          <span style="color: #f56c6c; font-weight: 600">¥{{ scope.row.amount }}</span>
        </template>
      </el-table-column>
      <el-table-column label="店铺" align="center" width="140">
        <template slot-scope="scope">{{ shopName(scope.row.shopId) }}</template>
      </el-table-column>
      <el-table-column label="订单号" align="center" prop="orderNum" show-overflow-tooltip />
      <el-table-column label="备注" align="center" prop="remark" show-overflow-tooltip />
      <el-table-column label="状态" align="center" width="90">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.status === 0" type="warning" size="small">待处理</el-tag>
          <el-tag v-else type="success" size="small">已结算</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="创建人" align="center" prop="createBy" width="100" />
      <el-table-column label="创建时间" align="center" prop="createTime" width="160" />
      <el-table-column label="操作" align="center" width="200" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button v-if="scope.row.status === 0" size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)">编辑</el-button>
          <el-button v-if="scope.row.status === 0" size="mini" type="text" icon="el-icon-check" @click="handleSettleOne(scope.row)">结算</el-button>
          <el-button v-if="scope.row.status === 1" size="mini" type="text" icon="el-icon-refresh-left" @click="handleRevoke(scope.row)">撤销结算</el-button>
          <el-button v-if="scope.row.status === 0" size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <!-- 速记工单弹窗：选类型 → 填金额 → 可选关联 -->
    <el-dialog title="速记工单" :visible.sync="quickOpen" width="520px" append-to-body @closed="resetQuick">
      <el-form ref="quickForm" :model="quickForm" label-width="70px">
        <el-form-item label="类型">
          <el-row :gutter="8">
            <el-col v-for="(t, i) in typeOptions" :key="i" :span="8">
              <div class="type-tile" :class="{ active: quickForm.type === i + 1 }" @click="quickForm.type = i + 1">
                {{ t.label }}
              </div>
            </el-col>
          </el-row>
        </el-form-item>
        <el-form-item label="金额" required>
          <el-input-number v-model="quickForm.amount" :min="0.01" :precision="2" :controls="false" placeholder="实际支出金额" style="width: 180px" />
          <span class="form-tip">{{ typeHint(quickForm.type) }}</span>
        </el-form-item>
        <el-form-item label="订单号">
          <el-input v-model="quickForm.orderNum" placeholder="选填，可后补" clearable style="width: 300px" />
        </el-form-item>
        <el-form-item v-if="quickForm.type === 3" label="补发商品">
          <el-input v-model="quickForm.goodsId" placeholder="选填，商品ID（补发联动生成出库单）" clearable style="width: 300px" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="quickOpen = false">取 消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="submitQuick">提交（10 秒完成）</el-button>
      </div>
    </el-dialog>

    <!-- 完整新建/编辑工单弹窗 -->
    <el-dialog :title="title" :visible.sync="open" width="560px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="类型" prop="type">
          <el-select v-model="form.type" placeholder="请选择类型" style="width: 200px">
            <el-option v-for="(t, i) in typeOptions" :key="i" :label="t.label" :value="i + 1" />
          </el-select>
        </el-form-item>
        <el-form-item label="金额" prop="amount">
          <el-input-number v-model="form.amount" :min="0.01" :precision="2" :controls="false" style="width: 200px" />
        </el-form-item>
        <el-form-item label="店铺" prop="shopId">
          <el-select v-model="form.shopId" placeholder="请选择店铺" clearable filterable style="width: 200px">
            <el-option v-for="s in shopList" :key="s.id" :label="s.name" :value="s.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="订单号" prop="orderNum">
          <el-input v-model="form.orderNum" placeholder="平台订单号，可后补" clearable style="width: 300px" />
        </el-form-item>
        <el-form-item v-if="form.type === 3" label="补发商品" prop="goodsId">
          <el-input v-model="form.goodsId" placeholder="商品ID（联动生成补发出库单）" clearable style="width: 300px" />
        </el-form-item>
        <el-form-item label="凭证图" prop="voucher">
          <image-upload v-model="form.voucher" :limit="3" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" :rows="2" placeholder="补发写清发货商品；其他类型写清支出原因" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="cancel">取 消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="submitForm">确 定</el-button>
      </div>
    </el-dialog>

    <!-- 批量导入弹窗 -->
    <el-dialog title="Excel 批量导入" :visible.sync="importOpen" width="480px" append-to-body>
      <el-alert type="info" :closable="false" show-icon style="margin-bottom: 12px"
        title="模板列：订单号、类型、金额、店铺、备注" description="类型填中文：补款/返现/补发/仅退款/退货退款/其他；店铺填店铺名称" />
      <el-upload ref="importUpload" drag accept=".xlsx,.xls" :limit="1" :auto-upload="false"
        :headers="uploadHeaders" :action="uploadUrl" :on-success="handleImportSuccess" :on-error="handleImportError">
        <i class="el-icon-upload"></i>
        <div class="el-upload__text">将售后明细 Excel 拖到此处，或<em>点击选择文件</em></div>
      </el-upload>
      <div slot="footer" class="dialog-footer">
        <el-button @click="importOpen = false">关 闭</el-button>
        <el-button type="primary" :loading="importLoading" @click="submitImport">导入</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listWorkOrder, quickWorkOrder, addWorkOrder, updateWorkOrder, delWorkOrder, settleWorkOrder, revokeWorkOrder } from '@/api/workOrder'
import { listShop } from '@/api/shop/shop'
import { getToken } from '@/utils/auth'

export default {
  name: 'WorkOrder',
  data() {
    return {
      loading: true,
      submitLoading: false,
      importLoading: false,
      showSearch: true,
      ids: [],
      multiple: true,
      total: 0,
      workOrderList: [],
      shopList: [],
      typeOptions: [
        { label: '补款', hint: '实际补给客户的钱' },
        { label: '返现', hint: '实际转给客户的现金' },
        { label: '补发', hint: '商品成本 + 快递费，提交后自动生成出库单' },
        { label: '仅退款', hint: '退款带来的额外损失（运费/包装/货损）' },
        { label: '退货退款', hint: '退回运费 + 货损 + 重新包装' },
        { label: '其他', hint: '实际支出，写清备注' }
      ],
      queryParams: { pageNum: 1, pageSize: 10, orderNum: null, waybillCode: null, shopId: null, type: null, status: null },
      quickOpen: false,
      quickForm: { type: null, amount: undefined, orderNum: null, goodsId: null },
      open: false,
      title: '',
      importOpen: false,
      form: {},
      rules: {
        type: [{ required: true, message: '请选择类型', trigger: 'change' }],
        amount: [{ required: true, message: '请填写金额', trigger: 'blur' }]
      },
      uploadUrl: process.env.VUE_APP_BASE_API + '/workOrder/import',
      uploadHeaders: { Authorization: 'Bearer ' + getToken() }
    }
  },
  created() {
    this.getList()
    this.getShops()
  },
  methods: {
    getList() {
      this.loading = true
      listWorkOrder(this.queryParams).then(res => {
        this.workOrderList = res.rows
        this.total = res.total
        this.loading = false
      })
    },
    getShops() {
      listShop().then(res => {
        this.shopList = res.rows || res.data || []
      }).catch(() => {})
    },
    shopName(id) {
      const s = this.shopList.find(x => String(x.id) === String(id))
      return s ? s.name : (id || '-')
    },
    typeLabel(type) {
      const t = this.typeOptions[(type || 1) - 1]
      return t ? t.label : '其他'
    },
    typeTag(type) {
      const tags = ['', 'warning', 'success', 'primary', 'danger', 'danger', 'info']
      return tags[type] || 'info'
    },
    typeHint(type) {
      const t = this.typeOptions[(type || 1) - 1]
      return t ? t.hint : ''
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    resetQuery() {
      this.resetForm('queryForm')
      this.handleQuery()
    },
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.id)
      this.multiple = !selection.length
    },
    // ========== 速记 ==========
    openQuick() {
      this.quickOpen = true
    },
    resetQuick() {
      this.quickForm = { type: null, amount: undefined, orderNum: null, goodsId: null }
    },
    submitQuick() {
      if (!this.quickForm.type) {
        this.$modal.msgError('请先选择工单类型')
        return
      }
      if (!this.quickForm.amount || this.quickForm.amount <= 0) {
        this.$modal.msgError('请填写金额')
        return
      }
      this.submitLoading = true
      quickWorkOrder(this.quickForm).then(() => {
        this.$modal.msgSuccess('已记录')
        this.quickOpen = false
        this.getList()
      }).finally(() => {
        this.submitLoading = false
      })
    },
    // ========== 完整新建/编辑 ==========
    openAdd() {
      this.form = { type: null, amount: undefined, shopId: null, orderNum: null, goodsId: null, voucher: null, remark: null }
      this.title = '完整新建工单'
      this.open = true
    },
    handleUpdate(row) {
      this.form = { ...row }
      this.title = '编辑工单'
      this.open = true
    },
    cancel() {
      this.open = false
    },
    submitForm() {
      this.$refs['form'].validate(valid => {
        if (!valid) return
        this.submitLoading = true
        if (this.form.id != null) {
          updateWorkOrder(this.form).then(() => {
            this.$modal.msgSuccess('修改成功')
            this.open = false
            this.getList()
          }).finally(() => { this.submitLoading = false })
        } else {
          addWorkOrder(this.form).then(() => {
            this.$modal.msgSuccess('新增成功')
            this.open = false
            this.getList()
          }).finally(() => { this.submitLoading = false })
        }
      })
    },
    // ========== 结算/撤销/删除 ==========
    handleSettle() {
      this.$modal.confirm('确认结算选中的 ' + this.ids.length + ' 笔工单吗？结算后金额锁定进利润计算').then(() => {
        return settleWorkOrder(this.ids.join(','))
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess('结算成功')
      }).catch(() => {})
    },
    handleSettleOne(row) {
      this.$modal.confirm('确认结算该工单（¥' + row.amount + '）吗？').then(() => {
        return settleWorkOrder(row.id)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess('结算成功')
      }).catch(() => {})
    },
    handleRevoke(row) {
      this.$modal.confirm('撤销结算后金额解锁回到待处理，确认撤销吗？').then(() => {
        return revokeWorkOrder(row.id)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess('已撤销')
      }).catch(() => {})
    },
    handleDelete(row) {
      this.$modal.confirm('确认删除该工单吗？').then(() => {
        return delWorkOrder(row.id)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess('删除成功')
      }).catch(() => {})
    },
    // ========== 导入 ==========
    openImport() {
      this.importOpen = true
    },
    submitImport() {
      this.$refs.importUpload.submit()
      this.importLoading = true
    },
    handleImportSuccess(res) {
      this.importLoading = false
      if (res.code === 200) {
        const d = res.data || {}
        this.$modal.msgSuccess('导入完成：成功 ' + (d.success || 0) + ' 条，失败 ' + (d.fail || 0) + ' 条' + (d.message ? '；' + d.message : ''))
        this.importOpen = false
        this.getList()
      } else {
        this.$modal.msgError(res.msg || '导入失败')
      }
    },
    handleImportError() {
      this.importLoading = false
      this.$modal.msgError('上传失败，请重试')
    }
  }
}
</script>

<style scoped>
.type-tile {
  border: 1px solid #dcdfe6;
  border-radius: 6px;
  text-align: center;
  padding: 8px 0;
  margin-bottom: 8px;
  cursor: pointer;
  font-size: 13px;
  color: #606266;
  transition: all 0.2s;
}
.type-tile:hover {
  border-color: #409eff;
  color: #409eff;
}
.type-tile.active {
  border-color: #409eff;
  background: #ecf5ff;
  color: #409eff;
  font-weight: 600;
}
.form-tip {
  margin-left: 10px;
  color: #909399;
  font-size: 12px;
}
</style>
