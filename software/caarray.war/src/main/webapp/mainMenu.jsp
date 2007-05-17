<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>

<h:form id="protocolMenu">
  <h:outputLabel value="Protocol Menu" />
  <h:commandLink id="addProtocol" action="#{protocolBean.addProtocol}" value="Add Protocol" />
  <h:commandLink id="searchProtocol" action="searchProtocols" value="Search Protocol" />
</h:form>