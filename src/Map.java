import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Random;

public class Map {
    public Double offset_x, offset_y, loc_x, loc_y, x_diff, y_diff, left_x, left_y, length, width;
    private BufferedImage background;
    private int size_x, size_y;
    public int mysnakeid;
    public static Snake mysnake;
    private ArrayList<Integer> foodx = new ArrayList<>();
    private ArrayList<Integer> foody = new ArrayList<>();
    private ArrayList<Integer> foodi = new ArrayList<>();
    public LinkedHashMap<Integer, Food> foods = new LinkedHashMap<>();
    private LinkedHashMap<Integer, Snake> snakes = new LinkedHashMap<>();
    private JLabel mouseloc;


    public Map(int xsize, int ysize, JLabel mouseloc){
        mysnakeid = 0;
        size_x = xsize;
        size_y = ysize;
        offset_x = 0.0;
        offset_y = 0.0;
        loc_x = 0.0;
        loc_y = 0.0;
        left_x = -400.0;
        left_y = -240.0;
        length = 800.0;
        width = 480.0;
        this.mouseloc = mouseloc;
        try {
            background = ImageIO.read(ClassLoader.getSystemResource( "bg-1.png" ));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void paint_background(Graphics2D g){
        g.drawImage(background, (int)Math.round(offset_x)-617, (int)Math.round(offset_y)-516, null);
        g.drawImage(background, (int)Math.round(offset_x)-607, (int)Math.round(offset_y)+221, null);
        g.setStroke(new BasicStroke(20, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.setPaint(Color.PINK);
        foods.values().forEach(food -> {
            g.fillOval((int) (food.x - 8 -left_x), (int) (food.y - 8 -left_y), 15, 15);
        });
        for (int i = foodx.size()-1; i > -1; i--){
            double locx = snakes.get(foodi.get(i)).locx;
            double locy = snakes.get(foodi.get(i)).locy;
            foodx.set(i, foodx.get(i) + (int)(Math.signum(locx-foodx.get(i)) * 10));
            foody.set(i, foody.get(i) + (int)(Math.signum(locy-foody.get(i)) * 10));
            if ((Math.abs(foodx.get(i)-locx)<10)||(Math.abs(foodx.get(i)-locx)<10)){
                foodx.remove(i);
                foody.remove(i);
                foodi.remove(i);
            }
            else
                g.fillOval((int)(foodx.get(i)-10 - left_x), (int)(foody.get(i)-10 - left_y), 15, 15);
        }
        snakes.values().forEach(snake -> {
            snake.draw_snake(g, left_x, left_y);
            g.setPaint(Color.GREEN);
            if (snake.snakeid == mysnakeid) {
                g.drawString("#1 Your snake " + mysnake.size, 10, 80);
                g.setPaint(Color.RED);
            }

            g.fillOval((int)(snake.locx+3000)/120,(int)(snake.locy+3000)/120,2, 2 );
        });
        g.setPaint(Color.ORANGE);
        g.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.drawRect(0, 0, 50, 50);
    }

    public void update(int snakeid, Double angle, double x, double y, Boolean status){
        snakes.get(snakeid).update(angle, x, y);
        if (snakeid == mysnakeid) {
            x_diff = loc_x - snakes.get(snakeid).locx;
            y_diff = loc_y - snakes.get(snakeid).locy;
            offset_x = (offset_x + 597.0 + x_diff) % 597.0;
            offset_y = (offset_y + 516.0 + y_diff) % 516.0;
            loc_x -= x_diff;
            loc_y -= y_diff;
            left_x -= x_diff;
            left_y -= y_diff;
            mouseloc.setText("(" + (int) (loc_x / 1) + "," + (int) (loc_y / 1) + ") Your size is  " + snakes.get(mysnakeid).size);
        }
        snakes.get(snakeid).set_boosting(status);
    }



    public void setmysnake_boost(Boolean status, GameClient client) throws IOException {
        mysnake.set_boosting(status);
    }

    public void set_head_loc(Double x, Double y){
        left_x -= (x - length/2);
        length = 2*x;
        left_y -= (y - width/2);
        width = 2*y;
    }

    public void add_food(int x, int y, int size){
        foods.put(x*size_x*2 + y, new Food(x, y, size));
    }

    public void remove_food(int x, int y, int id){
        foods.remove(x*size_x*2 + y);
        foodx.add(x);
        foody.add(y);
        foodi.add(id);
    }

    public void add_snake(int id, double x, double y, String name, ArrayList<Double> cords) {
        snakes.put(id, new Snake(id, x, y, Color.BLUE, Color.GREEN,20, cords, name));
    }

    public void remove_snake(int id){
        snakes.remove(id);
    }

    public void reset(){
        foodx = new ArrayList<>();
        foody = new ArrayList<>();
        foodi = new ArrayList<>();
        foods = new LinkedHashMap<>();
        snakes = new LinkedHashMap<>();
    }

    public Boolean excute(String input){
        String[] items = input.split(" ");
        switch (items[0]){
            case "update": update(Integer.parseInt(items[1]), Double.parseDouble(items[2]),Double.parseDouble(items[3]),Double.parseDouble(items[4]), Boolean.parseBoolean(items[5]));
            break;
            case "newsnake":
                ArrayList<Double> cords = new ArrayList<>();
                for (int i = 5; i < items.length; i++){
                    cords.add(Double.parseDouble(items[i]));
                }
                add_snake(Integer.parseInt(items[1]), Double.parseDouble(items[2]), Double.parseDouble(items[3]), items[4], cords);
            break;
            case "addfood": add_food(Integer.parseInt(items[1]), Integer.parseInt(items[2]), Integer.parseInt(items[3]));
            break;
            case "removefood": remove_food(Integer.parseInt(items[1]), Integer.parseInt(items[2]), Integer.parseInt(items[3]));
            break;
            case "bigger": snakes.get(Integer.parseInt(items[1])).bigger(Integer.parseInt(items[2]));
            break;
            case "yoursnake":
                mysnakeid = Integer.parseInt(items[1]);
                mysnake = snakes.get(Integer.parseInt(items[1]));
                break;
            case "removesnake":
                if (Integer.parseInt(items[1]) == mysnakeid)
                    return false;
                remove_snake(Integer.parseInt(items[1]));
                break;
            default: return true;
        }
        return true;
    }

}
