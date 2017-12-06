import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.LinkedHashMap;

public class Snake {
    public ArrayList<Double> cord;
    public Double current_x, current_y, diameter, stepsize, lastangle,locx, locy;
    private Color paint1, paint2;
    private Boolean gradient, boosting;
    private int snakeid;
    public int size;

    public Snake(Integer id, Double x, Double y, Color p1, Color p2, Integer size){
        this.size = size;
        cord = new ArrayList<>();
        snakeid = id;
        stepsize = 5.0;
        for(int i = 0; i < size; i++){
            cord.add(0.0);
        }
        lastangle = 0.0;
        paint1 = p1;
        paint2 = p2;
        diameter = 30.0;
        stepsize = 5.0;
        locx = x;
        locy = y;
        gradient = false;
        boosting = false;

    }

    public void draw_snake(Graphics2D g2d, Double map_x, Double map_y){
        current_x = locx - map_x - diameter/2;
        current_y = locy - map_y - diameter/2;
        g2d.setPaint(paint1);
        g2d.fill(new Ellipse2D.Double(current_x, current_y, diameter, diameter));
        if (gradient)
            g2d.setPaint(new GradientPaint(5, 30, paint1, 35, 100, paint2, true));
            //set gradient
        if (boosting&&(size > 30)){
            g2d.setPaint(new GradientPaint(5, 30, paint1, 35, 100, Color.YELLOW, true));
        }
        for(int i = cord.size()-1; i >= 0; i --){
            current_y += stepsize * Math.cos(cord.get(i));
            current_x += stepsize * Math.sin(cord.get(i));
            if (i %2 == 1)
                g2d.fill(new Ellipse2D.Double(current_x, current_y, diameter, diameter));
        }
        g2d.setPaint(Color.BLACK);
        current_x = locx - map_x - diameter/2;
        current_y = locy - map_y - diameter/2;
        current_y += diameter * Math.cos(lastangle+1.57)/4 + 3*diameter/8;
        current_x += diameter * Math.sin(lastangle+1.57)/4 + 3*diameter/8;
        g2d.fill(new Ellipse2D.Double(current_x, current_y, diameter/4, diameter/4));
        current_y += 2 * diameter * Math.cos(lastangle-1.57)/4;
        current_x += 2 * diameter * Math.sin(lastangle-1.57)/4;
        g2d.fill(new Ellipse2D.Double(current_x, current_y, diameter/4, diameter/4));

        //g2d.fill(new Ellipse2D.Double(head_x, head_y, diameter, diameter));
    }

    public void update(Double angle, LinkedHashMap<Integer, Integer> bodys, Map map){
        cord.remove(0);
        lastangle = angle;
        cord.add(lastangle);
        locx -= stepsize * Math.sin(angle);
        locy -= stepsize * Math.cos(angle);
    }

    public void set_color(Color c1, Color c2){
        paint1 = c1;
        paint2 = c2;
    }

    public void set_gradient(){
        gradient = !gradient;
    }


    public void set_boosting(Boolean bool){
        boosting = bool;
    }

    public void set_loc(Double x, Double y) {
        locx = x;
        locy = y;
    }

    public void bigger(int size){
        this.size += size;
        Double angle = cord.get(cord.size()-1);
        for (int i = 0; i < size; i++){
            cord.add(0, angle);
        }
    }
}
