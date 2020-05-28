package main;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public interface Dest {
    public static String listPageDest="download.asp?tb=xz&id=";

    public static String memberPageDest="list.asp?mod=170&sid=";

    public static String mainurl="http://www.henanjk.com/";

    public static String downPageDest1="location.href='downloadx.asp";

    public static String dir="D:\\mydir\\";

    public static String pageDest="<form name=fyform  method=post ><tr><td width=60% align=right>共";

    //列表数,线程数
    public static int f=20;

    public static Map<String,String> FAILPATH=new ConcurrentHashMap<String, String>();

    public static Map<Integer,String> AllFILEMAP=new ConcurrentHashMap<Integer, String>();

    public static RequestConfig REQUEST_CONFIG=RequestConfig.custom().setConnectTimeout(5000).setSocketTimeout(120000).build();

    public static CloseableHttpClient HTTP_CLIENT= HttpClients.custom().setDefaultRequestConfig(REQUEST_CONFIG).build();
}
