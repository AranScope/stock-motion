package com.aranscope;

import com.leapmotion.leap.*;
import javazoom.jl.player.Player;

import javax.swing.*;
import java.awt.*;
import java.io.FileInputStream;
import java.text.DecimalFormat;
import java.util.LinkedList;

/**
 * Created by aranscope on 28/11/15.
 */
public class LeapPanel extends JPanel {
    Controller controller;
    HandList hands;
    FingerList fingers;
    int screenWidth, screenHeight;
    LinkedList<Stock> stockList;
    Stock heldStock;
    DecimalFormat df = new DecimalFormat("#0.00");
    double score = 0.0;

    public LeapPanel(Controller controller, int width, int height) {
        super();
        this.setPreferredSize(new Dimension(width, height));
        this.controller = controller;
        this.screenWidth = width;
        this.screenHeight = height;
        stockList = new LinkedList<>();
        this.score = 1000.00;
    }

    public void addStock(Stock stock) {
        stockList.add(stock);
    }

    int stockUpdate = 0;

    public void update() {
        this.hands = controller.frame().hands();
        this.fingers = controller.frame().fingers();


        for (int x = 0; x < stockList.size(); x++) {
            Stock stock = stockList.get(x);

            if (stockUpdate > 100) {
                stock.update();
                stockUpdate = 0;
            }
            stockUpdate++;
            if (stock.y > getHeight() + stock.d / 2) {
                stockList.remove(stock);
                x--;
            } else {
                stock.x += stock.vx;
                stock.y += stock.vy;
            }
        }

        repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.clearRect(0, 0, getWidth(), getHeight());
        screenWidth = getWidth();
        screenHeight = getHeight();

        g2.setFont(new Font("Seruf", Font.PLAIN, 20));
        g2.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        drawBackground(g2);

        drawScore(g2);

        drawHold(g2);

        if (heldStock != null) {
            drawHud(g2);
        }

        drawHands(g2);

        drawStocks(g2);






//        if(grab()){
//            g2.setColor(Color.red);
//        }
//        else{
//            g2.setColor(Color.black);
//        }
//        if(fingers != null){
//            for(Finger finger: fingers){
//                Point fingerPoint = scale(finger.jointPosition(Finger.Joint.JOINT_TIP));
//                g2.fillOval(fingerPoint.x - 10, fingerPoint.y - 10, 20, 20);
//                fingerPoint = scale(finger.jointPosition(Finger.Joint.JOINT_PIP));
//                g2.fillOval(fingerPoint.x - 10, fingerPoint.y - 10, 20, 20);
//                fingerPoint = scale(finger.jointPosition(Finger.Joint.JOINT_MCP));
//                g2.fillOval(fingerPoint.x - 10, fingerPoint.y - 10, 20, 20);
//                fingerPoint = scale(finger.jointPosition(Finger.Joint.JOINT_DIP));
//                g2.fillOval(fingerPoint.x - 10, fingerPoint.y - 10, 20, 20);
//            }
//        }


    }

    public void drawHands(Graphics2D g2) {
        boolean grab = grab();
        if (grab) {
            g2.setColor(Color.decode("0xE74C3C"));
        } else g2.setColor(Color.decode("0xffffff"));

        if (hands != null) {
            for (Hand hand : hands) {
                Point handPoint = scale(hand.palmPosition());

                if (heldStock != null) {
                    if (grab) {
                        heldStock.x = handPoint.x;
                        heldStock.y = handPoint.y;
                    } else {
                        heldStock.vx = hand.palmVelocity().getX() / 20;
                        heldStock.vy = hand.palmVelocity().getZ() / 20;

                        if (heldStock.x > 25 && heldStock.x < 425 && heldStock.y > 25 && heldStock.y < getHeight() - 25) {
                            score += heldStock.stockValuesList.get(heldStock.valuePointer).price;
                            stockList.remove(heldStock);
                        }
                        else if(heldStock.x > getWidth() - 400 && heldStock.x < getWidth() - 25 && heldStock.y > 200 && heldStock.y < getHeight() - 25){
                            heldStock.vx = 0;
                            heldStock.vy = 0;
                        }

                        heldStock = null;
                    }
                } else if (grab) {
                    for (Stock stock : stockList) {
                        if (handPoint.distance(stock.x, stock.y) < stock.d / 2) {
                            stock.x = handPoint.x;
                            stock.y = handPoint.y;
                            playMp3("/home/aranscope/work/github/HackNotts/sound/pickup.mp3");

                            heldStock = stock;
                            if(heldStock.x > getWidth() - 400 && heldStock.x < getWidth() - 25 && heldStock.y > 200 && heldStock.y < getHeight() - 25){

                                if(heldStock.vy > 0){
                                    score -= heldStock.stockValuesList.get(heldStock.valuePointer).price;
                                }
                            }
                            else score -= heldStock.stockValuesList.get(heldStock.valuePointer).price;

                            stock.vx = 0;
                            stock.vy = 0;

                            break;
                        }
                    }
                }

                g2.fillOval(handPoint.x - 10, handPoint.y - 10, 20, 20);
            }
        }
    }

