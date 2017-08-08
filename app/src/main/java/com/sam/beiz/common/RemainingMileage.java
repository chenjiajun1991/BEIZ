package com.sam.beiz.common;

/**
 * Created by SAM-PC2 on 2017/4/27.
 */
public class RemainingMileage {

    //权重系数
    public  static final double WEIGHT = 0.8;

    public static final double maxDistance = 20;

    public  static final double maxVoltage = 29;
    public  static final double minVoltage = 24;

    public static double preRoute = 0;
    public static  double preVoltage = 29;

    public static double lastRoute = 10000;
    public static double lastVoltage = 24;

//    public static double getButDistance(){
//
//        double t = Math.abs(lastRoute - preRoute) / Math.abs(preVoltage - lastVoltage);
//
//        double s = Math.abs(lastVoltage - minVoltage)*t*WEIGHT;
//
//
//        return s;
//    }

   public static double getDistance(double voltage){
       double weight = 1;

       if(voltage>29){
           weight = 1;
       }
       if(voltage >= 28 && voltage < 29){
           weight = 0.95;
       }
       if(voltage >= 27 && voltage < 28){
           weight = 0.9;
       }
       if(voltage >= 26 && voltage < 27){
           weight =0.85;
       }
       if(voltage >= 25 && voltage < 26){
           weight = 0.8;
       }
       if(voltage >= 24 && voltage < 25){
           weight = 0.75;
       }
       if(voltage < 24){
           weight = 0;
       }

       double distance = (maxDistance/(maxVoltage - minVoltage))*(voltage - minVoltage)*weight;
    return  distance > 20 ? 20 : distance;
   }

}
