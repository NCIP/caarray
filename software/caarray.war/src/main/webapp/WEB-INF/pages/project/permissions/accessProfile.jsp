<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

<s:form action="ajax/project/permissions/saveProfile" theme="simple" id="profileForm">
                                <table class="searchresults" cellspacing="0">
                                    <tr>
                                        <th style="height: 2.5em;">Control Access to Specific Contents</th>
                                    </tr>
                                    <tr>
                                       <td class="filterrow" style="border-bottom: 1px solid #999">
                                            <s:label for="profileForm_accessProfile_securityLevel" value="Experiment Access"/>
                                            <s:select required="true" name="accessProfile.securityLevel" tabindex="1"
                                                  list="@gov.nih.nci.caarray.domain.permissions.SecurityLevel@values()" listValue="%{getText(resourceKey)}"/>
                                        </td>
                                    </tr>
                                    <tr>
                                        <th class="title"><a href="#">Sample ID</a></th>
                                    </tr>
                                </table>
                                
        <div class="scrolltable" style="height:21em">
                                                            <table class="searchresults" cellspacing="0" style="width:287px">
                                                            <c:forEach items="${accessProfile.sampleSecurityLevels}" var="sampleMapping"> 
                                                                <tr class="odd">
                                                                    <td><a href="experiment_sample_details.htm">${sampleMapping.key.name}</a></td>
                                                                    <td>${sampleMapping.key.description}</td>
                                                                    <td>
                                                                        <s:select required="true" name="accessProfile.sampleSecurityLevels[${sampleMapping.key.id}]" tabindex="1"
                                                                            list="@gov.nih.nci.caarray.domain.permissions.SecurityLevel@values()" listValue="%{getText(resourceKey)}"/>
                                                                    </td>
                                                                </tr>
                                                            </c:forEach>
                                                            </table>
        </div>                                                                                                                                        
</s:form>
