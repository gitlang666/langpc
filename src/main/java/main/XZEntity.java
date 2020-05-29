package main;

import sun.security.krb5.internal.crypto.Des;

import java.io.*;
import java.util.*;

public class XZEntity {

    private volatile static XZEntity xzEntity;

    public static XZEntity getXzEntity(){
        if(xzEntity==null){
            synchronized (XZEntity.class){
                if(xzEntity==null){
                    xzEntity=new XZEntity();
                }

            }
        }
        return xzEntity;
    }

    public String  xztype;
    public List<String> sid1=new ArrayList<String>();
    public List<String> sid1name=new ArrayList<String>();
    public List<String> sid2=new ArrayList<String>();
    public List<String> sid2name=new ArrayList<String>();
    public List<String> sid3=new ArrayList<String>();
    public List<String> sid3name=new ArrayList<String>();
    public List<String> sid4=new ArrayList<String>();
    public List<String> sid4name=new ArrayList<String>();
    public List<String> sid5=new ArrayList<String>();
    public List<String> sid5name=new ArrayList<String>();
    public String xzfl;
    public List<Integer> xzflwz=new ArrayList<Integer>();
    public Integer xzflslys;
    Properties properties=null;
    Map<String,Dir> map=new HashMap<String, Dir>();
    private XZEntity(){
         properties=new Properties();
        try {
            InputStream is=new FileInputStream(new File(getPath()+"\\pz.properties"));
            InputStreamReader isr=new InputStreamReader(is,"UTF-8");
            properties.load(isr);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("properties.name="+properties.getProperty("name"));
        this.xztype=properties.getProperty("xztype");
        this.xzfl=properties.getProperty("xzfl");
        this.xzflslys=Integer.parseInt(properties.getProperty("xzflslys"));
        jz(sid1,"sid1",0);jz(sid1name,"sid1name",0);jz(sid2,"sid2",0);jz(sid2name,"sid2name",0);jz(sid3,"sid3",0);jz(sid3name,"sid3name",0);
        jz(sid4,"sid4",0);jz(sid4name,"sid4name",0);jz(sid5,"sid5",0);jz(sid5name,"sid5name",0);
        jz(xzflwz,"xzflwz",1);
        init();

    }

    public void init(){
        if("all".equals(xztype)){
            jzmap(sid1,sid1name,0);
            jzmap(sid2,sid2name,0);
            jzmap(sid3,sid3name,0);
            jzmap(sid4,sid4name,0);
            jzmap(sid5,sid5name,0);
        }else if("part".equals(xztype)){
            if("sid1".equals(xzfl)){
                jzmap(sid1,sid1name,1);
            }else if("sid2".equals(xzfl)){
                jzmap(sid2,sid2name,1);
            }else if("sid3".equals(xzfl)){
                jzmap(sid3,sid3name,1);
            }else if("sid4".equals(xzfl)){
                jzmap(sid4,sid4name,1);
            }else if("sid5".equals(xzfl)){
                jzmap(sid5,sid5name,1);
            }else {

            }
        }else {

        }
    }

    public  String getPath()
    {
        String path = this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
        if(System.getProperty("os.name").contains("dows"))
        {
            path = path.substring(1,path.length());
        }
        if(path.contains("jar"))
        {
            path = path.substring(0,path.lastIndexOf("."));
            return path.substring(0,path.lastIndexOf("/"));
        }
        return path.replace("target/classes/", "");
    }

    private void jz(List p1,String p2,int p3){
        for (String s : properties.getProperty(p2).split(",")) {
            if(p3==0){
                p1.add(s);
            }else if(p3==1){
                p1.add(Integer.parseInt(s));
            }
        }
    }

    private void jzmap(List<String> sid,List<String> sidname,int flag){
        if(sid.size()!=sidname.size()){
            return;
        }
        if(flag==0){
            for(int i=0;i<sid.size();i++){
                String path=Dest.mainurl+ Dest.memberPageDest+sid.get(i);
                map.put(path,new Dir(sid.get(i),sidname.get(i)));
            }
        }else {
            for(int i=0;i<xzflwz.size();i++){
                String path=Dest.mainurl+ Dest.memberPageDest+sid.get(xzflwz.get(i));
                map.put(path,new Dir(sid.get(xzflwz.get(i)),sidname.get(xzflwz.get(i))));
            }
        }

    }

    public class Dir{
        public String sid;
        public String name;
        public Dir(String sid,String name){
            this.sid=sid;
            this.name=name;
        }
    }

}
