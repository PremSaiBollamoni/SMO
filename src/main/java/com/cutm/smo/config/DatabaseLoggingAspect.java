package com.cutm.smo.config;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import com.cutm.smo.util.LoggingUtil;
import java.util.Arrays;

/**
 * AOP Aspect for logging all database operations
 */
@Aspect
@Component
public class DatabaseLoggingAspect {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseLoggingAspect.class);

    /**
     * Safe serialization of objects to string
     */
    private String safeToString(Object obj) {
        try {
            if (obj == null) {
                return "null";
            }
            return obj.toString();
        } catch (Exception e) {
            return obj.getClass().getSimpleName() + "@" + Integer.toHexString(obj.hashCode());
        }
    }

    /**
     * Log all repository method calls
     */
    @Around("execution(* com.cutm.smo.repositories.*.*(..))")
    public Object logRepositoryMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        Object[] args = joinPoint.getArgs();

        long startTime = System.currentTimeMillis();

        try {
            logger.debug("=== DATABASE QUERY START ===");
            logger.debug("Repository: {}", className);
            logger.debug("Method: {}", methodName);
            logger.debug("Arguments: {}", Arrays.toString(args));

            Object result = joinPoint.proceed();

            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;

            logger.debug("=== DATABASE QUERY RESULT ===");
            logger.debug("Repository: {}", className);
            logger.debug("Method: {}", methodName);
            logger.debug("Execution Time: {} ms", duration);
            if (result != null) {
                logger.debug("Result: {}", safeToString(result));
            }

            if (duration > 500) {
                logger.warn("SLOW DATABASE QUERY: {}.{} took {} ms", className, methodName, duration);
            }

            return result;

        } catch (Exception e) {
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;

            logger.error("=== DATABASE QUERY ERROR ===");
            logger.error("Repository: {}", className);
            logger.error("Method: {}", methodName);
            logger.error("Execution Time: {} ms", duration);
            logger.error("Exception: {}", e.getMessage());
            logger.error("Stack Trace: ", e);

            throw e;
        }
    }

    /**
     * Log all service method calls
     */
    @Around("execution(* com.cutm.smo.services.*.*(..))")
    public Object logServiceMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        Object[] args = joinPoint.getArgs();

        long startTime = System.currentTimeMillis();

        try {
            logger.debug("=== SERVICE METHOD START ===");
            logger.debug("Service: {}", className);
            logger.debug("Method: {}", methodName);
            logger.debug("Arguments: {}", Arrays.toString(args));

            Object result = joinPoint.proceed();

            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;

            logger.debug("=== SERVICE METHOD RESULT ===");
            logger.debug("Service: {}", className);
            logger.debug("Method: {}", methodName);
            logger.debug("Execution Time: {} ms", duration);
            if (result != null) {
                logger.debug("Result: {}", safeToString(result));
            }

            if (duration > 1000) {
                logger.warn("SLOW SERVICE METHOD: {}.{} took {} ms", className, methodName, duration);
            }

            return result;

        } catch (Exception e) {
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;

            logger.error("=== SERVICE METHOD ERROR ===");
            logger.error("Service: {}", className);
            logger.error("Method: {}", methodName);
            logger.error("Execution Time: {} ms", duration);
            logger.error("Exception: {}", e.getMessage());
            logger.error("Stack Trace: ", e);

            throw e;
        }
    }
}
