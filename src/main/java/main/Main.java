package main;

import com.alibaba.fastjson.JSONArray;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

public class Main {
    static Logger logger= LoggerFactory.getLogger(Main.class);
    static String url="http://www.henanjk.com/";
    static String dest="list.asp?mod=170&sid=";
    static String mydir="C:\\Users\\Administrator\\Desktop\\lang\\";
    static List<Integer> downInteger;
    static List<String> downList=new ArrayList<String>();
    static Map<String,Integer> downMap= new HashMap<String, Integer>();
    static List<Header> headerList;
    public static void main(String[] args)  {


        //1.打开浏览器
        CloseableHttpClient httpClient = HttpClients.createDefault();
        //2.声明get请求
        HttpPost httpPost = new HttpPost("http://www.henanjk.com/yanzheng.asp");
        //3.开源中国为了安全，防止恶意攻击，在post请求中都限制了浏览器才能访问
        httpPost.addHeader("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/68.0.3440.106 Safari/537.36");
        httpPost.setHeader("Content-type", "application/x-www-form-urlencoded");//默认是:application/x-www-form-urlencoded
        //4.判断状态码
        List<NameValuePair> parameters = new ArrayList<NameValuePair>(0);
        parameters.add(new BasicNameValuePair("username", "sx5275"));
        parameters.add(new BasicNameValuePair("password", "626270"));
        parameters.add(new BasicNameValuePair("ucode", "250727058"));
        UrlEncodedFormEntity formEntity = null;
        try {
            formEntity = new UrlEncodedFormEntity(parameters,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            logger.info(e.getMessage(),e);
        }
        httpPost.setEntity(formEntity);

        //5.发送请求
        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(httpPost);
        } catch (IOException e) {
            logger.info(e.getMessage(),e);
        }
        logger.info(response.getStatusLine().getStatusCode()+"");
        if(response.getStatusLine().getStatusCode()==200){
            Header[] httpHeaders = httpPost.getAllHeaders();
            for (Header httpHeader : httpHeaders) {
                logger.info(httpHeader.getName() + ":" + httpHeader.getValue());
            }
            HttpEntity entity = response.getEntity();
            String string = null;
            try {
                string = EntityUtils.toString(entity, "utf-8");
            } catch (IOException e) {
                logger.info(e.getMessage(),e);
            }
            logger.info(string);
        }else if(response.getStatusLine().getStatusCode()==302){
            String newuri="";
            Header header = response.getFirstHeader("location"); // 跳转的目标地址是在 HTTP-HEAD 中的
            newuri = header.getValue(); // 这就是跳转后的地址，再向这个地址发出新申请，以便得到跳转后的信息是啥。
            logger.info(newuri);


            HttpPost httpPost1 = new HttpPost("http://www.henanjk.com/"+newuri);

            Header[] httpHeaders = response.getAllHeaders();
            headerList=new ArrayList<Header>();
            for (Header httpHeader : httpHeaders) {
                logger.info(httpHeader.getName() + ":" + httpHeader.getValue());
                if(httpHeader.getName().equals("Set-Cookie")){
                    logger.info("Cookie="+httpHeader.getName() + ":" + httpHeader.getValue());
                    httpPost1.addHeader("Cookie",httpHeader.getValue());
                    headerList.add(httpHeader);
                }
            }
            try {
                response=httpClient.execute(httpPost1);
                HttpEntity entity = response.getEntity();
                String string = null;
                try {
                    string = EntityUtils.toString(entity, "GBK");
                } catch (IOException e) {
                    logger.info(e.getMessage(),e);
                }
                logger.info(string);
                //找到目标
                downInteger=KMP.kmp(string,dest);
                logger.info("count="+downInteger.size());
                for (Integer integer : downInteger) {
                    String downstr=string.substring(integer,integer+dest.length()+3);
                    logger.info(integer+":"+downstr);
                    downList.add(downstr);
                    downMap.put(Dest.mainurl+downstr,integer);
                }
                logger.info("downMapcount="+downMap.size());
                int ix=-0;
                if(ix==-1){
                    filedown(url+"downloadx.asp?tb=xz&id=232244",headerList,ix);
                }else {
                    int js=0;
                    int f=5;
                    final CountDownLatch countDownLatch = new CountDownLatch(f);
                    MyCountDownLatch myCountDownLatch = new MyCountDownLatch(f);
                    for(Map.Entry<String,Integer> entry : downMap.entrySet()){
                        if(js>=f){
                            break;
                        }
                        logger.info(entry.getKey()+":"+entry.getValue());
                        MyThread myThread=new MyThread(entry.getKey(),headerList,entry.getKey(),myCountDownLatch);
                        MyThreadPool.fixedThreadPoll.execute(myThread);

                        js+=1;
                    }

                    while (myCountDownLatch.getFlag()>0){
                        Thread.sleep(10000);
                        logger.info("myCountDownLatchFlag="+myCountDownLatch.getFlag());
                    }
                    logger.info("全部加载完成");
                    System.exit(0);
                }





            } catch (IOException e) {
                logger.info(e.getMessage(),e);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
        //6.关闭资源
        try {
            response.close();
            httpClient.close();
        } catch (IOException e) {
            logger.info(e.getMessage(),e);
        }

    }

    public static void filedown(String URL_STR,List<Header> headerList,int flag){

        CloseableHttpClient httpClient = HttpClients.createDefault();
            OutputStream out = null;
            InputStream in = null;

            try {
                HttpGet httpGet = new HttpGet(URL_STR);
                for (Header header : headerList) {
                    httpGet.addHeader("Cookie", header.getValue());
                    logger.info(header.getName() + ":" + header.getValue());
                }
//                httpGet.addHeader("token", "sss");

                HttpResponse httpResponse = httpClient.execute(httpGet);
                HttpEntity entity = httpResponse.getEntity();
                if(flag!=-1) {
                    String string = EntityUtils.toString(entity, "GBK");
                    logger.info(string);
                    if (string.indexOf("location.href='downloadx.asp") > -1) {
                        string = string.substring(string.indexOf("'") + 1, string.lastIndexOf("'"));
                        logger.info(string);

                        filedown(url + string, headerList, -1);
                    }
                }
                Header[] httpHeaders = httpGet.getAllHeaders();
                for (Header httpHeader : httpHeaders) {
                    logger.info(httpHeader.getName() + ":" + httpHeader.getValue());
                }

                if(flag==1){
                    return;
                }

                in = entity.getContent();

                long length = entity.getContentLength();
                if (length <= 0) {
                    logger.info("下载文件不存在！");
                    return;
                }

                logger.info("The response value of token:" + httpResponse.getFirstHeader("token"));

                File file = new File(mydir+"test.zip");
                if(!file.exists()){
                    file.createNewFile();
                }

                out = new FileOutputStream(file);
                byte[] buffer = new byte[4096];
                int readLength = 0;
                while ((readLength=in.read(buffer)) > 0) {
                    byte[] bytes = new byte[readLength];
                    System.arraycopy(buffer, 0, bytes, 0, readLength);
                    out.write(bytes);
                }

                out.flush();

            } catch (IOException e) {
                logger.info(e.getMessage(),e);
            } catch (Exception e) {
                logger.info(e.getMessage(),e);
            }finally{
                try {
                    if(in != null){
                        in.close();
                    }
                } catch (IOException e) {
                    logger.info(e.getMessage(),e);
                }

                try {
                    if(out != null){
                        out.close();
                    }
                } catch (IOException e) {
                    logger.info(e.getMessage(),e);
                }
            }

    }
}
