package com.synectiks.dynModel.handlers;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import com.synectiks.commons.utils.IUtils;
import com.synectiks.dynModel.models.ReqLog;
import com.synectiks.dynModel.repositories.ReqLogRepository;

@Component
public class LoggingFilter extends OncePerRequestFilter {

	private static final List<MediaType> VISIBLE_TYPES = Arrays.asList(
			MediaType.valueOf("text/*"),
			MediaType.APPLICATION_FORM_URLENCODED,
			MediaType.APPLICATION_JSON,
			MediaType.APPLICATION_XML,
			MediaType.valueOf("application/*+json"),
			MediaType.valueOf("application/*+xml"),
			MediaType.MULTIPART_FORM_DATA);
	Logger log = LoggerFactory.getLogger(LoggingFilter.class);
	private static final Path path = Paths.get("logs/loggerReq.txt");
	private static BufferedWriter writer = null;

	private ReqLog reqLog = null;
	@Autowired
	private ReqLogRepository repo;

	@Override
	protected void doFilterInternal(HttpServletRequest request,
			HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		try {
			reqLog = new ReqLog();
			// Check if we have file at path
			checkPathExists();
			writer = Files.newBufferedWriter(path, Charset.forName("UTF-8"),
					StandardOpenOption.APPEND);
			if (isAsyncDispatch(request)) {
				filterChain.doFilter(request, response);
			} else {
				doFilterWrapped(wrapRequest(request), wrapResponse(response),
						filterChain);
			}
		} catch( Exception ex ) {
			logger.error( ex.getMessage(), ex);
		} finally {
			if (IUtils.isNull(writer)) {
				writer.close();
			}
		}
	}

	protected void doFilterWrapped(ContentCachingRequestWrapper request,
			ContentCachingResponseWrapper response, FilterChain filterChain)
			throws ServletException, IOException {
		try {
			beforeRequest(request, response);
			filterChain.doFilter(request, response);
		} finally {
			afterRequest(request, response);
			response.copyBodyToResponse();
			// Finally save entity log in db.
			repo.save(reqLog);
			// reset object
			reqLog = null;
		}
	}

	protected void beforeRequest(ContentCachingRequestWrapper request,
			ContentCachingResponseWrapper response) throws IOException {
		reqLog.setReqRemoteAddr(request.getRemoteAddr());
		if (log.isInfoEnabled()) {
			logRequestHeader(request, request.getRemoteAddr() + "|>");
		}
	}

	protected void afterRequest(ContentCachingRequestWrapper request,
			ContentCachingResponseWrapper response) throws IOException {
		if (log.isInfoEnabled()) {
			logRequestBody(request, request.getRemoteAddr() + "|>");
			logResponse(response, request.getRemoteAddr() + "|<");
		}
	}

	private void logRequestHeader(ContentCachingRequestWrapper request, String prefix)
			throws IOException {
		reqLog.setReqMethod(request.getMethod());
		reqLog.setReqUri(request.getRequestURI());
		String queryString = request.getQueryString();
		if (queryString == null) {
			printLines(prefix, request.getMethod(), request.getRequestURI());
			log.info("{} {} {}", prefix, request.getMethod(), request.getRequestURI());
		} else {
			reqLog.setReqQueryStr(queryString);
			printLines(prefix, request.getMethod(), request.getRequestURI(), queryString);
			log.info("{} {} {}?{}", prefix, request.getMethod(), request.getRequestURI(),
					queryString);
		}
		StringBuilder sb = new StringBuilder();
		Collections.list(request.getHeaderNames())
				.forEach(headerName -> Collections.list(request.getHeaders(headerName))
						.forEach(headerValue -> {
							sb.append(sb.length() > 0 ? ", " : "");
							sb.append(headerName + ": " + headerValue);
							log.info("{} {}: {}", prefix, headerName,headerValue);
						}));
		reqLog.setReqHeaders(sb.toString());
		printLines(prefix);
		String sesId = RequestContextHolder.currentRequestAttributes().getSessionId();
		if (IUtils.isNullOrEmpty(sesId)) {
			sesId = request.getSession().getId();
		}
		printLines(sesId);
		log.info("{}", prefix);

		log.info(" Session ID: ", sesId);
	}

