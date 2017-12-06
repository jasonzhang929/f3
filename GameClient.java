import java.net.*;
import java.io.*;

class GameClient{
    Socket s;
    static DataInputStream din;
    static DataOutputStream dout;
    BufferedReader br;

    GameClient() throws IOException {
        s=new Socket("localhost",3333);
        din=new DataInputStream(s.getInputStream());
        dout=new DataOutputStream(s.getOutputStream());
        br=new BufferedReader(new InputStreamReader(System.in));
    }

    public static void send_data(String data) throws IOException {
        dout.writeUTF(data);
        dout.flush();
    }

    public static String receive_data() throws IOException {
        return din.readUTF();
    }

    public void close_client() throws IOException {
        dout.close();
        s.close();
    }

    public static void main(String args[])throws Exception{
        GameClient client = new GameClient();
        String result = "";
        while (!result.equals("stop")){
            result = client.receive_data();
            System.out.println(result);
        }
        send_data("stop");
        result = receive_data();
        client.close_client();
    }
}