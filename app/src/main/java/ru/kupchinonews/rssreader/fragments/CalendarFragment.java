package ru.kupchinonews.rssreader.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.samsistemas.calendarview.widget.CalendarView;

import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.Property;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.Locale;

import ru.kupchinonews.rssreader.CalendarItem;
import ru.kupchinonews.rssreader.Events;
import ru.kupchinonews.rssreader.R;
import ru.kupchinonews.rssreader.activity.BaseActivity;
import ru.kupchinonews.rssreader.activity.MainActivity;
import ru.kupchinonews.rssreader.services.CalendarService;

public class CalendarFragment extends Fragment{

    private CalendarView mCalendarView;
    private TextView mTextView;
    private View mView;
    private ProgressBar mProgressBar;
    private TextView mNoInternetTextView;
    private static CalendarFragment mInstance;
    public static boolean mReady = false;
    public static boolean mDoneLoading = false;
    private Date mCurrentDate;
    private Date mControlDate;

    private MainActivity act;

    @Override
    public void onAttach(Activity myActivity) {
        super.onAttach(myActivity);
        this.act = (MainActivity) myActivity;
    }

    public static CalendarFragment getInstance() {
        if(mInstance == null)
            mInstance = new CalendarFragment();
        return  mInstance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_calendar, container, false);

        mNoInternetTextView = (TextView) mView.findViewById(R.id.no_internet_text_view);

        mProgressBar = (ProgressBar) mView.findViewById(R.id.progressBar);
        mCalendarView = (CalendarView) mView.findViewById(R.id.calendar_view);
        mCalendarView.setSelectedDayTextColor(getResources().getColor(R.color.colorPrimary));
        mCalendarView.setSelectedDayBackground(getResources().getColor(R.color.current_day_of_month));

        mCalendarView.getBackButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayEvents(false);

                Calendar c = Calendar.getInstance();
                c.setTime(mControlDate);
                c.add(Calendar.MONTH, -1);
                mControlDate = c.getTime();

