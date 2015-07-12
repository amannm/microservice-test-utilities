package com.amannmalik.service.test;

import javax.json.JsonObject;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * Created by Amann Malik (amannmalik@gmail.com) on 6/16/2015.
 */
public class ServiceRequestFactory {

    public static HttpURLConnection get(String urlString) throws IOException {
        URL url = tryConstructUrl(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setDoInput(true);
        return connection;
    }

    public static HttpURLConnection put(String urlString) throws IOException {
        URL url = tryConstructUrl(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("PUT");
        return connection;
    }

    public static HttpURLConnection put(String locationString, String value) throws IOException {
        URL url = tryConstructUrl(locationString);
        byte[] body = value.getBytes(StandardCharsets.UTF_8);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("PUT");
        connection.setRequestProperty("Content-Type", "text/plain");
        connection.setRequestProperty("Content-Length", Integer.toString(body.length));
        connection.setDoOutput(true);
        try (OutputStream outputStream = connection.getOutputStream()) {
            try (DataOutputStream dataOutputStream = new DataOutputStream(outputStream)) {
                dataOutputStream.write(body);
            }
        }
        return connection;
    }

    public static HttpURLConnection post(String urlString, JsonObject object) throws IOException {
        URL url = tryConstructUrl(urlString);
        byte[] body = object.toString().getBytes(StandardCharsets.UTF_8);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Content-Length", Integer.toString(body.length));
        connection.setDoOutput(true);
        try (OutputStream outputStream = connection.getOutputStream()) {
            try (DataOutputStream dataOutputStream = new DataOutputStream(outputStream)) {
                dataOutputStream.write(body);
            }
        }
        return connection;
    }

    public static HttpURLConnection patch(String urlString, JsonObject object) throws IOException {
        URL url = tryConstructUrl(urlString);
        byte[] body = object.toString().getBytes(StandardCharsets.UTF_8);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        //HACK: enables use of PATCH on HttpURLConnection
        try {
            Field methodField = HttpURLConnection.class.getDeclaredField("method");
            methodField.setAccessible(true);
            methodField.set(connection, "PATCH");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Content-Length", Integer.toString(body.length));
        connection.setDoOutput(true);
        try (OutputStream outputStream = connection.getOutputStream()) {
            try (DataOutputStream dataOutputStream = new DataOutputStream(outputStream)) {
                dataOutputStream.write(body);
            }
        }
        return connection;
    }

    public static HttpURLConnection delete(String urlString) throws IOException {
        URL url = tryConstructUrl(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("DELETE");
        return connection;
    }


    private static URL tryConstructUrl(String urlString) {
        try {
            return new URL(urlString);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

}
