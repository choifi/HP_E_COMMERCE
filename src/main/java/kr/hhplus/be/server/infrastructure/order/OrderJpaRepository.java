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
    
    // 사용자 ID로 주문 목록 조회
    @Query("SELECT o FROM OrderEntity o WHERE o.userId = :userId ORDER BY o.createdTime DESC")
    List<OrderEntity> findOrderListByUserId(int userId);
} 