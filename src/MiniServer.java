import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Deque;
import java.util.LinkedList;

public class MiniServer extends Thread{

    private Socket socket = null;
    private ServerMap map = null;
    private DataInputStream din;
    private DataOutputStream dout;
    public Deque<String> sendouts = new LinkedList();

    public MiniServer(Socket socket, ServerMap map) throws IOException {

        super("MiniServer");
        this.socket = socket;
        din=new DataInputStream(socket.getInputStream());
        dout=new DataOutputStream(socket.getOutputStream());
        this.map = map;

    }

    public void run(){
        String result = null;
        try {
            result = receive_data();
        } catch (IOException e) {
            e.printStackTrace();
        }
        while (!result.equals("stop")){
            try {
                map.excute(result, this);
                send_all();
                send_data("done");
                result = receive_data();
            }catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            send_data("stop");
            result = receive_data();
            close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        //Read input and process here
    }

    public void add_sendout(String data){
        sendouts.add(data);
    }

    public void send_all() throws IOException {
        while (sendouts.size() > 0){
            send_data(sendouts.pop());
        }
    }

    public void send_data(String data) throws IOException {
        //System.out.println("Send: " + data);
        dout.writeUTF(data);
        dout.flush();
    }

    public String receive_data() throws IOException {
        String data = din.readUTF();
        //System.out.println("Receive: " + data);
        return data;
    }

    public void close() throws IOException {
        din.close();
        socket.close();
    }
}