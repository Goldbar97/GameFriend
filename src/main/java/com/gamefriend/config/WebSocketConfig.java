package com.gamefriend.config;

import com.gamefriend.component.JwtProvider;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.converter.StringMessageConverter;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.core.Authentication;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

  private final JwtProvider jwtProvider;

  @Override
  public void registerStompEndpoints(StompEndpointRegistry registry) {

    registry.addEndpoint("/ws/chat")
        .setAllowedOrigins("http://localhost:8080")
        .withSockJS();
  }

  @Override
  public void configureClientInboundChannel(ChannelRegistration registration) {

    registration.interceptors(new ChannelInterceptor() {
      @Override
      public Message<?> preSend(Message<?> message, MessageChannel channel) {

        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
          // STOMP 메시지에서 Authorization 헤더 추출
          String authorizationHeader = accessor.getFirstNativeHeader("Authorization");

          if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String jwtToken = authorizationHeader.substring(7); // "Bearer " 이후의 JWT 토큰 추출
            if (jwtProvider.validateToken(jwtToken)) {
              Authentication authentication = jwtProvider.getAuthentication(jwtToken);

              accessor.setUser(authentication); // 사용자 정보를 설정
              accessor.getSessionAttributes()
                  .put("AUTHENTICATION", authentication); // 인증 정보를 세션에 저장
            }
          }
        } else {
          // 다른 명령어의 경우 세션에서 인증 정보 복원
          Authentication authentication = (Authentication) accessor.getSessionAttributes()
              .get("AUTHENTICATION");
          if (authentication != null) {
            accessor.setUser(authentication); // 사용자 정보를 설정
          }
        }

        return message;
      }
    });
  }

  @Override
  public boolean configureMessageConverters(List<MessageConverter> messageConverters) {

    messageConverters.add(new StringMessageConverter());
    return false;
  }

  @Override
  public void configureMessageBroker(MessageBrokerRegistry registry) {

    registry.enableSimpleBroker("/topic");
    registry.setApplicationDestinationPrefixes("/app");
  }
}