package server.util;

import config.IOUtil;
import org.apache.commons.io.FilenameUtils;
import server.Server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public class HttpResponse {
	private HttpHeaders httpHeaders;
	private HttpRequest request;
	private static final String INDEX = "index.html";
	private FileInputStream fis;
	private long fileLenght;
	private String method;
	private File file;

	public static class HttpResponseBuilder {
		private HttpRequest request;

		public HttpResponseBuilder httpRequest(HttpRequest request) {
			this.request = request;
			return this;
		}

		public HttpResponse build() {
			HttpResponse httpResp = new HttpResponse();
			httpResp.request = request;
			httpResp.method = request.getMethodName();
			httpResp.build();
			return httpResp;
		}
	}

	private void build() {

		if (!request.isValid()) {
			invalidRequest();
			return;
		}

		openFileInputStream();
		request = null;
	}

	private FileInputStream openFileInputStream() {

		String childPath = request.getPath();

		if (request.getFileExtension() == null) {
			if ((childPath.charAt(childPath.length() - 1) != '/'))
				childPath += "/";
			childPath += INDEX;
			request.setFileExtension(FilenameUtils.getExtension(childPath));
		}

		try {
			childPath = URLDecoder.decode(childPath, "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}

		file = IOUtil.getFile(Server.getServerConfigByName("Server").getRootDir(), childPath);

		if (!file.exists()) {
			if (childPath.contains(INDEX)) {
				forbidden();
				return null;
			}
			notFound();
			return null;
		}

		try {
			fis = new FileInputStream(file);
			fileLenght = file.length();
			OK();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			fis = null;
		}

		return fis;

	}

	public File getFile() {
		return file;
	}

	public FileInputStream getFileInputStream() {
		return this.fis;
	}

	private void OK() {
		httpHeaders = new HttpHeaders.HttpHeadersBuilder().code(ResponceCode.OK)
				.contentType(HttpUtil.getContentType(request.getFileExtension()))
				.contentLenght(Long.toString(fileLenght)).connection(HttpHeaders.KeepAlive).build();
	}

	private void invalidRequest() {
		httpHeaders = new HttpHeaders.HttpHeadersBuilder().code(ResponceCode.NOT_ALLOWED)
				.connection(HttpHeaders.CONNECTION_CLOSE).build();
	}

	private void notFound() {
		httpHeaders = new HttpHeaders.HttpHeadersBuilder().code(ResponceCode.NOT_FOUND)
				.connection(HttpHeaders.CONNECTION_CLOSE).build();
	}

	private void forbidden() {
		httpHeaders = new HttpHeaders.HttpHeadersBuilder().code(ResponceCode.FORBIDDEN)
				.connection(HttpHeaders.CONNECTION_CLOSE).build();
	}

	private static class ResponceCode {
		static final int OK = 200;
		static final int FORBIDDEN = 403;
		static final int NOT_FOUND = 404;
		static final int NOT_ALLOWED = 405;
	}

	public HttpHeaders getHttpHeaders() {
		return httpHeaders;
	}

	public long getFileLenght() {
		return fileLenght;
	}

	public String getMethodName() {
		return this.method;
	}

}
