package com.twm.ips.log.util;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class ObjectUtil {

	public static boolean isBasicDataType(Object obj) {
		if(obj instanceof String || obj instanceof Boolean || obj instanceof Date || obj instanceof BigDecimal ||
				obj instanceof Double || obj instanceof Integer || obj instanceof Long || obj instanceof Map ||
				obj instanceof List || obj instanceof Enum) {
			return true;
		}
		return false;
	}
}
