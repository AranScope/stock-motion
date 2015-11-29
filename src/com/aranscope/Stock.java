package com.aranscope;


import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.LinkedList;
import java.util.Random;

/**
 * Created by aranscope on 28/11/15.
 */
public class Stock {
    String name;
    LinkedList<StockValues> stockValuesList;
    int valuePointer = 0;
    double x;
    double y;
    double vx;
    double vy;
    double d;
    Color c;

    /**
     * Constructor, initialise stock values with random company from csv Quandl data. Assign velocities and positions to
     * stock orbs.
     * @param screenWidth
     * @param screenHeight
     */
    public Stock(int screenWidth, int screenHeight){
        name = getRandomDateSource();
        Random ra = new Random();

        LinkedList<String> lines = readLines(name);
        stockValuesList = new LinkedList<>();
        for(String line: lines){
            StockValues stockValues = new StockValues();
            stockValues.parseData(line);
            stockValuesList.add(stockValues);
        }

        this.name = name.substring(0, name.length() - 4);
        if(stockValuesList.size() > 0) {
            this.d = 15 + (int) (10 * Math.log(stockValuesList.getFirst().price * 15));
        }
        else this.d = 20;
        vy = ra.nextDouble() * 5;
        vx = ra.nextDouble() - 0.5;
        x = d/2 + ra.nextInt(Math.abs((int)(screenWidth - d)));
        y = -d;

        this.c = new Color(ra.nextInt(255),ra.nextInt(255),ra.nextInt(255));
    }

    /**
     * Get ellipse representing Stock orb.
     * @return
     */
    public Ellipse2D.Double getDrawable(){
        return new Ellipse2D.Double(x - d/2, y - d/2, d, d);
    }

    /**
     * Draw specific data about stock.
     * @param g2
     */
    public void drawData(Graphics2D g2){
        FontMetrics fm = g2.getFontMetrics();
        int stringHeight = 124;
        drawString(g2, stockValuesList.get(valuePointer).toString(), (int)(x + d/2 + 15), (int)(y  - stringHeight/2));
    }

    /**
     * Iterate the day of the stock.
     */
    public void update(){
        valuePointer += 1;
        if(valuePointer >= stockValuesList.size()){
            valuePointer = 0;
        }
    }

    /**
     * Draw a string over multiple lines based on line separator character.
     * @param g2
     * @param text
     * @param x
     * @param y
     */
    void drawString(Graphics2D g2, String text, int x, int y) {
        for (String line : text.split("\n"))
            g2.drawString(line, x, y += g2.getFontMetrics().getHeight() + 3);
    }

    /**
     * Get a random company stock code.
     * @return Random company stock code.
     */
    public String getRandomDateSource(){
        Random ra = new Random();
        File file = new File("/home/aranscope/work/github/HackNotts/res/");
        File[] files = file.listFiles();
        return files[ra.nextInt(files.length)].getName();
    }

    /**
     * Read lines from a file.
     * @param name
     * @return
     */
    public static LinkedList<String> readLines(String name){
        String fileName = "/home/aranscope/work/github/HackNotts/res/" + name;

        LinkedList<String> lines = new LinkedList<>();
        try {
            String line = "";
            FileReader fileReader =
                    new FileReader(fileName);


            BufferedReader bufferedReader =
                    new BufferedReader(fileReader);

            while ((line = bufferedReader.readLine()) != null) {
                lines.add(line);
            }

            // Always close files.
            bufferedReader.close();
        }catch (Exception e){}

        return lines;
    }
}
