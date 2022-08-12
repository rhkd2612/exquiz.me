package com.mumomu.exquizme.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jms.activemq.ActiveMQProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.net.InetSocketAddress;

@Configuration
@EnableWebSocketMessageBroker
public class StompConfig implements WebSocketMessageBrokerConfigurer {
    @Value("${spring.activemq.broker-relay-host}")
    private String brokerRelayHost;
    @Value("${spring.activemq.broker-virtual-host}")
    private String brokerVirtualHost;
    @Value("${spring.activemq.broker-port}")
    private int brokerPort;
    @Value("${spring.activemq.user}")
    private String activeMqUsername;
    @Value("${spring.activemq.password}")
    private String activeMqPassword;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 클라이언트가 ws://domain/stomp/room으로 커넥션을 연결하고 메세지 통신을 할 수 있다.
        registry.addEndpoint("/stomp/room")
                .setAllowedOrigins("*") // TODO setAllowedOrigins는 나중에 바꿔주어야한다(보안이슈)
                .withSockJS()
                .setClientLibraryUrl("https://cdnjs.cloudflare.com/ajax/libs/sockjs-client/1.1.2/sockjs.js");
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.setPathMatcher(new AntPathMatcher("."));
        registry.setApplicationDestinationPrefixes("/pub");
        registry.enableStompBrokerRelay("/queue","/topic","jms.topic.messages")
                .setRelayHost(brokerRelayHost)
                .setVirtualHost("/")
                .setRelayPort(brokerPort)
                .setClientLogin(activeMqUsername)
                .setClientPasscode(activeMqPassword);
    }
}

