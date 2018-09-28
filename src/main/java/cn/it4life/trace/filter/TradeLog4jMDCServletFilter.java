package cn.it4life.trace.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
//import java.text.SimpleDateFormat;
//import java.util.Date;
import java.util.UUID;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.OncePerRequestFilter;

import cn.it4life.trace.ReqTraceConstants;

/**
 * 
 * @ClassName: ReqTraceFilter
 * @Description:
 * @author it4life
 *
 */
public class TradeLog4jMDCServletFilter extends OncePerRequestFilter {
	private Logger logger = LoggerFactory.getLogger(TradeLog4jMDCServletFilter.class);

	// private static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd
	// HH:mm:ss");
	private static List<String> lstFilterUrl = new ArrayList<String>();

	// <filter>
	// <filter-name>reqTraceFilter</filter-name>
	// <filter-class>cn.it4life.trace.filter.TradeLog4jMDCServletFilter</filter-class>
	// <init-param>
	// <!-- url是requestURI,即端口之后的全路径（不含参数） -->
	// <param-name>filterUrl</param-name>
	// <param-value>/trade/submint;/xxx.jps;/abe/ang.png</param-value>
	// </init-param>
	// </filter>
	// <filter-mapping>
	// <filter-name>reqTraceFilter</filter-name>
	// <url-pattern>/*</url-pattern>
	// </filter-mapping>

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.web.filter.GenericFilterBean#initFilterBean()
	 */
	@Override
	protected void initFilterBean() throws ServletException {
		String filterUrlStr = this.getFilterConfig().getInitParameter("filterUrl");
		if (filterUrlStr != null && filterUrlStr.length() > 0) {
			logger.info("发现配置过滤url参数" + filterUrlStr);
			String[] urlArray = filterUrlStr.split(";");
			lstFilterUrl.addAll(Arrays.asList(urlArray));
		}
	}

	/**
	 * 异步处理可能会丢失数据
	 */
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		if (!lstFilterUrl.contains(request.getRequestURI())) {
			initContextHolders(request);
		}
		try {
			filterChain.doFilter(request, response);
		} finally {
			resetContextHolders();
		}
	}

	/**
	 * 获取或构造请求参数，默认是请求id和父类名称以及时间
	 * 
	 * @param request
	 */
	private void initContextHolders(HttpServletRequest request) {
		// ReqTraceMeta meta = new ReqTraceMeta();
		String requestId = request.getHeader(ReqTraceConstants.REQUEST_ID);
		if (requestId == null || requestId.length() == 0) {
			requestId = UUID.randomUUID().toString();
			// requestId=String.valueOf(IdGenerator.nextId(1, 1));
		}
		// meta.setRequestId(requestId);
		// String from = request.getHeader(ReqTraceUtil.REQUEST_FROM);
		// if (!StringUtils.isBlank(from)) {
		// meta.setFrom(from);
		// }
		// meta.setSelf(localName);
		org.apache.log4j.MDC.put(ReqTraceConstants.TRADE_TRACE_KEY, ReqTraceConstants.REQUEST_ID + ":" + requestId);
	}

	private void resetContextHolders() {
		org.apache.log4j.MDC.remove(ReqTraceConstants.TRADE_TRACE_KEY);
	}

	// public static String parseDate(Date date) {
	// String result = format.format(date);
	// return result;
	// }
}
