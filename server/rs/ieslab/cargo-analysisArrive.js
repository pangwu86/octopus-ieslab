/**
 * Created by pw on 14-10-13.
 */

function CargoAnalysisArriveCtl() {
    var module = $('.module-cargo-analysisArrive');

    var start = false;

    module.delegate('.btn-analysis', 'click', function () {
        if (start) {
            alert("其他用户用在分析, 请稍后再使用该功能")
            return;
        }

        // 上传
        $.upload({
            title: "上传到货日期文件",
            width: "50%",
            height: "50%",
            upload: {
                multi: false,
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
                    $.masker('close');
                    step2();
                }
            }
        });
    })


    var log = module.find('.log');
    var logTmp = '';
    var logNum = 0;
    var logTmpNum = 0;

    function step2() {
        $z.http.comet({
            url: '/ieslab/cargo/analysis/todb',
            onChange: function (respTxt, opt) {
                if (respTxt == '') {
                    return;
                }
                if (logNum > 1000) {
                    log.empty();
                    logNum = 0;
                }
                logTmp += '<div>' + currentTime() + " " + respTxt + '</div>';
                log.prepend(logTmp);
                logNum++;
            },
            onFinish: function () {
                log.prepend(logTmp);
                step3();
            }
        });
    }

    function step3() {
        window.location.href = "/ieslab/cargo/analysis/download"
    }
}