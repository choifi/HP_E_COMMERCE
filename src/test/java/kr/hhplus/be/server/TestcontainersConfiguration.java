package kr.hhplus.be.server;

import jakarta.annotation.PreDestroy;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.utility.DockerImageName;

@Configuration
class TestcontainersConfiguration {

	public static final MySQLContainer<?> MYSQL_CONTAINER;

	static {
		MYSQL_CONTAINER = new MySQLContainer<>(DockerImageName.parse("mysql:8.0"))
			.withDatabaseName("hhplus")
			.withUsername("test")
			.withPassword("test")
			.withReuse(true);  // 컨테이너 재사용 활성화
		
		try {
			MYSQL_CONTAINER.start();
			System.setProperty("spring.datasource.url", MYSQL_CONTAINER.getJdbcUrl() + "?characterEncoding=UTF-8&serverTimezone=UTC");
			System.setProperty("spring.datasource.username", MYSQL_CONTAINER.getUsername());
			System.setProperty("spring.datasource.password", MYSQL_CONTAINER.getPassword());
		} catch (Exception e) {
			System.err.println("Failed to start MySQL container: " + e.getMessage());
			throw e;
		}
	}

	@PreDestroy
	public void preDestroy() {
		if (MYSQL_CONTAINER != null && MYSQL_CONTAINER.isRunning()) {
			MYSQL_CONTAINER.stop();
		}
	}
}