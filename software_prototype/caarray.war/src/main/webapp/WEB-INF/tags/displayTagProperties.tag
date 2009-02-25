<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>

<display:setProperty name="basic.empty.showtable" value="true" />
<display:setProperty name="paging.banner.no_items_found">
    <div class="pagingtop"><span class="pagebanner">No {0} found.</span>
</display:setProperty>
<display:setProperty name="paging.banner.one_item_found">
    <div class="pagingtop"><span class="pagebanner">One {0} found.</span>
</display:setProperty>
<display:setProperty name="paging.banner.all_items_found">
    <div class="pagingtop"><span class="pagebanner">{0} {1} found, displaying all {2}.</span>
</display:setProperty>
<display:setProperty name="paging.banner.some_items_found">
    <div class="pagingtop"><span class="pagebanner">{0} {1} found, displaying {2} to {3}.</span>
</display:setProperty>
<display:setProperty name="paging.banner.full">
    <span class="pagelinks">[<a href="{1}">First</a>/<a href="{2}">Prev</a>]{0}[<a href="{3}">Next</a>/<a href="{4}">Last</a>]</span></div>
</display:setProperty>
<display:setProperty name="paging.banner.first">
    <span class="pagelinks">[First/Prev] {0}[<a href="{3}">Next</a>/<a href="{4}">Last</a>]</span></div>
</display:setProperty>
<display:setProperty name="paging.banner.last">
    <span class="pagelinks">[<a href="{1}">First</a>/<a href="{2}">Prev</a>]{0} [Next/Last]</span></div>
</display:setProperty>
<display:setProperty name="paging.banner.onepage">
    <span class="pagelinks"></span></div>
</display:setProperty>