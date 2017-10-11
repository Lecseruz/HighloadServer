package config;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class IOUtil {

	public static final int ONE_MB = 8 * 1024 * 1204;

	public static File getFile(String documentRoot, String childPath) {
		return new File(documentRoot + childPath);
	}

	public static byte[] converInputStreamToByteArray(InputStream inputStream) {
		try {
			return IOUtils.toByteArray(inputStream);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
}
