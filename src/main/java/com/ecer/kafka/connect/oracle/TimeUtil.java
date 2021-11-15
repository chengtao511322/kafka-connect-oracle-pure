package com.ecer.kafka.connect.oracle;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 时间工具类
 */
public class TimeUtil {

    public static SimpleDateFormat ymd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    //定义10分钟的查询步长
    public static final int timeStep = 10;

    public static long timeDifference(Date date1, Date date2){
        Date df1 = null;
        Date df2 = null;
        try {
            df1 = ymd.parse(ymd.format(date1));
            df2 = ymd.parse(ymd.format(date2));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        /*返回毫秒*/
        long totalTime = df1.getTime() - df2.getTime();
        /*返回秒*/
        long second=totalTime/1000;
        /*返回天*/
        long days = totalTime / (1000 * 60 * 60 * 24);
        /*返回时*/
        long hours = (totalTime - days * (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
        /*返回分*/
        long minutes = (totalTime - days * (1000 * 60 * 60 * 24) - hours * (1000 * 60 * 60)) / (1000 * 60);
        return totalTime;
    }

    /**
     * 传入年月日的字符串 yyyy-mm-dd hh:mm:ss
     * @param dateStr1
     * @param dateStr2
     * @return
     */
    public static long timeDifference(String dateStr1,String dateStr2){
        Date df1 = null;
        Date df2 = null;
        try {
            df1 = ymd.parse(dateStr1);
            df2 = ymd.parse(dateStr2);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        /*返回毫秒*/
        long totalTime = df1.getTime() - df2.getTime();
        /*返回秒*/
        long second=totalTime/1000;
        /*返回天*/
        long days = totalTime / (1000 * 60 * 60 * 24);
        /*返回时*/
        long hours = (totalTime - days * (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
        /*返回分*/
        long minutes = (totalTime - days * (1000 * 60 * 60 * 24) - hours * (1000 * 60 * 60)) / (1000 * 60);
        return totalTime;
    }

    /**
     * 时间加减
     * @param minute 整形分钟
     * @return 返回加指定时长后的时间字符串
     */
    public static String timeIncrease(String startTime,int minute){
        try {
            Date startDate = ymd.parse(startTime);
            long increaseTime = minute*60*1000;
            Date afterDate = new Date(startDate.getTime() +increaseTime );
            return ymd.format(afterDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}
