package com.aranscope;

import com.leapmotion.leap.Controller;

import javax.swing.*;
import java.util.Random;

public class Main {
    public static void main(String[] args) {
        Controller controller = new Controller();

        LeapPanel panel = new LeapPanel(controller, 1300,700);

        JFrame frame = new JFrame("Stock Motion");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        frame.add(panel);

        frame.pack();

        Random ra = new Random();
        while(true){
            if(ra.nextDouble() > 0.95){
                Stock stock = new Stock(panel.getWidth(), panel.getHeight());
                panel.addStock(stock);
            }
            try{
                Thread.sleep(20);
            }catch(Exception e){}
            panel.update();


        }

    }
}
