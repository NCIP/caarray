<%@ tag display-name="annotationCharacteristics"
        description="Renders a read-only view of miscellaneous characteristics associated with a biomaterial annotation"
        body-content="empty"%>

<%@ taglib uri="/struts-tags" prefix="s" %>

<%@ attribute name="item" required="true" type="gov.nih.nci.caarray.domain.sample.AbstractBioMaterial"%>

<s:iterator value="#attr.item.characteristics" id="characteristic">
    <s:if test="#characteristic.displayValue != '-&gt;'">
      <s:if test="#characteristic.category != null">
          <s:textfield theme="readonly" label="%{#characteristic.category.name}" value="%{#characteristic.displayValue}"/>
      </s:if>
      <s:else>
          <s:textfield theme="readonly" label="Uncategorized Value" value="%{#characteristic.displayValue}"/>
      </s:else>
    </s:if>
 </s:iterator>
