## 数据表结构

###物料表(t_material)

<table>
	<tr>
		<th>物料编码</th>
		<th>物料名称</th>
		<th>规格型号</th>
		<th>单位</th>
		<th>货号</th>
		<th>大类</th>
		<th>中类</th>
		<th>小类</th>
		<th>库管员</th>	
	</tr>
	<tr>
		<td>mcode</td>
		<td>name</td>
		<td>model</td>
		<td>unit</td>
		<td>itemNo</td>
		<td>cateL1</td>
		<td>cateL2</td>
		<td>cateL3</td>
		<td>smanager</td>
	</tr>
</table>

"物料编码"具有唯一性

###分类表(t_material_category)

<table>
	<tr>
		<th>分类级别</th>
		<th>分类名称</th>
	</tr>
	<tr>
		<td>level</td>
		<td>name</td>
	</tr>
</table>

###库存表(t_storage)

<table>
	<tr>
		<th>物料编码</th>
		<th>导入日期</th>
		<th>库存总量</th>
		<th>材料库位</th>
		<th>维护库位</th>
		<th>返修库位</th>
		<th>次品库位</th>
		<th>i6受控库位</th>
	</tr>
	<tr>
		<td>mcode</td>
		<td>impDate</td>
		<td>total</td>
		<td>material</td>
		<td>maintenance</td>
		<td>repair</td>
		<td>inferior</td>
		<td>i6</td>
	</tr>
</table>

###出入库表(t_storage_inout)

<table>
	<tr>
		<th>物料编码</th>
		<th>导入日期</th>
		<th>单据号</th>
		<th>批号</th>
		<th>货号</th>
		<th>业务类型</th>
		<th>收数量</th>
		<th>发数量</th>
	</tr>
	<tr>
		<td>mcode</td>
		<td>impDate</td>
		<td>docNo</td>
		<td>batchNo</td>
		<td>itemNo</td>
		<td>businessType</td>
		<td>inCount</td>
		<td>outCount</td>
	</tr>
</table>

<i>业务类型</i>:

* 采购入库 +
* 半成品入库 +
* 研发出库 -
* 工程出库 -
* 生产补料 -
* 生产领料 -
* 维护出库 -
* 采购退货 -

###订单表(t_order)

<table>
	<tr>
		<th>订单编号</th>
		<th>物料编码</th>
		<th>订单数量</th>
		<th>订货日期</th>
		<th>预计到货日期</th>
		<th>系统到货日期</th>
		<th>平均到货日期</th>
		<th>供应商</th>
	</tr>
	<tr>
		<td>oid</td>
		<td>mcode</td>
		<td>orderNum</td>
		<td>orderDate</td>
		<td>inDateForesee</td>
		<td>inDateSystem</td>
		<td>inDateAverage</td>
		<td>provider</td>
	</tr>
</table>

"订单编号"具有唯一性

###订单执行表(t_order_execute)

<table>
	<tr>
		<th>订单编号</th>
		<th>到货日期</th>
		<th>到货数量</th>
		<th>未检查数量</th>
		<th>合格数量</th>
		<th>不合格数量</th>
		<th>入库日期</th>
		<th>入库数量</th>
	</tr>
	<tr>
		<td>oid</td>
		<td>arriveDate</td>
		<td>arriveNum</td>
		<td>uncheckNum</td>
		<td>okNum</td>
		<td>failNum</td>
		<td>inDate</td>
		<td>inNum</td>
	</tr>
</table>

到货数量 =  未检查数量 + 合格数量 + 不合格数量 + 入库数量

货物会经过以下三个状态:

* 到货(未检查)
* 检查合格(检查不合格)
* 入库

## 业务功能点

###导入数据

库存与出入库数据按日期导入, 一天的数据仅允许导入一次

###库存与出入库查询

* 根据物料代码, 名称, 规格, 单据号等指定字段进行数据过滤
* 按日期查询, 查询某一天库存(出入库)情况
* 按日期段查询(从某天到某天), 列出这些天的库存(出入库)情况, 给出数字对比与曲线图
* 库存查询, 提示出时间段内最大库存与最小库存
* 出入库查询, 出入库可信息可分开显示, 不同业务类型可分开显示

###每日数据核实

前一天库存 + 出入库记录 = 后一天库存

###订单查询

* 查询某一订单的完成情况, 几日到货, 分几次到货, 到货数量等
* 分析订单完成的效率, 比如某一个供货商订单都是在多少日内完成
	以时间段分档, 0-15, 16-30, 31-45, 46-60, 60+


