import http from 'k6/http';
import { check, sleep } from 'k6';
import { Rate, Trend, Counter, Gauge } from 'k6/metrics';

// 커스텀 메트릭
export let errorRate = new Rate('errors');
export let couponResponseTime = new Trend('coupon_response_time');
export let successCount = new Counter('success_count');
export let soldOutCount = new Counter('sold_out_count');
export let alreadyIssuedCount = new Counter('already_issued_count');
export let virtualUsers = new Gauge('vus');
export let checksRate = new Rate('checks');
export let checksPerSecond = new Rate('checks_per_second');

// 100명 5분 테스트 설정
export let options = {
    stages: [
        { duration: '1m', target: 100 },  // 1분에 걸쳐 100명까지 증가
        { duration: '5m', target: 100 },  // 5분간 100명 유지
        { duration: '1m', target: 0 },    // 1분에 걸쳐 0명으로 감소
    ],
    thresholds: {
        http_req_duration: ['p(95)<1000'], // 95% 요청이 1초 이내
        http_req_failed: ['rate<0.10'],    // 에러율 10% 미만
        errors: ['rate<0.10'],             // 커스텀 에러율 10% 미만
    },
};

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';

export function setup() {
    console.log('🚀 쿠폰 발급 100명 5분 테스트 시작');
    console.log('⏱️ 테스트 시간: 5분');
    console.log('👥 동시 사용자: 100명');
    
    console.log('✅ 기존 쿠폰 정책 사용 (policyId: 1)');
    return { policyId: 1 };
}

export default function(data) {
    // Virtual Users 메트릭 업데이트
    virtualUsers.add(__VU);
    
    // 랜덤 사용자 ID 생성
    const userId = Math.floor(Math.random() * 10000) + 1;
    
    const couponRequest = {
        userId: userId,
        policyId: data.policyId  // setup에서 생성한 정책 사용
    };
    
    const startTime = Date.now();
    const response = http.post(`${BASE_URL}/api/coupons/request`, JSON.stringify(couponRequest), {
        headers: { 'Content-Type': 'application/json' },
    });
    const endTime = Date.now();
    
    const responseTime = endTime - startTime;
    couponResponseTime.add(responseTime);
    
    // 응답 분석
    let isSuccess = false;
    let isSoldOut = false;
    let isAlreadyIssued = false;
    
    if (response.status === 200) {
        const responseBody = JSON.parse(response.body);
        if (responseBody.message && responseBody.message.includes('성공')) {
            isSuccess = true;
            successCount.add(1);
        } else if (responseBody.message && responseBody.message.includes('소진')) {
            isSoldOut = true;
            soldOutCount.add(1);
        } else if (responseBody.message && responseBody.message.includes('이미 발급')) {
            isAlreadyIssued = true;
            alreadyIssuedCount.add(1);
        }
    }
    
    const success = check(response, {
        '쿠폰 발급 요청 성공': (r) => r.status === 200,
        '응답 시간 < 1초': (r) => r.timings.duration < 1000,
        '응답 시간 < 2초': (r) => r.timings.duration < 2000,
    });
    
    // Checks 메트릭 업데이트
    checksRate.add(success ? 1 : 0);
    checksPerSecond.add(success ? 1 : 0);
    errorRate.add(success ? 0 : 1);
    
    // 결과 로깅 (5% 확률로만 로그 출력)
    if (Math.random() < 0.05) {
        if (isSuccess) {
            console.log(`✅ 쿠폰 발급 성공 - UserId: ${userId}, ResponseTime: ${responseTime}ms`);
        } else if (isSoldOut) {
            console.log(`🔴 품절 - UserId: ${userId}, ResponseTime: ${responseTime}ms`);
        } else if (isAlreadyIssued) {
            console.log(`⚠️ 중복 발급 - UserId: ${userId}, ResponseTime: ${responseTime}ms`);
        } else {
            console.log(`❌ 요청 실패 - UserId: ${userId}, Status: ${response.status}, ResponseTime: ${responseTime}ms`);
        }
    }
    
    sleep(1); // 1초 대기
}

export function teardown(data) {
    console.log('✅ 쿠폰 발급 100명 5분 테스트 완료');
    console.log(`📊 성공: ${successCount.count}건`);
    console.log(`🔴 품절: ${soldOutCount.count}건`);
    console.log(`⚠️ 중복: ${alreadyIssuedCount.count}건`);
}
