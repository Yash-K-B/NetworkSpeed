package com.yash.networkspeed;

import android.util.Log;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class DateManipulate {
    private int day,month,year;
    DateManipulate(){
        Calendar c= GregorianCalendar.getInstance();
        this.day=c.get(Calendar.DAY_OF_MONTH);
        this.month=c.get(Calendar.MONTH)+1;
        this.year=c.get(Calendar.YEAR);
        //Log.d("msg","Date: "+day+"-"+month+"-"+year);
    }
    void refresh(){
        Calendar c= GregorianCalendar.getInstance();
        this.day=c.get(Calendar.DAY_OF_MONTH);
        this.month=c.get(Calendar.MONTH)+1;
        this.year=c.get(Calendar.YEAR);
    }
    String dateToString(){

        DecimalFormat df=new DecimalFormat("00");
        String var="D"+df.format(day)+""+df.format(month)+""+year;
//        Log.d("msg","Date: "+df.format(day)+"-"+df.format(month)+"-"+year+"/"+var);
        return var;
    }
    String getDisplayDate(int index){
        Calendar c= GregorianCalendar.getInstance();
        c.add(Calendar.DATE,-index);
        this.day=c.get(Calendar.DAY_OF_MONTH);
        this.month=c.get(Calendar.MONTH)+1;
        this.year=c.get(Calendar.YEAR);
        return (getDay(c.get(Calendar.DAY_OF_WEEK))+", "+day+" "+getMonth(this.month)+" "+year);

    }
    String getDate(int index){
        Calendar c= GregorianCalendar.getInstance();
        c.add(Calendar.DATE,-index);
        this.day=c.get(Calendar.DAY_OF_MONTH);
        this.month=c.get(Calendar.MONTH)+1;
        this.year=c.get(Calendar.YEAR);
//        Log.d("msg1","-"+index+"days:  :"+c.get(Calendar.DAY_OF_MONTH)+"-"+(c.get(Calendar.MONTH)+1)+"-"+c.get(Calendar.YEAR));
        return dateToString();
    }
    String getMonth(int month){
        String name;
        switch (month){
            case 1:
                name="Jan";
                break;
            case 2:
                name="Feb";
                break;
            case 3:
                name="Mar";
                break;
            case 4:
                name="Apr";
                break;
            case 5:
                name="May";
                break;
            case 6:
                name="Jun";
                break;
            case 7:
                name="Jul";
                break;
            case 8:
                name="Aug";
                break;
            case 9:
                name="Sep";
                break;
            case 10:
                name="Oct";
                break;
            case 11:
                name="Nov";
                break;
            case 12:
                name="Dec";
                break;
            default:
                name="None";
        }
        return name;
    }
    String getDay(int day){
        String name;
        switch (day){
            case 1:
                name="Sunday";
                break;
            case 2:
                name="Monday";
                break;
            case 3:
                name="Tuesday";
                break;
            case 4:
                name="Wednesday";
                break;
            case 5:
                name="Thursday";
                break;
            case 6:
                name="Friday";
                break;
            case 7:
                name="Saturday";
                break;
            default:
                name="None";
        }
        return name;
    }
}
