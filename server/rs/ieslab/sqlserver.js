/**
 * Created by pw on 14/10/22.
 */

function addFontColor(str, color) {
    return '<font style="color:' + color + '">' + str + '</font>';
};

function SSCtl($scope) {
    var module = $('.module-sqlserver');
    var tbJq = module.find('.sqlserver-table');

    var tbConf = {
        header: {
        },
        table: {
            pager: {
                pgsz: 20,
                pgnm: 1,
                pgcount: 0,
                total: 0
            },
            order: {
                by: 'dbName',
                asc: true
            },
            query: {
                url: "/ieslab/sqlserver/table/list",
                kwd: ""
            },
            columnsRender: {
                'status': function (rowData) {
                    if (rowData.status == 0) {
                        return addFontColor("正常", "#080")
                    }
                    if (rowData.status == 1) {
                        return addFontColor("导入中", "#00f")
                    }
                    if (rowData.status == 2) {
                        return addFontColor("错误", "#f00")
                    }
                },
                'remark': function (rowData) {
                    if (rowData.status == 0) {
                        return '<span class="ss-remark" db="' + rowData.dbName + '" tb="' + rowData.tableName + '">' + rowData.remark + '</span>';
                    }
                    return rowData.dbName;
                }
            },
            columns: [
                {
                    "fieldName": "status",
                    "columnName": "当前状态",
                    "show": true,
                    "width": 80
                },
                {
                    "fieldName": "remark",
                    "columnName": "说明",
                    "show": true,
                    "width": 150
                },
                {
                    "fieldName": "dbName",
                    "columnName": "数据库",
                    "show": true,
                    "width": 150
                },
                {
                    "fieldName": "tableName",
                    "columnName": "表(视图)",
                    "show": true,
                    "width": 150
                },
                {
                    "fieldName": "dnum",
                    "columnName": "ss数据量",
                    "show": true,
                    "width": 80
                },
                {
                    "fieldName": "inum",
                    "columnName": "导入数据量",
                    "show": true,
                    "width": 80
                },
                {
                    "fieldName": "errnum",
                    "columnName": "错误数据量",
                    "show": true,
                    "width": 80
                },
                {
                    "fieldName": "updateTime",
                    "columnName": "最后更新时间",
                    "show": true,
                    "width": 250
                },
                {
                    "fieldName": "useTime",
                    "columnName": "更新耗时",
                    "show": true,
                    "width": 150
                }
            ]
        }
    };

    tbJq.zgrid(tbConf);

    tbJq.delegate('.ss-remark', 'click', function () {
        var ss = $(this);
        var db = ss.attr('db');
        var tb = ss.attr('tb');
        $z.http.post('/ieslab/sqlserver/table/update', {
            'tableName': tb,
            'dbName': db
        }, function (re) {
            if (re.data) {
                alert(re.data);
            }
            tbJq.zgrid('refresh');
        });
    });
}