<template>
  <div class="app-container">
    <el-alert type="info" :closable="false" show-icon style="margin-bottom: 12px"
      title="固定费用直接计入店铺净利润，不参与链接分摊" />

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd">录入固定费用</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="info" plain icon="el-icon-upload2" size="mini" @click="openImport">批量导入</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="月份" prop="feeMonth">
        <el-date-picker v-model="queryParams.feeMonth" type="month" value-format="yyyy-MM" placeholder="选择月份" clearable style="width: 140px" />
      </el-form-item>
      <el-form-item label="店铺" prop="shopId">
        <el-select v-model="queryParams.shopId" placeholder="全部店铺" clearable filterable style="width: 160px">
          <el-option v-for="s in shopList" :key="s.id" :label="s.name" :value="s.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="费用项" prop="feeType">
        <el-select v-model="queryParams.feeType" placeholder="全部费用项" clearable style="width: 120px">
          <el-option v-for="t in feeTypes" :key="t" :label="t" :value="t" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-table v-loading="loading" :data="feeList">
      <el-table-column label="月份" align="center" prop="feeMonth" width="90" />
      <el-table-column label="店铺" align="center" width="160">
        <template slot-scope="scope">{{ shopName(scope.row.shopId) }}</template>
      </el-table-column>
      <el-table-column label="费用项" align="center" width="100">
        <template slot-scope="scope">
          <el-tag size="small" :type="feeTypeTag(scope.row.feeType)">{{ scope.row.feeType }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="金额" align="center" width="120">
        <template slot-scope="scope"><span style="color: #e6a23c; font-weight: 600">¥{{ scope.row.amount }}</span></template>
      </el-table-column>
      <el-table-column label="备注" align="center" prop="remark" show-overflow-tooltip />
      <el-table-column label="录入人" align="center" prop="createBy" width="100" />
      <el-table-column label="创建时间" align="center" prop="createTime" width="160" />
      <el-table-column label="操作" align="center" width="140">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)">编辑</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <!-- 录入/编辑弹窗 -->
    <el-dialog :title="title" :visible.sync="open" width="480px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="店铺" prop="shopId">
          <el-select v-model="form.shopId" placeholder="请选择店铺" filterable style="width: 240px">
            <el-option v-for="s in shopList" :key="s.id" :label="s.name" :value="s.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="费用项" prop="feeType">
          <el-select v-model="form.feeType" placeholder="选择或输入" filterable allow-create style="width: 240px">
            <el-option v-for="t in feeTypes" :key="t" :label="t" :value="t" />
          </el-select>
        </el-form-item>
        <el-form-item label="月份" prop="feeMonth">
          <el-date-picker v-model="form.feeMonth" type="month" value-format="yyyy-MM" placeholder="费用月份" style="width: 240px" />
        </el-form-item>
        <el-form-item label="金额" prop="amount">
          <el-input-number v-model="form.amount" :min="0.01" :precision="2" :controls="false" style="width: 240px" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" :rows="2" placeholder="同店同月同项可多条累加" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="cancel">取 消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="submitForm">确 定</el-button>
      </div>
    </el-dialog>

    <!-- 批量导入 -->
    <el-dialog title="Excel 批量导入" :visible.sync="importOpen" width="480px" append-to-body>
      <el-alert type="info" :closable="false" show-icon style="margin-bottom: 12px"
        title="模板列：店铺、费用项、月份、金额、备注" description="店铺填店铺名称；费用项：人工/房租/软件/包装/其他" />
      <el-upload ref="importUpload" drag accept=".xlsx,.xls" :limit="1" :auto-upload="false"
        :headers="uploadHeaders" :action="uploadUrl" :on-success="handleImportSuccess" :on-error="handleImportError">
        <i class="el-icon-upload"></i>
        <div class="el-upload__text">将固定费用 Excel 拖到此处，或<em>点击选择文件</em></div>
      </el-upload>
      <div slot="footer" class="dialog-footer">
        <el-button @click="importOpen = false">关 闭</el-button>
        <el-button type="primary" :loading="importLoading" @click="submitImport">导入</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listFixedFee, addFixedFee, updateFixedFee, delFixedFee } from '@/api/fee'
import { listShop } from '@/api/shop/shop'
import { getToken } from '@/utils/auth'

export default {
  name: 'FeeFixed',
  data() {
    return {
      loading: true,
      submitLoading: false,
      importLoading: false,
      showSearch: true,
      total: 0,
      feeList: [],
      shopList: [],
      feeTypes: ['人工', '房租', '软件', '包装', '其他'],
      queryParams: { pageNum: 1, pageSize: 10, feeMonth: null, shopId: null, feeType: null },
      open: false,
      importOpen: false,
      title: '',
      form: {},
      rules: {
        shopId: [{ required: true, message: '请选择店铺', trigger: 'change' }],
        feeType: [{ required: true, message: '请选择费用项', trigger: 'change' }],
        feeMonth: [{ required: true, message: '请选择月份', trigger: 'change' }],
        amount: [{ required: true, message: '请填写金额', trigger: 'blur' }]
      },
      uploadUrl: process.env.VUE_APP_BASE_API + '/fixedFee/import',
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
      listFixedFee(this.queryParams).then(res => {
        this.feeList = res.rows
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
    feeTypeTag(t) {
      const tags = { '人工': 'primary', '房租': 'warning', '软件': 'success', '包装': 'info', '其他': '' }
      return tags[t] || ''
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    resetQuery() {
      this.resetForm('queryForm')
      this.handleQuery()
    },
    handleAdd() {
      this.form = { shopId: null, feeType: null, feeMonth: null, amount: undefined, remark: null }
      this.title = '录入固定费用'
      this.open = true
    },
    handleUpdate(row) {
      this.form = { ...row }
      this.title = '编辑固定费用'
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
          updateFixedFee(this.form).then(() => {
            this.$modal.msgSuccess('修改成功')
            this.open = false
            this.getList()
          }).finally(() => { this.submitLoading = false })
        } else {
          addFixedFee(this.form).then(() => {
            this.$modal.msgSuccess('录入成功')
            this.open = false
            this.getList()
          }).finally(() => { this.submitLoading = false })
        }
      })
    },
    handleDelete(row) {
      this.$modal.confirm('确认删除该固定费用记录吗？').then(() => {
        return delFixedFee(row.id)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess('删除成功')
      }).catch(() => {})
    },
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
