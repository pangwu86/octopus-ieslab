/**
 * Created by pw on 14-10-13.
 */

function ConBaseInfoCtl($scope) {


    var module = $('.module-contract-baseinfo');

    var agentJq = module.find('.conbaseinfo-agent');

    agentJq.zgrid({
        header: {
        },
        table: {
            pager: {
                pgsz: 10,
                pgnm: 1,
                pgcount: 0,
                total: 0
            },
            order: {
                by: 'name',
                asc: true
            },
            query: {
                url: "/ieslab/contract/baseinfo/agent/list",
                kwd: ""
            },
            columns: [
                {
                    "fieldName": "name",
                    "columnName": "业务员",
                    "show": true,
                    "width": 463
                }
            ]
        }
    });


    var providerJq = module.find('.conbaseinfo-provider');

    providerJq.zgrid({
        header: {
        },
        table: {
            pager: {
                pgsz: 10,
                pgnm: 1,
                pgcount: 0,
                total: 0
            },
            order: {
                by: 'shortName',
                asc: true
            },
            query: {
                url: "/ieslab/contract/baseinfo/provider/list",
                kwd: ""
            },
            columns: [
                {
                    "fieldName": "shortName",
                    "columnName": "供应商简称",
                    "show": true,
                    "width": 163
                },
                {
                    "fieldName": "fullName",
                    "columnName": "供应商全称",
                    "show": true,
                    "width": 400
                },
                {
                    "fieldName": "code",
                    "columnName": "业务编码",
                    "show": false,
                    "width": 200
                }
            ]
        }
    });

    var addtip = {
        'provider': "全称 简称\n\n支持多行输入",
        'agent': "业务员名称\n\n支持多行输入"
    };

    module.delegate('.conbaseinfo-btn', 'click', function () {
        var btn = $(this);
        var cate = btn.attr('cate');
        var isProvider = cate == 'provider';
        $.masker({
            title: btn.html(),
            width: "400",
            height: "60%",
            closeBtn: true,
            btns: [
                {
                    clz: 'btn-add-baseinfo',
                    label: "添加",
                    event: {
                        type: 'click',
                        handle: function (sele) {
                            var ta = sele.find('.conbaseinfo-add-form textarea');
                            var txt = ta.val();
                            if ($z.util.isBlank(txt)) {
                                alert('不能提交空内容!');
                                return;
                            }
                            $z.http.post('/ieslab/contract/baseinfo/' + cate + '/add', {
                                'txt': txt
                            }, function (re) {
                                $.masker('close');
                                if (isProvider) {
                                    providerJq.zgrid('refresh');
                                } else {
                                    agentJq.zgrid('refresh');
                                }
                            });
                        }
                    }
                }
            ],
            body: function () {
                var html = '';
                html += '<div class="conbaseinfo-add-form">'
                html += '   <textarea placeholder="' + addtip[cate] + '"></textarea>';
                html += '</div>'
                return html;
            }
        });
    });

}