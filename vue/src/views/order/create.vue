<template>
  <div class="app-container">
    <el-alert type="info" :closable="false" show-icon style="margin-bottom: 12px"
      title="手动建单用于零星订单、补录历史单；保存后进入待发货，发货时填写快递费并快照商品成本" />

    <el-card shadow="never">
      <el-form ref="orderForm" :model="form" :rules="rules" label-width="90px">
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="店铺" prop="shopId">
              <el-select v-model="form.shopId" placeholder="请选择店铺" filterable style="width: 100%">
                <el-option v-for="s in shopList" :key="s.id" :label="s.name" :value="s.id" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="订单号" prop="orderNum">
              <el-input v-model="form.orderNum" placeholder="请输入订单号" clearable />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="下单时间" prop="orderTime">
              <el-date-picker v-model="form.orderTime" type="datetime" value-format="yyyy-MM-dd HH:mm:ss"
                placeholder="请选择下单时间" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="商品明细" required>
          <el-button size="mini" type="primary" icon="el-icon-plus" @click="skuDialogOpen = true">选择商品</el-button>
          <el-table :data="form.itemList" size="small" style="width: 100%; margin-top: 8px"
            empty-text="还没有商品，点击“选择商品”添加">
            <el-table-column label="图片" width="60" align="center">
              <template slot-scope="scope">
                <image-preview :src="scope.row.colorImage" :width="40" :height="40" />
              </template>
            </el-table-column>
            <el-table-column label="商品名称" prop="goodsName" min-width="220" show-overflow-tooltip />
            <el-table-column label="规格" prop="specName" width="130" show-overflow-tooltip />
            <el-table-column label="SKU编码" prop="specNum" width="150" show-overflow-tooltip />
            <el-table-column label="零售价" width="100" align="right">
              <template slot-scope="scope">{{ formatMoney(scope.row.retailPrice) }}</template>
            </el-table-column>
            <el-table-column label="数量" width="120" align="center">
              <template slot-scope="scope">
                <el-input-number v-model="scope.row.quantity" :min="1" size="small" controls-position="right"
                  style="width: 90px" @change="onQuantityChange(scope.row)" />
              </template>
            </el-table-column>
            <el-table-column label="商品金额" width="150" align="center">
              <template slot-scope="scope">
                <el-input-number v-model="scope.row.itemAmount" :min="0" :precision="2" :controls="false" size="small"
                  style="width: 120px" @change="calcPay" />
              </template>
            </el-table-column>
            <el-table-column label="操作" width="70" align="center">
              <template slot-scope="scope">
                <el-button size="mini" type="text" icon="el-icon-delete" @click="removeItem(scope.$index)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-form-item>

        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="实收金额" prop="payAmount">
              <el-input-number v-model="form.payAmount" :min="0" :precision="2" :controls="false" style="width: 160px" />
              <el-button size="mini" type="text" style="margin-left: 6px" @click="calcPay">按明细合计</el-button>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="平台扣费" prop="platformFee">
              <el-input-number v-model="form.platformFee" :min="0" :precision="2" :controls="false" style="width: 160px" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" :rows="2" placeholder="选填" />
        </el-form-item>
      </el-form>
      <div style="text-align: center">
        <el-button type="primary" :loading="submitLoading" @click="submitForm">保存订单</el-button>
        <el-button @click="handleReset">重 置</el-button>
      </div>
    </el-card>

    <goods-sku-select-dialog :visible.sync="skuDialogOpen" :selected-ids="selectedSkuIds" @confirm="handleSkuConfirm" />
  </div>
</template>

<script>
import { createManualOrder } from '@/api/orderImport'
import { listShop } from '@/api/shop/shop'
import GoodsSkuSelectDialog from './components/GoodsSkuSelectDialog'

export default {
  name: 'OrderCreate',
  components: { GoodsSkuSelectDialog },
  data() {
    return {
      shopList: [],
      skuDialogOpen: false,
      submitLoading: false,
      form: {
        shopId: null,
        orderNum: null,
        orderTime: null,
        payAmount: undefined,
        platformFee: undefined,
        remark: null,
        itemList: []
      },
      rules: {
        shopId: [{ required: true, message: '请选择店铺', trigger: 'change' }],
        orderNum: [{ required: true, message: '请填写订单号', trigger: 'blur' }],
        orderTime: [{ required: true, message: '请选择下单时间', trigger: 'change' }],
        payAmount: [{ required: true, message: '请填写实收金额', trigger: 'blur' }]
      }
    }
  },
  computed: {
    selectedSkuIds() {
      return this.form.itemList.map(it => it.skuId)
    }
  },
  created() {
    listShop().then(res => {
      this.shopList = res.rows || res.data || []
    }).catch(() => {})
  },
  methods: {
    handleSkuConfirm(rows) {
      const duplicates = []
      rows.forEach(sku => {
        const exists = this.form.itemList.some(it => String(it.skuId) === String(sku.id))
        if (exists) {
          duplicates.push(sku.goodsName)
          return
        }
        const price = Number(sku.retailPrice) || 0
        this.form.itemList.push({
          skuId: sku.id,
          goodsName: sku.goodsName,
          specName: sku.specName,
          specNum: sku.specNum,
          goodsNum: sku.goodsNum,
          colorImage: sku.colorImage,
          retailPrice: price,
          quantity: 1,
          itemAmount: this.round2(price)
        })
      })
      if (duplicates.length) {
        this.$modal.msgWarning('以下商品已在明细中：' + duplicates.join('、'))
      }
      this.calcPay()
    },
    removeItem(index) {
      this.form.itemList.splice(index, 1)
    },
    onQuantityChange(row) {
      if (row.retailPrice > 0) {
        row.itemAmount = this.round2(row.retailPrice * (row.quantity || 1))
      }
      this.calcPay()
    },
    calcPay() {
      const total = this.form.itemList.reduce((acc, it) => acc + (Number(it.itemAmount) || 0), 0)
      if (total > 0) {
        this.form.payAmount = this.round2(total)
      }
    },
    round2(value) {
      return Math.round((Number(value) || 0) * 100) / 100
    },
    formatMoney(value) {
      return value === null || value === undefined || value === '' ? '-' : Number(value).toFixed(2)
    },
    handleReset() {
      this.resetForm('orderForm')
      this.form.itemList = []
      this.form.payAmount = undefined
    },
    submitForm() {
      this.$refs['orderForm'].validate(valid => {
        if (!valid) return
        if (!this.form.itemList.length) {
          this.$modal.msgError('请至少添加一条商品明细')
          return
        }
        this.submitLoading = true
        createManualOrder({
          ...this.form,
          itemList: this.form.itemList.map(it => ({
            skuId: it.skuId,
            quantity: it.quantity,
            itemAmount: it.itemAmount
          }))
        }).then(() => {
          this.$modal.msgSuccess('建单成功，订单已进入待发货')
          this.handleReset()
        }).finally(() => {
          this.submitLoading = false
        })
      })
    }
  }
}
</script>
