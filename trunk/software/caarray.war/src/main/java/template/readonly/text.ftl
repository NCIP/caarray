<#include "/${parameters.templateDir}/${parameters.theme}/controlheader.ftl" />

<#if parameters.url?exists && parameters.url == 'true' && parameters.nameValue?exists>
  <a href="${parameters.nameValue}" <#t/>
  <#if parameters.target?exists>
    target=${parameters.target}<#t/>
  </#if>
  >${parameters.nameValue}</a><#t/>
<#elseif parameters.nameValue?exists>
  <@s.property value="parameters.nameValue"/>
</#if>

<#include "/${parameters.templateDir}/readonly/controlfooter.ftl" />

