<template>
  <div class="app-container">
    <el-card shadow="never" style="margin-bottom: 14px">
      <el-steps :active="step" finish-status="success" align-center>
        <el-step title="上传文件" description="选平台和店铺，上传订单 Excel"></el-step>
        <el-step title="表头映射" description="平台表头 → 系统字段"></el-step>
        <el-step title="预览确认" description="查重、匹配成本"></el-step>
        <el-step title="导入结果"></el-step>
      </el-steps>
    </el-card>

    <!-- 第一步：上传 -->
    <el-card v-show="step === 0" shadow="never" v-loading="parseLoading">
      <el-form :inline="true" label-width="70px">
        <el-form-item label="平台">
          <el-select v-model="platform" placeholder="选择平台" style="width: 160px">
            <el-option v-for="p in platformOptions" :key="p.value" :label="p.label" :value="p.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="店铺" required>
          <el-select v-model="shopId" placeholder="选择店铺" filterable style="width: 200px">
            <el-option v-for="s in shopList" :key="s.id" :label="s.name" :value="s.id" />
          </el-select>
        </el-form-item>
      </el-form>
      <el-upload drag accept=".xlsx,.xls" :limit="1" :auto-upload="false"
        :on-change="handleFileChange" :on-remove="handleFileRemove" :file-list="fileList">
        <i class="el-icon-upload"></i>
        <div class="el-upload__text">将订单 Excel 拖到此处，或<em>点击选择文件</em></div>
        <div class="el-upload__tip" slot="tip">支持淘宝/天猫、拼多多、抖店导出的订单表，上传后自动识别表头</div>
      </el-upload>
      <div v-if="headers.length" style="margin-top: 12px">
        <el-alert type="success" :closable="false" show-icon
          :title="`识别成功：共 ${rowCount} 行数据，${headers.length} 列`" />
        <div style="margin-top: 14px; text-align: center">
          <el-button type="primary" @click="step = 1" :disabled="!shopId">下一步：配置表头映射</el-button>
        </div>
      </div>
    </el-card>

    <!-- 第二步：映射 -->
    <el-card v-show="step === 1" shadow="never">
      <div class="mapping-toolbar">
        <span class="tpl-label">映射模板：</span>
        <el-select v-model="selectedTemplateId" placeholder="选择已有模板一键套用" clearable size="small" style="width: 220px" @change="applyTemplate">
          <el-option v-for="t in templates" :key="t.id" :label="t.templateName" :value="t.id" />
        </el-select>
        <el-button size="small" type="primary" plain icon="el-icon-folder-checked" :disabled="!mappingValid" @click="saveTemplate">保存当前映射为模板</el-button>
        <el-button size="small" type="danger" plain icon="el-icon-delete" :disabled="!selectedTemplateId" @click="deleteTemplate">删除该模板</el-button>
      </div>
      <el-table :data="mappingRows" size="small">
        <el-table-column label="系统字段" width="160">
          <template slot-scope="scope">
            {{ scope.row.label }}
            <el-tag v-if="scope.row.required" type="danger" size="mini">必填</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="对应 Excel 列" min-width="280">
          <template slot-scope="scope">
            <el-select v-model="mapping[scope.row.field]" placeholder="选择列" clearable size="small" style="width: 100%">
              <el-option v-for="(h, i) in headers" :key="i" :label="`${i + 1}. ${h || '（未命名列）'}`" :value="i" />
            </el-select>
          </template>
        </el-table-column>
        <el-table-column label="说明" min-width="220">
          <template slot-scope="scope">
            <span class="hint">{{ scope.row.hint }}</span>
          </template>
        </el-table-column>
      </el-table>
      <div style="margin-top: 14px; text-align: center">
        <el-button @click="step = 0">上一步</el-button>
        <el-button type="primary" :disabled="!mappingValid" :loading="previewLoading" @click="doPreview">下一步：预览确认</el-button>
      </div>
    </el-card>

    <!-- 第三步：预览 -->
    <el-card v-show="step === 2" shadow="never">
      <el-row :gutter="12" style="margin-bottom: 12px">
        <el-col :span="6"><div class="stat-box"><div class="stat-num">{{ previewData.totalRows }}</div><div class="stat-label">总行数</div></div></el-col>
        <el-col :span="6"><div class="stat-box"><div class="stat-num warn">{{ previewData.dupRows || 0 }}</div><div class="stat-label">重复订单号（跳过）</div></div></el-col>
        <el-col :span="6"><div class="stat-box"><div class="stat-num warn">{{ previewData.unmatchedSku || 0 }}</div><div class="stat-label">未匹配 SKU 行</div></div></el-col>
        <el-col :span="6"><div class="stat-box"><div class="stat-num info">{{ previewData.autoCreateEstimate || 0 }}</div><div class="stat-label">将自动建档</div></div></el-col>
      </el-row>
      <el-alert type="warning" :closable="false" show-icon style="margin-bottom: 10px"
        title="重复订单号将跳过；未匹配 SKU 自动建档（成本价留空），导入后请到「商品成本」补录" />
      <el-table :data="previewData.previewRows || []" size="small" max-height="420">
        <el-table-column label="行" prop="row" width="55" align="center" />
        <el-table-column label="订单号" prop="orderNum" min-width="150" show-overflow-tooltip />
        <el-table-column label="下单时间" prop="orderTime" width="150" align="center" />
        <el-table-column label="商品" prop="goodsTitle" min-width="160" show-overflow-tooltip />
        <el-table-column label="SKU" prop="skuNum" min-width="110" show-overflow-tooltip />
        <el-table-column label="数量" prop="quantity" width="60" align="center" />
        <el-table-column label="实收" prop="payAmount" width="85" align="center" />
        <el-table-column label="匹配SKU" prop="skuName" min-width="140" show-overflow-tooltip>
          <template slot-scope="scope">
            <span v-if="scope.row.matched">{{ scope.row.skuName }}</span>
            <el-tag v-else type="warning" size="mini">将自动建档</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="90" align="center">
          <template slot-scope="scope">
            <el-tag v-if="scope.row.dup" type="info" size="mini">重复跳过</el-tag>
            <el-tag v-else type="success" size="mini">将导入</el-tag>
          </template>
        </el-table-column>
      </el-table>
      <div style="margin-top: 14px; text-align: center">
        <el-button @click="step = 1">上一步</el-button>
        <el-button type="primary" :loading="commitLoading" @click="doCommit">确认导入</el-button>
      </div>
    </el-card>

    <!-- 第四步：结果 -->
    <el-card v-show="step === 3" shadow="never">
      <el-result icon="success" title="导入完成">
        <template slot="extra">
          <div class="result-grid">
            <div class="result-item"><div class="num ok">{{ result.success || 0 }}</div><div>导入订单</div></div>
            <div class="result-item"><div class="num">{{ result.skipped || 0 }}</div><div>重复跳过</div></div>
            <div class="result-item"><div class="num info">{{ result.autoCreated || 0 }}</div><div>自动建档</div></div>
            <div class="result-item"><div class="num warn">{{ result.unmatchedCost || 0 }}</div><div>未匹配成本明细</div></div>
          </div>
          <div style="margin-top: 16px">
            <el-button type="primary" plain @click="goGoodsCost">去补商品成本</el-button>
            <el-button plain @click="restart">继续导入</el-button>
            <el-button type="success" plain @click="$router.push('/profit/shop')">看店铺利润</el-button>
          </div>
        </template>
      </el-result>
    </el-card>
  </div>
