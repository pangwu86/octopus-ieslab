/**
 * Created by pw on 14-10-13.
 */


function addFontColor(str, color) {
    return '<font style="color:' + color + '">' + str + '</font>';
};

function ConManageCtl($scope) {

    var module = $('.module-contract-manage');
    var orderListJq = module.find('.conmanage-orderlist');
    var orderViewJq = module.find('.conmanage-fileview');

    var dwnumJq = module.find('.conmanage-download span b');

    var dwlist = {};
    var dwnu = 0;

    function updateDwNum() {
        dwnumJq.html(dwnu);
    }

    orderListJq.zgrid({
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
                by: 'createTime',
                asc: false
            },
            query: {
                url: "/ieslab/contract/con/list",
                kwd: ""
            },
            row: {
                hover: {
                    render: function (rowData) {
                        var html = "";
                        html += '<i class="fa fa-check-square-o fa-1x ' + (dwlist[rowData.orderNo] ? "chked" : "unchk") + '" order="' + rowData.orderNo + '" ></i>';
                        return html;
                    },
                    click: function (rowData, rhJq) {
                        var i = rhJq.find('i.fa');
                        if (i.hasClass('chked')) {
                            i.addClass('unchk');
                            i.removeClass('chked');
                            dwlist[rowData.orderNo] = false;
                            dwnu--;
                            updateDwNum();
                        } else {
                            i.addClass('chked');
                            i.removeClass('unchk');
                            dwlist[rowData.orderNo] = true;
                            dwnu++;
                            updateDwNum();
                        }
                    }
                }
            },
            columnsRender: {
                'orderNo': function (rowData) {
                    return '<span class="contract-orderNo" order="' + rowData.orderNo + '"  dirId="' + rowData.dirId + '">' + rowData.orderNo + '</>';
                },
                'pShortName': function (rowData) {
                    return addFontColor(rowData.pShortName, "#00f");
                }
            },
            columns: [
                {
                    "fieldName": "orderNo",
                    "columnName": "合同号",
                    "show": true,
                    "width": 120
                },
                {
                    "fieldName": "pFullName",
                    "columnName": "供应商全称",
                    "show": true,
                    "width": 200
                },
                {
                    "fieldName": "pShortName",
                    "columnName": "供应商简称",
                    "show": true,
                    "width": 100
                },
                {
                    "fieldName": "agent",
                    "columnName": "业务员",
                    "show": true,
                    "width": 80
                },
                {
                    "fieldName": "createTime",
                    "columnName": "上传时间",
                    "show": true,
                    "width": 150
                },
                {
                    "fieldName": "fileNum",
                    "columnName": "文件数",
                    "show": true,
                    "width": 50
                }
            ]
        }
    });

    var viewOpt = {
        root: {
            module: 'ies-contract',
            moduleKey: ""
        },
        multisel: false,
        mode: 'read',
        view: 'list'
    };

    orderViewJq.netdisk(viewOpt);

    module.delegate('.contract-orderNo', 'click', function (e) {
        var order = $(this);
        orderViewJq.netdisk('reload', 'ies-contract', order.attr('order'), order.attr('dirId'), order.attr('order'));
    });

    var isDownloading = false;


    module.delegate('.conmanage-download', 'click', function (e) {
        var order = $(this);
        if (!isDownloading) {
            var dlist = [];
            for (var k in dwlist) {
                if (dwlist[k]) {
                    dlist.push(k);
                }
            }
            if (dlist.length == 0) {
                alert('亲, 没有选中任何合同呀')
                return;
            }
            // 给出提示
            isDownloading = true;
            order.find('en').html('正在拼命打包中....');
            $z.http.post('/ieslab/contract/con/package/', {
                'olist': dlist.join(',')
            }, function (re) {
                if (re.data) {
                    var fmd5 = re.data;
                    window.location.href = "/ieslab/contract/con/download/" + fmd5;
                    isDownloading = false;
                    order.find('en').html('打包下载');
                }
            });
        }
    });

    module.delegate('.conmanage-upload', 'click', function () {
        var btn = $(this);
        $.upload({
            title: "上传合同",
            width: "90%",
            height: "80%",
            upload: {
                multi: true,
                type: ['jpg', 'jpeg', 'png', 'pdf'],
                afterDrop: function (file, upJq) {

                },
                doUpload: function (file, upJq, progress, callback) {
                    var xhr = new XMLHttpRequest();
                    if (!xhr.upload) {
                        alert("XMLHttpRequest object don't support upload for your browser!!!");
                        return;
                    }
                    xhr.upload.addEventListener("progress", function (e) {
                        progress(e);
                    }, false);
                    xhr.onreadystatechange = function (e) {
                        if (xhr.readyState == 4) {
                            if (xhr.status == 200) {
                                callback();
                            } else {
                                alret('Fail to upload "' + file.name + '"\n\n' + xhr.responseText);
                            }
                        }
                    };
                    // 准备请求对象头部信息
                    var contentType = "application/x-www-form-urlencoded; charset=utf-8";
                    xhr.open("POST", "/ieslab/contract/con/upload", true);
                    xhr.setRequestHeader('Content-type', contentType);
                    xhr.setRequestHeader('module', 'ies-contract');
                    xhr.setRequestHeader("fnm", "" + encodeURI(file.name));
                    xhr.send(file);
                },
                finishUpload: function () {
                    $.masker('close');
                    orderListJq.zgrid('refresh');
                }
            }
        });
    });


}