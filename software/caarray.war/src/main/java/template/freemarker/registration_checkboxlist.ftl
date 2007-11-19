<#include "/${parameters.templateDir}/readonly/controlheader.ftl" />

<#assign selectedCount = 0/>
<ul class="checklist" style="width:50%;">
    <@s.iterator value="parameters.list">
        <#assign selectedCount = selectedCount + 1/>
        <#assign itemKey = stack.findValue(parameters.listKey)/>
        <#assign itemValue = stack.findString(parameters.listValue)/>
        <li>
            <a name="${parameters.name}-${selectedCount}"></a><label for="${parameters.name}-${selectedCount}"><input id="${parameters.name}-${selectedCount}" name="${parameters.name}" value="${itemKey}" type="checkbox"/> ${itemValue}</label>
        </li>
    </@s.iterator>
</ul>

<#include "/${parameters.templateDir}/readonly/controlfooter.ftl" />

