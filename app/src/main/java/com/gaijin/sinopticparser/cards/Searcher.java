package com.gaijin.sinopticparser.cards;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Kachulyak Ivan.
 */
public class Searcher {

    String search_link = "search.php?q="; // pfd - name for searching
    private String mainLink;

    public Searcher(String mainLink) {
        this.mainLink = mainLink;
    }

    public ArrayList<String> getSearchingResult(String request) {
        int time = 25000;
        ArrayList<String> resultList = new ArrayList<>();
        String link = mainLink + search_link + request;
            HttpURLConnection connection = null;
            try {
                connection = (HttpURLConnection) new URL(link).openConnection();
                connection.setRequestMethod("GET");
                connection.setReadTimeout(time);
                connection.setConnectTimeout(time);
                connection.setUseCaches(false);
                connection.connect();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                if (connection.getResponseCode() == 200) {
                    BufferedReader fb = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
                    //BufferedWriter fw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(baseFile)));
                    String line = null;
                    System.out.println("        ##  Search  ##      ");
                    for (line = fb.readLine(); line != null; line = fb.readLine()) {
                        //fw.write(line);
                        //System.out.println(line);
                        resultList.add(line);
                    }
                    fb.close();
                    //fw.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        return resultList;
    }

}