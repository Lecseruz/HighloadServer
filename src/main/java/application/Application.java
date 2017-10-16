package application;

import server.Server;

public class Application {

    public static void main(String[] args) throws Exception {
        Server server = new Server.ServerBuilder().port(80)
                .serverName("Server")
                .rootDir("/var/www/html")
                .threads(4).build();
        server.run();
    }
}
