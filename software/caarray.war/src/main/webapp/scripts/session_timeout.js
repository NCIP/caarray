function calcOffset() {
    var serverTime = parseInt(getCookie('serverTime'));
    var clientTimeOffset = (new Date()).getTime() - serverTime;
    setCookie('clientTimeOffset', clientTimeOffset);
}

function checkSession() {
    var sessionExpiry = parseInt(getCookie('sessionExpiry'));
    var timeOffset = parseInt(getCookie('clientTimeOffset'));
    var localTime = (new Date()).getTime();
    var timeToExpiration = sessionExpiry - localTime + timeOffset;
    if (timeToExpiration > 0) {
        if (timeToExpiration < 50000) {
            debugger;
            Ext.Msg.show({
                title: 'Session expiring soon',
                msg: 'Do you want to extend your session?',
                buttons: Ext.Msg.YESNO,
                fn: extendSession
            });
        } else {
            setTimeout('checkSession()', 10000);
        }
    }
}

function extendSession(button) {
    if (button == 'yes') {
        xmlHttp = new XMLHttpRequest();
        xmlHttp.open( "GET", 'notYetImplemented.jsp', false );
        xmlHttp.send( null );
        checkSession();
    }
}