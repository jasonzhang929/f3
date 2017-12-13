import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class MainServer {

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = null;
        boolean listeningSocket = true;
        ArrayList<MiniServer> servers = new ArrayList<>();
        try {
            serverSocket = new ServerSocket(3333);
        } catch (IOException e) {
            System.err.println("Could not listen on port: 3333");
        }
        ServerMap map = new ServerMap(3000, 3000, servers);
        while(listeningSocket){
            Socket clientSocket = serverSocket.accept();
            MiniServer mini = new MiniServer(clientSocket, map);
            servers.add(mini);
            mini.start();
        }
        serverSocket.close();
    }
}