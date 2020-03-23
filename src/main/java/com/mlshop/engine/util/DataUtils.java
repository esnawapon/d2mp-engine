package com.mlshop.engine.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DataUtils {
    public static boolean isSize(String size) {
        if (size.equals("m")) return true;
        if (size.equals("l")) return true;
        if (size.indexOf("s") == size.length() - 1 && size.length() <= 2) return true;
        if (size.indexOf("xl") == size.length() - 2 && size.length() <= 3) return true;
        return false;
    }

    public static List<String> genDateFromNow(int numberOfMonths) {
        List<String> results = new ArrayList();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
        for (int i = 0; i < numberOfMonths; i++) {
            Calendar now = Calendar.getInstance();
            now.set(Calendar.DATE, 1);
            now.add(Calendar.MONTH, i);
            results.add(sdf.format(now.getTime()));
        }
        return results;

    }
}
