package main;

import java.util.ArrayList;
import java.util.List;

public class KMP {
    public static void main(String[] args) {

        String source = "abccdfabvsfnabc";
        String dest = "ab";

        List<Integer> list=kmp(source,dest);
        for (Integer integer : list) {
            System.out.println(integer);
        }

        String s1="qwreqwer\"asdfa";
        String s2=s1.substring(s1.indexOf("re")+2,s1.indexOf("\""));
        System.out.println(s2);
        System.out.println(s1.substring(s1.lastIndexOf("d")+1));
        KMP kmp=new KMP();
        System.out.println(kmp.getPath());
    }

    /**
     * kmp求子串出现的次数
     * @param source
     * @param dest
     * @return
     */
    public static List<Integer> kmp(String source, String dest) {
        int[] next = kmpNext(dest);
        List<Integer> list=new ArrayList<Integer>();
        int count = 0;
        for (int i = 0, j = 0; i < source.length(); i++) {
            while (j > 0 && source.charAt(i) != dest.charAt(j)) {
                j = next[j - 1];
            }
            if (source.charAt(i) == dest.charAt(j)) {
                j++;
            }
            if (j == dest.length()) {
                count++;
                list.add((i-next.length+1));
                j = 0;
            }
        }
        return list;
    }

    /**
     * 子串的部分匹配表，相当于子串与子串自己做了一次kmp算法
     * @param dest
     * @return
     */
    private static int[] kmpNext(String dest) {
        int[] next = new int[dest.length()];
        next[0] = 0;
        for (int i = 1, j = 0; i < dest.length(); i++) {
            while (j > 0 && dest.charAt(i) != dest.charAt(j)) {
                j = next[j - 1];
            }
            if (dest.charAt(i) == dest.charAt(j)) {
                j++;
            }
            next[i] = j;
        }
        return next;
    }

    public String getPath()
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

}
