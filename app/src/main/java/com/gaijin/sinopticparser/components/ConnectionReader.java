package com.gaijin.sinopticparser.components;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Kachulyak Ivan.
 */
public class ConnectionReader {

    private HttpURLConnection connection;

    public ConnectionReader(HttpURLConnection connection) {
        this.connection = connection;
    }

    public String getConnectionResult() throws IOException {
        String resultString = "";

        if (connection.getResponseCode() == 200) {
            BufferedReader fb = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
            String line = null;
            for (line = fb.readLine(); line != null; line = fb.readLine()) {
                resultString += line;
                System.out.println(line);
            }
            fb.close();
        }

        return resultString;
    }

    public ArrayList<String> getConnectionResultAsList() throws IOException {
        ArrayList<String> resultList = new ArrayList<>();

        if (connection.getResponseCode() == 200) {
            // Read all data from request answer
            BufferedReader fb = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
            String line = null;
            for (line = fb.readLine(); line != null; line = fb.readLine()) {
                resultList.add(line);
            }
            fb.close();
        }

        return resultList;
    }

    public File getConnectionResultAsFile(String path) throws IOException {
        File baseFile = new File(path);
        return getConnectionResultAsFile(baseFile);
    }

    public File getConnectionResultAsFile(File file) throws IOException {

        if (connection.getResponseCode() == 200) {
            BufferedReader fb = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
            BufferedWriter fw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
            String line = null;
            for (line = fb.readLine(); line != null; line = fb.readLine()) {
                fw.write(line);
            }
            fb.close();
            fw.close();
        }

        return file;
    }

}
