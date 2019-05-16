package com.amura.rakesh.event;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Utility {

    // To find current date
    public static String currentDate() {

        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Calendar calendar = Calendar.getInstance();
        String currentDate = dateFormat.format(calendar.getTime());

        return currentDate;
    }

    // To convert date into specific format
    public static String convertDate(String s) {

        Date initDate = null;
        try {
            initDate = new SimpleDateFormat("dd-MM-yyyy").parse(s);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        String parsedDate = formatter.format(initDate);

        return parsedDate;

    }


}
