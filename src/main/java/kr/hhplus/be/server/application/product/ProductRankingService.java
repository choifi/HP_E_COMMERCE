package kr.hhplus.be.server.application.product;

import kr.hhplus.be.server.domain.product.ProductRankingEvent;
import org.redisson.api.RBatch;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
public class ProductRankingService {
    
    private static final String PRODUCT_RANKING_KEY = "product:ranking:sales";
    private static final int TOP_PRODUCTS_LIMIT = 10;
    
    private final RedissonClient redissonClient;
    
    public ProductRankingService(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }
    
    // 주문 완료 후 상품 판매량 랭킹 업데이트
    @Transactional
    public void updateProductRanking(List<ProductRankingEvent.ProductRankingData> productDataList) {
        try {
            log.debug("랭킹 업데이트 시작: {} 개", productDataList.size());
            
            // Redis 배치 처리
            RBatch batch = redissonClient.createBatch();
            
            for (ProductRankingEvent.ProductRankingData data : productDataList) {
                String productKey = String.valueOf(data.getProductId());
                double quantity = data.getQuantity();
                
                // 점수 증가
                batch.getScoredSortedSet(PRODUCT_RANKING_KEY).addScoreAsync(productKey, quantity);
            }
            
            // 실행
            batch.execute();
            
            log.debug("랭킹 업데이트 완료: {} 개", productDataList.size());
            
        } catch (Exception e) {
            log.error("랭킹 업데이트 실패", e);
            throw new RuntimeException("랭킹 업데이트 중 오류가 발생했습니다.", e);
        }
    }
    
    // 인기 상품 TOP N 조회
    @Cacheable(value = "product_ranking", key = "'top_" + TOP_PRODUCTS_LIMIT + "'")
    public List<Integer> getTopRankingProducts() {
        RScoredSortedSet<String> rankingSet = redissonClient.getScoredSortedSet(PRODUCT_RANKING_KEY);
        
        // 판매량 기준 내림차순으로 TOP N 상품 ID 조회
        return rankingSet.valueRangeReversed(0, TOP_PRODUCTS_LIMIT - 1)
            .stream()
            .map(Integer::parseInt)
            .collect(Collectors.toList());
    }
    
    // 특정 상품의 랭킹 정보 조회
    public ProductRankingInfo getProductRanking(int productId) {
        RScoredSortedSet<String> rankingSet = redissonClient.getScoredSortedSet(PRODUCT_RANKING_KEY);
        String productKey = String.valueOf(productId);
        
        Double score = rankingSet.getScore(productKey);
        if (score == null) {
            return new ProductRankingInfo(productId, 0, -1);
        }
        
        // 해당 상품의 순위 계산 (0부터 시작)
        int rank = rankingSet.rank(productKey);
        
        return new ProductRankingInfo(productId, score.intValue(), rank);
    }
    
    public static class ProductRankingInfo {
        private final int productId;
        private final int salesCount;
        private final int rank;
        
        public ProductRankingInfo(int productId, int salesCount, int rank) {
            this.productId = productId;
            this.salesCount = salesCount;
            this.rank = rank;
        }
        
        public int getProductId() { return productId; }
        public int getSalesCount() { return salesCount; }
        public int getRank() { return rank; }
    }
}