</template>

<script>
import { parseOrderExcel, previewOrderImport, commitOrderImport, listImportTemplate, saveImportTemplate, delImportTemplate } from '@/api/orderImport'
import { listShop } from '@/api/shop/shop'

// 表头关键词自动建议
const FIELD_HINTS = {
  orderNum: ['订单号', '订单编号', '单号', '订单id'],
  orderTime: ['下单时间', '付款时间', '订单时间', '创建时间', '支付时间'],
  payAmount: ['实收', '实际支付', '买家实付', '支付金额', '应付金额'],
  expressFee: ['快递', '运费', '邮费', '物流费'],
  platformFee: ['平台扣费', '佣金', '扣点', '平台费用', '技术服务费'],
  goodsTitle: ['商品标题', '商品名称', '宝贝标题', '品名'],
  skuNum: ['规格编码', 'sku编码', '商家编码', '规格编号', '货号', '规格'],
  quantity: ['数量', '购买数量', '件数'],
  itemAmount: ['商品金额', '小计', '金额', '实付'],
  goodsPrice: ['单价', '商品单价']
}

export default {
  name: 'OrderImport',
  data() {
    return {
      step: 0,
      platform: 1,
      shopId: null,
      shopList: [],
      platformOptions: [
        { label: '淘宝/天猫', value: 1 },
        { label: '京东', value: 2 },
        { label: '拼多多', value: 3 },
        { label: '抖店', value: 4 },
        { label: '微信小店', value: 5 },
        { label: '其他渠道', value: 9 }
      ],
      file: null,
      fileList: [],
      parseLoading: false,
      previewLoading: false,
      commitLoading: false,
      headers: [],
      rowCount: 0,
      mapping: {},
      mappingRows: [
        { field: 'orderNum', label: '订单号', required: true, hint: '平台订单号，用于查重与账单匹配' },
        { field: 'orderTime', label: '下单时间', required: false, hint: '用于按月统计利润' },
        { field: 'payAmount', label: '实收金额', required: false, hint: '订单级金额取第一行' },
        { field: 'expressFee', label: '快递费', required: false, hint: '订单级取第一行' },
        { field: 'platformFee', label: '平台扣费(参考)', required: false, hint: '回退参考；优先用对账单' },
        { field: 'goodsTitle', label: '商品名称', required: false, hint: '自动建档时的商品名' },
        { field: 'skuNum', label: '规格编码/SKU', required: false, hint: '匹配成本价的关键字段' },
        { field: 'quantity', label: '数量', required: false, hint: '默认 1' },
        { field: 'itemAmount', label: '商品金额', required: false, hint: '链接利润按其占比分摊' },
        { field: 'goodsPrice', label: '商品单价', required: false, hint: '选填' }
      ],
      templates: [],
      selectedTemplateId: null,
      previewData: {},
      result: {}
    }
  },
  computed: {
    mappingValid() {
      return this.mapping.orderNum !== null && this.mapping.orderNum !== undefined
    }
  },
  created() {
    this.getShops()
  },
  methods: {
    getShops() {
      listShop().then(res => {
        this.shopList = res.rows || res.data || []
      }).catch(() => {})
    },
    handleFileChange(file) {
      if (!file || !file.raw) return
      this.file = file.raw
      this.parseFile()
    },
    handleFileRemove() {
      this.file = null
      this.fileList = []
      this.headers = []
      this.rowCount = 0
      this.mapping = {}
    },
    parseFile() {
      if (!this.file) return
      this.parseLoading = true
      parseOrderExcel(this.file).then(res => {
        const d = res.data || {}
        this.headers = d.headers || []
        this.rowCount = d.rowCount || 0
        this.autoMap()
        this.loadTemplates()
      }).finally(() => {
        this.parseLoading = false
      })
    },
    autoMap() {
      const mapping = {}
      const used = new Set()
      Object.keys(FIELD_HINTS).forEach(field => {
        const hints = FIELD_HINTS[field]
        for (let i = 0; i < this.headers.length; i++) {
          if (used.has(i)) continue
          const h = (this.headers[i] || '').toLowerCase()
          if (h && hints.some(k => h.indexOf(k.toLowerCase()) > -1)) {
            mapping[field] = i
            used.add(i)
            break
          }
        }
      })
      this.mapping = mapping
    },
    // ==================== 模板 ====================
    loadTemplates() {
      listImportTemplate(this.platform).then(res => {
        this.templates = res.data || []
      }).catch(() => {})
    },
    applyTemplate(id) {
      if (!id) return
      const t = this.templates.find(x => x.id === id)
      if (t && t.mappingJson) {
        try {
          const m = JSON.parse(t.mappingJson)
          const fixed = {}
          Object.keys(m).forEach(k => { fixed[k] = Number(m[k]) })
          this.mapping = fixed
        } catch (e) {
          this.$modal.msgError('模板映射格式错误')
        }
      }
    },
    saveTemplate() {
      this.$prompt('请输入模板名称', '保存映射模板', {
        confirmButtonText: '保存',
        cancelButtonText: '取消',
        inputValue: this.platformLabel() + '导入模板'
      }).then(({ value }) => {
        return saveImportTemplate({
          platform: this.platform,
          templateName: value,
          mappingJson: JSON.stringify(this.mapping)
        })
      }).then(() => {
        this.$modal.msgSuccess('模板已保存')
        this.loadTemplates()
      }).catch(() => {})
    },
    deleteTemplate() {
      const t = this.templates.find(x => x.id === this.selectedTemplateId)
      if (!t) return
      this.$modal.confirm('确认删除模板「' + t.templateName + '」吗？').then(() => {
        return delImportTemplate(t.id)
      }).then(() => {
        this.selectedTemplateId = null
        this.loadTemplates()
        this.$modal.msgSuccess('删除成功')
      }).catch(() => {})
    },
    platformLabel() {
      const p = this.platformOptions.find(x => x.value === this.platform)
      return p ? p.label : '订单'
    },
    // ==================== 预览/提交 ====================
    doPreview() {
      if (!this.shopId) {
        this.$modal.msgError('请先选择店铺')
        return
      }
      this.previewLoading = true
      previewOrderImport(this.file, this.shopId, this.mapping).then(res => {
        this.previewData = res.data || {}
        this.step = 2
      }).finally(() => {
        this.previewLoading = false
      })
    },
    doCommit() {
      this.$modal.confirm('确认导入吗？重复订单号将自动跳过').then(() => {
        this.commitLoading = true
        return commitOrderImport(this.file, this.shopId, this.mapping)
      }).then(res => {
        this.result = res.data || {}
        this.step = 3
      }).catch(() => {}).finally(() => {
        this.commitLoading = false
      })
    },
    restart() {
      this.step = 0
      this.file = null
      this.fileList = []
      this.headers = []
      this.rowCount = 0
      this.mapping = {}
      this.previewData = {}
      this.result = {}
    },
    goGoodsCost() {
      this.$router.push('/goodsCost/profile').catch(() => {})
    }
  }
}
</script>

<style scoped>
.mapping-toolbar {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 12px;
}
.tpl-label {
  font-size: 13px;
  color: #606266;
}
.hint {
  color: #909399;
  font-size: 12px;
}
.stat-box {
  background: #f5f7fa;
  border-radius: 6px;
  text-align: center;
  padding: 12px 0;
}
.stat-num {
  font-size: 22px;
  font-weight: 700;
  color: #303133;
}
.stat-num.warn {
  color: #e6a23c;
}
.stat-num.info {
  color: #409eff;
}
.stat-label {
  font-size: 12px;
  color: #909399;
  margin-top: 2px;
}
.result-grid {
  display: flex;
  justify-content: center;
  gap: 40px;
  margin-bottom: 8px;
}
.result-item {
  text-align: center;
  color: #606266;
  font-size: 13px;
}
.result-item .num {
  font-size: 26px;
  font-weight: 700;
  color: #303133;
}
.result-item .num.ok {
  color: #67c23a;
}
.result-item .num.warn {
  color: #e6a23c;
}
.result-item .num.info {
  color: #409eff;
}
</style>
