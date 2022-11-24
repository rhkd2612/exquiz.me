package com.mumomu.exquizme.common.config;

import com.mumomu.exquizme.common.interceptor.HttpHandshakeInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.broker.BrokerAvailabilityEvent;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompReactorNettyCodec;
import org.springframework.messaging.tcp.reactor.ReactorNettyTcpClient;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import reactor.netty.tcp.SslProvider;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.function.Supplier;

@Slf4j
@Configuration
@EnableWebSocketMessageBroker
public class StompConfig implements WebSocketMessageBrokerConfigurer {
    @Value("${spring.activemq.activeMQServerList}")
    private String[] brokerRelayHost;
    @Value("${spring.activemq.broker-port}")
    private int brokerPort;
    @Value("${spring.activemq.user}")
    private String activeMqUsername;
    @Value("${spring.activemq.password}")
    private String activeMqPassword;

    private int index = 1;
    private int successIndex = -1;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 클라이언트가 https://{domain}/stomp/room으로 커넥션을 연결하고 메세지 통신을 할 수 있다.
        registry.addEndpoint("/stomp")
                .setAllowedOriginPatterns("*") // TODO setAllowedOrigins는 나중에 바꿔주어야한다(보안이슈)
                .withSockJS()
                .setInterceptors(new HttpHandshakeInterceptor());
        //.setClientLibraryUrl("https://cdnjs.cloudflare.com/ajax/libs/sockjs-client/1.1.2/sockjs.js");
    }

    // TODO 계속 ALB로 topic이 연결 되어있으면 요금이 계속 나올텐데, 연결 처리 방법은?
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.setApplicationDestinationPrefixes("/pub");

        ReactorNettyTcpClient<byte[]> client = new ReactorNettyTcpClient<>(tcpClient -> {
            return tcpClient
                    .remoteAddress(socketAddressSupplier())
                    .doOnConnected(connection -> {
                        log.info("new stomp connected");
                        successIndex = index;
                    })
                    .doOnDisconnected(connection -> {
                        // TODO 사용자 종료 시 관련 메세지 보내도록 설정
                        log.info("stomp disconnected");
                    })
                    .secure(SslProvider.defaultClientProvider());
        },
                new StompReactorNettyCodec());

        registry.setUserDestinationPrefix("/user");

        registry.enableStompBrokerRelay("/queue", "/topic")
                //.setUserDestinationBroadcast("/user/registry.broadcast")
                .setClientLogin(activeMqUsername)
                .setClientPasscode(activeMqPassword)
                .setSystemLogin(activeMqUsername)
                .setSystemPasscode(activeMqPassword)
                .setTcpClient(client);
    }

    private Supplier<? extends SocketAddress> socketAddressSupplier() {
        Supplier<? extends SocketAddress> socketAddressSupplier = () -> {
            index = (successIndex == -1) ? flipIndex() : successIndex;
            return new InetSocketAddress(brokerRelayHost[index], brokerPort);
        };

        return socketAddressSupplier;
    }

//    @Scheduled(fixedDelay = 100000000)
//    private void clearSuccessIndex(){
//        System.out.println("clear successIdx");
//        successIndex = -1;
//    }

    private int flipIndex(){
        return 1 - index;
    }
}

