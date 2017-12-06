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
    public static Snake mysnake;
    private ArrayList<Integer> foodx = new ArrayList<>();
    private ArrayList<Integer> foody = new ArrayList<>();
    public LinkedHashMap<Integer, Food> foods = new LinkedHashMap<>();
    LinkedHashMap<Integer, Snake> snakes = new LinkedHashMap<>();


    public Map(int xsize, int ysize){
        mysnake = new Snake(0,0.0, 0.0, Color.BLUE, Color.GREEN, 20);
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
        try {
            background = ImageIO.read(new File("/Users/jasonzhang/Desktop/bg-1.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Random r = new Random();
        int Low = -500;
        int High = 500;
        Low = -xsize;
        High = xsize;


    }

    public void paint_background(Graphics2D g){
        g.drawImage(background, (int)Math.round(offset_x)-617, (int)Math.round(offset_y)-516, null);
        g.drawImage(background, (int)Math.round(offset_x)-607, (int)Math.round(offset_y)+221, null);
        g.setStroke(new BasicStroke(20, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.setPaint(Color.PINK);
        foods.values().forEach(food -> {
            g.fillOval((int) (food.x - food.size / 2-left_x), (int) (food.y - food.size / 2-left_y), 20, 20);
        });
        for (int i = 0; i < foodx.size(); i++){
            g.fillOval((int)(foodx.get(i)-10 - left_x), (int)(foody.get(i)-10 - left_y), 20, 20);
        }
        mysnake.draw_snake(g, left_x, left_y);
    }

    public Boolean update(Double angle, JLabel mouseloc){
        x_diff = loc_x - mysnake.locx;
        y_diff = loc_y - mysnake.locy;
        offset_x = (offset_x +597.0+ x_diff)%597.0;
        offset_y = (offset_y +516.0+ y_diff)%516.0;
        loc_x -= x_diff;
        loc_y -= y_diff;
        left_x -= x_diff;
        left_y -= y_diff;
        mouseloc.setText("(" + (int)(loc_x/1) + "," + (int)(loc_y/1) + ") Your size is  " + mysnake.size);
        if ((Math.abs(loc_x) > size_x)||(Math.abs(loc_y) > size_y)){
            return false;
        }
        return true;
    }

    public void set_head_loc(Double x, Double y){
        left_x -= (x - length/2);
        length = 2*x;
        left_y -= (y - width/2);
        width = 2*y;
    }

    public void set_loc(Double x, Double y){
        mysnake = new Snake(0,loc_x + 50, loc_y + 50, Color.BLUE, Color.GREEN,20);
    }

    public void add_food(int x, int y, int size){
        foods.put(x*size_x*2 + y, new Food(x, y, size));
    }

    public void remove_food(int x, int y){
        foods.remove(x*size_x*2 + y);
        foodx.add(x);
        foody.add(y);
    }

    public void add_snake(int id, double x, double y) {
        snakes.put(id, new Snake(id, x, y, Color.BLUE, Color.GREEN,20));
    }

    public void excute(String input){
        String[] items = input.split(" ");
        System.out.println(input);
        switch (items[0]){
            case "newsnake": add_snake(Integer.parseInt(items[1]), Double.parseDouble(items[2]), Double.parseDouble(items[3]));
            break;
            case "addfood": add_food(Integer.parseInt(items[1]), Integer.parseInt(items[2]), Integer.parseInt(items[3]));
            break;
            default: return;


        }
    }

}
