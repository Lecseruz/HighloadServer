package application;

import server.Server;

public class Application {

    public static void main(String[] args) throws Exception {
//        Server server = new Server.ServerBuilder().port(80)
//                .serverName("Server")
//                .rootDir("/home/magomed/Highload/http-test-suite")
//                .threads(4).build();
//        server.run();
        Server server = new Server.ServerBuilder().port(8080)
                .serverName("Server")
                .rootDir("/home/magomed/Highload/HigloadServer")
                .threads(4).build();
        server.run();
    }
}
