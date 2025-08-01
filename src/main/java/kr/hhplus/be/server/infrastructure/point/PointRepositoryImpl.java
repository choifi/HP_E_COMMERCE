package kr.hhplus.be.server.infrastructure.point;

import kr.hhplus.be.server.domain.point.Point;
import kr.hhplus.be.server.domain.point.PointHistory;
import kr.hhplus.be.server.domain.point.PointRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class PointRepositoryImpl implements PointRepository {

    private final PointJpaRepository pointJpaRepository;
    private final PointHistoryJpaRepository pointHistoryJpaRepository;

    public PointRepositoryImpl(PointJpaRepository pointJpaRepository,
                              PointHistoryJpaRepository pointHistoryJpaRepository) {
        this.pointJpaRepository = pointJpaRepository;
        this.pointHistoryJpaRepository = pointHistoryJpaRepository;
    }

    @Override
    public Optional<Point> findByUserId(int userId) {
        return pointJpaRepository.findByUserId(userId)
            .map(PointEntity::toDomain);
    }

    @Override
    public Point save(Point point) {
        PointEntity entity = PointEntity.fromDomain(point);
        PointEntity savedEntity = pointJpaRepository.save(entity);
        return savedEntity.toDomain();
    }

    @Override
    public PointHistory saveHistory(PointHistory pointHistory) {
        PointHistoryEntity entity = PointHistoryEntity.fromDomain(pointHistory);
        PointHistoryEntity savedEntity = pointHistoryJpaRepository.save(entity);
        return savedEntity.toDomain();
    }

    @Override
    public List<PointHistory> findHistoryByUserId(int userId) {
        return pointHistoryJpaRepository.findHistoryByUserId(userId)
            .stream()
            .map(PointHistoryEntity::toDomain)
            .collect(Collectors.toList());
    }
} 