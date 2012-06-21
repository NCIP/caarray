<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>
<style type="text/css">

.export .instructions
{ margin:0 }
.export .boxpad
{ border-bottom:1px solid #ccc; padding-bottom:8px; }
.export .boxpad .row, .export .boxpad .row_disabled
{ border-bottom:1px solid #ccc; padding-top:10px; padding-left:14px; margin:0 10px}
div.accordion-content H4
{ padding:10px 14px; font-size:110%; color:#333; }
.export .boxpad H5
{ padding:2px 0; font-size:100%; }
.export .export_type_icon
{ display:block; float:left; width:24px; height:32px; padding:0 10px 10px 0; }
.export .export_type_icon IMG
{ width:24px; height:32px; }
.row_disabled H5, .row_disabled DIV.descr
{ color:#999; }
.export_error
{ margin:5px 12px 10px 12px; padding:10px; font-style:italic; background:#fdf0cc; color:#c36c06; }
div.export_error
{ height:10em; overflow:auto; }
ul.export_error li
{ list-style-position:inside; list-style-type: disc; }
.erroricon
{ float:left; padding-right:5px }
.export .boxpad H4.bordertop
{ border-top:1px solid #999; padding-top:20px; }

div.accordion-content div.tableboxpad
{ height: 350px; background-color: white;}
</style>


<caarray:tabPane paneTitleKey="project.tabs.downloadData" subtab="true">

        <div class="tableboxpad export">
            <div class="boxpad">
    <!--Accordion-->
    <div id="download-accordion" class="accordion">
        <!--Data Sets-->
        <div class="accordion-toggle top">Download Experiment File Packages</div>
        <div class="accordion-content">
            <c:choose>
                <c:when test="${empty files}">
                    <div class="row_disabled">
                        <div class="export_type_icon"><img src="<c:url value="/images/ico_zip_disabled.png"/>" alt="Zip (Disabled)" /></div>
                        <h5>No Experiment Files</h5>
                        <div class="descr">There are no experiment files to download in a .ZIP</div>
                        <div class="clear"></div>
                    </div>
                </c:when>
                <c:otherwise>
                    <c:url var="downloadAllUrl" value="/project/files/downloadByType.action">
                        <c:param name="project.id" value="${project.id}" />
                    </c:url>
                    <div class="row">
                        <div class="export_type_icon">
                        	<a href="${downloadAllUrl}">
                        		<img src="<c:url value="/images/ico_zip.png"/>"
                        			 alt="Zip"
                        			 title="Zip"/>
                       		</a>
                      	</div>
                        <h5><a href="${downloadAllUrl}">All Experiment Files</a></h5>
                        <div class="descr">Download all experiment files in a .ZIP</div>
                        <div class="clear"></div>
                    </div>

                    <s:iterator id="type" value="fileTypes">
                        <c:url var="downloadUrl" value="/project/files/downloadByType.action">
                            <c:param name="project.id" value="${project.id}" />
                            <c:param name="fileType" value="${type}" />
                        </c:url>
                        <div class="row">
                            <div class="export_type_icon">
                            	<a href="${downloadUrl}">
                            		<img src="<c:url value="/images/ico_zip.png"/>" 
                            			 alt="Zip"
                            			 title="Zip"/>
                            	</a>
                           	</div>
                            <h5><a href="${downloadUrl}">All ${type} Files</a></h5>
                            <div class="descr">Download all ${type} files in a .ZIP</div>
                            <div class="clear"></div>
                        </div>
                    </s:iterator>

                    <c:if test="${!empty project.userVisibleSupplementalFiles}">
                        <c:url var="downloadUrl" value="/project/files/downloadByType.action">
                            <c:param name="project.id" value="${project.id}" />
                            <c:param name="fileStatus" value="SUPPLEMENTAL" />
                        </c:url>
                        <div class="row">
                            <div class="export_type_icon">
                            	<a href="${downloadUrl}">
                            		<img src="<c:url value="/images/ico_zip.png"/>" 
                            			 alt="Zip"
                            			 title="Zip"/>
                          		</a>
                         	</div>
                            <h5><a href="${downloadUrl}">All SUPPLEMENTAL Files</a></h5>
                            <div class="descr">Download all SUPPLEMENTAL files in a .ZIP</div>
                            <div class="clear"></div>
                        </div>
                    </c:if>
                </c:otherwise>
            </c:choose>
        </div>
        <!--/Data Sets-->
        <!--Individual Files-->
        <c:url var="downloadSelectedUrl" value="/ajax/project/files/downloadFiles.action">
            <c:param name="project.id" value="${project.id}"/>
            <c:param name="editMode" value="${editMode}" />
        </c:url>
        
        <div class="accordion-toggle" ajaxLocation="${downloadSelectedUrl}">Download Selected Experiment Files</div>
        <div class="accordion-content" style="height:33em"> <!--height fixed here because of an intermittent bug-->
            <img alt="Indicator" align="absmiddle" src="<c:url value="/images/indicator.gif"/>" /> Loading...
        </div>
        <!--/Individual Files-->

        <!--Export-->
        <c:url var="exportDetailsUrl" value="/ajax/project/export/details.action">
            <c:param name="project.id" value="${project.id}"/>
            <c:param name="editMode" value="${editMode}" />
        </c:url>
        <div class="accordion-toggle" ajaxLocation="${exportDetailsUrl}">Export Experiment Annotation Packages</div>
        <div class="accordion-content">
            <img alt="Indicator" align="absmiddle" src="<c:url value="/images/indicator.gif"/>" /> Loading...
        </div>        
        <!--/Export-->
    </div>
    <!--/Accordion-->
            </div>
        </div>
</caarray:tabPane>

<script type="text/javascript">
new Accordion("download-accordion", 1);
</script>