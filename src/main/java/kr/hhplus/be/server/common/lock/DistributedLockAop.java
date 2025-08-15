package kr.hhplus.be.server.common.lock;

import kr.hhplus.be.server.common.support.CustomSpringELParser;
import kr.hhplus.be.server.config.AopForTransaction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * @DistributedLock 선언 시 수행되는 Aop class
 */
@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class DistributedLockAop {
    private static final String REDISSON_LOCK_PREFIX = "LOCK:";

    private final RedissonClient redissonClient;
    private final AopForTransaction aopForTransaction;

    @Around("@annotation(kr.hhplus.be.server.common.lock.DistributedLock)")
    public Object lock(final ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        DistributedLock distributedLock = method.getAnnotation(DistributedLock.class);

        String key = REDISSON_LOCK_PREFIX + CustomSpringELParser.getDynamicValue(signature.getParameterNames(), joinPoint.getArgs(), distributedLock.key());
        log.info("=== 분산락 시작 ===");
        log.info("메서드: {}, 락 키: {}", method.getName(), key);
        log.info("대기시간: {}, 임대시간: {}, 시간단위: {}", distributedLock.waitTime(), distributedLock.leaseTime(), distributedLock.timeUnit());
        
        RLock rLock = redissonClient.getLock(key);
        log.info("Redisson 락 객체 생성 완료");

        try {
            log.info("락 획득 시도 중...");
            boolean available = rLock.tryLock(distributedLock.waitTime(), distributedLock.leaseTime(), distributedLock.timeUnit());
            log.info("락 획득 결과: {}", available);
            
            if (!available) {
                log.warn("락 획득 실패! 메서드: {}, 키: {}", method.getName(), key);
                throw new IllegalStateException("분산락 획득에 실패했습니다. 잠시 후 다시 시도해주세요.");
            }

            log.info("락 획득 성공! 메서드 실행 시작");
            // AopForTransaction을 통해 트랜잭션 전파
            Object result = aopForTransaction.proceed(joinPoint);
            log.info("메서드 실행 완료, 결과: {}", result);
            return result;
        } catch (InterruptedException e) {
            log.error("락 획득 중 인터럽트 발생: {}", e.getMessage());
            Thread.currentThread().interrupt();
            throw new InterruptedException("분산락 대기 중 인터럽트가 발생했습니다.");
        } catch (Exception e) {
            log.error("메서드 실행 중 예외 발생: {}", e.getMessage(), e);
            throw e;
        } finally {
            try {
                if (rLock.isHeldByCurrentThread()) {
                    rLock.unlock();
                    log.info("락 해제 완료");
                } else {
                    log.info("현재 스레드가 락을 보유하지 않음");
                }
            } catch (IllegalMonitorStateException e) {
                log.info("Redisson Lock Already UnLock serviceName={} key={}",
                        method.getName(), key);
            }
            log.info("=== 분산락 종료 ===");
        }
    }
}