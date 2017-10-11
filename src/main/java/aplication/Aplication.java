package aplication;

import server.Server;

public class Aplication {

    public static void main(String[] args) throws Exception {
        Server server = new Server.ServerBuilder().port(8080)
                .serverName("Server")
                .rootDir("/home/magomed/IdeaProjects/WebServer")
                .threads(4).build();
        server.run();
    }
}
