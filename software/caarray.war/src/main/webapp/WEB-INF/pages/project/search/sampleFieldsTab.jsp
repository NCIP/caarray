<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>
            <div class="boxpad" style="background:url('../images/bg_shadow_tanpanel_a.gif') repeat-x;">
                <s:form id="searchform" action="/search/basicSearch.action" cssClass="alttable">
                    <tr>
                        <td class="tdLabel">
                            <s:label theme="simple"><b><fmt:message key="search.type"/>:</b></s:label>
                        </td>
                        <td>
                            <s:radio id="searchType" name="searchType" key="search.type"
                            list="@gov.nih.nci.caarray.web.action.SearchAction@getSearchTypeSelection()"
                            listValue="%{getText(label)}"
                            listKey="value"
                            value="@gov.nih.nci.caarray.domain.search.SearchTypeSelection@SEARCH_BY_EXPERIMENT"
                            onclick="populateCategory()" theme="simple"/>
                        </td>
                    </tr>
                    <tr>
                        <td class="tdLabel">
                            <s:label theme="simple"><b><fmt:message key="search.category"/>:</b></s:label>
                        </td>
                        <td>
                            <s:select id="selectExpCat" name="categoryExp"
                            list="@gov.nih.nci.caarray.web.action.SearchAction@getSearchCategories()" listValue="%{getText(label)}"
                            listKey="value" value="EXPERIMENT_ID" theme="simple" onchange="checkExpCategorySelection()" />

                            <s:select id="selectSampleCat" name="categorySample"
                            list="@gov.nih.nci.caarray.web.action.SearchAction@getSearchBiometricCategories()" listValue="%{getText(label)}"
                            listKey="value" value="SAMPLE_NAME"  cssStyle="display:none;" theme="simple" onchange="checkSampleCategorySelection()"/>
                        </td>
                    </tr>
                            <c:url var="autocompleteUrl" value="/ajax/search/searchForCharacteristicCategories.action" />
                            <c:set var="editMode" value="true" scope="request"/>
                            <caarray:listSelector baseId="filterCharBox" listField="${selectedCategory}" listLabel="Characteristics"
                                listFieldName="selectedCategory" tabIndex="3" required="true" multiple="false"
                                hideAddButton="true"
                                filterFieldName="filterInput" objectLabel="name" objectValue="id"
                                autocompleteUrl="${autocompleteUrl}" divstyle="display:none;"/>
                     <tr>
                        <td class="tdLabel">
                            <div id="keywordlabel"><b><fmt:message key="search.keyword"/>:</b></div>
                        </td>
                        <td>
                            <s:textfield id="keywordTxtField" name="keyword" key="search.keyword" theme="simple"/>
                        </td>
                     </tr>
                            <c:url var="autoOrgCompleteUrl" value="/ajax/search/searchForOrganismNames.action" />
                            <caarray:listSelector baseId="filterOrgBox" listField="${selectedOganism}" listLabel="Keyword"
                                listFieldName="selectedOrganism" tabIndex="5" required="true" multiple="false"
                                hideAddButton="true"
                                filterFieldName="filterKeyword" objectLabel="scientificName" objectValue="id"
                                autocompleteUrl="${autoOrgCompleteUrl}" divstyle="display:none;"/>
                    <tr>
                        <td class="tdLabel">
                            <s:label theme="simple"><b><fmt:message key="search.location"/>:</b></s:label>
                        </td>
                        <td colspan="2">
                            <s:select name="location" list="#{'NCICB':'NCICB'}"
                                      headerKey="" headerValue="(All Locations)" theme="simple" />
                        </td>
                    </tr>
                    <tr>
                        <td colspan="2" class="centered">
                    <del class="btnwrapper">
                        <ul id="btnrow">
                            <caarray:action onclick="checkFields()" actionClass="search" text="Search"/>
                         </ul>
                     </del>
                        </td>
                    </tr>
                    <input type="submit" class="enableEnterSubmit"/>
                </s:form>
                <caarray:focusFirstElement formId="searchform"/>
           </div>