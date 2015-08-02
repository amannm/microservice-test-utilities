package com.amannmalik.service.test;

import javax.net.ServerSocketFactory;
import java.net.ServerSocket;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by Amann Malik (amannmalik@gmail.com) on 7/11/2015.
 */
public class LocalEnvironment {

    public static int findRandomOpenPort() {
        return findRandomOpenPort(1024, 65535);
    }

    public static int findRandomOpenPort(int minPort, int maxPort) {
        if (minPort < 0) {
            throw new IllegalArgumentException("Specified minimum port [" + minPort + "] is less than 0");
        }
        if (maxPort > 65535) {
            throw new IllegalArgumentException("Specified maximum port [" + maxPort + "] is greater than 65535");
        }
        if (minPort > maxPort) {
            throw new IllegalArgumentException("Specified minimum port [" + minPort + "] is greater than specified maximum port [" + maxPort + "]");
        }
        List<Integer> ports = IntStream.range(minPort, maxPort).boxed().collect(Collectors.toList());
        Collections.shuffle(ports);
        Optional<Integer> integer = ports.stream().filter(LocalEnvironment::isOpenPort).findAny();
        if (integer.isPresent()) {
            return integer.get();
        } else {
            throw new RuntimeException("Unable to find open port within specified range [" + minPort + "-" + maxPort + "]");
        }
    }

    private static boolean isOpenPort(int port) {
        try {
            ServerSocket serverSocket = ServerSocketFactory.getDefault().createServerSocket(port);
            serverSocket.close();
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

}
