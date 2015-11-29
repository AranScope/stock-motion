package com.aranscope;

import java.util.LinkedList;

/**
 * Created by aranscope on 28/11/15.
 */
public class StockValues {
    String date;
    double price;
    double high;
    double low;
    double volume;
    double lastClose;
    double change;
    double var;

    public String toString(){
        return "Date: " + date + "\nPrice: £" + price + "\nHigh: £" + high + "\nLow: £" + low + "\nChange: £" + change;
    }

    public void parseData(String data){
        String[] values = data.split(",");
        try {
            date = values[0];
            price = Double.parseDouble(values[1]);
            high = Double.parseDouble(values[2]);
            low = Double.parseDouble(values[3]);
            volume = Double.parseDouble(values[4]);
            lastClose = Double.parseDouble(values[5]);
            change = Double.parseDouble(values[6]);
            var = Double.parseDouble(values[7]);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
