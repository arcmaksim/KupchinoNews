package ru.kupchinonews.rssreader.models;

import java.util.ArrayList;
import java.util.Date;

public class Events {

    private static Events sEvents;

    private static ArrayList<CalendarItem> mEvents;

    private Events() {
        mEvents = new ArrayList<>();
    }

    public static Events get() {
        if (sEvents == null)
            sEvents = new Events();
        return sEvents;
    }

    public void setEvents(ArrayList<CalendarItem> events) {
        mEvents = events;
    }

    public ArrayList<CalendarItem> getEvents() {
        return mEvents;
    }

    public static String getDateEventData(Date date) {
        String result = "";
        String temp;
        boolean flag = false;
        String temp1, temp2 = "";
        for(CalendarItem item : mEvents) {
            temp1 = String.valueOf(item.getDTStart().getMinutes()).length() == 1 ? "0" + item.getDTStart().getMinutes() : String.valueOf(item.getDTStart().getMinutes());
            if(item.getDTEnd() != null)
                temp2 = String.valueOf(item.getDTEnd().getMinutes()).length() == 1 ? "0" + item.getDTEnd().getMinutes() : String.valueOf(item.getDTEnd().getMinutes());
            temp = (item.getDTEnd() != null && (item.getDTEnd().getHours() != item.getDTStart().getHours() ||
                    item.getDTStart().getMinutes() != item.getDTEnd().getMinutes())) ?
                    item.getDTStart().getHours() + ":" + temp1 + " - " + item.getDTEnd().getHours() + ":" + temp2 :
                    item.getDTStart().getHours() + ":" + temp1;
            for(Date d : item.getRDate()) {
                if(date.getMonth() == d.getMonth() && date.getDate() == d.getDate()) {
                    if(flag)
                        result += "\n\n";
                    result += item.getSummary() + "\n" + temp + "\n" + item.getLocation();
                    flag = true;
                }
            }
        }
        return result;
    }

}