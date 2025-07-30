package kr.hhplus.be.server.infrastructure.order;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

// 주문 JPA Repository 인터페이스
@Repository
public interface OrderJpaRepository extends JpaRepository<OrderEntity, Integer> {
    
    // 주문 ID로 조회
    @Override
    @Query("SELECT o FROM OrderEntity o WHERE o.orderId = :orderId")
    Optional<OrderEntity> findById(@Param("orderId") Integer orderId);
    
    // 사용자 ID로 주문 목록 조회
    @Query("SELECT o FROM OrderEntity o WHERE o.userId = :userId ORDER BY o.createdTime DESC")
    List<OrderEntity> findByUserIdOrderByCreatedTimeDesc(@Param("userId") int userId);
} 