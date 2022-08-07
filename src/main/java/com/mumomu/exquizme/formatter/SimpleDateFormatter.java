package com.mumomu.exquizme.formatter;

import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class SimpleDateFormatter{
    private static SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");

    public static String formatDateToString(Date date){
        return formatter.format(date);
    }
}
