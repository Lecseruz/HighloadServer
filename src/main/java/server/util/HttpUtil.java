package server.util;

import java.util.HashMap;
import java.util.Map;

public class HttpUtil {

	private static final Map<String, String> ContentType = new HashMap<String, String>();

	static {
		ContentType.put("html", "text/html");
		ContentType.put("css", "text/css");
		ContentType.put("js", "text/javascript");
		ContentType.put("jpg", "image/jpeg");
		ContentType.put("jpeg", "image/jpeg");
		ContentType.put("png", "image/png");
		ContentType.put("gif", "image/gif");
		ContentType.put("swf", "application/x-shockwave-flash");
		ContentType.put("txt", "text/plain");
	}

	public static String getContentType(String expansion) {
		return ContentType.get(expansion);
	}

	public static enum Method {
		HEAD, GET;

		public static boolean isHEAD(String m) {
			return m.equals(HEAD.toString());
		}

		public static boolean isGET(String m) {
			return m.equals(GET.toString());
		}

		public static String getMethodName(String m) {
			if (HEAD.toString().equals(m) || GET.toString().equals(m))
				return m;
			return null;
		}

	}

}
