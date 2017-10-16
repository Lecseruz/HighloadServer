package application.util;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ParseCmd {

	private int port = 8080;
	private String rootDir = System.getProperty("user.dir") + "/static/http-test-suite";
	private int countOfThreads = Runtime.getRuntime().availableProcessors();

	private static String TAG = ParseCmd.class.getName() + ": ";
	private static Logger log = Logger.getLogger(ParseCmd.class.getName());

	public void parse(String[] args) {
		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("-r")) {
				rootDir = args[i + 1];
				File rootDirectory = new File(new File(rootDir).getAbsolutePath());
				if (!rootDirectory.exists()) {
					log.log(Level.WARNING, TAG + rootDir + " not found\n");
					System.exit(0);
				}
				try {
					rootDir = rootDirectory.getCanonicalPath();
				} catch (IOException e) {
					e.printStackTrace();
				}

			} else if (args[i].equals("-c")) {
				countOfThreads = Integer.parseInt(args[i + 1]);
			} else if (args[i].equals("-p")) {
				port = Integer.parseInt(args[i + 1]);
			}
		}
	}

	public final int getPort() {
		return port;
	}

	public final String getRootDir() {
		return rootDir;
	}

	public final int getCountOfThreads() {
		return countOfThreads;
	}

}
