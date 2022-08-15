package com.mumomu.exquizme.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompReactorNettyCodec;
import org.springframework.messaging.tcp.reactor.ReactorNettyTcpClient;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import reactor.netty.tcp.SslProvider;

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
        registry.addEndpoint("/stomp")
                .setAllowedOrigins("*"); // TODO setAllowedOrigins는 나중에 바꿔주어야한다(보안이슈)
                //.withSockJS();
                //.setClientLibraryUrl("https://cdnjs.cloudflare.com/ajax/libs/sockjs-client/1.1.2/sockjs.js");
    }



    // TODO 계속 ALB로 topic이 연결 되어있으면 요금이 계속 나올텐데, 연결 처리 방법은?
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        //registry.setPathMatcher(new AntPathMatcher("."));
        registry.setApplicationDestinationPrefixes("/pub");
        //registry.enableSimpleBroker("/sub");

        ReactorNettyTcpClient<byte[]> client = new ReactorNettyTcpClient<>(tcpClient -> tcpClient.host(brokerRelayHost).port(brokerPort)
                .secure(SslProvider.defaultClientProvider()),
                new StompReactorNettyCodec());

        registry.enableStompBrokerRelay("/queue","/topic")
                .setClientLogin(activeMqUsername)
                .setClientPasscode(activeMqPassword)
                .setSystemLogin(activeMqUsername)
                .setSystemPasscode(activeMqPassword)
                .setTcpClient(client);
    }
}

