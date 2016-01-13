package com.twm.ips.log4j.layout;

import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import org.apache.commons.lang.time.FastDateFormat;
import org.apache.log4j.Layout;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.spi.LoggingEvent;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.twm.ips.log.annotation.enums.GenericFieldName;

public class LogstashJsonEventLayout extends Layout {

	public static final TimeZone timeZone = TimeZone.getTimeZone("Asia/Taipei");
	
	public static final FastDateFormat ISO_DATETIME_TIME_ZONE_FORMAT_WITH_MILLIS = FastDateFormat
			.getInstance("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timeZone);
	private HashMap<String, Object> exceptionInformation;
	private boolean ignoreThrowable = false;
	private boolean activeIgnoreThrowable = ignoreThrowable;
	private String hostname = new HostData().getHostName();
	private String systemID;

	public static String dateFormat(long timestamp) {
		return ISO_DATETIME_TIME_ZONE_FORMAT_WITH_MILLIS.format(timestamp);
		
	}

	@Override
	public void activateOptions() {
		activeIgnoreThrowable = ignoreThrowable;
	}

	@Override
	public String format(LoggingEvent event) {
		String mapAsJson = "" ;
		try{
			long timestamp = event.getTimeStamp();
			ParseLogObject plo = new ParseLogObject();
			Map message = (HashMap<String,Object>)event.getMessage();
			message.put(GenericFieldName.SystemID.toString(), systemID);
			message.put("@"+GenericFieldName.timestamp.toString(), dateFormat(timestamp));
			message.put("@"+GenericFieldName.hostname.toString(), hostname);
			message.put("requestTimestamp", dateFormat((long)message.get("requestTimestamp")));
			message.put("responseTimestamp", dateFormat((long)message.get("responseTimestamp")));
			plo.parse(message);
			mapAsJson = new ObjectMapper().writeValueAsString(message);
			return mapAsJson;
		} catch(Exception e) {
			e.printStackTrace();
			LogLog.error(this.getClass().getName() + ": Convert to json string error", e);
		}
		return mapAsJson;
		
	}

	@Override
	public boolean ignoresThrowable() {
		return ignoreThrowable;
	}

	public String getSystemID() {
		return systemID;
	}

	public void setSystemID(String systemID) {
		this.systemID = systemID;
	}


	
}
