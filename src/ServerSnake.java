import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.io.IOException;
import java.util.*;

public class ServerSnake {
    //public ArrayList<Double> cord;
    public Deque<Double> cord;
    public Deque<Integer> bodyx, bodyy;
    //public ArrayList<Integer> bodyx, bodyy;
    public Double current_x, current_y, diameter, stepsize, lastangle, limit, locx, locy;
    private Boolean boosting;
    public int snakeid, boosting_counter;
    public int size, size_x, size_y;
    public String name;

    public ServerSnake(Integer id, Double x, Double y, LinkedHashMap bodys, Integer size, int sizex, int sizey, String name){
        this.size = size;
        this.name = name;
        snakeid = id;
        boosting_counter = 0;
        cord = new LinkedList<>();
        bodyx = new LinkedList<>();
        bodyy = new LinkedList<>();
        stepsize = 5.0;
        int y_loc = y.intValue() + (size - 1) * stepsize.intValue();
        for(int i = 0; i < size; i++){
            cord.add(0.0);
            bodyx.add(x.intValue());
            bodyy.add(y_loc);
            bodys.put(x.intValue()*6000 + y_loc + 18003000, snakeid);
            //System.out.println("add " + snakeid + " " + x.intValue() + " "+ y_loc + " " + (x.intValue()*6000 + y_loc + 18003000));
            y_loc -= stepsize.intValue();
        }
        size_x = sizex;
        size_y = sizey;
        diameter = 30.0;
        stepsize = 5.0;
        locx = x;
        locy = y;
        lastangle = 0.0;
        limit = 0.19;
        boosting = false;
    }

    public Boolean update(Double angle, LinkedHashMap<Integer, Integer> bodys,LinkedHashMap<Integer, Food> foods, ServerMap map) throws IOException {
        if ((Math.abs(angle - lastangle) < limit) || (6.283 - Math.abs(angle - lastangle) < limit))
            lastangle = angle;
        else if (Math.abs(angle - lastangle) > 3.1415)
            lastangle += -limit * Math.signum(angle-lastangle);
        else
            lastangle += limit * Math.signum(angle-lastangle);
        cord.removeFirst();
        cord.add(lastangle);
        locx -= stepsize * Math.sin(lastangle);
        locy -= stepsize * Math.cos(lastangle);
        bodyx.add(locx.intValue());
        bodyy.add(locy.intValue());
        bodys.put(locx.intValue() * 6000 + locy.intValue()+ 18003000, snakeid);
        //System.out.println("add " + snakeid + " " + locx.intValue() + " "+ locy.intValue() + " " + (locx.intValue() * 6000 + locy.intValue()+ 18003000));
        bodys.remove(bodyx.pop()*6000 + bodyy.pop()+ 18003000);
        if(boosting&&(size > 30)){
            cord.removeFirst();
            cord.add(lastangle);
            locx -= stepsize * Math.sin(lastangle);
            locy -= stepsize * Math.cos(lastangle);
            boosting_counter = (boosting_counter + 1)%10;
            bodyx.add(locx.intValue());
            bodyy.add(locy.intValue());
            bodys.put(locx.intValue() * 6000 + locy.intValue()+ 18003000, snakeid);
            //System.out.println("add " + snakeid + " " + locx.intValue() + " "+ locy.intValue() + " " + (locx.intValue() * 6000 + locy.intValue()+ 18003000));
            bodys.remove(bodyx.pop()*6000 + bodyy.pop()+ 18003000);
            if (boosting_counter == 0){
                cord.removeFirst();
                bodys.remove(bodyx.pop() * 6000 + bodyy.pop()+ 18003000);
                map.add_food(bodyx.getFirst(), bodyy.getFirst(), 1);
                size -= 1;
            }
        }
        for (int i = (int)(locx-10); i < (int)(locx+10); i++){
            for (int j = (int)(locy-10); j < (int)(locy+10); j++){
                if ((bodys.get(i * 6000 + j+ 18003000) != null)&&((bodys.get(i * 6000 + j+ 18003000) != snakeid))){
                    System.out.println("Dead " + snakeid + " " + i + " " + j + " " + (i * 6000 + j+ 18003000) + " " + bodys.get(i * 6000 + j+ 18003000));
                    if( snakeid == 11){
                         Iterator<Integer> x = map.snakebodys.keySet().iterator();
                        while(x.hasNext()) {
                            System.out.println(x.toString());
                            x.next();
                        }
                    }
                    return true;
                }
            }
        }
        for (int i = (int)(locx-30); i < (int)(locx+30); i++){
            for (int j = (int)(locy-30); j < (int)(locy+30); j++){
                if (foods.get(i * size_x * 2 + j) != null){
                    bigger(foods.get(i * size_x * 2 + j).size, bodys, map);
                    map.remove_food(i, j, snakeid);
                }
            }
        }
        return false;
    }

    public void set_boosting(Boolean bool){
        boosting = bool;
        if (boosting)
            limit = 0.12;
        else
            limit = 0.19;
    }

    public void bigger(int size, LinkedHashMap<Integer, Integer> bodys, ServerMap map) throws IOException {
        this.size += size;
        Double angle = cord.getLast();
        current_x = bodyx.getFirst() + 0.01;
        current_y = bodyy.getFirst() + 0.01;
        for (int i = 0; i < size; i++){
            cord.addFirst(angle);
            current_y += stepsize * Math.cos(angle);
            current_x += stepsize * Math.sin(angle);
            bodyx.addFirst(current_x.intValue());
            bodyy.addFirst(current_y.intValue());
            bodys.put((int)(current_x * 6000 + current_y+ 18003000), snakeid);
            //System.out.println("add " + snakeid + " " + locx.intValue() + " "+ locy.intValue() + " " + (locx.intValue() * 6000 + locy.intValue() + 18003000));
        }
        map.send_to_all("bigger " + Integer.toString(snakeid) + " " + Integer.toString(size));
    }
}
