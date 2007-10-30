<tr>
    <td class="tdLabel"><#rt/>
<#if parameters.label?exists>
    <label <#t/>
<#if parameters.id?exists>
        for="${parameters.id?html}" <#t/>
</#if>
        class="label"<#t/>
    ><#t/>
${parameters.label?html}<#t/>
:<#t/>
</label><#t/>
</#if>
    </td><#lt/>
