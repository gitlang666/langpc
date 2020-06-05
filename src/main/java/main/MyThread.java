package main;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CountDownLatch;

public class MyThread implements Runnable {
    Logger logger= LoggerFactory.getLogger(MyThread.class);
    public String url;
    public PageDownList pageDownList;
    public CountDownLatch countDownLatch;
    public MyCountDownLatch myCountDownLatch;
    public String dirName;

    private Page page=new Page();
    public MyThread(String url,  List<Header> headerList,String key, MyCountDownLatch countDownLatch) {
        this.url = url;
        this.pageDownList = new PageDownList(headerList,key,page);
        this.myCountDownLatch = countDownLatch;
    }

    public MyThread(String url,  List<Header> headerList,String key, MyCountDownLatch countDownLatch,String dirName) {
        this.url = url;
        this.dirName=dirName;
        this.pageDownList = new PageDownList(headerList,key,page,dirName);
        this.myCountDownLatch = countDownLatch;
    }

    @Override
    public void run() {
        pageDownList.getResultType(ResultType.RESULT_TYPE_4,pageDownList.getResult(url),"",page.gettPage(),url);
        logger.info(url+"/"+Thread.currentThread().getId()+"/"+page.gettPage()+"页下载完成");
        page.settPage((page.gettPage()+1));
        for (;page.gettPage()<=page.getSumPage() && page.gettPage()<=XZEntity.getXzEntity().xzflslys;){
            String newUrl=url+"&page="+page.gettPage();
            pageDownList.getResultType(ResultType.RESULT_TYPE_4,pageDownList.getResult(newUrl),"",page.gettPage(),newUrl);
            logger.info(newUrl+"/"+Thread.currentThread().getId()+"/"+page.gettPage()+"页下载完成");
            page.settPage((page.gettPage()+1));
        }
        logger.info("完成："+dirName+",共"+(page.gettPage()-1)+"页"+"TheradID="+Thread.currentThread().getId());
        this.myCountDownLatch.downLatch();

//        this.countDownLatch.countDown();
    }
}
