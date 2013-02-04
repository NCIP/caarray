//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.services;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;

/**
 * Invoked by the Java security framework to authenticate remote caArray users.
 */
final class CaArrayCallbackHandler implements CallbackHandler {

    private final String username;
    private final String password;

    CaArrayCallbackHandler(final String username, final String password) {
        super();
        this.username = username;
        this.password = password;
    }

    public void handle(final Callback[] callbacks) throws UnsupportedCallbackException {
        for (int i = 0; i < callbacks.length; i++) {
            final Callback callback = callbacks[i];
            handle(callback);
        }

    }

    private void handle(final Callback callback) throws UnsupportedCallbackException {
        if (callback instanceof NameCallback) {
            handleNameCallback((NameCallback) callback);
        } else if (callback instanceof PasswordCallback) {
            handlePasswordCallback((PasswordCallback) callback);
        } else {
            throw new UnsupportedCallbackException(callback);
        }
    }

    private void handleNameCallback(final NameCallback callback) {
        callback.setName(username);
    }

    private void handlePasswordCallback(final PasswordCallback callback) {
        callback.setPassword(password.toCharArray());
    }

}
