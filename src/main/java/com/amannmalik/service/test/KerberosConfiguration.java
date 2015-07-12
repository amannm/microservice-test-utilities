package com.amannmalik.service.test;

import javax.security.auth.login.AppConfigurationEntry;
import javax.security.auth.login.Configuration;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by amann.malik on 7/2/2015.
 */
public class KerberosConfiguration extends Configuration {

    @Override
    public AppConfigurationEntry[] getAppConfigurationEntry(String name) {
        Map<String, String> options = new HashMap<>();
        options.put("doNotPrompt", "false");
        options.put("useTicketCache", "false");
        AppConfigurationEntry[] entries = new AppConfigurationEntry[1];
        entries[0] = new AppConfigurationEntry("com.sun.security.auth.module.Krb5LoginModule", AppConfigurationEntry.LoginModuleControlFlag.REQUIRED, options);
        return entries;
    }
}


