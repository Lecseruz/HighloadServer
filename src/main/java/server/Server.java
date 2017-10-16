package server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;
import server.handlers.ServerHandler;
import server.handlers.HttpParserHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server {

    private static Map<String, ServerConfig> serversConfigs = new HashMap<String, ServerConfig>();

    private static Logger log = Logger.getLogger(Server.class.getName());
    private static String TAG = Server.class.getName() + ": ";

    private int port = 80;
    private String rootDir = System.getProperty("java.io.tmpdir");
    private int countOfThreads = Runtime.getRuntime().availableProcessors();
    private String serverName;

    public static class ServerBuilder {

        private int port = 80;
        private String rootDir = System.getProperty("java.io.tmpdir");
        private int countOfThreads = Runtime.getRuntime().availableProcessors();
        private String serverName = "Server";

        public ServerBuilder port(int port) {
            this.port = port;
            return this;
        }

        public ServerBuilder rootDir(String rootDir) {
            this.rootDir = rootDir;
            return this;
        }

        public ServerBuilder threads(int threads) {
            this.countOfThreads = threads;
            return this;
        }

        public ServerBuilder serverName(String serverName) {
            this.serverName = serverName;
            return this;
        }

        public Server build() {
            return new Server(port, rootDir, countOfThreads, serverName);
        }
    }

    public static ServerConfig getServerConfigByName(String name) {
        return serversConfigs.get(name);
    }

    private Server(int port, String rootDir, int countOfThreads, String serverName) {
        this.port = port;
        this.rootDir = rootDir;
        this.countOfThreads = countOfThreads;
        this.serverName = serverName;
        serversConfigs.put(serverName, new ServerConfig(port, rootDir));
    }

    public void run() throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup(this.countOfThreads);

        ServerBootstrap b = new ServerBootstrap();
        final EventExecutorGroup groupServerHandler = new DefaultEventExecutorGroup(this.countOfThreads);
        final EventExecutorGroup groupStringDecoder = new DefaultEventExecutorGroup(this.countOfThreads);
        final EventExecutorGroup groupHttpParser = new DefaultEventExecutorGroup(this.countOfThreads);

        b.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.INFO))
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(groupStringDecoder, "stringDecoder", new StringDecoder());
                        pipeline.addLast(groupHttpParser, "httpParser", new HttpParserHandler());

                        // ===========================================================
                        // 2. run handler with slow business logic
                        // in separate thread from I/O thread
                        // ===========================================================
                        pipeline.addLast(groupServerHandler, "serverHandler", new ServerHandler());
                    }
                });

        b.childOption(ChannelOption.SO_KEEPALIVE, true).childOption(ChannelOption.TCP_NODELAY, true);

        StringBuilder msg = new StringBuilder();
        msg.append(TAG).append("Netty Server bind to port: ").append(port).append(",\n").append("root dir: ")
                .append(rootDir).append(",\n").append("workers: ").append(this.countOfThreads).append("\n");

        log.log(Level.INFO, msg.toString());
        try {
            b.bind(port).sync();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static final class ServerConfig {

        private int port;
        private String rootDir;

        private ServerConfig(int port, String rootDir) {
            this.port = port;
            this.rootDir = rootDir;
        }

        public int getPort() {
            return port;
        }

        public String getRootDir() {
            return rootDir;
        }
    }
}
