package cn.it4life.trace;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @ClassName: ReqTraceContextLog4jMDCHolder
 * @Description: Request trace for log4j
 * @author it4life
 *
 */
public class ReqTraceContextLog4jMDCHolder {

	public static String getRequestId() {
		Object reqTraceIdStr = org.apache.log4j.MDC.get(ReqTraceConstants.TRADE_TRACE_KEY);
		if (null != reqTraceIdStr) {
			String currTheadLocalReqIdStr = (String) reqTraceIdStr;
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
			traceMap.put(ReqTraceConstants.REQUEST_ID, (String) requestId);
		}
		return traceMap;
	}
}
