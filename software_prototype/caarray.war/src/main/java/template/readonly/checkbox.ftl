<#include "/${parameters.templateDir}/${parameters.theme}/controlheader.ftl" />

<#if parameters.nameValue?exists && parameters.nameValue>
  Yes
<#else>
  No
</#if>

<#include "/${parameters.templateDir}/readonly/controlfooter.ftl" />

    