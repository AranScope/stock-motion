package com.aranscope;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.LinkedList;
import java.util.jar.Pack200;

/**
 * Created by aranscope on 28/11/15.
 */
public class Test {
    static LinkedList<Stock> stocks;

    public static void main(String[] args){
        stocks = new LinkedList<>();
//        String url ="https://www.quandl.com/api/v3/datasets.csv?database_code=LSE&per_page=100&sort_by=id&page=1&auth_token=EGgEF-iNiNsVfQmfiwnM";
//        try {
//            System.out.println(getHTML(url));
//        }catch(Exception e){
//            e.printStackTrace();
//        }

        LinkedList<String> read = readLines("/home/aranscope/work/github/HackNotts/res/LSE-datasets-codes.csv");

        LinkedList<String> spoofData = new LinkedList<>();

        for(String name: parseLines(read)){
            String url = "https://www.quandl.com/api/v3/datasets/LSE/" + name + ".csv?auth_token=EGgEF-iNiNsVfQmfiwnM&start_date=2015-08-09";
            String fileName = "/home/aranscope/work/github/HackNotts/res/" + name + ".csv";
            try {
                LinkedList<String> data = getHTMLLines(url);
                Collections.reverse(data);
                writeLines(data, fileName);

            }catch(Exception e){
                e.printStackTrace();
            }
        }


    }

    public static LinkedList<String> parseLines(LinkedList<String> lines){
        LinkedList<String> parsedLines = new LinkedList<>();
        String[] data;
        String lineToAdd;

        for(String line: lines){
            data = line.split(",");
            lineToAdd = "";
            lineToAdd += (data[0].substring(4));
            if(!lineToAdd.equalsIgnoreCase("false")){
                parsedLines.add(lineToAdd);

            }
        }

        return parsedLines;
    }

    public static LinkedList<String> parseLines2(LinkedList<String> lines){
        LinkedList<String> parsedLines = new LinkedList<>();
        String[] data;
        String lineToAdd;

        int i = 0;
        for(String line: lines){
            if( i != 0) {
                data = line.split(",");
                lineToAdd = "";
                lineToAdd += (data[2]);
                if (!lineToAdd.equalsIgnoreCase("false")) {
                    parsedLines.add(lineToAdd);

                }
            }
            i = 1;
        }

        return parsedLines;
    }

    public static void writeLines(LinkedList<String> lines, String filename){
        try{
            FileWriter writer = new FileWriter(filename);

            int i = 0;
            label: for(String line: lines) {
                if (i != 0) {
                    if(line == "0.0"){
                        File file = new File(filename);
                        file.delete();
                        break label;
                    }
                    writer.write(line + "\n");
                }
                i= 1;
            }

            writer.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public static LinkedList<String> readLines(String fileName){
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

    public static String getHTML(String urlToRead) throws Exception {
        StringBuilder result = new StringBuilder();
        URL url = new URL(urlToRead);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String line;
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }
        rd.close();
        return result.toString();
    }

    public static LinkedList<String> getHTMLLines(String urlToRead) throws Exception {
        LinkedList<String> data = new LinkedList<>();
        StringBuilder result = new StringBuilder();
        URL url = new URL(urlToRead);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String line;
        int i = 0;
        while ((line = rd.readLine()) != null) {
            if(i != 0) data.add(line);

            i = 1;
        }
        rd.close();
        return data;
    }
}
