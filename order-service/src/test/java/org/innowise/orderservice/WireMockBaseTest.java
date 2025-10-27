package org.innowise.orderservice;


import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

public class WireMockBaseTest {
    protected static WireMockServer wireMockServer = new WireMockServer(10001);

    @BeforeAll
    static void startWireMock() {
        wireMockServer.start();
        WireMock.configureFor("localhost", 10001);
    }

    @AfterAll
    static void stopWireMock() {
        wireMockServer.stop();
    }
}
