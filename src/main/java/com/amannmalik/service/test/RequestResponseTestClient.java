package com.amannmalik.service.test;

import org.glassfish.tyrus.client.ClientManager;
import org.junit.Assert;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.websocket.*;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by amann.malik on 7/2/2015.
 */
public class RequestResponseTestClient {

    private Session session;

    public JsonObject synchronousCall(JsonObject requestMessage, long timeout, String responseRoute) {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<JsonObject> objectReference = new AtomicReference<>();
        session.addMessageHandler(new MessageHandler.Whole<String>() {

            @Override
            public void onMessage(String t) {
                System.out.println(t);

                JsonObject object;
                try (JsonReader parser = Json.createReader(new StringReader(t))) {
                    object = parser.readObject();
                }

                if (responseRoute.equals(object.getString("message"))) {
                    session.removeMessageHandler(this);
                    objectReference.compareAndSet(null, object);
                    latch.countDown();
                }
            }
        });
        try {
            session.getBasicRemote().sendText(requestMessage.toString());
            Assert.assertTrue("message request timed out", latch.await(timeout, TimeUnit.SECONDS));
            return objectReference.get();
        } catch (IOException | InterruptedException e) {
            Assert.fail(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void connect(String serviceEndpoint, long timeout) {
        CountDownLatch latch = new CountDownLatch(1);
        try {
            final ClientEndpointConfig cec = ClientEndpointConfig.Builder.create().build();
            ClientManager client = ClientManager.createClient("org.glassfish.tyrus.container.jdk.client.JdkClientContainer");
            session = client.connectToServer(new Endpoint() {

                @Override
                public void onOpen(Session session, EndpointConfig config) {
                    latch.countDown();
                }
            }, cec, new URI(serviceEndpoint));
        } catch (URISyntaxException | DeploymentException | IOException ex) {
            Assert.fail(ex.getMessage());
        }
        try {
            Assert.assertTrue("connection request timed out", latch.await(timeout, TimeUnit.SECONDS));
        } catch (InterruptedException e) {
            Assert.fail(e.getMessage());
        }
    }

    public void disconnect() {
        if (session != null && session.isOpen()) {
            try {
                session.close();
                return;
            } catch (IOException ex) {
                Assert.fail(ex.getMessage());
            }
        } else {
            Assert.fail();
        }
    }

}
