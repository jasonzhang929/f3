import java.net.*;
import java.io.*;
import java.util.ArrayList;

class GameServer{
    ArrayList<Socket> sockets = new ArrayList<>();
    ServerSocket ss;
    Socket s;
    DataInputStream din;
    DataOutputStream dout;
    BufferedReader br;

    GameServer() throws IOException {
        ss=new ServerSocket(3333);
        s=ss.accept();
        din=new DataInputStream(s.getInputStream());
        dout=new DataOutputStream(s.getOutputStream());
        br=new BufferedReader(new InputStreamReader(System.in));
    }

    public static void main(String args[])throws Exception{
        GameServer server = new GameServer();
        server.start_game();

    }

    public void start_game() throws IOException {
        ServerMap map = new ServerMap(3000, 3000, this);
        send_data("set");
        String result = "";
        while (!result.equals("finished")){
            result = receive_data();
        }
        send_data("stop");

        //ServerMap map = new ServerMap(3000, 3000, this);
        close();


    }
    public void send_data(String data) throws IOException {
        dout.writeUTF(data);
        dout.flush();
    }

    public String receive_data() throws IOException {
        return din.readUTF();
    }

    public void close() throws IOException {
        din.close();
        s.close();
        ss.close();
    }


}