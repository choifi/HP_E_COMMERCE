package kr.hhplus.be.server.application.product;

import kr.hhplus.be.server.domain.product.ProductRankingEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductRankingEventHandler {
    
    private final ProductRankingService productRankingService;
    
    // 주문 완료 이벤트 -> 상품 랭킹 업데이트
    @EventListener
    @Async
    public void handleProductRankingEvent(ProductRankingEvent event) {
        try {
            log.info("상품 랭킹 업데이트 시작: {}개 상품", event.getProductRankingDataList().size());
            
            productRankingService.updateProductRanking(event.getProductRankingDataList());
            
            log.info("상품 랭킹 업데이트 완료");
        } catch (Exception e) {
            log.error("상품 랭킹 업데이트 실패", e);
        }
    }
}

