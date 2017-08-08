package com.sam.beiz.Config;



/**
 * Created by zhejunliu on 2017/3/27.
 */

public class ConfigURL {

   public static final String checkjson = "http://114.55.33.1/push/index.php";
//   public static final String healthUrl="http://114.55.33.1/newsphp/menbi.php";
//   public static final String replaceHost="114.55.33.1";
//   public static final String mainText="http://114.55.33.1/newsphp/maintext.php";
//   public static final String cad="http://114.55.33.1/newsphp/getImages.php";
//   public static final String cad2="http://114.55.33.1/newsphp/getImage.php";
//   public static final String picstorepath="http://114.55.33.1/upload/user_images/";
   public static String storeTaoBaoUrl = "https://beizhenylqx.tmall.com/";

//   public static final String onlinerepair="http://114.55.33.1/newsphp/onlinerepair.php";
//   public static final String phystoreurl="http://114.55.33.1/newsphp/phystore.php";

   public static  Boolean Netconnect=true;
   public static final String replaceHost="http://114.55.33.1";
   public static final String healthUrl=           replaceHost+      "/newsphp/menbi.php";   //健康管家list
   public static final String mainText=             replaceHost+      "/newsphp/maintext.php"; //传入id get
   public static final String cad=                   replaceHost+      "/newsphp/getImages.php";     //滚动3栏广告
   public static final String cad2=                  replaceHost+      "/newsphp/getImage.php";    //贝珍商城上部分广告
   public static final String picstorepath=         replaceHost+      "/Beiz/upload/user_images/";   //单独图片广告对应的图片路径前部分(配合cad,cad2的json一起使用)
   public static final String onlinerepair=         replaceHost+      "/newsphp/onlinerepair.php";   //在线报修  保修政策
   public static final String phystoreurl=          replaceHost+      "/newsphp/phystore.php";     //实体店

   public static final String repairListurl=        replaceHost+      "/newsphp/onlineRepairImageWord.php";     //在线报修图文
   public static final String repairListurl3=        replaceHost+      "/newsphp/onlineRepairImageWord3.php";     //在线报修图文
   //  public static final String localhost="http://114.55.33.1/";
   public static final String UPLOAD_URL =          replaceHost+        "/Beiz/repair/upload.php";

   public static final String UPLOADNoPic_URL =    replaceHost+          "/Beiz/repair/uploadNoPic.php";
   public static final String getRepair_URL =      replaceHost+          "/Beiz/info/getRepairInfo.php";
   public static final String getNRepair_URL =     replaceHost+         "/Beiz/info/getNRepairInfo.php";


   public static  final String fee_URL=              replaceHost+         "/newsphp/maintenancefee.php";
   public static final String  onlineRepairAboveurl=replaceHost+         "/newsphp/onlineRepairAbove.php";
   public static final String  repairQuestion=replaceHost+         "/newsphp/question.php";


   public static final String checkJson="http://114.55.33.1:8080/boyuan/user/signin.json?";
   public static final java.lang.String sendNopic = replaceHost+  "/newsphp/send/sendNoPic.php";
   public static final java.lang.String sendpic =   replaceHost+     "/newsphp/send/sendPic.php";
   public static final java.lang.String sendvideo =   replaceHost+     "/newsphp/send/sendVideo.php";





}
