package server.util;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class HttpHeaders {

	private final static Map<Integer, String> codeDescription = new HashMap<Integer, String>();

	private final static String SERVER_NAME = "Server:";
	private final static String DATE = "Date:";
	private final static String CONNECTION = "Connection:";
	private final static String CONTENT_LENGHT = "Content-Length:";
	private final static String CONTENT_TYPE = "Content-Type:";

	public final static String CONNECTION_CLOSE = " Close";
	public final static String KeepAlive = " Keep-Alive";

	private StringBuilder headers;

	static {
		codeDescription.put(200, "OK");
		codeDescription.put(403, "Forbidden");
		codeDescription.put(404, "Not Found");
		codeDescription.put(405, "Method Not Allowed");
	}

	public static class HttpHeadersBuilder {

		private StringBuilder headers = new StringBuilder();
		private String codeStatus = "HTTP/1.1 {code} {message}\r\n";

		public HttpHeaders build() {

			HttpHeaders httpHeaders = new HttpHeaders(headers);

			headers.append("\r\n");

			return httpHeaders;
		}

		public HttpHeadersBuilder code(int codeStatus) {
			this.codeStatus = this.codeStatus.replaceAll("\\{code\\}", Integer.toString(codeStatus))
					.replaceAll("\\{message\\}", codeDescription.get(codeStatus));
			headers.append(this.codeStatus);
			setCommonHttpHeaders();
			return this;
		}

		public HttpHeadersBuilder connection(String connection) {
			setConnection(connection);
			return this;
		}

		public HttpHeadersBuilder contentType(String contentType) {
			headers.append(CONTENT_TYPE).append(" ").append(contentType).append("\r\n");
			return this;
		}

		public HttpHeadersBuilder contentLenght(String contentLenght) {
			headers.append(CONTENT_LENGHT).append(" ").append(contentLenght).append("\r\n");
			return this;
		}

		private void setCommonHttpHeaders() {
			headers.append(SERVER_NAME).append(" NettyServer\r\n").append(DATE).append(" ")
					.append(Calendar.getInstance().getTime().toString()).append("\r\n");
		}

		private void setConnection(String connection) {
			headers.append(CONNECTION).append(" " + connection + "\r\n");
		}

	}

	private HttpHeaders(StringBuilder headers) {
		this.headers = headers;
	}

	public StringBuilder getHttpHeaders() {
		return this.headers;
	}

}
