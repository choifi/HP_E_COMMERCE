package kr.hhplus.be.server.domain.order;

import java.util.List;
import java.util.Optional;

// 주문 저장소 인
public interface OrderRepository {
    
    // 주문 저장
    Order save(Order order);
    
    // 주문 ID로 조회
    Optional<Order> findById(int orderId);
    
    // 사용자 ID로 주문 목록 조회
    List<Order> findByUserId(int userId);
} 