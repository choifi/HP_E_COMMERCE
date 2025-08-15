package kr.hhplus.be.server.config;

import org.springframework.transaction.annotation.Transactional;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;

@Component
public class AopForTransaction {

    @Transactional(propagation = Propagation.REQUIRED)
    public Object proceed(final ProceedingJoinPoint joinPoint) throws Throwable {
        return joinPoint.proceed();
    }
}
