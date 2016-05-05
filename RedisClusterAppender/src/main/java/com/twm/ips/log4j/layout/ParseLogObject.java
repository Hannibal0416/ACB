package com.twm.ips.log4j.layout;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;

import com.twm.ips.log.annotation.LogClientIP;
import com.twm.ips.log.annotation.LogEncryption;
import com.twm.ips.log.annotation.LogField;
import com.twm.ips.log.annotation.LogFunctionName;
import com.twm.ips.log.annotation.LogID;
import com.twm.ips.log.annotation.LogIgnored;
import com.twm.ips.log.annotation.LogSourceTimestamp;
import com.twm.ips.log.annotation.enums.EncryptionAlogrithm;
import com.twm.ips.log.annotation.enums.GenericFieldName;
import com.twm.ips.log.util.ObjectUtil;

/**
 * 
 * @author HannibalHan
 *
 */
public class ParseLogObject {
	
	private Map<String,Object> params = new HashMap<String,Object>();
	
	public void parse(Map<String,Object> params) throws Exception {
		this.params = params;
		Object request = params.get("request");
		Object response = params.get("response");
		Map<String,Object> requestMap = new HashMap<String,Object>();
		Map<String,Object> reponseMap = new HashMap<String,Object>();
		//replace request bean to request map.

		if(response != null && !ObjectUtil.isBasicDataType(response)) {
			params.put("response", reponseMap);
			parseClass(response, reponseMap);
		} else if (ObjectUtil.isBasicDataType(response)){
			params.put("response", response);
		}
		if(request != null && !ObjectUtil.isBasicDataType(request)) {
			params.put("request", requestMap);
			parseClass(request, requestMap);
		} else if (ObjectUtil.isBasicDataType(request)) {
			params.put("request", request);
		}
	
	}

	private void parseClass(Object obj, Map<String, Object> objMap)
			throws Exception, ClassNotFoundException {
		Class superCls = obj.getClass().getSuperclass();
		while(superCls != null) {
			Field fieldList[] = superCls.getDeclaredFields();
			parseField(fieldList,obj,objMap,0);
			superCls = superCls.getSuperclass();
		}
		Class cls = Class.forName(obj.getClass().getName());
		Field fieldList[] = cls.getDeclaredFields();
		objMap.put("objectName", cls.getSimpleName());
		parseField(fieldList,obj,objMap,0);
	}
	
	private void parseField(Field[] fieldList,Object beanObj,Map params,int count) throws Exception {
		if(count++ < 10) {
			for (Field field : fieldList) {
	        	// if this field is a basic data type.
				field.setAccessible(true);
				if(field.get(beanObj) != null) {
					if(ObjectUtil.isBasicDataType(field.get(beanObj))) {
						Annotation annotationList[] = field.getAnnotations();
						if(annotationList.length > 0) {
							parseAnnotation(annotationList,field,beanObj,params);
						} else {
							params.put(field.getName(), field.get(beanObj));
						}
					// call itself.
					} else {
						if(!isIgnored(field.getAnnotations())) {
							Object innerBeanObj = field.get(beanObj);
							if(innerBeanObj != null) {
								Class cls = Class.forName(innerBeanObj.getClass().getName());
//								Map beanMap = new HashMap<String,Object>();
//								params.put(field.getName(), beanMap);
								parseField(cls.getDeclaredFields(),innerBeanObj,params,count);
							}
						}
					}
				}
	        }
		}
		
	}
	
	private boolean isIgnored(Annotation[] annotationList) {
		for(Annotation anno : annotationList) {
			if(anno instanceof LogIgnored) {
				return true;
			}
		}
		return false;
	}
	
	
	private void parseAnnotation(Annotation[] annotationList,Field field,Object beanObj,Map params) throws Exception{
		
		for(Annotation anno : annotationList) {
    		if(anno instanceof LogEncryption) {
    			LogEncryption logEncryption = field.getAnnotation(LogEncryption.class);
    			if(EncryptionAlogrithm.MD5.equals(logEncryption.value())) {
    				params.put(field.getName(), DigestUtils.md5Hex((String)field.get(beanObj)));
    			} else if(EncryptionAlogrithm.SHA1.equals(logEncryption.value())) {
    				params.put(field.getName(), DigestUtils.sha1Hex((String)field.get(beanObj)));
    			} else if(EncryptionAlogrithm.SHA2.equals(logEncryption.value())) {
    				params.put(field.getName(), DigestUtils.sha256Hex((String)field.get(beanObj)));
    			}
    		} else if (anno instanceof LogField) {
    			LogField logField = field.getAnnotation(LogField.class);
    			if(StringUtils.isNotEmpty(logField.value())) {
    				this.params.put(logField.value(), field.get(beanObj));
    				params.put(logField.value(), field.get(beanObj));
    			} else {
    				this.params.put(field.getName(), field.get(beanObj));
    				params.put(field.getName(), field.get(beanObj));
    			}
    			
    		} else if (anno instanceof LogID) {
    			if(this.params.get(GenericFieldName.id.toString()) != null) {
    				if(((LogID) anno).value()) {
    					this.params.put(GenericFieldName.id.toString(), field.get(beanObj));
        				params.put(field.getName(), field.get(beanObj));
    				}
    			} else {
    				this.params.put(GenericFieldName.id.toString(), field.get(beanObj));
    				params.put(field.getName(), field.get(beanObj));
    			}
    		} else if (anno instanceof LogIgnored) {
    			// do nothing
    		} else if (anno instanceof LogSourceTimestamp) {
    			this.params.put(GenericFieldName.sourceTimestamp.toString(), field.get(beanObj));
    			params.put(field.getName(), field.get(beanObj));
    		} else if (anno instanceof LogClientIP) {
    			this.params.put(GenericFieldName.clientIP.toString(), field.get(beanObj));
    			params.put(field.getName(), field.get(beanObj));
    		} else if (anno instanceof LogFunctionName) {
    			this.params.put(GenericFieldName.functionName.toString(), field.get(beanObj));
    			params.put(field.getName(), field.get(beanObj));
    			// if annotations are not in the IPS annotations.
    		} else {
    			params.put(field.getName(), field.get(beanObj));
    		}
    	}
	}
	

}
