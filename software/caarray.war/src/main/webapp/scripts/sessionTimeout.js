var sessionTimeout = {
    warningLength: 30000,
    init: function(timeoutFunction) {
        var serverTime = parseInt(getCookie('serverTime'));
        this.timeOffset = (new Date()).getTime() - serverTime;
        this._timeout = timeoutFunction;
        this._showWarning();
    },
    _timeTilExpire: function() {
        var sessionExpiry = parseInt(getCookie('sessionExpiry'));
        var localTime = (new Date()).getTime();
        return sessionExpiry - localTime + this.timeOffset;
    },
    _showWarning: function() {
        var timeToExpiration = this._timeTilExpire();
        if (timeToExpiration > 0) {
            if (timeToExpiration < this.warningLength) {
                Ext.Msg.show({
                    title: 'Session expiring soon',
                    msg: 'Do you want to extend your session?',
                    closable: false,
                    buttonText: { yes: 'Keep Working', cancel: 'Logoff' },
                    fn: sessionTimeout._extendSession
                });
                setTimeout("sessionTimeout._hideWarning()", timeToExpiration);
            } else {
                setTimeout("sessionTimeout._showWarning()", timeToExpiration - this.warningLength);
            }
        }
    },
    _hideWarning: function() {
        if (Ext.Msg.isVisible()) {
            var timeToExpiration = this._timeTilExpire();
            if (timeToExpiration > 0) {
                Ext.Msg.hide();
                setTimeout("sessionTimeout._showWarning()", timeToExpiration - this.warningLength);
            } else {
                sessionTimeout._timeout();
            }
        }
    },
    _extendSession: function(button) {
        if (button == 'yes') {
            xmlHttp = new XMLHttpRequest();
            xmlHttp.open( "GET", 'keepAlive.jsp', false );
            xmlHttp.send( null );
            sessionTimeout._showWarning();
        } else {
            sessionTimeout._timeout();
        }
    }
};
