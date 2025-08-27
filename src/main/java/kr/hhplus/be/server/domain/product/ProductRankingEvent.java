package kr.hhplus.be.server.domain.product;

import java.util.List;

public class ProductRankingEvent {
    private final List<ProductRankingData> productRankingDataList;
    
    public ProductRankingEvent(List<ProductRankingData> productRankingDataList) {
        this.productRankingDataList = productRankingDataList;
    }
    
    public List<ProductRankingData> getProductRankingDataList() {
        return productRankingDataList;
    }
    
    public static class ProductRankingData {
        private final int productId;
        private final int quantity;
        
        public ProductRankingData(int productId, int quantity) {
            this.productId = productId;
            this.quantity = quantity;
        }
        
        public int getProductId() { return productId; }
        public int getQuantity() { return quantity; }
    }
}
