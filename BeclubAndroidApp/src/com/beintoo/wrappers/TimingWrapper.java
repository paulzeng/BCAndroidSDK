package com.beintoo.wrappers;


import com.beintoo.utils.DebugUtility;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class TimingWrapper {

     private String montiming;
    private String tuetiming;
    private String wedtiming;
    private String thutiming;
    private String fritiming;
    private String sattiming;
    private String suntiming;
    private String datetimingfrom;
    private String datetimingto;


    public String getMontiming() {
        return montiming;
    }

    public void setMontiming(String montiming) {
        this.montiming = montiming;
    }

    public String getTuetiming() {
        return tuetiming;
    }

    public void setTuetiming(String tuetiming) {
        this.tuetiming = tuetiming;
    }

    public String getWedtiming() {
        return wedtiming;
    }

    public void setWedtiming(String wedtiming) {
        this.wedtiming = wedtiming;
    }

    public String getThutiming() {
        return thutiming;
    }

    public void setThutiming(String thutiming) {
        this.thutiming = thutiming;
    }

    public String getFritiming() {
        return fritiming;
    }

    public void setFritiming(String fritiming) {
        this.fritiming = fritiming;
    }

    public String getSattiming() {
        return sattiming;
    }

    public void setSattiming(String sattiming) {
        this.sattiming = sattiming;
    }

    public String getSuntiming() {
        return suntiming;
    }

    public void setSuntiming(String suntiming) {
        this.suntiming = suntiming;
    }

    public String getDatetimingfrom() {
        return datetimingfrom;
    }

    public void setDatetimingfrom(String datetimingfrom) {
        this.datetimingfrom = datetimingfrom;
    }

    public String getDatetimingto() {
        return datetimingto;
    }

    public void setDatetimingto(String datetimingto) {
        this.datetimingto = datetimingto;
    }

    public String[] getDaysRange(){
        String[] dayrange = {};
        if(montiming != null){
            dayrange[0] = "Monday";
        }else if(tuetiming != null){
            dayrange[0] = "Tuesday";
        }else if(wedtiming != null){
            dayrange[0] = "Wednesday";
        }else if(thutiming != null){
            dayrange[0] = "Thursday";
        }else if(fritiming != null){
            dayrange[0] = "Friday";
        }else if(sattiming != null){
            dayrange[0] = "Saturday";
        }else if(suntiming != null){
            dayrange[0] = "Sunday";
        }

        if(suntiming != null){
            dayrange[1] = "Sunday";
        }else if(sattiming != null){
            dayrange[1] = "Saturday";
        }else if(fritiming != null){
            dayrange[1] = "Friday";
        }else if(thutiming != null){
            dayrange[1] = "Thursday";
        }else if(wedtiming != null){
            dayrange[1] = "Wednesday";
        }else if(tuetiming != null){
            dayrange[1] = "Tuesday";
        }else if(montiming != null){
            dayrange[1] = "Monday";
        }

        return dayrange;
    }

    public String[] getDayTimeRange(){
        List<List<String>> daysAndTimes = new ArrayList<List<String>>();

        daysAndTimes.add(new ArrayList<String>(){{add("Monday");add(montiming);}});
        daysAndTimes.add(new ArrayList<String>(){{add("Tuesday");add(tuetiming);}});
        daysAndTimes.add(new ArrayList<String>(){{add("Wednesday");add(wedtiming);}});
        daysAndTimes.add(new ArrayList<String>(){{add("Thursday");add(thutiming);}});
        daysAndTimes.add(new ArrayList<String>(){{add("Friday");add(fritiming);}});
        daysAndTimes.add(new ArrayList<String>(){{add("Saturday");add(sattiming);}});
        daysAndTimes.add(new ArrayList<String>(){{add("Sunday");add(suntiming);}});

        String openingDay = null;
        String closingDay = null;
        String openingTime = null;
        String closingTime = null;

        DebugUtility.showLog(daysAndTimes.toString());

        for(List<String> dayHours : daysAndTimes){
            if(dayHours.get(1) != null){
                if(openingDay == null){
                    openingDay = dayHours.get(0);
                    String[] hours = dayHours.get(1).split(";");
                    for(int i = 0; i < hours.length; i++){
                        String[] splitted = hours[i].split("-");
                        if(i == 0){
                            Calendar c = Calendar.getInstance(Locale.getDefault());
                            c.set(0,0,0, Integer.parseInt(splitted[0]), 0);
                            openingTime = new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(c.getTime());
                        }
                        if(i == hours.length - 1){
                            Calendar c = Calendar.getInstance(Locale.getDefault());
                            c.set(0,0,0, Integer.parseInt(splitted[1]), 0);
                            closingTime = new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(c.getTime());
                        }
                    }
                }
            }
        }

        Collections.reverse(daysAndTimes);
        for(List<String> dayHours : daysAndTimes){
            if(dayHours.get(1) != null){
                if(closingDay == null){
                    closingDay = dayHours.get(0);
                }
            }
        }

        return new String[]{openingDay, closingDay, openingTime, closingTime};
    }
}
