<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>
<html>
<head>
    <meta name="showLoginSidebar" content="true"/>
    <script type="text/javascript">

            populateCategory = function() {
                if ($('searchTypeSEARCH_BY_SAMPLE').checked == true) {
                    $('selectSampleCat').show();
                    $('selectExpCat').hide();
                    checkSampleCategorySelection();
                } else if ($('searchTypeSEARCH_BY_EXPERIMENT').checked == true) {
                    $('selectExpCat').show();
                    $('selectSampleCat').hide();
                    hideFilterBox('filterCharBox');
                    checkExpCategorySelection();
                } else {
                    $('selectExpCat').hide();
                    $('selectSampleCat').hide();
                    hideFilterBox('filterCharBox');
                }
            }

            checkSampleCategorySelection = function() {
                if ($('selectSampleCat').value == 'OTHER_CHARACTERISTICS') {
                    showFilterBox('filterCharBox');
                    hideFilterBox('filterOrgBox');
                    showKeywordTxtBox();
                } else if ($('selectSampleCat').value == 'SAMPLE_ORGANISM') {
                    hideKeywordTxtBox();
                    showFilterBox('filterOrgBox');
                    hideFilterBox('filterCharBox');
                } else {
                    showKeywordTxtBox();
                    hideFilterBox('filterCharBox');
                    hideFilterBox('filterOrgBox');
                }
            }

            checkExpCategorySelection = function() {
                if ($('selectExpCat').value == 'ORGANISM') {
                    hideKeywordTxtBox();
                    showFilterBox('filterOrgBox');
                    hideFilterBox('filterCharBox');
                } else {
                    showKeywordTxtBox();
                    hideFilterBox('filterCharBox');
                    hideFilterBox('filterOrgBox');
                }
            }

            showFilterBox = function(boxname) {
                var filterBoxL = boxname+'OuterDivLabel';
                var filterBoxB = boxname+'OuterDivBody';
                $(filterBoxL).show();
                $(filterBoxB).show();
            }

            hideFilterBox = function(boxname) {
                var filterBoxL = boxname+'OuterDivLabel';
                var filterBoxB = boxname+'OuterDivBody';
                $(filterBoxL).hide();
                $(filterBoxB).hide();
            }

            showKeywordTxtBox = function() {
                $('keywordTxtField').show();
                $('keywordlabel').show();
            }

            hideKeywordTxtBox = function() {
                $('keywordTxtField').hide();
                $('keywordlabel').hide();
            }

            checkFields = function() {

                if ( (($('searchTypeSEARCH_BY_EXPERIMENT').checked == true && $('selectExpCat').value == 'ORGANISM') ||
                     ($('searchTypeSEARCH_BY_SAMPLE').checked == true && $('selectSampleCat').value == 'SAMPLE_ORGANISM')) &&
                     ($('filterOrgBoxSelectedItemDiv').lastChild == null || $('filterOrgBoxSelectedItemDiv').lastChild.id == null)) {
                        alert('An organism must be selected.');
                        return false;
                }
                else if ($('searchTypeSEARCH_BY_SAMPLE').checked == true &&
                  $('selectSampleCat').value == 'OTHER_CHARACTERISTICS' &&
                    ($('filterCharBoxSelectedItemDiv').lastChild == null || $('filterCharBoxSelectedItemDiv').lastChild.id == null)) {
                        alert('A characteristic must be selected.');
                        return false;
                }
                else if ($('searchTypeSEARCH_BY_SAMPLE').checked == true &&
                    $('selectSampleCat').value == 'SAMPLE_EXTERNAL_ID' &&
                    $('keywordTxtField').value.length < 1 ) {
                    alert('An external sample id must be at least 1 character long.');
                    return false;
                } else if ($('searchTypeSEARCH_BY_SAMPLE').checked == true &&
                    $('selectSampleCat').value != 'OTHER_CHARACTERISTICS' &&
                    $('selectSampleCat').value != 'SAMPLE_ORGANISM' &&
                    $('selectSampleCat').value != 'SAMPLE_EXTERNAL_ID' &&
                    $('keywordTxtField').value.length < 3 ) {
                    alert('Keyword must be at least 3 characters long.');
                    return false;
                }

                $('searchform').submit();

            }

    </script>

</head>
<body>
    <div class="homebanner"><img src="<c:url value="/images/banner_caarray.jpg"/>" width="598" height="140" alt="" /></div>
    <h1>Welcome to the caArray Data Portal</h1>
    <caarray:helpPrint/>
    <p><strong>caArray</strong> is an open-source, web and programmatically accessible array data management system. caArray guides the annotation and exchange of array data using a federated model of local installations whose results are shareable across the cancer Biomedical Informatics Grid (caBIG™). caArray furthers translational cancer research through acquisition, dissemination and aggregation of semantically interoperable array data to support subsequent analysis by tools and services on and off the Grid. As array technology advances and matures, caArray will extend its logical library of assay management.</p>
    <div id="browsesearchwrapper" class="padme">
         <ajax:tabPanel panelStyleId="tabs" panelStyleClass="tabs2" currentStyleClass="active" contentStyleId="tabboxwrapper" contentStyleClass="tabboxwrapper"
                postFunction="TabUtils.setSelectedTab" preFunction="TabUtils.showLoadingText">
                <c:url value="/ajax/search/showSearchFieldsTab.action" var="tabExpUrl">
                    <c:param name="searchField" value="browse"/>
                </c:url>
                <caarray:tab caption="Browse caArray" baseUrl="${tabExpUrl}" defaultTab="true" />
                <c:url value="/ajax/search/showSearchFieldsTab.action" var="tabBioUrl">
                    <c:param name="searchField" value="biomaterials"/>
                </c:url>
                <caarray:tab caption="Search caArray" baseUrl="${tabBioUrl}" defaultTab="false" />
        </ajax:tabPanel>
    </div>
    <div class="clear"></div>
    </body>
</html>
