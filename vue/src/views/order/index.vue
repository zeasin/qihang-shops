<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="订单编号" prop="orderNum">
        <el-input
          v-model="queryParams.orderNum"
          placeholder="请输入订单编号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="店铺" prop="shopId">
        <el-select v-model="queryParams.shopId" placeholder="请选择店铺" clearable @change="handleQuery">
         <el-option
            v-for="item in shopList"
            :key="item.id"
            :label="item.name"
            :value="item.id">
            <span style="float: left">{{ item.name }}</span>


              <span style="float: right; color: #8492a6; font-size: 13px"  v-if="item.type === 9">线下渠道</span>
              <span style="float: right; color: #8492a6; font-size: 13px"  v-if="item.type === 3">拼多多</span>
              <span style="float: right; color: #8492a6; font-size: 13px"  v-if="item.type === 5">微信小店</span>
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="下单时间" prop="orderTime">
        <el-date-picker clearable
                        v-model="orderTime" value-format="yyyy-MM-dd"
                        type="daterange"
                        range-separator="至"
                        start-placeholder="开始日期"
                        end-placeholder="结束日期">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="订单状态" prop="orderStatus">
        <el-select v-model="queryParams.orderStatus" placeholder="请选择状态" clearable @change="handleQuery">
          <el-option label="待发货" value="1" ></el-option>
          <el-option label="已发货" value="2"></el-option>
          <el-option label="已完成" value="3"> </el-option>
          <el-option label="已取消" value="11"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
