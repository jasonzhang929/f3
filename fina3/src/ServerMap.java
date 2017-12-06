import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Random;

public class ServerMap {
    private int size_x, size_y;
    private int snakeid_counter;
    public LinkedHashMap<Integer, Food> foods = new LinkedHashMap<>();
    public LinkedHashMap<Integer, ServerSnake> snakes = new LinkedHashMap<>();
    public static LinkedHashMap<Integer, Integer> snakebodys = new LinkedHashMap<>();
    private GameServer server;


    public ServerMap(int xsize, int ysize, GameServer server) throws IOException {
        this.server = server;
        snakeid_counter = 0;
        size_x = xsize;
        size_y = ysize;
        Random r = new Random();
        int Low = -500;
        int High = 500;
        for (int i = 0; i < 20; i++){
            add_snake(r.nextInt(High-Low) + Low+1.0,r.nextInt(High-Low) + Low+1.0);
        }
        Low = -xsize;
        High = xsize;
        for (int i = 0; i < 1000; i++){
            add_food(r.nextInt(High-Low) + Low,r.nextInt(High-Low) + Low, 1);
        }
    }

    public String update_snake(int snakeid, double angle) throws IOException {
        ServerSnake snake = snakes.get(snakeid);
        if (snake.update(angle, snakebodys, foods, this)){
            remove_snake(snakeid);
            return "die " + Integer.toString(snakeid);
        }
        else{
            return "update " + Integer.toString(snakeid) + " " + Double.toString(snake.lastangle);
        }
    }


    public void add_food(int x, int y, int size) throws IOException {
        foods.put(x*size_x*2 + y, new Food(x, y, size));
        server.send_data("addfood " + Integer.toString(x) + " " + Integer.toString(y) + " "+ Integer.toString(size));
    }

    public void remove_food(int x, int y) throws IOException {
        foods.remove(x*size_x*2 + y);
        server.send_data("removefood " + Integer.toString(x) + " " + Integer.toString(y));
    }

    public void add_snake(double x, double y) throws IOException {
        snakes.put(snakeid_counter, new ServerSnake(snakeid_counter, x, y, snakebodys, 20, size_x, size_y));
        snakeid_counter++;
        server.send_data("newsnake " + Integer.toString(snakeid_counter-1) + " " + Double.toString(x) + " " + Double.toString(y));
    }

    public void remove_snake(int id) throws IOException {
        ServerSnake dead = snakes.get(id);
        int foodsize = 4*dead.size/(dead.cord.size());
        for(int k = dead.bodyx.size()-1; k > -1; k --){
            if (k % 4 == 0)
                add_food((dead.bodyx.get(k)/1),(dead.bodyy.get(k)/1), foodsize);
            snakebodys.remove(dead.bodyx.get(k) * 6000 + dead.bodyy.get(k));
        }
        snakes.remove(id);
        server.send_data("removesnake " + Integer.toString(id));
    }
}
