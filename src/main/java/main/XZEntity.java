package main;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class XZEntity {
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
    public XZEntity(){
         properties=new Properties();
        try {
            InputStream is=new FileInputStream(new File(getPath()+"pz.properties"));
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


}
