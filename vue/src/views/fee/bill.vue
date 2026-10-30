<template>
  <div class="app-container">
    <el-alert type="info" :closable="false" show-icon style="margin-bottom: 12px"
      title="每月上传平台账单 Excel（淘宝账房/拼多多对账中心/抖店结算），按订单号自动匹配，平台扣费以账单为准" />

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-upload2" size="mini" @click="openImport">上传对账单</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="el-icon-connection" size="mini" :loading="matchLoading" @click="handleMatch">自动匹配订单</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete">删除选中</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="账期" prop="billMonth">
        <el-date-picker v-model="queryParams.billMonth" type="month" value-format="yyyy-MM" placeholder="选择账期" clearable style="width: 140px" />
      </el-form-item>
      <el-form-item label="店铺" prop="shopId">
        <el-select v-model="queryParams.shopId" placeholder="全部店铺" clearable filterable style="width: 160px">
          <el-option v-for="s in shopList" :key="s.id" :label="s.name" :value="s.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="订单号" prop="orderNum">
        <el-input v-model="queryParams.orderNum" placeholder="请输入订单号" clearable style="width: 180px" @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="匹配状态" prop="matchStatus">
        <el-select v-model="queryParams.matchStatus" placeholder="全部" clearable style="width: 120px">
          <el-option label="未匹配" :value="0" />
          <el-option label="已匹配" :value="1" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-table v-loading="loading" :data="billList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="50" align="center" />
      <el-table-column label="账期" align="center" prop="billMonth" width="80" />
      <el-table-column label="店铺" align="center" width="140">
        <template slot-scope="scope">{{ shopName(scope.row.shopId) }}</template>
      </el-table-column>
      <el-table-column label="订单号" align="center" prop="orderNum" show-overflow-tooltip />
      <el-table-column label="业务类型" align="center" prop="bizType" width="110" />
      <el-table-column label="收支" align="center" width="70">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.incomeType === 1" type="success" size="small">收入</el-tag>
          <el-tag v-else type="danger" size="small">支出</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="金额" align="center" width="110">
        <template slot-scope="scope">
          <span :style="{ color: scope.row.incomeType === 1 ? '#67c23a' : '#f56c6c', fontWeight: 600 }">
            {{ scope.row.incomeType === 1 ? '+' : '-' }}¥{{ scope.row.amount }}
          </span>
        </template>
      </el-table-column>
      <el-table-column label="发生时间" align="center" prop="bizTime" width="160" />
      <el-table-column label="匹配状态" align="center" width="90">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.matchStatus === 1" type="success" size="small">已匹配</el-tag>
          <el-tag v-else type="danger" size="small">未匹配</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="上传批次" align="center" prop="batchNo" width="140" show-overflow-tooltip />
      <el-table-column label="操作" align="center" width="90">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <!-- 上传对账单 -->
    <el-dialog title="上传对账单" :visible.sync="importOpen" width="520px" append-to-body>
      <el-form ref="importForm" :model="importForm" :rules="importRules" label-width="90px">
        <el-form-item label="店铺" prop="shopId">
          <el-select v-model="importForm.shopId" placeholder="请选择店铺" filterable style="width: 280px">
            <el-option v-for="s in shopList" :key="s.id" :label="s.name" :value="s.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="账期月份" prop="billMonth">
          <el-date-picker v-model="importForm.billMonth" type="month" value-format="yyyy-MM" placeholder="账单所属月份" style="width: 280px" />
        </el-form-item>
      </el-form>
      <el-alert type="info" :closable="false" show-icon style="margin-bottom: 12px"
        title="模板列：订单号、业务类型、收支、金额、发生时间" description="收支填：收入 或 支出；金额填正数；导入后自动按订单号匹配" />
      <el-upload ref="importUpload" drag accept=".xlsx,.xls" :limit="1" :auto-upload="false"
        :data="importForm" :headers="uploadHeaders" :action="uploadUrl" :on-success="handleImportSuccess" :on-error="handleImportError">
        <i class="el-icon-upload"></i>
        <div class="el-upload__text">将对账单 Excel 拖到此处，或<em>点击选择文件</em></div>
      </el-upload>
      <div slot="footer" class="dialog-footer">
        <el-button @click="importOpen = false">关 闭</el-button>
        <el-button type="primary" :loading="importLoading" @click="submitImport">上传并导入</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listPlatformBill, matchPlatformBill, delPlatformBill } from '@/api/fee'
import { listShop } from '@/api/shop/shop'
import { getToken } from '@/utils/auth'

export default {
  name: 'FeeBill',
  data() {
    return {
      loading: true,
      importLoading: false,
      matchLoading: false,
      showSearch: true,
      ids: [],
      multiple: true,
      total: 0,
      billList: [],
      shopList: [],
      queryParams: { pageNum: 1, pageSize: 10, billMonth: null, shopId: null, orderNum: null, matchStatus: null },
      importOpen: false,
      importForm: { shopId: null, billMonth: null },
      importRules: {
        shopId: [{ required: true, message: '请选择店铺', trigger: 'change' }],
        billMonth: [{ required: true, message: '请选择账期月份', trigger: 'change' }]
      },
      uploadUrl: process.env.VUE_APP_BASE_API + '/platformBill/import',
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
      listPlatformBill(this.queryParams).then(res => {
        this.billList = res.rows
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
    handleMatch() {
      this.matchLoading = true
      matchPlatformBill().then(res => {
        this.$modal.msgSuccess(res.msg || '匹配完成')
        this.getList()
      }).finally(() => { this.matchLoading = false })
    },
    handleDelete(row) {
      const ids = row.id ? [row.id] : this.ids
      this.$modal.confirm('确认删除选中的 ' + ids.length + ' 条账单明细吗？传错可整批重传').then(() => {
        return delPlatformBill(ids.join(','))
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess('删除成功')
      }).catch(() => {})
    },
    openImport() {
      this.importForm = { shopId: null, billMonth: null }
      this.importOpen = true
    },
    submitImport() {
      this.$refs['importForm'].validate(valid => {
        if (!valid) return
        this.$refs.importUpload.submit()
        this.importLoading = true
      })
    },
    handleImportSuccess(res) {
      this.importLoading = false
      if (res.code === 200) {
        const d = res.data || {}
        this.$modal.msgSuccess('导入完成：成功 ' + (d.success || 0) + ' 条，失败 ' + (d.fail || 0) + ' 条，自动匹配 ' + (d.matched || 0) + ' 条' + (d.message ? '；' + d.message : ''))
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
