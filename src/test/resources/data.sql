-- 테스트용 초기 데이터

-- 테스트 사용자 생성
INSERT INTO user (user_id, name, password) VALUES 
(1, '테스트 사용자1', 'password123'),
(2, '테스트 사용자2', 'password456');

-- 테스트 상품 생성
INSERT INTO product (product_id, name, price, stock) VALUES 
(1, '테스트 상품1', 1000, 10),
(2, '테스트 상품2', 2000, 5),
(3, '테스트 상품3', 3000, 15);

-- 테스트 포인트 생성
INSERT INTO point (point_id, user_id, current_point) VALUES 
(1, 1, 10000),
(2, 2, 5000);

-- 테스트 쿠폰 정책 생성
INSERT INTO coupon_policy (policy_id, name, discount_rate, valid_days, max_count, start_date, end_date) VALUES 
(1, '10% 할인 쿠폰', 10, 7, 100, NOW(), DATE_ADD(NOW(), INTERVAL 30 DAY)),
(2, '20% 할인 쿠폰', 20, 14, 50, NOW(), DATE_ADD(NOW(), INTERVAL 30 DAY)); 