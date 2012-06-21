<#include "/${parameters.templateDir}/${parameters.theme}/controlheader.ftl" />

<@s.iterator value="parameters.list">
    <#if parameters.listKey?exists>
        <#assign itemKey = stack.findValue(parameters.listKey)/>
    <#else>
        <#assign itemKey = stack.findValue('top')/>
    </#if>
    <#assign itemKeyStr = itemKey.toString() />
    <#if parameters.listValue?exists>
        <#assign itemValue = stack.findString(parameters.listValue)/>
    <#else>
        <#assign itemValue = stack.findString('top')/>
    </#if>
    <#if tag.contains(parameters.nameValue, itemKeyStr) == true>
        ${itemValue?html}
    </#if>
</@s.iterator>

<#include "/${parameters.templateDir}/readonly/controlfooter.ftl" />

    