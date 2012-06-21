<#include "/${parameters.templateDir}/${parameters.theme}/controlheader.ftl" />

<#if parameters.nameValue?exists>
  <@s.property value="parameters.nameValue.toString()?replace('.', 'X', 'r')"/>
</#if>

<#include "/${parameters.templateDir}/readonly/controlfooter.ftl" />

    