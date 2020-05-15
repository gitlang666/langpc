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
    private CloseableHttpClient httpClient = Dest.HTTP_CLIENT;
    List<Header> headerList=null;
    public  PageDownList(List<Header> headerList,String listUrl,Page mPage){
        this.headerList=headerList;
        this.listUrl=listUrl;
        this.mPage=mPage;
    }
    public HttpEntity getResult(String url){
        HttpGet httpGet = new HttpGet(url);
        httpGet.setConfig(Dest.REQUEST_CONFIG);
        addHttpHeader(httpGet,headerList);
//                httpGet.addHeader("token", "sss");
        HttpResponse httpResponse = null;
        try {
            httpResponse = httpClient.execute(httpGet);

        } catch (IOException e) {
            logger.info(e.getMessage(),e);
        }
        HttpEntity entity = httpResponse.getEntity();
        if(entity==null){
            return getResult(url);
        }
        return entity;
    }

    public void getResultType(ResultType resultType,HttpEntity entity,String filename,int page,String aUrl){
        this.page=page;
        if (resultType==ResultType.RESULT_TYPE_4){
            Map<String,String> map=getDownList(getResultString(entity),Dest.listPageDest);

            logger.info("map.count="+map.size());

            for(Map.Entry<String,String> entry:map.entrySet()){
                HttpEntity httpEntity=this.getResult(entry.getKey());
                getResultType(ResultType.RESULT_TYPE_2,httpEntity,entry.getValue(),page,entry.getKey());
            }
        }else if(resultType==ResultType.RESULT_TYPE_2){
            String string=getResultString(entity);
            if (string.indexOf(Dest.downPageDest1) > -1) {
                string = Dest.mainurl+ string.substring(string.indexOf("'") + 1, string.lastIndexOf("'"));
                //得到地址
                HttpEntity httpEntity=getResult(string);
                getResultType(ResultType.RESULT_TYPE_3,httpEntity,filename,page,string);
            }
        }else if(resultType==ResultType.RESULT_TYPE_3){
            getDownFile(entity,filename,aUrl);


        }
    }

    private void addHttpHeader(HttpRequestBase httpMethod,List<Header> headerList){
        for (Header header : headerList) {
            httpMethod.addHeader("Cookie", header.getValue());
//            logger.info(header.getName() + ":" + header.getValue());
        }
    }

    //响应体以string返回
    public String getResultString(HttpEntity entity){
        String string=null;
        try {
            string = EntityUtils.toString(entity, "GBK");
        } catch (IOException e) {
            logger.info(e.getMessage(),e);
        }
        return string;
    }

    //返回String中的目标字段
    public Map<String,String> getDownList(String source,String dest){
        Map<String,String> stringIntegerMap=new HashMap<String, String>();
        List<Integer> integerList=KMP.kmp(source,dest);
        if(page==1){
            List<Integer> integerList1=KMP.kmp(source,Dest.pageDest);
            if(integerList1.size()>0){
                String page1=source.substring(integerList1.get(0)+Dest.pageDest.length());
                String page2=page1.substring(page1.indexOf("/")+1,page1.indexOf("页"));
                sumPage=Integer.parseInt(page2);
                logger.info("sumPage="+sumPage);
                this.mPage.setSumPage(sumPage);
            }else {
                sumPage=1;
                logger.info("sumPage="+sumPage);
                this.mPage.setSumPage(sumPage);
            }

        }

        logger.info("integerList.size="+integerList.size());
        for (Integer integer : integerList) {
            String s2=source.substring(integer);
            String downstr=s2.substring(0,s2.indexOf("\""));

            String title=s2.substring(s2.indexOf("title=\"")+7);

            title=title.substring(0,title.indexOf("\""));

            stringIntegerMap.put(Dest.mainurl+downstr,title+Thread.currentThread().getId()+"==id=="+downstr.substring(downstr.lastIndexOf("=")+1)+".zip");
//            logger.info(Dest.mainurl+downstr+":"+title);
        }
        return stringIntegerMap;
    }

    //类型是文件
    public void getDownFile(HttpEntity httpEntity,String fileName,String aUrl){
        OutputStream out = null;
        InputStream in = null;
        try {

            in = httpEntity.getContent();

            long length = httpEntity.getContentLength();
            if (length <= 0) {
                logger.info("下载文件不存在！"+fileName+":"+aUrl);
                byte[] bytes=EntityUtils.toByteArray(httpEntity);
                String rehtml=new String(bytes,"GBK");
//                logger.info("返回html="+rehtml);
                if(aUrl.indexOf("tc=")>-1){
                    String newUrl1=aUrl.substring(0,aUrl.indexOf("tc="));
                    String newUrl2=aUrl.substring(aUrl.indexOf("tc="));
                    if(newUrl2.indexOf("&")>-1){
                        newUrl1=newUrl1+newUrl2.substring(newUrl2.indexOf("&")+1);
                    }
                    logger.info("新的url="+newUrl1);
                    getDownFile(getResult(newUrl1),fileName,newUrl1);
                }else {
                    filedown(aUrl,fileName,0);
                }
                return;
            }

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
            logger.info("下载文件成功！"+fileName+":"+aUrl);
        } catch (IOException e) {
            logger.info(e.getMessage(),e);
        } finally {
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


    public  void filedown(String URL_STR,String fileName,int flag){
        flag+=1;
        CloseableHttpClient httpClient = Dest.HTTP_CLIENT;
        OutputStream out = null;
        InputStream in = null;

        try {
            HttpGet httpGet = new HttpGet(URL_STR);
            httpGet.setConfig(Dest.REQUEST_CONFIG);
            for (Header header : headerList) {
                httpGet.addHeader("Cookie", header.getValue());
            }
            HttpResponse httpResponse = httpClient.execute(httpGet);
            HttpEntity entity = httpResponse.getEntity();
            String path=Dest.dir+"thread"+Thread.currentThread().getId()+"\\"+page+"\\"+fileName;
            in = entity.getContent();

            long length = entity.getContentLength();
            if (length <= 0) {
                String reslut=EntityUtils.toString(entity,"GBK");
                if(flag<=5){
                    logger.info("下载文件不存在！filedown 第"+flag+"次重试失败"+fileName+":"+URL_STR+"  \n============================================"+reslut);
                    filedown( URL_STR, fileName, flag);
                    return;
                }else {
                    logger.info("下载文件不存在！filedown 第"+flag+"次重试失败,不在重试"+fileName+":"+URL_STR+"  \n============================================"+reslut);
                    //下载地址和保存位置
                    Dest.FAILPATH.put(URL_STR,path);
                    return;
                }

            }

            logger.info("The response value of token:" + httpResponse.getFirstHeader("token"));

            File file = new File(path);
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
            logger.info("下载文件成功！filedown第"+flag+"次成功"+fileName+":"+URL_STR);
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
