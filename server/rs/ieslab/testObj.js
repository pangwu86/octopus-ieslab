function TestObjCtl($scope) {
	var module = $('.module-content');
	var gridJq = module.find('.testObj-table');

	gridJq.zgrid({
		header : {},
		table : {
			pager : {
				pgsz : 20,
				pgnm : 1,
				pgcount : 0,
				total : 0
			},
			order : {
				by : '',
				asc : true
			},
			query : {
				url : "/ieslab/test/query",
				kwd : ""
			},
			columns : [ {
				"fieldName" : 'name',
				"columnName" : '姓名',
				"show" : true,
				"width" : 400
			}, {
				"fieldName" : 'age',
				"columnName" : '年龄',
				"show" : true,
				"width" : 100
			}, {
				"fieldName" : 'createTime',
				"columnName" : '创建日期',
				"show" : true,
				"width" : 400
			} ]
		}
	});
}