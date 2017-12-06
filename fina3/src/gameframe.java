import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;


/**
 * @author jasonzhang
 */


public class gameframe extends JPanel implements MouseListener,
        MouseMotionListener{
    static Color color1, color2;//temp color
    static Point P1; //position tracker
    static JLabel mouseloc;//display position
    static Double angle;
    public static Double center_x, center_y;
    static Boolean running = false;
    public static Boolean boosting;
    public static Map map;
    public static GameClient client;




    public gameframe() throws IOException {
        //initialize

        mouseloc = new JLabel("(0,0)");
        P1 = null;
        color1 = color2 = Color.BLUE;
        addMouseListener(this);
        addMouseMotionListener(this);
        center_x = this.getWidth()/2.0;
        center_y = this.getHeight()/2.0;
        map = new Map(3000, 3000);
        angle = 1.0;
        boosting = false;



    }

    @Override
    public void mouseClicked(MouseEvent e) {
        //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void mousePressed(MouseEvent e) {
        boosting = true;
        map.mysnake.set_boosting(true);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        boosting = false;
        map.mysnake.set_boosting(false);
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void mouseExited(MouseEvent e) {
        //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        P1 = e.getPoint();
        angle = Math.atan((P1.x - center_x)/(P1.y - center_y+0.01));
        if ((P1.y - center_y) >= 0)
            angle += 3.1415;


    }

    @Override
    public void mouseMoved(MouseEvent e) {
        P1 = e.getPoint();
        angle = Math.atan((P1.x - center_x)/(P1.y - center_y+ 0.01));
        if ((P1.y - center_y) >= 0)
            angle += 3.1415;
         //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        int number;
        Color paint1, paint2;
        String[] items;
        Graphics2D g2d = (Graphics2D) g;
        map.paint_background(g2d);
    }

    public static void main(String[] args) throws IOException {
        JFrame dframe = new JFrame("Siyuan Zhang's Slither");
        dframe.setSize(800, 600);
        final gameframe drwp = new gameframe();
        drwp.setPreferredSize(new Dimension(dframe.getWidth(), (dframe.getHeight() - 86)));
        drwp.center_x = dframe.getWidth() / 2.0;
        drwp.center_y = (dframe.getHeight() - 120)/2.0;
        dframe.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent evt) {
                center_x = dframe.getWidth() / 2.0;
                if (dframe.getWidth() > 474) {
                    center_y = dframe.getHeight() / 2.0 - 43;
                    drwp.setPreferredSize(new Dimension(dframe.getWidth(), (dframe.getHeight() - 86)));
                }//resizing the game fram
                else {
                    center_y = dframe.getHeight() / 2.0 - 60;
                    drwp.setPreferredSize(new Dimension(dframe.getWidth(), (dframe.getHeight() - 120)));
                }
                map.set_head_loc(center_x, center_y);
            }
        });
        drwp.setBackground(Color.LIGHT_GRAY);
        JButton undo = new JButton("Start");
        undo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!running) {
                    running = true;
                    try {
                        start_game(drwp);
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });
        JButton clear = new JButton("Pause");
        clear.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                running = false;
            }
        });
        JButton firstcolor = new JButton("1st color");
        firstcolor.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame colorchoose = new JFrame();
                color1 = JColorChooser.showDialog(colorchoose, "Select a color", Color.BLUE);
                map.mysnake.set_color(color1, color2);
            }
        });
        JButton secondcolor = new JButton("2nd color");
        secondcolor.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame colorchoose = new JFrame();
                color2 = JColorChooser.showDialog(colorchoose, "Select a color", Color.BLUE);
                map.mysnake.set_color(color1, color2);
            }
        });


        JCheckBox gradient = new JCheckBox();
        gradient.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                map.mysnake.set_gradient();

            }
        });


        JLabel gradientl = new JLabel("Use Gradient");

        FlowLayout experimentLayout = new FlowLayout();
        dframe.setLayout(experimentLayout);
        Box box = Box.createHorizontalBox();
        box.add(undo);
        box.add(clear);

        dframe.add(box);
        box = Box.createHorizontalBox();
        box.add(gradient);
        box.add(gradientl);
        box.add(firstcolor);
        box.add(secondcolor);

        dframe.add(box);
        dframe.add(drwp, BorderLayout.CENTER);
        dframe.add(mouseloc);
        dframe.setVisible(true);
        dframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }

    private static void start_game(gameframe game) throws IOException{
        client = new GameClient();
        String result = "";
        while (!result.equals("set")){
            result = client.receive_data();
            map.excute(result);
        }
        client.send_data("finished");
        result = client.receive_data();
        client.close_client();
        new Thread(() -> {
            long startTime;
            while (running) {
                startTime = System.currentTimeMillis();
                running = map.update(angle, mouseloc);
                if (!running) {
                    map.set_loc(0.0, 0.0);

                }
                game.repaint();
                try {
                    Thread.sleep(Math.max(1, 20 - (System.currentTimeMillis() - startTime)));
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
            try {
                client.close_client();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

}
