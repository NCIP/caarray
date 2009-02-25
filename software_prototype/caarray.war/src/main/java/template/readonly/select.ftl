<#include "/${parameters.templateDir}/${parameters.theme}/controlheader.ftl" />

<#if parameters.multiple?default(false) && parameters.nameValue?exists>
	<#assign selectedSize = parameters.nameValue?size>
<#else>
	<#assign selectedSize = 0>
</#if>
<#assign selectedCount = 0/>

<@s.iterator value="parameters.list">
        <#if parameters.listKey?exists>
            <#if stack.findValue(parameters.listKey)?exists>
              <#assign itemKey = stack.findValue(parameters.listKey)/>
              <#assign itemKeyStr = itemKey.toString()/>
            <#else>
              <#assign itemKey = ''/>
              <#assign itemKeyStr = ''/>
            </#if>
        <#else>
            <#assign itemKey = stack.findValue('top')/>
            <#assign itemKeyStr = itemKey.toString()/>
        </#if>
        <#if parameters.listValue?exists>
            <#if stack.findString(parameters.listValue)?exists>
              <#assign itemValue = stack.findString(parameters.listValue)/>
            <#else>
              <#assign itemValue = ''/>
            </#if>
        <#else>
            <#assign itemValue = stack.findString('top')/>
        </#if>
        <#if tag.contains(parameters.nameValue, itemKey) == true>
	        <#assign selectedCount = selectedCount + 1/>
        	${itemValue?html}<#if selectedCount < selectedSize>, </#if>
        </#if>
</@s.iterator>

<#include "/${parameters.templateDir}/readonly/controlfooter.ftl" />

    