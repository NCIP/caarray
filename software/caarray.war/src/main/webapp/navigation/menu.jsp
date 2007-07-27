<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>

<h:form id="mainMenu">
  <h:outputLabel value="Project Menu" />
  <h:commandLink id="proposeProject" action="#{projectProposalBean.startNewProposal}" value="Propose Project" />
  <h:commandLink id="importDemo" action="demoForm" value="Import Demo" />
</h:form>