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

	public static TimeZone TIME_ZONE;
	private String localTimeZone;
	
	public static FastDateFormat ISO_DATETIME_TIME_ZONE_FORMAT_WITH_MILLIS;
	private boolean ignoreThrowable = false;
	private String hostname = new HostData().getHostName();
	private String systemID;

	public static String dateFormat(long timestamp) {
		return ISO_DATETIME_TIME_ZONE_FORMAT_WITH_MILLIS.format(timestamp);
	}

	@Override
	public void activateOptions() {
		TIME_ZONE = TimeZone.getTimeZone(localTimeZone);
		ISO_DATETIME_TIME_ZONE_FORMAT_WITH_MILLIS = FastDateFormat.getInstance("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", TIME_ZONE);
		hostname = new HostData().getHostName();
	}

	@SuppressWarnings("unchecked")
	@Override
	public String format(LoggingEvent event) {
		String mapAsJson = "" ;
		try{
			long timestamp = event.getTimeStamp();
			ParseLogObject plo = new ParseLogObject();
			Map message = (HashMap<String,Object>)event.getMessage();
			message.put(GenericFieldName.systemID.toString(), systemID);
			message.put("@"+GenericFieldName.timestamp.toString(), dateFormat(timestamp));
			message.put(GenericFieldName.hostname.toString(), hostname);
			message.put(GenericFieldName.requestTimestamp.toString(), dateFormat((Long)message.get(GenericFieldName.responseTimestamp.toString())));
			message.put(GenericFieldName.responseTimestamp.toString(), dateFormat((Long)message.get(GenericFieldName.responseTimestamp.toString())));
			plo.parse(message);
			mapAsJson = new ObjectMapper().writeValueAsString(message);
			return mapAsJson;
		} catch(Exception e) {
			LogLog.error(this.getClass().getName() + ": error while converting message to json string", e);
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

	public String getLocalTimeZone() {
		return localTimeZone;
	}

	public void setLocalTimeZone(String localTimeZone) {
		this.localTimeZone = localTimeZone;
	}
	

	
}
