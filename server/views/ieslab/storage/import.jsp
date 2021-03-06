<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/jsp/_setup.jsp" %>
<%@include file="_rs.jsp" %>
<div class="module-content module-storage-import">
    <div class="import-label">
        ${msg['import.label.form']}
    </div>
    <div class="import-dashboard">
        <div>
            <span>${msg['import.title.material']}</span>
            <span class="btn-import" type="material">${msg['import.input.submit']}</span>
        </div>
        <div>
            <span>${msg['import.title.storage']}</span>
            <span class="btn-import" type="storage">${msg['import.input.submit']}</span>
        </div>
        <div>
            <span>${msg['import.title.storage']}-2</span>
            <span class="btn-import" type="storage2">${msg['import.input.submit']}</span>
        </div>
    </div>
    <div class="import-label">
        ${msg['import.label.log']}
    </div>
    <div class="import-log">
    </div>
</div>