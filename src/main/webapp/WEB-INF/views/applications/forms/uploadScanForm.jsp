<%@ include file="/common/taglibs.jsp"%>

<spring:url value="/organizations/{orgId}/applications/{appId}/scans/upload" var="uploadUrl">
	<spring:param name="orgId" value="${ application.organization.id }"/>
	<spring:param name="appId" value="${ application.id }"/>
</spring:url>
<form:form id="scanForm" style="margin-bottom:0px" modelAttribute="application" method="post" autocomplete="off" action="${ fn:escapeXml(uploadUrl) }" enctype="multipart/form-data">
	<div class="modal-body">
		File <input id="fileInput" type="file" name="file" size="50" />
	</div>
	<div class="modal-footer">
		<span style="float:left;font-size:8;" class="errors">Average file uploads take a few seconds but <br>larger files (2GB+) can take several minutes.</span>
		<button id="closeScanModalButton" class="btn" data-dismiss="modal" aria-hidden="true">Close</button>
		<button id="submitScanModal" class="btn btn-primary">Upload Scan</button>
	</div>
</form:form>