                mCalendarView.setCurrentDay(mCurrentDate);
                mCalendarView.getNextButton().setVisibility(View.VISIBLE);
                mCalendarView.getBackButton().setVisibility(View.GONE);
                mCalendarView.backButton();
                displayEvents(true);
                mTextView.setText("");
            }
        });

        mCalendarView.getNextButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayEvents(false);

                Calendar c = Calendar.getInstance();
                c.setTime(mControlDate);
                c.add(Calendar.MONTH, 1);
                mControlDate = c.getTime();

                mCalendarView.getNextButton().setVisibility(View.GONE);
                mCalendarView.getBackButton().setVisibility(View.VISIBLE);
                mCalendarView.nextButton();
                mCalendarView.unsetCurrentDay(mCurrentDate);
                displayEvents(true);
                mTextView.setText("");
            }
        });

        mCalendarView.getBackButton().setVisibility(View.GONE);

        mTextView = (TextView) mView.findViewById(R.id.calendar_text_view);
        mTextView.setTypeface(BaseActivity.getDefaultFont());
        mCalendarView.setFirstDayOfWeek(Calendar.MONDAY);
        mCalendarView.setIsOverflowDateVisible(true);
        mCalendarView.setBackButtonColor(R.color.colorPrimary);
        mCalendarView.setNextButtonColor(R.color.colorPrimary);
        mCalendarView.refreshCalendar(Calendar.getInstance(Locale.getDefault()));
        mCalendarView.setOnDateClickListener(new CalendarView.OnDateClickListener() {
            @Override
            public void onDateClick(@NonNull Date date) {
                mCalendarView.setDateAsSelected(date);
                mTextView.setText(Events.getDateEventData(date));
                mCalendarView.updateTitleLayout(date.getDate());
            }
        });

        if(!act.hasInternet()) {
            mNoInternetTextView.setVisibility(View.VISIBLE);
        } else {
            mProgressBar.setVisibility(View.VISIBLE);
        }

        mDoneLoading = true;
        if(mReady)
            startCalendarService();

        mCurrentDate = new Date(System.currentTimeMillis());

        mControlDate = new Date(mCurrentDate.getYear(), mCurrentDate.getMonth(), 5);

        return mView;
    }

    public void startCalendarService() {
        mNoInternetTextView.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);
        Intent intent = new Intent(getActivity(), CalendarService.class);
        intent.putExtra(CalendarService.RECEIVER, resultReceiver1);
        getActivity().startService(intent);
    }

    private void displayEvents(boolean flag) {
        CalendarItem event;
        int year;
        int month;
        int day;
        Date leftBorder, rightBorder;

        year = (mControlDate.getMonth() == 0) ? mControlDate.getYear() - 1 : mControlDate.getYear();
        month = (mControlDate.getMonth() == 0) ? 11 : mControlDate.getMonth() - 1;
        day = getDaysOfMonth(new Date(year, month, 10));
        leftBorder = new Date(year, month, day);

        year = (mControlDate.getMonth() == 11) ? mControlDate.getYear() + 1 : mControlDate.getYear();
        month = (mControlDate.getMonth() == 11) ? 0 : mControlDate.getMonth() + 1;
        day = 1;
        rightBorder = new Date(year, month, day);
        for(int i = 0;i<Events.get().getEvents().size();i++) {
            event = Events.get().getEvents().get(i);
            for(int j=0;j<event.getRDate().size();j++)
                if(event.getRDate().get(j).after(leftBorder) && event.getRDate().get(j).before(rightBorder))
                    mCalendarView.setDateHasEvents(event.getRDate().get(j), flag);
        }
    }

    private int getDaysOfMonth(Date date) {
        int iYear = date.getYear();
        int iMonth = date.getMonth();
        int iDay = date.getDate();

        Calendar mycal = new GregorianCalendar(iYear, iMonth, iDay);

        return mycal.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    private void prepareEvents() {
        Calendar c = Calendar.getInstance();
        c.setFirstDayOfWeek(Calendar.MONDAY);
        CalendarItem event;

        Date mLastDay = (mCurrentDate.getMonth() >= 10) ? new Date(mCurrentDate.getYear() + 1, (mCurrentDate.getMonth() + 2) % 12, 1) : new Date(mCurrentDate.getYear(), (mCurrentDate.getMonth() + 2) % 12, 1);
        for(int j = 0;j<Events.get().getEvents().size();j++) {
            event = Events.get().getEvents().get(j);
            if(event.getDTEnd() != null && event.getRRule() != 0) {
                Date tmp = event.getDTStart();
                while(tmp.before(mLastDay)) {
                    int b1 = event.getRRuleBit(tmp.getDay()-1);
                    if(b1 == 1 && !tmp.before(mCurrentDate))
                        event.addRDate(tmp);
                    c.setTime(tmp);
                    c.add(Calendar.DATE, 1);
                    tmp = c.getTime();
                }
            } else if (event.getRDate().size() == 0) {
                if(!event.getDTStart().before(mCurrentDate))
                    event.addRDate(event.getDTStart());
            }
        }
        displayEvents(true);
    }

    private final ResultReceiver resultReceiver1 = new ResultReceiver(new Handler()) {
        @SuppressWarnings("unchecked")
        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            net.fortuna.ical4j.model.Calendar calendar = (net.fortuna.ical4j.model.Calendar) resultData.getSerializable(CalendarService.CALENDAR);
            ArrayList<CalendarItem> items = parseCalendar(calendar);

            if (calendar != null) {
                Events.get().setEvents(items);
            } else {
                Toast.makeText(getActivity(), getString(R.string.calendar_error), Toast.LENGTH_LONG).show();
            }

            prepareEvents();
            //ArrayList a = Events.get().getEvents();

            mProgressBar.setVisibility(View.GONE);
            mCalendarView.setVisibility(View.VISIBLE);
            mTextView.setVisibility(View.VISIBLE);
            getActivity().stopService(new Intent(getActivity(), CalendarService.class));
        }
    };

    private byte parseDays(String[] ar) {
        byte result = 0;
        for (int i=0;i<ar.length;i++) {
            switch(ar[i]) {
                case "MO":
                    result |= 1;
                    break;
                case "TU":
                    result |= 1 << 1;
                    break;
                case "WE":
                    result |= 1 << 2;
                    break;
                case "TH":
                    result |= 1 << 3;
                    break;
                case "FR":
                    result |= 1 << 4;
                    break;
                case "SA":
                    result |= 1 << 5;
                    break;
                case "SU":
                    result |= 1 << 6;
                    break;
            }
        }
        return result;
    }

    private ArrayList<CalendarItem> parseCalendar(net.fortuna.ical4j.model.Calendar calendar) {
        ArrayList events = new ArrayList<>();
        String s;
        Date date;
        for (Iterator i = calendar.getComponents().iterator(); i.hasNext();) {
            Component component = (Component) i.next();
            SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
            if (component.getName().equals("VEVENT")) {
                CalendarItem event = new CalendarItem();
                for (Iterator j = component.getProperties().iterator(); j.hasNext();) {
                    Property property = (Property) j.next();
                    switch (property.getName()) {
                        case "RRULE":
                            s = property.getValue();
                            int index1 = s.indexOf(";BYDAY");
                            int index2 = s.indexOf(";WKST");
                            if (index1 < index2) {
                                s = s.substring(s.indexOf("BYDAY=") + "BYDAY=".length(), s.indexOf(";WKST"));
                            } else {
                                s = s.substring(s.indexOf("BYDAY=") + "BYDAY=".length(), s.length());
                            }
                            event.setRRule(parseDays(s.split("[,]")));
                            break;
                        case "CATEGORIES":
                            event.setCategories(property.getValue());
                            break;
                        case "LOCATION":
                            event.setLocation(property.getValue());
                            break;
                        case "DTSTART":
                            s = property.getValue();
                            s = s.replaceAll("T", "");
                            event.setDTStart(df.parse(s, new ParsePosition(0)));
                            break;
                        case "DTEND":
                            s = property.getValue();
                            s = s.replaceAll("T", "");
                            event.setDTEnd(df.parse(s, new ParsePosition(0)));
                            break;
                        case "RDATE":
                            s = property.getValue();
                            s = s.replaceAll("T", "");
                            date = df.parse(s, new ParsePosition(0));
                            if(!date.before(mCurrentDate))
                                event.addRDate(date);
                            break;
                        case "SUMMARY":
                            event.setSummary(property.getValue());
                            break;
                        case "X-TAGS":
                            event.setXTags(property.getValue());
                            break;
                    }
                }
                events.add(event);
            }
        }
        return events;
    }
}
