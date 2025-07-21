
## 포인트
CREATE TABLE point (
    point_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    current_point INT DEFAULT 0,  
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES user(user_id)
)

## 포인트 사용내역 
CREATE TABLE point_history (
    history_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    point_id INT NOT NULL,
    point_amount INT NOT NULL, -- (+충전, -사용)
    type VARCHAR(10) NOT NULL CHECK (type IN ('CHARGE', 'USE')) -- 'CHARGE=충전, USE=사용',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES user(user_id),
    FOREIGN KEY (point_id) REFERENCES point(point_id)
)

## 상품
CREATE TABLE product (
    product_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    price INT NOT NULL,
    stock INT NOT NULL,
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP,
)

## 주문
CREATE TABLE orders (
    order_id INT AUTO_INCREMENT PRIMARY KEY,  
    user_id INT NOT NULL,
    total_amount INT NOT NULL -- 할인 전 금액,
    discount_amount INT DEFAULT 0 -- 할인 금액,
    amount INT NOT NULL -- 실제 결제 금액,   
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING'  -- 상태: PENDING, CANCEL_REQUEST, EXPIRED, COMPLETED
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP,    
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES user(user_id)
)

## 주문 상품
CREATE TABLE order_item (
    order_item_id INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT NOT NULL,
    product_id INT NOT NULL,
    quantity INT NOT NULL,
    unit_price INT NOT NULL, -- 상품 단가
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (order_id) REFERENCES `orders`(order_id),
    FOREIGN KEY (product_id) REFERENCES product(product_id)
)   

## 주문 상태 관리
CREATE TABLE order_status_history (
    status_history_id INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT NOT NULL,
    status VARCHAR(20) NOT NULL
        CHECK (status IN ('PENDING', 'CANCEL_REQUESTED', 'EXPIRED', 'COMPLETED')),
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (order_id) REFERENCES orders(order_id)
);

## 주문 결제 내역 
CREATE TABLE payment (
    payment_id INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT NOT NULL,
    coupon_id INT NULL, -- 사용된 쿠폰
    total_amount INT NOT NULL, -- 할인 전 금액
    discount_amount INT DEFAULT 0, -- 할인 금액
    amount INT NOT NULL, -- 실제 결제 금액
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (order_id) REFERENCES `orders`(order_id)
)


## 유저
CREATE TABLE user (
    user_id INT AUTO_INCREMENT PRIMARY KEY,   
    name VARCHAR(100) NOT NULL,             
    password VARCHAR(255) NOT NULL,           
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP,
)

## 쿠폰 정책 
CREATE TABLE coupon_policy (
    policy_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    discount_rate INT NOT NULL COMMENT '할인률 (%)',
    valid_days INT NOT NULL, -- 만료기간 (7/5/1/3/999일)
    issued_count INT NOT NULL, -- 현재 발급 수량
    max_count INT NOT NULL, -- 발급 최대 가능 수량
    start_date DATETIME NOT NULL -- 쿠폰 시작 날짜 
    end_date DATETIME NOT NULL   -- 쿠폰 만료 날짜
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP,
)

# 쿠폰
CREATE TABLE coupon (
    coupon_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    policy_id INT NOT NULL,
    status VARCHAR(10) DEFAULT 'ISSUED' CHECK (status IN ('ISSUED', 'USED', 'EXPIRED')),
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    used_time DATETIME NULL -- 쿠폰 사용한 날짜
    FOREIGN KEY (user_id) REFERENCES user(user_id),
    FOREIGN KEY (policy_id) REFERENCES coupon_policy(policy_id)
)