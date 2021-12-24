package com.sirius.library.mobile;





import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;


/**
 * Created by gerc on 09.11.2014.
 */
public class DateUtils {
    public static final String DATEFORMAT_YYYY_MM_DDT_HH_MM_SS = "yyyy-MM-dd'T'HH:mm:ss";
    public static final String PATTERN_ddMMyyyyHHmmss = "dd.MM.yyyy HH:mm:ss";
    public static final String PATTERN_ddMMyyyyHHmm = "dd.MM.yyyy HH:mm";
    public static final String PATTERN_HHmmddMM = "HH:mm dd.MM";
    public static final String PATTERN_ddMMyyyy_COMMA = "dd.MM.yyyy";
    public static final String PATTERN_HHmm = "HH:mm";
    public static final String DATEFORMAT_HH_MM = "HH:mm";
    public static final String DATEFORMAT_HH_MM_SS = "HH:mm:ss";
    public static final String PATTERN_HH = "HH";
    public static final String PATTERN_MM = "mm";
    public static final String DATEFORMAT_DD_LLL_YYYY = "dd LLL yyyy";
    public static final String DATEFORMAT_DD_LLL = "dd LLL";
    //Wed, 30-May-2018 19:25:25 GMT
    //Wed, 30 Aug 2018 00:00:00 GMT
    public static final String PATTERN_ddMMM_COOKIE = "EEE, dd-MMM-yyyy HH:mm:ss z";
    public static final String PATTERN_ddMMM_COOKIE2 = "EEE, dd MMM yyyy HH:mm:ss z";
    public static final String PATTERN_PARSEGSON = "yyyy-MM-dd HH:mm:ss";
    public static final String PATTERN_ddMMM_yyyy = "dd MMM, yyyy";
    public static final String PATTERN_ddMMMyyyy = "dd MMM yyyy";
    public static final String PATTERN_ddMMMMyyyy = "dd MMMM yyyy";
    public static final String PATTERN_ddMMMyyyyHHmm = "dd MMM yyyy HH:mm";
    public static final String PATTERN_E = "E";
    public static final String DATEFORMAT_EEEE = "EEEE";
    public static final String PATTERN_EE = "EE";
    public static final String PATTERN_EEE = "EEE";
    public static final String PATTERN_EEEE = "EEEE";
    public static final String PATTERN_EEddMMM_yyyy = "EE, dd MMM, yyyy";
    public static final String PATTERN_ddMMM_yyyy_EEEE = "dd.MM.yyyy, EEEE";
    //13.01.2017 16:17
    public static final String PATTERN_EEddMMM = "EE, MMM dd";
    public static final String PATTERN_DATETIME = "yyyy-MM-dd HH:mm:ss";
    public static final String PATTERN_DATETIME_DOT = "dd.MM.yyyy HH:mm";
    public static final String PATTERN_DATE = "yyyy-MM-dd";
    public static final String PATTERN_TIMELINE = "MMM yyyy";
    public static final String PATTERN_TIMELINE2 = "MMM  dd yyyy";
    public static final String PATTERN_DAY_MONTH = "dd MMMM";
    public static final String PATTERN_DAY_MONTH_IN_TIME = "dd MMMM в HH:mm";
    public static final String PATTERN_ROSTER_STATUS_RESPONSE = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'";
    public static final String PATTERN_ROSTER_STATUS_RESPONSE2 = "yyyy-MM-dd HH:mm:ss.SSSSSSZ";
    // 2019-01-15 18:42:01Z
    public static final String PATTERN_INDY_MESSAGE = "yyyy-MM-dd HH:mm:ssZ";
    public static final String PATTERN_INDY_MESSAGE2 = "yyyy-MM-dd'T'HH:mm:ssZ";


    public static String formatDate(String dateStr, SimpleDateFormat srcFormat, SimpleDateFormat dstformat) {
        if (dateStr == null) return "";
        try {
            Date date = srcFormat.parse(dateStr);
            return dstformat.format(date);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateStr;
    }


    public static Date getDateFromString(String dateStr, String pattern, boolean fromGmt) {
        if (dateStr == null) return new Date();
        try {
            SimpleDateFormat srcFormat = new SimpleDateFormat(pattern);
            if (fromGmt) {
                srcFormat.setTimeZone(TimeZone.getTimeZone("GMT+0:00"));
            }
            return srcFormat.parse(dateStr);


        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Date();
    }


    public static String getStringFromDate(Date date, String pattern, boolean isGMTTime) {

        String formatedDate = "";
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat(pattern, Locale.getDefault());
            if (isGMTTime) {
                dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+0:00"));
            }
            formatedDate = dateFormat.format(date);
        } catch (Exception r) {

        }


        return formatedDate;

    }


    public static String getStringFromDate(Calendar date, String pattern, boolean isGMTTime) {

        String formatedDate = "";
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat(pattern, Locale.getDefault());
            if (isGMTTime) {
                dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+0:00"));
            }
            formatedDate = dateFormat.format(date.getTime());
        } catch (Exception r) {

        }


        return formatedDate;

    }

