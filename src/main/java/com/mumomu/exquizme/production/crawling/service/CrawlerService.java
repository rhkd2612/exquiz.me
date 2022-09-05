package com.mumomu.exquizme.production.crawling.service;

import com.mumomu.exquizme.production.crawling.exception.ResultNotFoundException;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class CrawlerService {
    public List<String> crawlImages(String keyword) throws Exception {
        List<String> result = new ArrayList<>();

        try {
            Connection con = Jsoup.connect("https://www.google.com/search?q=" + keyword + "&rlz=1C5CHFA_enKR1009KR1009&source=lnms&tbm=isch&sa=X&ved=2ahUKEwiC-5Xfo8j5AhVOqlYBHRRWCtsQ_AUoAXoECAEQBA&biw=1283&bih=1000&dpr=2");
            //Connection con = Jsoup.connect("https://search.naver.com/search.naver?where=image&sm=tab_jum&query=" + keyword);

            Document doc = con.get();

            //System.out.println(doc);

            Elements source = doc.select("img");
            //System.out.println("Size : " + source.size());

            for (int i = 0; i < source.size(); i++) {
                String res = source.get(i).attr("data-src");
                if (res.isEmpty())continue;
                //System.out.println(res);
                result.add(res);
            }

            if (result.isEmpty()) {
                throw new ResultNotFoundException("No results found");
            }

            return result;
        } catch (IOException e) {
            throw e;
        }
    }
}
