package com.amannmalik.service.test;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.security.auth.Subject;
import javax.security.auth.login.Configuration;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.security.PrivilegedAction;
import java.util.Properties;

/**
 * Created by amann.malik on 7/2/2015.
 */
public class AuthenticationClient {

    private static final String AUTH_ENDPOINT = "http://aus-dataops1/auth/token?service=lead-pool-service";
    private static final String KRB5_CONF_PATH;

    static {
        ClassLoader classLoader = AuthenticationClient.class.getClassLoader();
        try {
            KRB5_CONF_PATH = Paths.get(classLoader.getResource("krb5.conf").toURI()).toString();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }


    public static String getAuthenticationToken(String user, String password) {

        Properties properties = System.getProperties();

        properties.setProperty("java.security.krb5.conf", KRB5_CONF_PATH);
        properties.setProperty("javax.security.auth.useSubjectCredsOnly", "false");
        //properties.setProperty("sun.security.krb5.debug", "true");

        JsonObject secureJson = getSecureJson(user, password, AUTH_ENDPOINT);
        String token = secureJson.getString("token");

        properties.remove("java.security.krb5.conf");
        properties.remove("javax.security.auth.useSubjectCredsOnly");
        //properties.remove("sun.security.krb5.debug");

        return token;

    }


    private static JsonObject getSecureJson(String user, String password, String urlString) {

        PrivilegedAction<JsonObject> action = () -> {
            try {
                HttpURLConnection connection = ServiceRequestFactory.get(urlString);
                int responseCode = connection.getResponseCode();
                switch (responseCode) {
                    case 200:
                        try (JsonReader reader = Json.createReader(connection.getInputStream())) {
                            return reader.readObject();
                        }
                    case 404:
                    case 400:
                    case 500:
                    default:
                        throw new RuntimeException("invalid HTTP response: " + responseCode);
                }
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        };

        try {
            Configuration oldConfiguration = Configuration.getConfiguration();
            Configuration.setConfiguration(new KerberosConfiguration());
            LoginContext loginContext = new LoginContext("", new KerberosCallbackHandler(user, password));
            loginContext.login();
            JsonObject object = Subject.doAs(loginContext.getSubject(), action);
            loginContext.logout();
            Configuration.setConfiguration(oldConfiguration);
            return object;
        } catch (LoginException ex) {
            throw new RuntimeException(ex);
        }
    }


}
