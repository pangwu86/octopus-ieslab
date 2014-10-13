<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/jsp/_setup.jsp" %>
<link rel="stylesheet" href="${rs}/ieslab/contract.css">
<script language="JavaScript" src="${rs}/ieslab/contract-manage.js"></script>
<div class="module-content module-contract-manage">

    <div class="conbaseinfo-menu">
        <div class="conbaseinfo-btn conmanage-upload" cate="order">上传合同</div>
        <div class="conbaseinfo-btn conmanage-download" cate="download"><en>下载合同</en> <span>已选中合同: <b>0</b></span></div>
    </div>

    <div class="conmanage-orderlist"></div>
    <div class="conmanage-fileview"></div>

</div>