    public void drawBackground(Graphics2D g2) {
        g2.setColor(Color.decode("0x2C3E50"));
        g2.fillRect(0, 0, getWidth(), getHeight());
    }


    public void drawStocks(Graphics2D g2) {
        FontMetrics fm = g2.getFontMetrics();

        for (Stock stock : stockList) {
            g2.setColor(stock.c);
            g2.fill(stock.getDrawable());

            g2.setColor(Color.decode("0xffffff"));
            g2.draw(stock.getDrawable());


            g2.drawString(stock.name, (int) (stock.x - fm.stringWidth(stock.name) / 2), (int) (stock.y + fm.getAscent() / 2));

            if (stock == heldStock) {
                stock.drawData(g2);
            }
        }


    }

    public void drawHud(Graphics2D g2) {
        if (heldStock.x > 25 && heldStock.x < 425 && heldStock.y > 25 && heldStock.y < getHeight() - 25) {
            g2.setColor(new Color(255, 0, 0, 200));
            g2.fillRoundRect(25, 25, 400, getHeight() - 50, 25, 25);
            g2.setColor(new Color(255, 255, 255, 200));
            g2.drawRoundRect(25, 25, 400, getHeight() - 50, 25, 25);
        } else {
            g2.setColor(new Color(255, 0, 0, 40));
            g2.fillRoundRect(25, 25, 400, getHeight() - 50, 25, 25);
            g2.setColor(new Color(255, 255, 255, 40));
            g2.drawRoundRect(25, 25, 400, getHeight() - 50, 25, 25);
        }


        String sell = "Sell (£" + df.format(heldStock.stockValuesList.get(heldStock.valuePointer).price) + ")";

        g2.setColor(Color.white);
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(sell, 25 + 200 - fm.stringWidth(sell) / 2, getHeight() / 2 + fm.getAscent() / 2);
    }

    public void drawScore(Graphics2D g2) {
        g2.setColor(new Color(0, 255, 0, 40));
        int width = 400;
        int height = 150;
        g2.fillRoundRect(getWidth() - width - 25, 25, width, height, 25, 25);
        g2.setColor(new Color(255, 255, 255, 40));
        g2.drawRoundRect(getWidth() - width - 25, 25, width, height, 25, 25);


        String playerScore = "Score: £" + df.format(score);

        g2.setColor(Color.white);
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(playerScore, getWidth() - width/2 - 25- fm.stringWidth(playerScore) / 2, 25 + height / 2 + fm.getAscent() / 2);
    }

    public void drawHold(Graphics2D g2){
        int width = 400;
        int height = getHeight() - 225;


        if(heldStock != null && heldStock.x > getWidth() - 400 && heldStock.x < getWidth() - 25 && heldStock.y > 200 && heldStock.y < getHeight() - 25) {
            g2.setColor(new Color(0, 0, 255, 200));
            g2.fillRoundRect(getWidth() - width - 25, 200, width, height, 25, 25);
            g2.setColor(new Color(255, 255, 255, 200));
            g2.drawRoundRect(getWidth() - width - 25, 200, width, height, 25, 25);
        }
        else {
            g2.setColor(new Color(0, 0, 255, 40));
            g2.fillRoundRect(getWidth() - width - 25, 200, width, height, 25, 25);
            g2.setColor(new Color(255, 255, 255, 40));
            g2.drawRoundRect(getWidth() - width - 25, 200, width, height, 25, 25);
        }







        String store = "Store";

        g2.setColor(Color.white);
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(store, getWidth() - width/2 - 25- fm.stringWidth(store) / 2, 200 + height / 2 + fm.getAscent() / 2);

    }

    public boolean grab() {
        if (hands != null) {
            for (Hand hand : hands) {
                if (heldStock != null) {
                    if (hand.grabStrength() > 0.5) {
                        return true;
                    } else return false;
                } else if (hand.grabStrength() > 0.5) {
                    return true;
                } else return false;
            }
        }

        return false;
    }

    public Point scale(Vector position) {
        controller.frame().interactionBox().center();
        float leapWidth = controller.frame().interactionBox().width();
        float leapHeight = controller.frame().interactionBox().height();

        return new Point((int) ((position.getX() + leapWidth / 2) * (screenWidth / leapWidth)), (int) (((position.getZ() + leapHeight / 2) * (screenHeight / leapHeight))));
    }

    private Player mp3Player;
    private Thread playerThread;

    public void playMp3(String filepath) {
        try {
            FileInputStream file = new FileInputStream(filepath);
            mp3Player = new Player(file);

            playerThread = new Thread();
            playerThread.start();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
