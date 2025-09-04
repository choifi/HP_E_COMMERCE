package kr.hhplus.be.server.infrastructure.message;

public interface MessageClient {
    <T> void send(String topic, String key, T message);
}
