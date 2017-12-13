import com.sun.org.apache.xpath.internal.operations.Bool;

import java.io.IOException;
import java.util.*;

public class ServerMap {
    private int size_x, size_y;
    private int snakeid_counter;
    public static LinkedHashMap<Integer, Food> foods = new LinkedHashMap<>();
    public static LinkedHashMap<Integer, ServerSnake> snakes = new LinkedHashMap<>();
    public static LinkedHashMap<Integer, Integer> snakebodys = new LinkedHashMap<>();
    public static ArrayList<MiniServer> servers = new ArrayList<>();
    private static ArrayList<Integer> bots;


    public ServerMap(int xsize, int ysize, ArrayList<MiniServer> servers) throws IOException {
        snakeid_counter = 0;
        this.servers = servers;
        bots = new ArrayList<>();
        size_x = xsize;
        size_y = ysize;
        Random r = new Random();
        int Low = -2000;
        int High = 2000;
        for (int i = 0; i < 10; i++)
            bots.add(add_snake(r.nextInt(High-Low) + Low+1.0,r.nextInt(High-Low) + Low+1.0, "bot"));
        Low = -xsize;
        High = xsize;
        for (int i = 0; i < 100; i++){
            add_food(r.nextInt(High-Low) + Low,r.nextInt(High-Low) + Low, 1);
        }
    }

    public void update_snake(int snakeid, double angle, Boolean boost) throws IOException {
        ServerSnake snake = snakes.get(snakeid);
        snake.set_boosting(boost);
        if (snake.update(angle, snakebodys, foods, this)){
            remove_snake(snakeid);
        }
        else{
            send_to_all("update " + Integer.toString(snakeid) + " " + Double.toString(snake.lastangle)
                    + " " + Double.toString(snake.locx) + " " + Double.toString(snake.locy) + " " + Boolean.toString(boost));
        }

    }

    public void update_bots() throws IOException {
        double angle;
        for(int i = bots.size()-1; i > -1; i--){
            ServerSnake bot = snakes.get(bots.get(i));
            final double[] mini = new double[1];
            mini[0] = 5000.0;
            final int[] minindex = {-1};
            snakes.values().forEach(snake -> {
                double x = Math.abs(snake.locx - bot.locx) + Math.abs(snake.locy - bot.locy);
                if ((x < mini[0]) && (x > 0)) {
                    mini[0] = x;
                    minindex[0] = snake.snakeid;
                }
            });
            angle = 0.0;
            if (minindex[0] != -1) {
                ServerSnake enemy = snakes.get(minindex[0]);
                angle = -Math.atan((enemy.locx - bot.locx) / (enemy.locy - bot.locy + 0.01));
                if ((enemy.locy - bot.locy) < 0)
                    angle += 3.1415;
            }
            if (bot.locx > 500){
                angle = 1.57;
            }
            if (bot.locx < -500){
                angle = -1.57;
            }
            if (bot.locy > 500){
                angle = 0;
            }
            if (bot.locy < -500){
                angle = 3.14;
            }

            if(bot.update(angle, snakebodys, foods, this)) {
                bots.remove(i);
                remove_snake(bot.snakeid);
            }
            else{
                send_to_all("update " + Integer.toString(bot.snakeid) + " " + Double.toString(bot.lastangle)
                        + " " + Double.toString(bot.locx) + " " + Double.toString(bot.locy) + " " + Boolean.toString(false));
            }
        }
    }


    public void add_food(int x, int y, int size) throws IOException {
        foods.put(x*size_x*2 + y, new Food(x, y, size));
        send_to_all("addfood " + Integer.toString(x) + " " + Integer.toString(y) + " "+ Integer.toString(size));
    }

    public void remove_food(int x, int y, int id) throws IOException {
        foods.remove(x*size_x*2 + y);
        send_to_all("removefood " + Integer.toString(x) + " " + Integer.toString(y) + " " + Integer.toString(id));
    }

    private int add_snake(double x, double y, String name) throws IOException {
        snakes.put(snakeid_counter, new ServerSnake(snakeid_counter, x, y, snakebodys, 20, size_x, size_y, name));
        String z = ("newsnake " +
                Integer.toString(snakeid_counter) + " " + Double.toString(x)
                + " " + Double.toString(y) + " " + name);
        for(Iterator<Double> itr = snakes.get(snakeid_counter).cord.iterator(); itr.hasNext();)  {
            z += (" " + Double.toString(itr.next()));
        }
        send_to_all(z);
        snakeid_counter++;
        return snakeid_counter-1;
    }

    private void remove_snake(int id) throws IOException {
        ServerSnake dead = snakes.get(id);
        int foodsize = 4*dead.size/(dead.cord.size());
        int x, y;
        int k = 0;
        while(dead.bodyx.size() > 0){
            x = dead.bodyx.pop();
            y = dead.bodyy.pop();
            if (k % 4 == 0)
                add_food(x, y, foodsize);
            snakebodys.remove(x * 6000 + y + 18003000);
            k++;
        }
        snakes.remove(id);
        send_to_all("removesnake " + Integer.toString(id));
    }

    public void send_to_all(String data){
        for (MiniServer x: servers){
            x.add_sendout(data);
        }
    }

    public void current_state(MiniServer server){
        server.sendouts = new LinkedList<>();
        snakes.values().forEach(snake -> {
            String x = ("newsnake " +
                    Integer.toString(snake.snakeid) + " " + Double.toString(snake.locx)
                    + " " + Double.toString(snake.locy)) + " " + snake.name;
            for(Iterator<Double> itr = snake.cord.iterator(); itr.hasNext();)  {
                x += (" " + Double.toString(itr.next()));
            }
            server.add_sendout(x);
        });
        foods.values().forEach(food-> {
            server.add_sendout("addfood " + Integer.toString(food.x) + " " + Integer.toString(food.y) + " "+ Integer.toString(food.size));
        });
    }

    public void excute(String input, MiniServer server) throws IOException {
        String[] items = input.split(" ");
        switch (items[0]){
            case "update":
                update_bots();
                update_snake(Integer.parseInt(items[1]), Double.parseDouble(items[2]), Boolean.parseBoolean(items[3]));
                break;
            case "start":
                current_state(server);
                server.add_sendout("yoursnake " + Integer.toString(add_snake(0, 0, items[1])));
                break;
            default: return;
        }
    }


}
