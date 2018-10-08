package com.gaijin.sinopticparser.searching;

import com.gaijin.sinopticparser.components.ConnectionReader;
import com.gaijin.sinopticparser.components.Variables;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Kachulyak Ivan.
 * <p>
 * This class is for working with the search engine  on Sinoptic web page
 */
public class Searcher implements Variables {

    public Searcher() {
    }

    /**
     * This function get name of city and send request for searching.
     *
     * @param request - name of city for searching
     * @return - list of find city, which meet to request
     */
    public ArrayList<String> getSearchingResult(String request) {
        int timeOut = 2500;
        ArrayList<String> resultList = new ArrayList<>();

        // Create link request
        String link = MAIN_LINK + SEARCH_LINK + request;
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) new URL(link).openConnection();
            connection.setRequestMethod("get");
            connection.setReadTimeout(timeOut);
            connection.setConnectTimeout(timeOut);
            connection.setUseCaches(false);
            connection.connect();
        } catch (IOException e) {
            e.printStackTrace();
        }

        ConnectionReader connector = new ConnectionReader(connection);
        try {
            resultList = connector.getConnectionResultAsList();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return resultList;
    }

}
