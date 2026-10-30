<template>
  <div class="app-container">
    <el-alert type="info" :closable="false" show-icon style="margin-bottom: 12px"
      title="成本价用于利润核算：订单导入自动按 SKU 匹配；未填成本的 SKU 会在利润页完整度提示" />

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="info" plain icon="el-icon-upload2" size="mini" @click="importOpen = true">Excel 导入成本</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="关键字" prop="goodsName">
        <el-input v-model="queryParams.goodsName" placeholder="商品名称" clearable style="width: 180px" @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="商品编码" prop="goodsNum">
        <el-input v-model="queryParams.goodsNum" placeholder="商品编码" clearable style="width: 140px" @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="SKU编码" prop="specNum">
        <el-input v-model="queryParams.specNum" placeholder="规格编码" clearable style="width: 140px" @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="成本" prop="costEmpty">
        <el-select v-model="queryParams.costEmpty" placeholder="全部" clearable style="width: 130px">
          <el-option label="仅未填成本" :value="1" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-table v-loading="loading" :data="skuList">
      <el-table-column label="商品名称" align="left" prop="goodsName" min-width="200" show-overflow-tooltip />
      <el-table-column label="规格" align="center" prop="specName" width="130" show-overflow-tooltip />
      <el-table-column label="规格编码" align="center" prop="specNum" width="140" show-overflow-tooltip />
      <el-table-column label="商品编码" align="center" prop="goodsNum" width="130" show-overflow-tooltip />
      <el-table-column label="成本价" align="center" width="160">
        <template slot-scope="scope">
          <el-input-number v-model="scope.row.unitCost" :min="0" :precision="2" :controls="false" size="mini"
            style="width: 120px" placeholder="未填" @change="saveCost(scope.row)" />
        </template>
      </el-table-column>
      <el-table-column label="零售价" align="center" width="160">
        <template slot-scope="scope">
          <el-input-number v-model="scope.row.retailPrice" :min="0" :precision="2" :controls="false" size="mini"
            style="width: 120px" @change="saveCost(scope.row)" />
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" width="90">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.unitCost == null || scope.row.unitCost <= 0" type="warning" size="small">未填成本</el-tag>
          <el-tag v-else type="success" size="small">已填</el-tag>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <!-- Excel 导入 -->
    <el-dialog title="Excel 导入成本" :visible.sync="importOpen" width="480px" append-to-body>
      <el-alert type="info" :closable="false" show-icon style="margin-bottom: 12px"
        title="模板列：商品名称、规格名称、规格编码、商品编码、成本价、售价" description="按规格编码匹配更新成本价；不存在的自动建档（含成本价）" />
      <el-upload ref="importUpload" drag accept=".xlsx,.xls" :limit="1" :auto-upload="false"
        :on-change="handleFileChange" :on-remove="handleFileRemove">
        <i class="el-icon-upload"></i>
        <div class="el-upload__text">将成本 Excel 拖到此处，或<em>点击选择文件</em></div>
      </el-upload>
      <div slot="footer" class="dialog-footer">
        <el-button @click="importOpen = false">关 闭</el-button>
        <el-button type="primary" :loading="importLoading" @click="submitImport">导入</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listCostSku, updateSkuCost, importGoodsCost } from '@/api/goodsCost'

export default {
  name: 'GoodsCostProfile',
  data() {
    return {
      loading: false,
      importLoading: false,
      showSearch: true,
      total: 0,
      skuList: [],
      importOpen: false,
      importFile: null,
      queryParams: { pageNum: 1, pageSize: 10, goodsName: null, goodsNum: null, specNum: null, costEmpty: null }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.loading = true
      listCostSku(this.queryParams).then(res => {
        this.skuList = (res.rows || []).map(r => {
          return { ...r, unitCost: r.unitCost == null ? undefined : Number(r.unitCost), retailPrice: r.retailPrice == null ? undefined : Number(r.retailPrice) }
        })
        this.total = res.total
        this.loading = false
      })
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    resetQuery() {
      this.resetForm('queryForm')
      this.handleQuery()
    },
    saveCost(row) {
      if (row.unitCost === undefined && row.retailPrice === undefined) return
      updateSkuCost({ id: row.id, unitCost: row.unitCost, retailPrice: row.retailPrice }).then(() => {
        this.$modal.msgSuccess('已保存')
      }).catch(() => {})
    },
    handleFileChange(file) {
      this.importFile = file ? file.raw : null
    },
    handleFileRemove() {
      this.importFile = null
    },
    submitImport() {
      if (!this.importFile) {
        this.$modal.msgError('请先选择文件')
        return
      }
      this.importLoading = true
      importGoodsCost(this.importFile).then(res => {
        const d = res.data || {}
        this.$modal.msgSuccess(`导入完成：更新 ${d.updated || 0} 条，新建 ${d.created || 0} 条，失败 ${d.fail || 0} 条` + (d.message ? '；' + d.message : ''))
        this.importOpen = false
        this.getList()
      }).finally(() => {
        this.importLoading = false
      })
    }
  }
}
</script>
