package kr.hhplus.be.server.infrastructure.point;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface PointJpaRepository extends JpaRepository<PointEntity, Integer> {
    
    // 사용자 ID로 포인트 조회
    Optional<PointEntity> findByUserId(int userId);
} 