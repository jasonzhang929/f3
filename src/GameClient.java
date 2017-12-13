import java.net.*;
import java.io.*;

class GameClient{
    Socket s;
    static DataInputStream din;
    static DataOutputStream dout;
    String server_ip;

    GameClient(String ip) throws IOException {
        server_ip = ip;
        s=new Socket(server_ip,3333);
        din=new DataInputStream(s.getInputStream());
        dout=new DataOutputStream(s.getOutputStream());
    }


    public static void send_data(String data) throws IOException {
        //System.out.println("Send " + data);
        dout.writeUTF(data);
        dout.flush();
    }

    public static String receive_data() throws IOException {
        String data = din.readUTF();
        //System.out.println("Receive: " + data);
        return data;
    }

    public void close_client() throws IOException {
        dout.close();
        s.close();
    }

    /*public static void main(String args[])throws Exception{
        GameClient client = new GameClient();
        String result = "";
        while (!result.equals("stop")){
            result = client.receive_data();
            System.out.println(result);
        }
        send_data("stop");
        client.close_client();
    }*/
}