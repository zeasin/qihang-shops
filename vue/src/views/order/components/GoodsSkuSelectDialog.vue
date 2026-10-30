<template>
  <el-dialog :visible.sync="dialogVisible" title="选择商品" width="1050px" append-to-body @open="handleOpen">
    <el-form :model="queryParams" size="small" :inline="true" label-width="70px">
      <el-form-item label="商品名称">
        <el-input v-model="queryParams.goodsName" placeholder="商品名称" clearable style="width: 180px"
          @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="商品编码">
        <el-input v-model="queryParams.goodsNum" placeholder="商品编码" clearable style="width: 150px"
          @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="SKU编码">
        <el-input v-model="queryParams.specNum" placeholder="规格编码" clearable style="width: 150px"
          @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-table v-loading="loading" :data="skuList" size="small" height="420" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="50" align="center" :selectable="isSelectable" />
      <el-table-column label="图片" align="center" width="70">
        <template slot-scope="scope">
          <image-preview :src="scope.row.colorImage" :width="40" :height="40" />
        </template>
      </el-table-column>
      <el-table-column label="商品名称" prop="goodsName" min-width="180" show-overflow-tooltip />
      <el-table-column label="规格" prop="specName" width="120" show-overflow-tooltip />
      <el-table-column label="SKU编码" prop="specNum" width="140" show-overflow-tooltip />
      <el-table-column label="商品编码" prop="goodsNum" width="130" show-overflow-tooltip />
      <el-table-column label="成本价" align="right" width="90">
        <template slot-scope="scope">{{ formatMoney(scope.row.unitCost) }}</template>
      </el-table-column>
      <el-table-column label="零售价" align="right" width="90">
        <template slot-scope="scope">{{ formatMoney(scope.row.retailPrice) }}</template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize" @pagination="getList" />

    <div slot="footer" class="dialog-footer">
      <span class="selected-tip">已选 {{ multipleSelection.length }} 个规格</span>
      <el-button @click="dialogVisible = false">取 消</el-button>
      <el-button type="primary" :disabled="multipleSelection.length === 0" @click="confirmSelect">确认添加</el-button>
    </div>
  </el-dialog>
</template>

<script>
import { listCostSku } from '@/api/goodsCost'

export default {
  name: 'GoodsSkuSelectDialog',
  props: {
    visible: {
      type: Boolean,
      default: false
    },
    // 已加入明细的规格ID，弹窗内禁用，避免重复添加
    selectedIds: {
      type: Array,
      default: () => []
    }
  },
  data() {
    return {
      dialogVisible: false,
      loading: false,
      total: 0,
      skuList: [],
      multipleSelection: [],
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        goodsName: null,
        goodsNum: null,
        specNum: null
      }
    }
  },
  watch: {
    visible(val) {
      this.dialogVisible = val
    },
    dialogVisible(val) {
      this.$emit('update:visible', val)
    }
  },
  methods: {
    handleOpen() {
      this.multipleSelection = []
      this.getList()
    },
    getList() {
      this.loading = true
      listCostSku(this.queryParams).then(res => {
        this.skuList = res.rows || []
        this.total = res.total || 0
      }).finally(() => {
        this.loading = false
      })
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    resetQuery() {
      this.queryParams = { pageNum: 1, pageSize: 10, goodsName: null, goodsNum: null, specNum: null }
      this.getList()
    },
    handleSelectionChange(selection) {
      this.multipleSelection = selection
    },
    isSelectable(row) {
      return !this.selectedIds.some(id => String(id) === String(row.id))
    },
    confirmSelect() {
      this.$emit('confirm', this.multipleSelection.slice())
      this.dialogVisible = false
    },
    formatMoney(value) {
      return value === null || value === undefined || value === '' ? '-' : Number(value).toFixed(2)
    }
  }
}
</script>

<style scoped>
.selected-tip {
  float: left;
  line-height: 32px;
  color: #606266;
  font-size: 13px;
}
</style>
