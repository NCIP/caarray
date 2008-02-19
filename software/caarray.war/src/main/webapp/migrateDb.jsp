<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>


<div class="padme">
    <div id="tabboxwrapper_notabs">
        <div class="boxpad2">
            <h3>Migrate Database</h3>
        </div>

        <div class="boxpad">
            <p class="instructions">
                Your database schema version is out of date.  Please back up your data if necessary,
                then click on the button below to run the migration scripts.
            </p>
            <s:form method="post" action="/migrateDb.action">
                <input type="submit" value="Migrate Database"/>
            </s:form>
        </div>
    </div>
</div>