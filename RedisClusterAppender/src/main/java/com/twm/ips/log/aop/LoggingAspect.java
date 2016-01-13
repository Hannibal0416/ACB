package com.twm.ips.log.aop;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.lang.time.StopWatch;
import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

import com.twm.ips.log.annotation.LogID;
import com.twm.ips.log.annotation.LogRequest;
import com.twm.ips.log.annotation.enums.GenericFieldName;

//@Aspect
public class LoggingAspect {
	private Logger logger = Logger.getLogger(LoggingAspect.class);
	private Logger redisLogger = Logger.getLogger("redis");
//	@Around("execution(* com.twm.ips.log.aop.test.*.*(..))")
	public void logAround(ProceedingJoinPoint joinPoint) throws Throwable {
		StopWatch stopWatch = new StopWatch();
		Object rtnObj = null;
		try {
			logger.debug("RedisClusterLogger :" + joinPoint.getClass().getName() + "." + joinPoint.getSignature().getName());
			stopWatch.start();
		} catch (Exception e) {
			logger.error(ExceptionUtils.getStackTrace(e));
		}
		
		try {
			rtnObj = joinPoint.proceed();
		} finally {
			
			try{
				stopWatch.stop();
				Map<String,Object> params = new HashMap<String,Object>();
				MethodSignature signature = (MethodSignature) joinPoint.getSignature();
				Annotation[][] annosArray = signature.getMethod().getParameterAnnotations();
				Object[] argsArray = joinPoint.getArgs();
				for(int i = 0 ; i < annosArray.length ; i++) {
					Annotation[] anno = annosArray[i];
					if(anno.length > 0) {
						if(anno[0] instanceof LogRequest) {
							logger.debug("LogRequest" + argsArray[i]);
							if ( argsArray[i] != null) 
								params.put("request", argsArray[i] );
						} else if (anno[0] instanceof LogID) {
							if ( argsArray[i] != null) 
								params.put(GenericFieldName.ID.toString(), argsArray[i] );
						}
						
					}
				}
				params.put("requestTimestamp", stopWatch.getStartTime());
				params.put("responseTimestamp", stopWatch.getStartTime() + stopWatch.getTime());
				params.put("totalTimeMillis", stopWatch.getTime());
				if ( rtnObj != null) 
					params.put("response", rtnObj);
				redisLogger.fatal(params);
				
			} catch (Exception e) {
				logger.error(ExceptionUtils.getStackTrace(e));
			}
		}
	}
}
