package com.sivaram.populartvseries;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Common {
    public static void makeToast(String msg, Context context){
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }
    public static String changeDateFormat(String inputDateStr){

        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, yyyy");
        String outputDateStr="";
        try {
            Date date = inputFormat.parse(inputDateStr);
            outputDateStr = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return outputDateStr;
    }

    public static boolean getNetWorkStatus(ConnectivityManager conManager){
        boolean mobileData = false;
        boolean wifi = false;
        NetworkInfo[] networkInfo = conManager.getAllNetworkInfo();
        for (NetworkInfo netInfo : networkInfo) {
            if (netInfo.getTypeName().equalsIgnoreCase("WIFI")) {
                if (netInfo.isConnected()) {
                    mobileData = true;
                }
            }
            else if (netInfo.getTypeName().equalsIgnoreCase("MOBILE")) {
                if (netInfo.isConnected()) {
                    wifi = true;
                }
            }
        }
        return mobileData || wifi;
    }
}
