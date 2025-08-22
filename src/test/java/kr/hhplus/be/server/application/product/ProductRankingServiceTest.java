package kr.hhplus.be.server.application.product;

import kr.hhplus.be.server.domain.product.ProductRankingEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.redisson.api.RBatch;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RScoredSortedSetAsync;
import org.redisson.api.RedissonClient;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductRankingServiceTest {

    @Mock
    private RedissonClient redissonClient;
    
    @Mock
    private RBatch batch;
    
    @Mock
    private RScoredSortedSet<Object> rankingSet;
    @Mock
    private RScoredSortedSetAsync<Object> rankingSetAsync;
    
    private ProductRankingService productRankingService;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        productRankingService = new ProductRankingService(redissonClient);
        
        when(redissonClient.createBatch()).thenReturn(batch);
        when(batch.getScoredSortedSet(anyString())).thenReturn(rankingSetAsync);
        
     
        when(redissonClient.getScoredSortedSet(anyString())).thenReturn(rankingSet);
    }
    
    @Test
    void updateProductRanking_성공() {
        // given
        List<ProductRankingEvent.ProductRankingData> productDataList = Arrays.asList(
            new ProductRankingEvent.ProductRankingData(1, 5),
            new ProductRankingEvent.ProductRankingData(2, 3)
        );
        
        // when
        productRankingService.updateProductRanking(productDataList);
        
        // then
        verify(redissonClient).createBatch();
        verify(batch).execute();
    }
    
    @Test
    void getTopRankingProducts_성공() {
        // given
        List<Object> topProductIds = Arrays.asList("1", "2", "3", "4", "5", "6", "7", "8", "9", "10");
        when(rankingSet.valueRangeReversed(0, 9)).thenReturn(topProductIds);
        
        // when
        List<Integer> result = productRankingService.getTopRankingProducts();
        
        // then
        assertEquals(10, result.size());
        assertEquals(1, result.get(0));
        assertEquals(2, result.get(1));
        assertEquals(10, result.get(9));
    }
    
    @Test
    void getProductRanking_상품존재시_랭킹정보반환() {
        // given
        int productId = 1;
        when(rankingSet.getScore("1")).thenReturn(100.0);
        when(rankingSet.rank("1")).thenReturn(2);
        
        // when
        ProductRankingService.ProductRankingInfo result = productRankingService.getProductRanking(productId);
        
        // then
        assertEquals(1, result.getProductId());
        assertEquals(100, result.getSalesCount());
        assertEquals(2, result.getRank());
    }
    
    @Test
    void getProductRanking_상품없을시_기본값반환() {
        // given
        int productId = 999;
        when(rankingSet.getScore("999")).thenReturn(null);
        
        // when
        ProductRankingService.ProductRankingInfo result = productRankingService.getProductRanking(productId);
        
        // then
        assertEquals(999, result.getProductId());
        assertEquals(0, result.getSalesCount());
        assertEquals(-1, result.getRank());
    }
}
