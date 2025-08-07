package kr.hhplus.be.server.infrastructure.point;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PointHistoryJpaRepository extends JpaRepository<PointHistoryEntity, Integer> {
    
    // 사용자 ID로 포인트 내역 조회 
    @Query("SELECT ph FROM PointHistoryEntity ph WHERE ph.userId = :userId ORDER BY ph.createdTime DESC")
    List<PointHistoryEntity> findHistoryByUserId(@Param("userId") int userId);
} 