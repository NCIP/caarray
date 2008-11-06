<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>
      <div id="browsetab" class="boxpad" style="background:url('../images/bg_shadow_tanpanel_a.gif') repeat-x;">

                <table class="alttable" cellspacing="0">
                    <tr>
                        <th colspan="2" class="centered">
                    <label for="location"><fmt:message key="search.location"/></label>
                    <s:select name="location" theme="simple"
                              list="#{'NCICB':'NCICB'}"
                              headerKey="" headerValue="(All Locations)"/>
                        </th>
                    </tr>
                    <c:forEach items="${browseItems}" var="row" varStatus="rowStatus">
                        <tr class="${rowStatus.count % 2 == 0 ? 'even' : 'odd'}">
                            <td>
                                <s:if test="${!empty row.category}">
                                    <c:url value="/browse.action" var="browseLink">
                                        <c:param name="category" value="${row.category}"/>
                                    </c:url>
                                    <a href="${browseLink}"><s:text name="${row.category.resourceKey}"/></a>
                                </s:if><s:else>
                                    <s:text name="${row.resourceKey}"/>
                                </s:else>
                            </td>
                            <td>
                                <s:if test="${!empty row.category}">
                                    <c:url value="/browse.action" var="browseLink">
                                        <c:param name="category" value="${row.category}"/>
                                    </c:url>
                                    <a href="${browseLink}">${row.count}</a>
                                </s:if><s:else>${row.count}</s:else>
                            </td>
                        </tr>
                    </c:forEach>
                </table>

          </div>