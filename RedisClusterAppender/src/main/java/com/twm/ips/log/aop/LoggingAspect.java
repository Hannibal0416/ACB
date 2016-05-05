package com.twm.ips.log.aop;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.lang.time.StopWatch;
import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

import com.twm.ips.log.annotation.LogFunctionName;
import com.twm.ips.log.annotation.LogID;
import com.twm.ips.log.annotation.LogIgnoreResponse;
import com.twm.ips.log.annotation.LogRequest;
import com.twm.ips.log.annotation.enums.GenericFieldName;

//@Aspect
public class LoggingAspect {
	private Logger logger = Logger.getLogger(LoggingAspect.class);
	private Logger redisClusterLogger = Logger.getLogger("redisClusterLogger");
//	@Around("execution(* com.twm.ips.log.aop.test.*.*(..))")
	public  Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
		StopWatch stopWatch = new StopWatch();
		Object rtnObj = null;
		String exceptionMessage = null;
		try {
			logger.info("RedisClusterLogger :" + joinPoint.getSignature().getDeclaringType().getSimpleName() + "." + joinPoint.getSignature().getName());
			stopWatch.start();
		} catch (Exception e) {
			logger.error(ExceptionUtils.getStackTrace(e));
		}
		
		try {
			return rtnObj = joinPoint.proceed();
		} catch (Exception e) {
			exceptionMessage = e.getMessage();
			throw e;
		} finally {
			
			try{
				stopWatch.stop();
				Map<String,Object> params = new HashMap<String,Object>();
				MethodSignature signature = (MethodSignature) joinPoint.getSignature();
				Annotation[][] annosArray = signature.getMethod().getParameterAnnotations();
				Object[] argsArray = joinPoint.getArgs();
				for(int i = 0 ; i < annosArray.length ; i++) {
					Annotation[] anno = annosArray[i];
					for( int j = 0 ; j < anno.length ; j++) {
						if(anno[j] instanceof LogRequest) {
							logger.debug("LogRequest" + argsArray[i]);
							if ( argsArray[i] != null) 
								params.put("request", argsArray[i] );
						} else if (anno[j] instanceof LogID) {
							if ( argsArray[i] != null) 
								params.put(GenericFieldName.id.toString(), argsArray[i] );
						} else if (anno[j] instanceof LogFunctionName) {
							if ( argsArray[i] != null) 
								params.put(GenericFieldName.functionName.toString(), argsArray[i] );
						}
						
					}
				}
				params.put(GenericFieldName.requestTimestamp.toString(), new Long(stopWatch.getStartTime()));
				params.put(GenericFieldName.responseTimestamp.toString(),  new Long(stopWatch.getStartTime() + stopWatch.getTime()));
				params.put(GenericFieldName.localTimeMillis.toString(),  new Long(stopWatch.getTime()));
				params.put(GenericFieldName.methodName.toString(), signature.getMethod().getName());
				params.put(GenericFieldName.classSimpleName.toString(), joinPoint.getSignature().getDeclaringType().getSimpleName());
				params.put(GenericFieldName.className.toString(), joinPoint.getSignature().getDeclaringTypeName());
				
//				Annotation[] annoArr = signature.getMethod().getAnnotations();
				boolean isLogResp= true;
				for(Annotation anno : signature.getMethod().getAnnotations()){
					if(anno instanceof LogIgnoreResponse) {
						isLogResp =false;
					}
				}
				
				if ( rtnObj != null && isLogResp) 
					params.put("response", rtnObj);
				if (exceptionMessage != null) {
					params.put(GenericFieldName.exceptionMessage.toString(), exceptionMessage);
				}
				redisClusterLogger.info(params);
				
			} catch (Exception e) {
				logger.error(ExceptionUtils.getStackTrace(e));
			}
		}
	}
}
