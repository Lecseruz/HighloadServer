package server.util;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public class HttpRequest {
	private String methodName;
	private String path;
	private String httpVersion;
	private String fileExtension;
	private boolean isValid;
	private long originalHttpRequestHash;

	public static class HttpRequestBuilder {

		private String methodName;
		private String path;
		private String httpVersion;
		private String fileExtension;
		private boolean isValid = true;

		public HttpRequest build(String http) {

			HttpRequest httpReq = new HttpRequest();

			parseHttp(http);
			httpReq.setFileExtension(fileExtension);
			httpReq.setHttpVersion(httpVersion);
			httpReq.setMethodName(methodName);
			httpReq.setPath(path);
			httpReq.setValid(isValid);

			long hash = httpReq.toString().hashCode();
			httpReq.originalHttpRequestHash = hash;

			return httpReq;
		}

		private void parseHttp(String httpRequest) {

			if (StringUtils.isEmpty(httpRequest) || !httpRequest.contains("HTTP")) {
				isValid = false;
				return;
			}

			httpRequest = httpRequest.substring(0, httpRequest.indexOf("HTTP") + "HTTP/1.1".length());

			this.methodName = httpRequest.substring(0, httpRequest.indexOf(" "));

			if (HttpUtil.Method.getMethodName(this.methodName) == null) {
				isValid = false;
				return;
			}

			this.path = httpRequest.substring(methodName.length() + 1, httpRequest.indexOf("HTTP") - 1);
			if (path.contains("?"))
				this.path = path.substring(0, this.path.indexOf('?'));

			this.httpVersion = httpRequest.substring(path.length() + methodName.length() + 2, httpRequest.length());

			String[] split = httpVersion.split(" ");

			if (split.length > 1) {
				httpVersion = split[1];
			}

			if (!httpVersion.equals("HTTP/1.0") && !httpVersion.equals("HTTP/1.1")) {
				isValid = false;
				return;
			}

			getFileExtension();

		}

		private void getFileExtension() {
			try {
				this.fileExtension = FilenameUtils.getExtension(URLDecoder.decode(this.path, "UTF-8"));
				if (this.fileExtension.equals(""))
					this.fileExtension = null;
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getHttpVersion() {
		return httpVersion;
	}

	public void setHttpVersion(String httpVersion) {
		this.httpVersion = httpVersion;
	}

	public String getFileExtension() {
		return fileExtension;
	}

	public void setFileExtension(String fileExtension) {
		this.fileExtension = fileExtension;
	}

	public boolean isValid() {
		return isValid;
	}

	public void setValid(boolean isValid) {
		this.isValid = isValid;
	}

	public final long getHttpRequestHash() {
		return this.originalHttpRequestHash;
	}

	@Override
	public String toString() {
		StringBuilder string = new StringBuilder(1024);
		string.append("HttpRequest [methodName=").append(methodName).append(", path=").append(path)
				.append(", httpVersion=").append(httpVersion).append(", fileExtension=").append(fileExtension)
				.append(", isValid=").append(isValid).append("]");
		return string.toString();
	}

}
