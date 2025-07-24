package kr.hhplus.be.server.infrastructure.order;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface OrderItemJpaRepository extends JpaRepository<OrderItemEntity, Integer> {
    // 기본 CRUD 메서드들은 JpaRepository에서 제공
} 