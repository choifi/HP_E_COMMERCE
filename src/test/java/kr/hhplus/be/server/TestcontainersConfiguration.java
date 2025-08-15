package kr.hhplus.be.server;

import jakarta.annotation.PreDestroy;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

@Configuration
class TestcontainersConfiguration {

	public static final MySQLContainer<?> MYSQL_CONTAINER;
	public static final GenericContainer<?> REDIS_CONTAINER;

	static {
		// MySQL 컨테이너 설정
		MYSQL_CONTAINER = new MySQLContainer<>(DockerImageName.parse("mysql:8.0"))
			.withDatabaseName("hhplus")
			.withUsername("test")
			.withPassword("test")
			.withReuse(true);
		
		// Redis 컨테이너 설정
		REDIS_CONTAINER = new GenericContainer<>(DockerImageName.parse("redis:7-alpine"))
			.withExposedPorts(6379)
			.withReuse(true);
		
		try {
			// MySQL 시작
			MYSQL_CONTAINER.start();
			System.setProperty("spring.datasource.url", MYSQL_CONTAINER.getJdbcUrl() + "?characterEncoding=UTF-8&serverTimezone=UTC");
			System.setProperty("spring.datasource.username", MYSQL_CONTAINER.getUsername());
			System.setProperty("spring.datasource.password", MYSQL_CONTAINER.getPassword());
			
			// Redis 시작
			REDIS_CONTAINER.start();
			System.setProperty("spring.data.redis.host", REDIS_CONTAINER.getHost());
			System.setProperty("spring.data.redis.port", String.valueOf(REDIS_CONTAINER.getMappedPort(6379)));
			
		} catch (Exception e) {
			System.err.println("Failed to start containers: " + e.getMessage());
			throw e;
		}
	}

	@PreDestroy
	public void preDestroy() {
		if (MYSQL_CONTAINER != null && MYSQL_CONTAINER.isRunning()) {
			MYSQL_CONTAINER.stop();
		}
		if (REDIS_CONTAINER != null && REDIS_CONTAINER.isRunning()) {
			REDIS_CONTAINER.stop();
		}
	}
}