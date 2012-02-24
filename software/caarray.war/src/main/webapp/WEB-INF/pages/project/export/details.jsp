<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>
        <c:url var="exportToMageTabUrl" value="/ajax/project/export/exportToMageTab.action">
            <c:param name="project.id" value="${project.id}" />
        </c:url>

        <c:url var="exportToGeoZipUrl" value="/ajax/project/export/exportToGeoArchive.action">
            <c:param name="project.id" value="${project.id}" />
            <c:param name="type" value="ZIP" />
        </c:url>
        <c:url var="exportToGeoTgzUrl" value="/ajax/project/export/exportToGeoArchive.action">
            <c:param name="project.id" value="${project.id}" />
            <c:param name="type" value="TGZ" />
        </c:url>
        <c:url var="exportToGeoInfoUrl" value="/ajax/project/export/exportToGeoInfo.action">
            <c:param name="project.id" value="${project.id}" />
        </c:url>

        <p class="instructions">You may choose to download experiment data in one of two GEO SOFT export types, or in MAGE-TAB format.</p>
            <h4>Microarray and Gene Expression Tabular Format (MAGE-TAB)</h4>
            <div class="row">
                <div class="export_type_icon">
                	<a href="${exportToMageTabUrl}">
                		<img src="<c:url value="/images/ico_zip.png"/>" 
                			 alt="Zip"
                			 title="Zip"/>
              		</a>
             	</div>
                <h5><a href="${exportToMageTabUrl}">MAGE-TAB (Annotations Only)</a></h5>
                <div class="descr">Export experiment annotations in MAGE-TAB IDF and SDRF format.</div>
                <div class="clear"></div>
            </div>


            <h4>Gene Expression Omnibus (GEO) Simple Omnibus Format in Text (SOFT)</h4>

            <s:if test="geoValidation.isEmpty()">
            <div class="row">
                <div class="export_type_icon">
                	<a href="${exportToGeoInfoUrl}">
                		<img src="<c:url value="/images/ico_geo.png"/>" 
                			 alt="GEO SOFT (File Only)"
                			 title="GEO SOFT (File Only)"/>
               		</a>
              	</div>
                <h5><a href="${exportToGeoInfoUrl}">GEO SOFT (File Only)</a></h5>
                <div class="descr">Export experiment annotations in GEO SOFT format.</div>
                <div class="clear"></div>
            </div>


            <div class="row">
                <s:if test="geoZipOk">
                <div class="export_type_icon">
                	<a href="${exportToGeoZipUrl}">
                		<img src="<c:url value="/images/ico_zip.png"/>" 
                			 alt="Zip" 
                			 title="Zip"/>
               		</a>
              	</div>
                <h5><a href="${exportToGeoZipUrl}">GEO SOFT (Submissions Package)</a></h5>
                <div class="descr">Export experiment annotations in GEO SOFT format, packaged with experiment data files.</div>
                </s:if>
                <s:else>
                <div class="export_type_icon">
                	<a href="${exportToGeoTgzUrl}">
                		<img src="<c:url value="/images/ico_gz.png"/>" 
                			 alt="Tgz"
                			 title="Tgz"/>
               		</a>
              	</div>
                <h5><a href="${exportToGeoTgzUrl}">GEO SOFT (Submissions Package)</a></h5>
                <div class="descr">Export experiment annotations in GEO SOFT format, packaged with experiment data files. Note that the
                    files are packaged in TGZ format, which may not be accepted by the GEO SOFT automatic submission process.</div>
                </s:else>
                <div class="clear"></div>
            </div>
            </s:if>
            <s:else>
            <div class="row">
                <div class="export_error"><img src="<c:url value="/images/ico_error.gif"/>" class="erroricon" alt="">
                    <b>GEO SOFT export is not available for this experiment for the following reasons:</b>
                    <ul class="export_error">
                    <s:iterator value="geoValidation" id = "message">
                        <li><c:out value="${message}"/></li>
                    </s:iterator>
                    </ul>
                </div>
                <div class="row_disabled">
                <div class="export_type_icon"><img src="<c:url value="/images/ico_geo_disabled.png"/>" alt="GEO SOFT (Disabled)" /></a></div>
                <h5>GEO SOFT export unavailable</a></h5>
                <div class="descr">Export experiment in a GEO SOFT format is not available.</div>
                </div>
                <div class="clear"></div>
            </div>
            </s:else>
