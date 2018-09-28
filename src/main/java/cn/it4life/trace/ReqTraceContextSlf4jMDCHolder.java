package cn.it4life.trace;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @ClassName: ReqTraceContextSlf4jMDCHolder
 * @Description: Request trace for slf4j
 * @author it4life
 *
 */
public class ReqTraceContextSlf4jMDCHolder {

	public static String getRequestId() {
		String currTheadLocalReqIdStr = org.slf4j.MDC.get(ReqTraceConstants.TRADE_TRACE_KEY);
		if (currTheadLocalReqIdStr != null && currTheadLocalReqIdStr.length() > 0) {
			if (currTheadLocalReqIdStr.contains(":")) {
				int splitIndex = currTheadLocalReqIdStr.indexOf(":") + 1;
				if (currTheadLocalReqIdStr.length() > splitIndex) {
					return currTheadLocalReqIdStr.substring(splitIndex);
				}
			}
		}
		return "";
	}

	/**
	 * 封装http请求头信息，用于消息ID跟踪传递
	 * 
	 * @return
	 */
	public static Map<String, String> getTraceHeaders() {
		Map<String, String> traceMap = new HashMap<String, String>();
		String requestId = getRequestId();
		if (requestId != null && requestId.length() > 0) {
			traceMap.put(ReqTraceConstants.REQUEST_ID, requestId);
		}
		return traceMap;
	}
}
