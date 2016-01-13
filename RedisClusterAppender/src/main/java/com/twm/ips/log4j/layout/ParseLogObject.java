package com.twm.ips.log4j.layout;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;

import com.twm.ips.log.annotation.LogEncryption;
import com.twm.ips.log.annotation.LogField;
import com.twm.ips.log.annotation.LogID;
import com.twm.ips.log.annotation.LogIgnored;
import com.twm.ips.log.annotation.LogLocation;
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

		if(response != null) {
			params.put("response", reponseMap);
			Class cls = Class.forName(request.getClass().getName());
			Field fieldList[] = cls.getDeclaredFields();
			parseField(fieldList,response,reponseMap);
		}
		if(request != null) {
			params.put("request", requestMap);
			Class cls = Class.forName(request.getClass().getName());
			Field fieldList[] = cls.getDeclaredFields();
			parseField(fieldList,request,requestMap);
		}
	
	}
	
	private void parseField(Field[] fieldList,Object beanObj,Map params) throws Exception {
		for (Field field : fieldList) {
        	// if this field is a basic data type.
			field.setAccessible(true);
			if(ObjectUtil.isBasicDataType(field.get(beanObj))) {
				Annotation annotationList[] = field.getAnnotations();
				if(annotationList.length > 0)
					parseAnnotation(annotationList,field,beanObj,params);
				else 
					params.put(field.getName(), field.get(beanObj));
			// call itself.
			} else {
				Object innerBeanObj = field.get(beanObj);
				Class cls = Class.forName(innerBeanObj.getClass().getName());
				Map beanMap = new HashMap<String,Object>();
				params.put(field.getName(), beanMap);
				parseField(cls.getDeclaredFields(),innerBeanObj,beanMap);
			}
        }
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
    			this.params.put(GenericFieldName.ID.toString(), field.get(beanObj));
    			params.put(GenericFieldName.ID.toString(), field.get(beanObj));
    		} else if (anno instanceof LogIgnored) {
    			// do nothing
    		} else if (anno instanceof LogLocation) {
    			this.params.put(GenericFieldName.GeoLocation.toString(), field.get(beanObj));
    		// if annotations are not in the IPS annotations.
    		} else {
    			params.put(field.getName(), field.get(beanObj));
    		}
    	}
	}
	

}
