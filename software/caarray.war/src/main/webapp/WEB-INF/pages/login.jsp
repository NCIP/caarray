<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

<page:applyDecorator name="default">
<head>
</head>
<body>
    <div id="content" class="homepage">
        <div class="homebanner"><img src="${ctx}/images/banner_caarray.jpg" width="600" height="140" alt="" /></div>
        <h1>Welcome to the caArray Data Portal</h1>
        <p><strong>caArray</strong> is an open-source, role-based, Web and programmatically accessible data management system that guides the annotation and exchange of array data through a federated model of local and centralized installations. It provides browser-based and programmatic access to the data stored locally; enables mechanisms for accessing all local installation data over <a href="http://cabig.nci.nih.gov/workspaces/Architecture/caGrid/" class="external" target"#">caGrid</a>; supports silver compatibility with <a href="http://cabig.cancer.gov/index.asp" class="external" target"#">caBIG</a> guidelines, promotes compatibility with the MIAME 1.1 guidelines and the import of MAGE-TAB and provides a data service for caBIG analytical services.</p>
        <%--
        <div id="browsesearchwrapper">
            <div id="browseboxhome">
                <h2>Browse caArray</h2>
                <div class="boxpad">
                    <table class="alttable" cellspacing="0">
                        <tr>
                            <th colspan="2">
                                Location:
                                <select name="browsenode" id="browsenode">
                                    <option>NCICB</option>
                                </select>
                            </th>
                        </tr>
                        <tr class="odd">
                            <td class="label1"><a href="browseresults.htm">Experiments</a></td>
                            <td class="num"><a href="browseresults.htm">1123</a></td>
                        </tr>
                        <tr>
                            <td class="label1"><a href="browseresults.htm">Organisms</a></td>
                            <td class="num"><a href="browseresults.htm">37</a></td>
                        </tr>
                        <tr class="odd">
                            <td class="label1"><a href="browseresults.htm">Manufacturers</a></td>
                            <td class="num"><a href="browseresults.htm">23</a></td>
                        </tr>
                        <tr>
                            <td class="label1"><a href="browseresults.htm">Unique Array Designs</a></td>
                            <td class="num"><a href="browseresults.htm">15</a></td>
                        </tr>
                    </table>
                </div>
            </div>
            <div id="searchboxhome">
                <h2>Search caArray</h2>
                <div class="boxpad">
                    <form name="searchform" id="searchform" action="#">
                        <table class="alttable">
                            <tr>
                                <td colspan="2" class="alignright"><a href="search_advanced.htm">advanced search</a></td>
                            </tr>
                            <tr>
                                <td class="label">Keyword:</td>
                                <td><input type="text" name="keyw" id="keyw" style="width:185px" /></td>
                            </tr>
                            <tr>
                                <td class="label">Category:</td>
                                <td>
                                    <select name="searchcat" id="searchcat" style="width:190px">
                                        <option>-- Please select a category --</option>
                                    </select>
                                </td>
                            </tr>
                            <tr>
                                <td class="label">Location:</td>
                                <td>
                                    <select name="searchnode" id="browsenode" style="width:190px">
                                        <option>NCICB</option>
                                    </select>
                                </td>
                            </tr>
                            <tr>
                                <td colspan="2" class="centered"><input type="image" src="${ctx}/images/btn_search_home.gif" alt="Search" value="Search" class="button" /></td>
                            </tr>
                        </table>
                    </form>
                </div>
            </div>
        </div>
        --%>
        <div class="clear"></div>
    </div>
</body>
</page:applyDecorator>