<!--      <el-col :span="1.5">-->
<!--        <el-button-->
<!--          type="primary"-->
<!--          plain-->
<!--          icon="el-icon-refresh"-->
<!--          size="mini"-->
<!--          @click="handleCreate"-->
<!--        >手动创建订单</el-button>-->
<!--      </el-col>-->
<!--      <el-col :span="1.5">-->
<!--        <el-button-->
<!--          type="primary"-->
<!--          plain-->
<!--          icon="el-icon-refresh"-->
<!--          size="mini"-->
<!--          :disabled="multiple"-->
<!--          @click="handleShip"-->
<!--        >批量分配发货</el-button>-->
<!--      </el-col>-->
<!--      <el-col :span="1.5">-->
<!--        <el-button-->
<!--          type="warning"-->
<!--          plain-->
<!--          icon="el-icon-download"-->
<!--          size="mini"-->
<!--          @click="handleExport"-->
<!--          v-hasPermi="['shop:order:export']"-->
<!--        >导出</el-button>-->
<!--      </el-col>-->
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="orderList" @selection-change="handleSelectionChange">
      <el-table-column label="订单号" width="190">
        <template slot-scope="scope">
          <div class="order-num">
            <el-button size="mini" type="text" @click="handleDetail(scope.row)">{{ scope.row.orderNum }}</el-button>
            <i class="el-icon-copy-document tag-copy" :data-clipboard-text="scope.row.orderNum" @click="copyActiveCode($event, scope.row.orderNum)"></i>
          </div>
          <div class="order-shop">
            <el-tag size="mini" type="info">{{ shopName(scope.row) }}</el-tag>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="商品" min-width="360">
        <template slot-scope="scope">
          <div v-for="item in visibleItems(scope.row)" :key="item.id" class="goods-row">
            <el-image class="goods-img" :src="item.goodsImg" fit="cover"></el-image>
            <div class="goods-info">
              <div class="goods-title" :title="item.goodsTitle">{{ item.goodsTitle }}</div>
              <div class="goods-spec">
                <span>{{ getSkuValues(item.goodsSpec) }}</span>
                <span class="goods-qty">x{{ item.quantity }}</span>
              </div>
            </div>
          </div>
          <div v-if="itemCount(scope.row) > 3" class="goods-more">共 {{ itemCount(scope.row) }} 款商品</div>
        </template>
      </el-table-column>
      <el-table-column label="商品金额" width="110" align="right">
        <template slot-scope="scope">
          <span class="money-muted">{{ formatMoney(scope.row.goodsAmount) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="实收金额" width="115" align="right">
        <template slot-scope="scope">
          <span class="money-pay">{{ formatMoney(payAmount(scope.row)) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="快递费" width="100" align="right">
        <template slot-scope="scope">
          <span class="money-muted">{{ formatMoney(scope.row.expressFee) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="订单状态" width="130" align="center">
        <template slot-scope="scope">
          <el-tag size="small" :type="orderStatusMeta(scope.row.orderStatus).type">{{ orderStatusMeta(scope.row.orderStatus).text }}</el-tag>
          <div v-if="scope.row.refundStatus && scope.row.refundStatus !== 1" class="tag-line">
            <el-tag size="mini" type="danger">{{ refundStatusText(scope.row.refundStatus) }}</el-tag>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="发货状态" width="130" align="center">
        <template slot-scope="scope">
          <el-tag size="small" :type="shipStatusMeta(scope.row.shipStatus).type">{{ shipStatusMeta(scope.row.shipStatus).text }}</el-tag>
          <div v-if="scope.row.shipStatus > 0 && shipTypeText(scope.row.shipType)" class="tag-line">
            <el-tag size="mini" type="info">{{ shipTypeText(scope.row.shipType) }}</el-tag>
          </div>
          <div v-if="scope.row.waybillCode" class="waybill-line">
            <span class="waybill-code" :title="scope.row.waybillCode">{{ scope.row.waybillCode }}</span>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="收件人" width="190">
        <template slot-scope="scope">
          <template v-if="receiverText(scope.row)">
            <div class="receiver-name">{{ receiverText(scope.row) }}</div>
            <div class="receiver-addr" :title="fullAddress(scope.row)">{{ fullAddress(scope.row) }}</div>
          </template>
          <span v-else class="text-muted">-</span>
        </template>
      </el-table-column>
      <el-table-column label="下单时间" width="165" align="center">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.orderTime) || '-' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="备注" width="150" show-overflow-tooltip>
        <template slot-scope="scope">
          <span v-if="orderMemo(scope.row)" class="order-memo">{{ orderMemo(scope.row) }}</span>
          <span v-else class="text-muted">-</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="130" align="center" fixed="right" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-view"
            @click="handleDetail(scope.row)"
          >详情</el-button>
          <el-button
            v-if="scope.row.orderStatus === 1"
            size="mini"
            type="text"
            icon="el-icon-truck"
            @click="handleShip(scope.row)"
          >发货</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 订单详情对话框 -->
    <el-dialog :title="detailTitle" :visible.sync="detailOpen" width="1100px" append-to-body>

      <el-form ref="form" :model="form" :rules="rules" label-width="80px" inline>
        <el-descriptions title="订单信息">
            <el-descriptions-item label="ID">{{form.id}}</el-descriptions-item>
            <el-descriptions-item label="订单号">{{form.orderNum}}</el-descriptions-item>

            <el-descriptions-item label="店铺">
              {{ shopList.find(x=>x.id === form.shopId)?shopList.find(x=>x.id === form.shopId).name:'' }}
              <el-tag size="small" v-if="form.shopType === 5">视频号小店</el-tag>
            </el-descriptions-item>


            <el-descriptions-item label="标签">
              <el-tag size="small" v-if="form.tag ==='1' ">实售</el-tag>
              <el-tag size="small" v-if="form.tag ==='2' ">淘宝客</el-tag>
              <el-tag size="small" v-if="form.tag ==='3' ">刷单</el-tag>
              <el-tag size="small" v-if="form.tag ==='4' ">返现</el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="买家留言">
              {{form.buyerMemo}}
            </el-descriptions-item>
            <el-descriptions-item label="备注">
              {{form.remark}}
            </el-descriptions-item>
            <el-descriptions-item label="创建时间">
              {{ form.createTime }}
              <!-- <el-date-picker
              disabled
                v-model="form.orderCreateTime"
                type="datetime"
                value-format="yyyy-MM-dd HH:mm:ss"
                placeholder="请选择订单创建时间">
              </el-date-picker> -->
            </el-descriptions-item>
             <el-descriptions-item label="支付时间"> {{ form.payTime }}</el-descriptions-item>
             <el-descriptions-item label="最后更新时间"> {{ form.updateTime }}</el-descriptions-item>

            <el-descriptions-item label="订单状态">
              <el-tag v-if="form.orderStatus === 1" style="margin-bottom: 6px;">待发货</el-tag>
              <el-tag v-if="form.orderStatus === 2" style="margin-bottom: 6px;">已发货</el-tag>
              <el-tag v-if="form.orderStatus === 3" style="margin-bottom: 6px;">已完成</el-tag>
              <el-tag v-if="form.orderStatus === 11" style="margin-bottom: 6px;">订单取消</el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="退款状态">
              <el-tag v-if="form.refundStatus === 1">无售后或售后关闭</el-tag>
              <el-tag v-if="form.refundStatus === 2">售后处理中</el-tag>
               <el-tag v-if="form.refundStatus === 3">退款中</el-tag>
               <el-tag v-if="form.refundStatus === 4">退款成功</el-tag>
            </el-descriptions-item>

        </el-descriptions>
        <el-descriptions title="付款信息">
            <el-descriptions-item label="商品金额">{{formatMoney(form.goodsAmount)}}</el-descriptions-item>
            <el-descriptions-item label="快递费">{{formatMoney(form.expressFee)}}</el-descriptions-item>
            <el-descriptions-item label="平台扣费">{{formatMoney(form.platformFee)}}</el-descriptions-item>
            <el-descriptions-item label="实收金额">{{formatMoney(payAmount(form))}}</el-descriptions-item>
        </el-descriptions>


         <el-descriptions title="收货信息">
          <el-descriptions-item label="收件人姓名">{{form.receiverName}}</el-descriptions-item>
          <el-descriptions-item label="收件人手机号">{{form.receiverMobile || form.receiverPhone}}</el-descriptions-item>
          <el-descriptions-item label="省市区">{{form.province}}{{form.city}}{{form.town}}</el-descriptions-item>
          <el-descriptions-item label="详细地址">{{form.address}}</el-descriptions-item>
        </el-descriptions>
        <el-descriptions title="发货信息">
          <el-descriptions-item label="发货方式">
            <el-tag v-if="form.shipType === 0"  type="danger">自己发货</el-tag>
            <el-tag v-if="form.shipType === 1" type="danger">联合发货</el-tag>
            <el-tag v-if="form.shipType === 2" type="danger">供应商发货</el-tag>
          </el-descriptions-item>
            <el-descriptions-item label="快递单号">{{form.waybillCode || '-'}}</el-descriptions-item>
            <el-descriptions-item label="发货时间">{{form.shipTime}}</el-descriptions-item>
        </el-descriptions>

        <el-divider content-position="center">订单商品</el-divider>
        <el-table :data="form.itemList"  style="margin-bottom: 10px;">
          <!-- <el-table-column type="selection" width="50" align="center" /> -->
          <el-table-column label="序号" align="center" type="index" width="50"/>

          <el-table-column label="商品图片" prop="goodsImg" width="80">
            <template slot-scope="scope">
              <el-image style="width: 70px; height: 70px" :src="scope.row.goodsImg"></el-image>
            </template>
          </el-table-column>
          <el-table-column label="商品标题" prop="goodsTitle" ></el-table-column>
          <el-table-column label="SKU" prop="goodsSpec" width="150"></el-table-column>
          <el-table-column label="sku编码" prop="skuNum"></el-table-column>
          <el-table-column label="单价" prop="goodsPrice"></el-table-column>
          <el-table-column label="数量" prop="quantity"></el-table-column>
          <el-table-column label="成本价（发货快照）" width="140">
            <template slot-scope="scope">{{ formatAmount(scope.row.unitCost) }}</template>
          </el-table-column>
          <el-table-column label="成本金额" width="110">
            <template slot-scope="scope">{{ costAmount(scope.row) }}</template>
          </el-table-column>
        </el-table>
      </el-form>
      <!-- <div slot="footer" class="dialog-footer" v-if="isAudit">
        <el-button type="primary" @click="submitConfirmForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div> -->
    </el-dialog>
    <!-- 订单发货确认对话框 -->
    <el-dialog title="订单发货" :visible.sync="shipOpen" width="760px" append-to-body>
      <el-descriptions :column="2" border size="small">
        <el-descriptions-item label="订单号">{{ shipForm.orderNum }}</el-descriptions-item>
        <el-descriptions-item label="店铺">{{ shipForm.shopName }}</el-descriptions-item>
        <el-descriptions-item label="商品件数">{{ shipForm.totalQty }}</el-descriptions-item>
        <el-descriptions-item label="实收金额">{{ formatAmount(shipForm.payAmount) }}</el-descriptions-item>
      </el-descriptions>

      <el-table :data="shipForm.itemList" size="small" style="margin-top: 12px">
        <el-table-column label="商品" prop="goodsTitle" min-width="200" show-overflow-tooltip />
        <el-table-column label="规格" prop="goodsSpec" width="130" show-overflow-tooltip />
        <el-table-column label="数量" prop="quantity" width="70" align="center" />
        <el-table-column label="单价" width="90" align="right">
          <template slot-scope="scope">{{ formatAmount(scope.row.goodsPrice) }}</template>
        </el-table-column>
      </el-table>

      <el-alert type="info" :closable="false" show-icon style="margin-top: 12px"
        title="确认发货后按「商品成本档案」当前成本价写入本单明细快照，之后调整成本档案不影响本单利润" />

      <el-form label-width="180px" style="margin-top: 12px">
        <el-form-item label="发货成本（快递费）" required>
          <el-input-number v-model="shipForm.expressFee" :min="0" :precision="2" :controls="false" style="width: 200px" />
        </el-form-item>
        <el-form-item label="快递单号">
          <el-input v-model="shipForm.waybillCode" placeholder="选填，后续工单可按快递单号查单" clearable maxlength="55" style="width: 260px" />
        </el-form-item>
      </el-form>

      <div slot="footer" class="dialog-footer">
        <el-button @click="shipOpen = false">取 消</el-button>
        <el-button type="primary" :loading="shipLoading" @click="submitShip">确认发货</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {listOrder, getOrder, shipOrder} from "@/api/order/order";
import { listShop } from "@/api/shop/shop";
import Clipboard from "clipboard";

export default {
  name: "Order",
  data() {
    return {
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      // 子表选中数据
      checkedSShopOrderItem: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      orderTime: null,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 店铺订单表格数据
      orderList: [],
      // ${subTable.functionName}表格数据
      sShopOrderItemList: [],
      shopList:[],
      // 弹出层标题
      detailTitle:'订单详情',
      detailOpen:false,
      isAudit:false,
      // 发货弹窗
      shipOpen: false,
      shipLoading: false,
      shipForm: {
        id: null,
        orderNum: '',
        shopName: '',
        payAmount: null,
        itemList: [],
        totalQty: 0,
        expressFee: 0,
        waybillCode: ''
      },
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        orderNum: null,
        shopId: null,
        startTime:null,
        endTime:null
      },
      // 表单参数
      form: {
        orderIds:null,
        shipType:null,
        remark:null
      },
      // 表单校验
      rules: {
        orderIds: [{ required: true, message: '不能为空' }],
        shipType: [{ required: true, message: '请选择发货方式' }],
      }
    };
  },
  created() {
     listShop({}).then(response => {
        this.shopList = response.rows;
      });
    this.getList();
  },
  methods: {
    getSkuValues(spec){
      try {
        // 解析 JSON，返回一个数组
        const parsedSpec = JSON.parse(spec) || [];

        // 使用 map 提取所有 value，使用 join() 用逗号连接
        return parsedSpec.map(item => item.attr_value || item.value).join(', ') || '';
      } catch (error) {
        return spec; // 如果 JSON 解析出错，返回空字符串
      }
    },
    shopName(row) {
      const shop = this.shopList.find(x => x.id == row.shopId)
      return shop ? shop.name : ''
    },
    visibleItems(row) {
      return (row.itemList || []).slice(0, 3)
    },
    itemCount(row) {
      return (row.itemList || []).length
    },
    formatMoney(value) {
      if (value === null || value === undefined || value === '') return '-'
      return '￥' + Number(value).toFixed(2).replace(/\d(?=(\d{3})+\.)/g, '$&,')
    },
    payAmount(row) {
      return row.payAmount !== null && row.payAmount !== undefined ? row.payAmount : row.orderAmount
    },
    orderStatusMeta(status) {
      const map = {
        0: { text: '待选择发货方式', type: 'warning' },
        1: { text: '待发货', type: 'warning' },
        2: { text: '已发货', type: '' },
        3: { text: '已完成', type: 'success' },
        4: { text: '已完成', type: 'success' },
        11: { text: '订单取消', type: 'info' }
      }
      return map[status] || { text: '未知', type: 'info' }
    },
    refundStatusText(status) {
      return { 2: '售后处理中', 3: '退款中', 4: '退款成功' }[status] || ''
    },
    shipStatusMeta(status) {
      const map = {
        0: { text: '待发货', type: 'warning' },
        1: { text: '部分发货', type: 'warning' },
        2: { text: '全部发货', type: 'success' }
      }
      return map[status] || { text: '未知', type: 'info' }
    },
    shipTypeText(type) {
      return { 0: '自己发货', 1: '联合发货', 2: '供应商发货' }[type] || ''
    },
    fullAddress(row) {
      return [row.province, row.city, row.town, row.address].filter(Boolean).join(' ')
    },
    receiverText(row) {
      return [row.receiverName, row.receiverMobile || row.receiverPhone].filter(Boolean).join(' ')
    },
    orderMemo(row) {
      return row.remark || row.buyerMemo || row.sellerMemo || ''
    },
    copyActiveCode(event,queryParams) {
      console.log(queryParams)
      const clipboard = new Clipboard(".tag-copy")
      clipboard.on('success', e => {
        this.$message({ type: 'success', message: '复制成功' })
        // 释放内存
        clipboard.destroy()
      })
      clipboard.on('error', e => {
        // 不支持复制
        this.$message({ type: 'waning', message: '该浏览器不支持自动复制' })
        // 释放内存
        clipboard.destroy()
      })
    },

    /** 查询店铺订单列表 */
    getList() {
      this.loading = true;
      if(this.orderTime){
        this.queryParams.startTime = this.orderTime[0]
        this.queryParams.endTime = this.orderTime[1]
      }else {
        this.queryParams.startTime = null
        this.queryParams.endTime = null
      }
      listOrder(this.queryParams).then(response => {
        this.orderList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm("queryForm");
      this.handleQuery();
    },
    isRowSelectable(row, index) {
      return row.orderStatus === 1 ;
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.id)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    reset(){

    },
    /** 详情按钮操作 */
    handleDetail(row) {
      this.reset();
      const id = row.id || this.ids
      getOrder(id).then(response => {
        this.form = response.data;
        // this.$nextTick(()=>{
        //   this.form.shipType = response.data.shipType
        // })
        this.detailOpen = true;
        this.detailTitle = "订单详情";
      });
      this.isAudit = false
    },
    handleCreate(){
      this.$router.push("/order/create")
    },
    formatAmount(value) {
      return value === null || value === undefined || value === '' ? '-' : Number(value).toFixed(2)
    },
    costAmount(row) {
      if (row.unitCost === null || row.unitCost === undefined) return '-'
      return (Number(row.unitCost) * (row.quantity || 0)).toFixed(2)
    },
    /** 打开发货弹窗 */
    handleShip(row) {
      const items = row.itemList || []
      const shop = this.shopList.find(x => x.id == row.shopId)
      this.shipForm = {
        id: row.id,
        orderNum: row.orderNum,
        shopName: shop ? shop.name : '',
        payAmount: row.payAmount !== null && row.payAmount !== undefined ? row.payAmount : row.orderAmount,
        itemList: items,
        totalQty: items.reduce((sum, it) => sum + (it.quantity || 0), 0),
        expressFee: 0,
        waybillCode: ''
      }
      this.shipOpen = true
    },
    /** 确认发货 */
    submitShip() {
      if (this.shipForm.expressFee === null || this.shipForm.expressFee === undefined) {
        this.$modal.msgError('请填写发货成本（快递费）')
        return
      }
      this.shipLoading = true
      const waybillCode = (this.shipForm.waybillCode || '').trim()
      shipOrder({ ids: [this.shipForm.id], expressFee: this.shipForm.expressFee, waybillCode: waybillCode || null }).then(() => {
        this.$modal.msgSuccess('发货成功，已记录商品成本快照')
        this.shipOpen = false
        this.getList()
      }).finally(() => {
        this.shipLoading = false
      })
    }
  }
};
</script>

<style scoped>
.order-num {
  display: flex;
  align-items: center;
}
.order-num ::v-deep .el-button {
  padding: 0;
  line-height: 20px;
}
.tag-copy {
  margin-left: 4px;
  cursor: pointer;
  color: #909399;
}
.order-shop {
  margin-top: 4px;
}
.goods-row {
  display: flex;
  align-items: center;
}
.goods-row + .goods-row {
  margin-top: 8px;
}
.goods-img {
  width: 44px;
  height: 44px;
  border-radius: 4px;
  flex: none;
}
.goods-info {
  margin-left: 8px;
  min-width: 0;
}
.goods-title {
  font-size: 13px;
  line-height: 18px;
  color: #303133;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.goods-spec {
  font-size: 12px;
  line-height: 18px;
  color: #909399;
}
.goods-qty {
  margin-left: 6px;
  color: #606266;
}
.goods-more {
  margin-top: 4px;
  font-size: 12px;
  color: #909399;
}
.money-pay {
  color: #f56c6c;
  font-weight: 600;
}
.money-muted {
  color: #606266;
}
.tag-line {
  margin-top: 4px;
}
.receiver-name {
  font-size: 13px;
  color: #303133;
}
.receiver-addr {
  font-size: 12px;
  color: #909399;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.order-memo {
  font-size: 12px;
  color: #ed5565;
}
.waybill-line {
  margin-top: 4px;
}
.waybill-code {
  display: inline-block;
  max-width: 100%;
  font-size: 12px;
  color: #606266;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  vertical-align: bottom;
}
.text-muted {
  color: #c0c4cc;
}
</style>
