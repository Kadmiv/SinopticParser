package com.gaijin.sinopticparser;

import com.gaijin.sinopticparser.cards.DaySite;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;



/**
 * Created by Kachulyak Ivan.
 */
public class JsoupParser {

    public DaySite getTodayClass(String baseUrl) {
        Document doc = null;
        try {
            doc = Jsoup.connect(baseUrl).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Element body = doc.body();
        DaySite day = new DaySite();
        day.parseDayInfo(body);
        System.out.println(day.toString());
        return day;
    }
}
