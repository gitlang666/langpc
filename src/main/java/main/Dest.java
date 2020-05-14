package main;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public interface Dest {
    public static String listPageDest="download.asp?tb=xz&id=";

    public static String memberPageDest="list.asp?mod=170&sid=";

    public static String mainurl="http://www.henanjk.com/";

    public static String downPageDest1="location.href='downloadx.asp";

    public static String dir="D:\\mydir\\";

    public static String pageDest="<form name=fyform  method=post ><tr><td width=60% align=right>共";

    //页数
    public static int range=5;

    public static Map<String,String> FAILPATH=new ConcurrentHashMap<String, String>();
}
