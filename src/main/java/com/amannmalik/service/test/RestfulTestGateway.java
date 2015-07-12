package com.amannmalik.service.test;

import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

/**
 * Created by Amann Malik (amannmalik@gmail.com) on 6/18/2015.
 */
public class RestfulTestGateway {

    private static final Logger LOG = LoggerFactory.getLogger(RestfulTestGateway.class);

    public static String generateNew(String endpointString, JsonObject object) throws IOException {
        LOG.info("attempting to POST resource to {} with {}", endpointString, object.toString());
        HttpURLConnection connection = ServiceRequestFactory.post(endpointString, object);
        int responseCode = connection.getResponseCode();
        switch (responseCode) {
            case 201:
                return connection.getHeaderField("Location");
            case 400:
            case 500:
                Assert.fail("incorrect response: " + responseCode);
                break;
            default:
                Assert.fail("invalid response: " + responseCode);
                break;
        }
        return null;
    }

    public static String addNew(String resourceUriString) throws IOException {
        LOG.info("attempting to PUT resource to {} with {}", resourceUriString);
        HttpURLConnection connection = ServiceRequestFactory.put(resourceUriString);
        int responseCode = connection.getResponseCode();
        switch (responseCode) {
            case 201:
                return connection.getHeaderField("Location");
            case 400:
            case 500:
                Assert.fail("incorrect response: " + responseCode);
                break;
            default:
                Assert.fail("invalid response: " + responseCode);
                break;
        }
        return null;
    }


    public static JsonObject readExistent(String locationString) throws IOException {
        HttpURLConnection connection = ServiceRequestFactory.get(locationString);
        int responseCode = connection.getResponseCode();
        switch (responseCode) {
            case 200:
                try (InputStream inputStream = connection.getInputStream()) {
                    try (JsonReader parser = Json.createReader(inputStream)) {
                        return parser.readObject();
                    }
                }
            case 304:
            case 404:
            case 500:
                Assert.fail("incorrect response: " + responseCode);
                break;
            default:
                Assert.fail("invalid response: " + responseCode);
                break;
        }
        return null;
    }

    public static void transformExistent(String locationString, JsonObject object) throws IOException {
        LOG.info("attempting to PATCH resource to {} with {}", locationString, object.toString());
        HttpURLConnection connection = ServiceRequestFactory.patch(locationString, object);
        int responseCode = connection.getResponseCode();
        switch (responseCode) {
            case 204:
                break;
            case 400:
            case 404:
            case 500:
                Assert.fail("incorrect response: " + responseCode);
                break;
            default:
                Assert.fail("invalid response: " + responseCode);
                break;
        }
    }

    public static void setExistent(String locationString, String value) throws IOException {
        HttpURLConnection connection = ServiceRequestFactory.put(locationString, value);
        int responseCode = connection.getResponseCode();
        switch (responseCode) {
            case 204:
                break;
            case 400:
            case 404:
            case 500:
                Assert.fail("incorrect response: " + responseCode);
                break;
            default:
                Assert.fail("invalid response: " + responseCode);
                break;
        }
    }


    public static void deleteExistent(String locationString) throws IOException {
        HttpURLConnection connection = ServiceRequestFactory.delete(locationString);
        int responseCode = connection.getResponseCode();
        switch (responseCode) {
            case 204:
                break;
            case 404:
            case 500:
                Assert.fail("incorrect response: " + responseCode);
                break;
            default:
                Assert.fail("invalid response: " + responseCode);
                break;
        }
    }

    public static void deleteNonExistent(String locationString) throws IOException {
        HttpURLConnection connection = ServiceRequestFactory.delete(locationString);
        int responseCode = connection.getResponseCode();
        switch (responseCode) {
            case 404:
                break;
            case 204:
            case 500:
                Assert.fail("incorrect response: " + responseCode);
                break;
            default:
                Assert.fail("invalid response: " + responseCode);
                break;
        }
    }


}
