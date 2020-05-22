package main;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class AllFile {
    Logger logger= LoggerFactory.getLogger(AllFile.class);
    public void getAllFile(Map<String,Integer> map,List<Header> headerList){
        int flag=2;
        int i=0;
        MyCountDownLatch myCountDownLatch=new MyCountDownLatch(flag);
        for(Map.Entry<String,Integer> entry: map.entrySet()){
            if(i>=flag){
                break;
            }
            AllFileWork allFileWork=new AllFileWork(headerList,entry.getKey(),myCountDownLatch);
            MyThreadPool.fixedThreadPoll.execute(allFileWork);
            i++;
        }
        while (myCountDownLatch.getFlag()>0){
            try {
                Thread.sleep(20000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            logger.info("myCountDownLatch.getFlag="+myCountDownLatch.getFlag()+",ALLFILEMAP="+Dest.AllFILEMAP.size());
        }
        logger.info("所有记录数="+Dest.AllFILEMAP.size());
        return ;
    }

    public class AllFileWork implements Runnable{
        PageDownList pageDownList=null;
        Page page=null;
        String url=null;
        MyCountDownLatch myCountDownLatch;
        public AllFileWork(List<Header> headerList,String url,MyCountDownLatch myCountDownLatch){
            page=new Page();
            pageDownList=new PageDownList(headerList,url,page);
            this.url=url;
            this.myCountDownLatch=myCountDownLatch;
        }

        @Override
        public void run() {
            HttpEntity entity=pageDownList.getResult(url);
            String resultS=pageDownList.getResultString(entity);
            Map<String,String> map=pageDownList.getDownList(resultS,Dest.listPageDest);
            for(Map.Entry<String,String> entry: map.entrySet()){
                Dest.AllFILEMAP.put(Integer.parseInt(entry.getKey().substring(entry.getKey().lastIndexOf("=")+1)),entry.getKey());
            }
            for(page.settPage(page.gettPage()+1);page.gettPage()<=page.getSumPage();page.settPage(page.gettPage()+1)){
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                pageDownList.page=page.gettPage();
                entity=pageDownList.getResult(url+"&page="+page.gettPage());
                if(entity!=null){
                    resultS=pageDownList.getResultString(entity);
                    map=pageDownList.getDownList(resultS,Dest.listPageDest);
                    for(Map.Entry<String,String> entry: map.entrySet()){
                        Dest.AllFILEMAP.put(Integer.parseInt(entry.getKey().substring(entry.getKey().lastIndexOf("=")+1)),entry.getKey());
                    }
                }

            }
            myCountDownLatch.downLatch();

        }

    }
}
