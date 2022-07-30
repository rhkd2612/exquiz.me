//package com.mumomu.exquizme.config;
//
//import org.springframework.context.annotation.Configuration;
//import org.springframework.messaging.simp.config.MessageBrokerRegistry;
//import org.springframework.scheduling.TaskScheduler;
//import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
//import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
//import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
//import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
//
//@Configuration
//@EnableWebSocketMessageBroker
//public class RoomConfig implements WebSocketMessageBrokerConfigurer {
//    @Override
//    public void registerStompEndpoints(StompEndpointRegistry registry) {
//        registry.addEndpoint("/ws/room").setAllowedOriginPatterns("*").withSockJS();
//    }
//
//    @Override
//    public void configureMessageBroker(MessageBrokerRegistry registry) {
//        // 해당 브로커를 통해 통신
//        registry.enableSimpleBroker("/queue", "/topic")
//                .setTaskScheduler(taskScheduler())
//                .setHeartbeatValue(new long[]{3000L, 3000L});
//        registry.setApplicationDestinationPrefixes("/app");
//    }
//
//    public TaskScheduler taskScheduler(){
//        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
//        taskScheduler.initialize();
//        return taskScheduler;
//    }
//}
