package main;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PageDownList {
    Logger logger= LoggerFactory.getLogger(PageDownList.class);
    private int sumPage;
    private int page=1;
    private String listUrl;
    private Page mPage;
    //1.打开浏览器
    private CloseableHttpClient httpClient = HttpClients.createDefault();
    List<Header> headerList=null;
    public  PageDownList(List<Header> headerList,String listUrl,Page mPage){
        this.headerList=headerList;
        this.listUrl=listUrl;
        this.mPage=mPage;
    }
    public HttpEntity getResult(String url){
        HttpGet httpGet = new HttpGet(url);
        addHttpHeader(httpGet,headerList);
//                httpGet.addHeader("token", "sss");
        HttpResponse httpResponse = null;
        try {
            httpResponse = httpClient.execute(httpGet);

        } catch (IOException e) {
            e.printStackTrace();
        }
        HttpEntity entity = httpResponse.getEntity();
        return entity;
    }

    public void getResultType(ResultType resultType,HttpEntity entity,String filename,int page){
        this.page=page;
        if (resultType==ResultType.RESULT_TYPE_4){
            Map<String,String> map=getDownList(getResultString(entity),Dest.listPageDest);

            System.out.println("map.count="+map.size());

            for(Map.Entry<String,String> entry:map.entrySet()){
                HttpEntity httpEntity=this.getResult(entry.getKey());
                getResultType(ResultType.RESULT_TYPE_2,httpEntity,entry.getValue(),page);
            }
        }else if(resultType==ResultType.RESULT_TYPE_2){
            String string=getResultString(entity);
            if (string.indexOf(Dest.downPageDest1) > -1) {
                string = Dest.mainurl+ string.substring(string.indexOf("'") + 1, string.lastIndexOf("'"));
                //得到地址
                HttpEntity httpEntity=getResult(string);
                getResultType(ResultType.RESULT_TYPE_3,httpEntity,filename,page);
            }
        }else if(resultType==ResultType.RESULT_TYPE_3){
            getDownFile(entity,filename);


        }
    }

    private void addHttpHeader(HttpRequestBase httpMethod,List<Header> headerList){
        for (Header header : headerList) {
            httpMethod.addHeader("Cookie", header.getValue());
//            System.out.println(header.getName() + ":" + header.getValue());
        }
    }

    //响应体以string返回
    public String getResultString(HttpEntity entity){
        String string=null;
        try {
            string = EntityUtils.toString(entity, "GBK");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return string;
    }

    //返回String中的目标字段
    public Map<String,String> getDownList(String source,String dest){
        Map<String,String> stringIntegerMap=new HashMap<String, String>();
        List<Integer> integerList=KMP.kmp(source,dest);
        if(page==1){
            List<Integer> integerList1=KMP.kmp(source,Dest.pageDest);
            String page1=source.substring(integerList1.get(0)+Dest.pageDest.length());
            String page2=page1.substring(page1.indexOf("/")+1,page1.indexOf("页"));
            sumPage=Integer.parseInt(page2);
            logger.info("sumPage="+sumPage);
            this.mPage.setSumPage(sumPage);
        }

        System.out.println("integerList.size="+integerList.size());
        for (Integer integer : integerList) {
            String s2=source.substring(integer);
            String downstr=s2.substring(0,s2.indexOf("\""));

            String title=s2.substring(s2.indexOf("title=\"")+7);

            title=title.substring(0,title.indexOf("\""));

            stringIntegerMap.put(Dest.mainurl+downstr,title+Thread.currentThread().getId()+downstr.substring(downstr.lastIndexOf("=")+1)+".zip");
//            System.out.println(Dest.mainurl+downstr+":"+title);
        }
        return stringIntegerMap;
    }

    //类型是文件
    public void getDownFile(HttpEntity httpEntity,String fileName){
        OutputStream out = null;
        InputStream in = null;
        try {
            in = httpEntity.getContent();
            long length = httpEntity.getContentLength();
            if (length <= 0) {
                System.out.println("下载文件不存在！"+fileName);
                return;
            }

            String tid=Thread.currentThread().getName()+System.currentTimeMillis();
            String path=Dest.dir+"thread"+Thread.currentThread().getId()+"\\"+page+"\\"+fileName;
            File file = new File(path);
            File fileParent = file.getParentFile();
            if(!fileParent.exists()){
                fileParent.mkdirs();
            }
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
            e.printStackTrace();
        } finally {
            try {
                if(in != null){
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                if(out != null){
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
