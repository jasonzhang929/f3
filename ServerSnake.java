import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public class ServerSnake {
    public ArrayList<Double> cord;
    public ArrayList<Integer> bodyx, bodyy;
    public Double current_x, current_y, diameter, stepsize, lastangle, limit, locx, locy;
    private Boolean boosting;
    private int snakeid, boosting_counter;
    public int size, size_x, size_y;

    public ServerSnake(Integer id, Double x, Double y, LinkedHashMap bodys, Integer size, int sizex, int sizey){
        this.size = size;
        cord = new ArrayList<>();
        snakeid = id;
        boosting_counter = 0;
        bodyx = new ArrayList<>();
        bodyy = new ArrayList<>();
        stepsize = 5.0;
        for(int i = 0; i < size; i++){
            cord.add(0.0);
            bodyx.add((int)(x/1));
            bodyy.add((int)(y + (19-i) * stepsize));
            bodys.put(bodyx.get(i) * 2 * 3000 + bodyy.get(i), snakeid);
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
        cord.remove(0);
        if ((Math.abs(angle - lastangle) < limit) || (6.283 - Math.abs(angle - lastangle) < limit))
            lastangle = angle;
        else if (Math.abs(angle - lastangle) > 3.1415)
            lastangle += -limit * Math.signum(angle-lastangle);
        else
            lastangle += limit * Math.signum(angle-lastangle);
        cord.add(lastangle);
        locx -= stepsize * Math.sin(lastangle);
        locy -= stepsize * Math.cos(lastangle);
        if(boosting&&(size > 30)){
            cord.remove(0);
            cord.add(lastangle);
            locx -= stepsize * Math.sin(lastangle);
            locy -= stepsize * Math.cos(lastangle);
            boosting_counter = (boosting_counter + 1)%10;
            bodyx.add((int)(locx/1));
            bodyy.add((int)(locy/1));
            bodys.put((int)(locx/1) * 6000 + (int)(locy/1), snakeid);
            bodys.remove(bodyx.get(0) * 6000 + bodyy.get(0));
            bodyx.remove(0);
            bodyy.remove(0);
            if (boosting_counter == 0){
                cord.remove(0);
                bodys.remove(bodyx.get(0) * 6000 + bodyy.get(0));
                map.add_food(bodyx.get(1), bodyy.get(1), 1);
                bodyx.remove(0);
                bodyy.remove(0);
                size -= 1;
            }
        }
        for (int i = (int)(locx-10); i < (int)(locx+10); i++){
            for (int j = (int)(locy-10); j < (int)(locy+10); j++){
                if ((bodys.get(i * 6000 + j) != null)&&((bodys.get(i * 6000 + j) != snakeid))){
                    return true;
                }
            }
        }
        for (int i = (int)(locx-30); i < (int)(locx+30); i++){
            for (int j = (int)(locy-30); j < (int)(locy+30); j++){
                if (foods.get(i * size_x * 2 + j) != null){
                    bigger(foods.get(i * size_x * 2 + j).size, bodys);
                    map.remove_food(i, j);
                }
            }
        }
        bodyx.add((int)(locx/1));
        bodyy.add((int)(locy/1));
        bodys.put((int)(locx/1) * 6000 + (int)(locy/1), snakeid);
        bodys.remove(bodyx.get(0) * 6000 + bodyy.get(0));
        bodyx.remove(0);
        bodyy.remove(0);
        return false;
    }



    public void set_boosting(Boolean bool){
        boosting = bool;
        if (boosting)
            limit = 0.12;
        else
            limit = 0.19;
    }

    public void set_loc(Double x, Double y) {
        locx = x;
        locy = y;
    }

    public void bigger(int size, LinkedHashMap<Integer, Integer> bodys ){
        this.size += size;
        Double angle = cord.get(cord.size()-1);
        current_x = bodyx.get(0)+ 0.01;
        current_y = bodyy.get(0)+ 0.01;
        for (int i = 0; i < size; i++){
            cord.add(0, angle);
            current_y += stepsize * Math.cos(angle);
            current_x += stepsize * Math.sin(angle);
            bodyx.add(0, (int)(current_x/1));
            bodyy.add(0, (int)(current_y/1));
            bodys.put((int)(current_x * 6000 + current_y), snakeid);
        }
    }

    public Boolean bot_update(double angle, Double x, Double y, LinkedHashMap<Integer, Integer> bodys,LinkedHashMap<Integer, Food> foods, ServerMap map) throws IOException {
        if ((Math.abs(x-locx) < 200)&&((Math.abs(y-locy) < 200))){
            angle = Math.atan((x - locx)/(y - locy+0.01));
            if ((y - locy) < 0)
                angle += 3.1415;
        }
        cord.remove(0);
        if ((Math.abs(angle - lastangle) < limit) || (6.283 - Math.abs(angle - lastangle) < limit))
            lastangle = angle;
        else if (Math.abs(angle - lastangle) > 3.1415)
            lastangle += -limit * Math.signum(angle-lastangle);
        else
            lastangle += limit * Math.signum(angle-lastangle);
        cord.add(lastangle);
        locx -= stepsize * Math.sin(lastangle);
        locy -= stepsize * Math.cos(lastangle);
        for (int i = (int)(locx-10); i < (int)(locx+10); i++){
            for (int j = (int)(locy-10); j < (int)(locy+10); j++){
                if ((bodys.get(i * 6000 + j) != null)&&(!(bodys.get(i * 6000 + j) == snakeid))){
                    return true;
                }
            }
        }
        for (int i = (int)(locx-30); i < (int)(locx+30); i++){
            for (int j = (int)(locy-30); j < (int)(locy+30); j++){
                if (foods.get(i * size_x * 2 + j) != null){
                    bigger(foods.get(i * size_x * 2 + j).size, bodys);
                    map.remove_food(i, j);
                }
            }
        }
        bodyx.add((int)(locx/1));
        bodyy.add((int)(locy/1));
        bodys.put((int)(locx/1) * 6000 + (int)(locy/1), snakeid);
        bodys.remove(bodyx.get(0) * 6000 + bodyy.get(0));
        bodyx.remove(0);
        bodyy.remove(0);
        return false;
    }
}
