package com.amannmalik.service.test;

import javax.security.auth.callback.*;
import java.io.IOException;

/**
 * Created by amann.malik on 7/1/2015.
 */
public class KerberosCallbackHandler implements CallbackHandler {

    private final String user;
    private final String password;

    public KerberosCallbackHandler(String user, String password) {
        this.user = user;
        this.password = password;
    }

    public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {

        for (Callback callback : callbacks) {

            if (callback instanceof NameCallback) {
                NameCallback nc = (NameCallback) callback;
                nc.setName(user);
            } else if (callback instanceof PasswordCallback) {
                PasswordCallback pc = (PasswordCallback) callback;
                pc.setPassword(password.toCharArray());
            } else {
                throw new UnsupportedCallbackException(callback, "Unknown Callback");
            }

        }
    }
}
