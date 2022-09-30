package demo.nio.ultimate;

public class MainThread {

    //以上实现的功能：
    //1. 同一个端口进来的连接交给同一个boss线程处理，不同的连接会轮询boss线程组，交由不同的boss线程处理。
    //2. 不同的boss线程处理accept请求后，剩下的其它事件都交由同一个worker线程组处理。
    //
    //这样就完成了boss只需要负责监听accept连接，后续的read/write事件交由worker去执行。

    public static void main(String[] args) {
        //1,创建 IO Thread  （一个或者多个）
        //boss有自己的线程组
        SelectorThreadGroup boss = new SelectorThreadGroup(3);  //创建slector线程组并启动线程，一个线程一个selector
        //worker有自己的线程组
        SelectorThreadGroup worker = new SelectorThreadGroup(3);
        // boss线程组拥有worker线程组，因为boss一旦accept得到client后得去worker中 next出一个线程分配
        boss.setWorker(worker);

        // boss线程组可以同时监听多个端口
        boss.bind(9999);
        boss.bind(8888);
        boss.bind(6666);
        boss.bind(7777);

    }
}
