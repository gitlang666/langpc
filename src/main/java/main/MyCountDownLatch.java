package main;

public class MyCountDownLatch {
    private int flag=0;

    public MyCountDownLatch(int flag) {

        this.flag = flag;
    }

    public synchronized void downLatch(){
        this.flag--;
    }
    public int getFlag(){
        return this.flag;
    }
}