    public static boolean isDateToday(Date date) {
        Calendar calendar = Calendar.getInstance();
        return date.getDate() == calendar.get(Calendar.DAY_OF_MONTH) &&
                date.getYear() == calendar.get(Calendar.YEAR) &&
                date.getMonth() == calendar.get(Calendar.MONTH);
    }

    public static boolean isDateYesterday(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        return date.getDate() == calendar.get(Calendar.DAY_OF_MONTH) &&
                date.getYear() == calendar.get(Calendar.YEAR) &&
                date.getMonth() == calendar.get(Calendar.MONTH);

    }

    public static String parseDateToEEddMMMString(Date date) {

        String formatedDate = "";
        try {
            //    Log.d("mylog23", "parseDateToHhmmString date =" + date);
            SimpleDateFormat dateFormat = new SimpleDateFormat(PATTERN_EEddMMM);

            formatedDate = dateFormat.format(date);
        } catch (Exception r) {

        }


        return formatedDate;

    }

    public static String parseDateToHhmmString(Date date) {

        String formatedDate = "";
        try {

            SimpleDateFormat dateFormat = new SimpleDateFormat(PATTERN_HHmm);

            formatedDate = dateFormat.format(date);
        } catch (Exception r) {

        }


        return formatedDate;

    }


    public static String parseDateToDdMMyyyyString(Date date) {
        String formatedDate = "";

        SimpleDateFormat dateFormat = new SimpleDateFormat(PATTERN_ddMMyyyy_COMMA);

        formatedDate = dateFormat.format(date);


        return formatedDate;

    }

    public static Calendar parseDateFromHHmm(String time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(0);
        //  String time = "530pm";
        SimpleDateFormat dateFormat = new SimpleDateFormat(PATTERN_HHmm);
        if (!"".equals(time) && time != null) {
            try {
                Date date = new Date(0);
                date = dateFormat.parse(time);
                calendar = Calendar.getInstance();
                calendar.setTime(date);
                calendar.setTimeZone(TimeZone.getDefault());
                return calendar;
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        return calendar;
    }

    public static String parseDayOfWeekFromCalendar(Date date, int type) {
        String dayofWeek = "";
        try {
            String format = PATTERN_EEEE;
            switch (type) {
                case 1:
                    format = PATTERN_E;
                    break;
                case 2:
                    format = PATTERN_EE;
                    break;
                case 3:
                    format = PATTERN_EEE;
                    break;
                default:
                    format = PATTERN_EEEE;
            }
            SimpleDateFormat dateFormat = new SimpleDateFormat(format);
            dayofWeek = dateFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dayofWeek;
    }

    public static Calendar parseDateFromDdMMyyyy(String val) {
        SimpleDateFormat parseFormat = new SimpleDateFormat(PATTERN_ddMMM_COOKIE, Locale.ENGLISH);
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(parseFormat.parse(val));
            return calendar;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Calendar.getInstance();
    }

    public static Calendar parseDateWithDayOfWeek(int dayOfWeek) {
        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.DAY_OF_WEEK, dayOfWeek);
        //   Log.d("mylog35", "calendar.getTime2=" + calendar.getTime());
        return calendar;
    }


    public static String getMonthForInt(Locale locale, int num) {
        if (locale == null) {
            locale = Locale.getDefault();
        }
        String month = "wrong";
        DateFormatSymbols dfs = new DateFormatSymbols(locale);
        String[] months = dfs.getMonths();
        if (num >= 0 && num <= 11) {
            month = months[num];
        }
        return month;
    }

    public static Date stringHHmmTodate(String date) {
        Date formattedDate = new Date();
        if (date.isEmpty()) {
            return formattedDate;
        }
        SimpleDateFormat format = new SimpleDateFormat(DATEFORMAT_HH_MM);
        try {
            formattedDate = format.parse(date);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return formattedDate;
    }

    public static String dateToHHmmss(Date date) {
        String formattedDate = "";
        if (date == null) {
            return formattedDate;
        }
        SimpleDateFormat format = new SimpleDateFormat(DATEFORMAT_HH_MM_SS);
        try {
            formattedDate = format.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return formattedDate;
    }


    public static String dateToddMMyyyyHHmmss(Date date) {
        String formattedDate = "";
        if (date == null) {
            return formattedDate;
        }
        SimpleDateFormat format = new SimpleDateFormat(DATEFORMAT_YYYY_MM_DDT_HH_MM_SS);
        try {
            formattedDate = format.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return formattedDate;
    }


}
