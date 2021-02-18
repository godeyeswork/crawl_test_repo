package jp.co.xxxyyyzzz.ws.interceptor;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@SuppressWarnings({"unused"})
@Aspect
@Component
public class LogInterceptor {
    private final Logger logger = LoggerFactory.getLogger(LogInterceptor.class);

    @Before("execution(public * jp.co.xxxyyyzzz..*(..)) && !within(LogInterceptor) && !within(jp.co.xxxyyyzzz.ws.controller.MaintController)")
    public void methodStart(JoinPoint joinPoint) {
        infoLog(joinPoint.getTarget().getClass().toString(), joinPoint.getSignature().getName(), "start");
    }

    @After("execution(public * jp.co.xxxyyyzzz..*(..)) && !within(LogInterceptor) && !within(jp.co.xxxyyyzzz.ws.controller.MaintController)")
    public void methodEnd(JoinPoint joinPoint) {
        infoLog(joinPoint.getTarget().getClass().toString(), joinPoint.getSignature().getName(), "end");
    }

    @Before("(execution(private * jp.co.xxxyyyzzz..*(..)) || execution(protected * jp.co.xxxyyyzzz..*(..))) && !within(LogInterceptor) && !within(jp.co.xxxyyyzzz.ws.controller.MaintController)")
    public void internalMethodStart(JoinPoint joinPoint) {
        debugLog(joinPoint.getTarget().getClass().toString(), joinPoint.getSignature().getName(), "start");
    }

    @After("(execution(private * jp.co.xxxyyyzzz..*(..)) || execution(protected * jp.co.xxxyyyzzz..*(..))) && !within(LogInterceptor) && !within(jp.co.xxxyyyzzz.ws.controller.MaintController)")
    public void internalMethodEnd(JoinPoint joinPoint) {
        debugLog(joinPoint.getTarget().getClass().toString(), joinPoint.getSignature().getName(), "end");
    }

    private void infoLog(String className, String methodName, String message){
        logger.info(className + "." + methodName + "() " + message + ".");
    }
    private void debugLog(String className, String methodName, String message){
        logger.debug(className + "." + methodName + "() " + message + ".");
    }

}