	private void printLines(String... args) throws IOException {

		try {
			for (String varArgs : args) {
				writer.write(varArgs);
				writer.newLine();
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}

	}

	private void logRequestBody(ContentCachingRequestWrapper request, String prefix) {
		byte[] content = request.getContentAsByteArray();
		//reqLog.setReqContent(new String(content));
		reqLog.setReqContLen(content.length);
		reqLog.setReqContentType(request.getContentType());
		reqLog.setReqEncoding(request.getCharacterEncoding());
		if (content.length > 0) {
			reqLog.setReqContent(logContent(content, request.getContentType(),
					request.getCharacterEncoding(), prefix));
		}
	}

	private void logResponse(ContentCachingResponseWrapper response, String prefix)
			throws IOException {
		int status = response.getStatus();
		String strStatus = HttpStatus.valueOf(status).getReasonPhrase();
		reqLog.setResStatus(strStatus);
		printLines(prefix, String.valueOf(status), strStatus);
		log.info("{} {} {}", prefix, status, strStatus);
		StringBuilder sb = new StringBuilder();
		response.getHeaderNames().forEach(
				headerName -> response.getHeaders(headerName).forEach(headerValue -> {
					sb.append(sb.length() > 0 ? ", " : "");
					sb.append(headerName + ": " + headerValue);
					log.info("{} {}: {}", prefix, headerName, headerValue);
				}));
		reqLog.setResHeaders(sb.toString());
		printLines(prefix);
		log.info("{}", prefix);
		byte[] content = response.getContentAsByteArray();
		//reqLog.setResContent(new String(content));
		reqLog.setResContLen(content.length);
		reqLog.setResContentType(response.getContentType());
		reqLog.setResEncoding(response.getCharacterEncoding());
		if (content.length > 0) {
			reqLog.setResContent(logContent(content, response.getContentType(),
					response.getCharacterEncoding(), prefix));
		}
	}

	private String logContent(byte[] content, String contentType, String contentEncoding,
			String prefix) {
		MediaType mediaType = MediaType.valueOf(contentType);
		boolean visible = VISIBLE_TYPES.stream()
				.anyMatch(visibleType -> visibleType.includes(mediaType));
		String cntnt = null;
		if (visible) {
			try {
				String contentString = new String(content, contentEncoding);
				cntnt = contentString;
				Stream.of(contentString.split("\r\n|\r|\n")).forEach(line -> {
					try {
						printLines(line);
					} catch (IOException e) {
						e.printStackTrace();
					}
				});
				// log.info("{} {}", prefix, line));
			} catch (UnsupportedEncodingException e) {
				cntnt = "Unsupported encoding.";
				log.info("{} [{} bytes content]", prefix, content.length);
			}
		} else {
			try {
				cntnt = new String(content, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			log.info("{} [{} bytes content]", prefix, content.length);
		}
		return cntnt;
	}

	private static ContentCachingRequestWrapper wrapRequest(HttpServletRequest request) {
		if (request instanceof ContentCachingRequestWrapper) {
			return (ContentCachingRequestWrapper) request;
		} else {
			return new ContentCachingRequestWrapper(request);
		}
	}

	private static ContentCachingResponseWrapper wrapResponse(
			HttpServletResponse response) {
		if (response instanceof ContentCachingResponseWrapper) {
			return (ContentCachingResponseWrapper) response;
		} else {
			return new ContentCachingResponseWrapper(response);
		}
	}

	private static void checkPathExists() throws IOException {
		File f = path.toFile();
		if (!f.exists()) {
			f.getParentFile().mkdirs();
			f.createNewFile();
		}
	}
}