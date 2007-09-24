<%@ include file="/common/taglibs.jsp"%>

<head>
</head>
<body>
    <div id="content" class="homepage">
        <h1>Propose Experiment</h1>
        <%@ include file="/common/messages.jsp" %>
        <s:form id="projectForm" action="saveProject" method="post">
            <table>
                <tr>
                    <th>Title for new project:</th>
                    <td>
                        <s:textfield id="title" name="proposal.project.experiment.title"/>
                    </td>
                </tr>
                <tr>
                    <td colspan="2">
                        <s:submit id="submit" key="button.save"/>
                    </td>
                </tr>
            </table>
        </s:form>
    </div>
 </body>