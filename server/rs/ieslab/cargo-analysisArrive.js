/**
 * Created by pw on 14-10-13.
 */

function CargoAnalysisArriveCtl() {
    var module = $('.module-cargo-analysisArrive');

    var anaBtn = module.find('.btn-analysis');

    module.delegate('.btn-analysis', 'click', function () {
        $z.http.get("/ieslab/cargo/analysis/doing", function (re) {
            if (re.data) {
                alert("其他用户正在使用该功能, 请稍后...")
            } else {
                anaBtn.html("工作中...");
                // 上传
                module.find('.progress li.n1').addClass('ing');
                $.upload({
                    title: "上传到货日期文件",
                    width: 350,
                    height: 250,
                    upload: {
                        num: 1,
                        type: ['xls'],
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
                            xhr.open("POST", "/ieslab/cargo/analysis/upload", true);
                            xhr.setRequestHeader('Content-type', contentType);
                            xhr.setRequestHeader("fnm", "" + encodeURI(file.name));
                            xhr.send(file);
                        },
                        finishUpload: function () {
                            module.find('.progress li.n1').addClass('done');
                            $.masker('close');
                            step2();
                        }
                    }
                });
            }
        });
    })

    function step2() {
        module.find('.progress li.n2').addClass('ing');
        $z.http.get('/ieslab/cargo/analysis/todb', function (re) {
            module.find('.progress li.n2').addClass('done');
            step3();
        });
    }

    function step3() {
        module.find('.progress li.n3').addClass('done');
        anaBtn.html("完成");
        window.location.href = "/ieslab/cargo/analysis/download"
        setTimeout(function () {
            anaBtn.html("开始");
            module.find('.progress li').removeClass('done').removeClass('ing');
        }, 1500);
    }
}