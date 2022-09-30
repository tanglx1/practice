package demo.nio.ultimate;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.Channel;
import java.nio.channels.ServerSocketChannel;
import java.util.concurrent.atomic.AtomicInteger;

public class SelectorThreadGroup {

    ServerSocketChannel server=null;
    SelectorThread[] sts;
    AtomicInteger xid = new AtomicInteger(0);

    SelectorThreadGroup  stg =  this;// 默认是boss

    // 可以选择设置worker，处理客户端读写请求
    public void setWorker(SelectorThreadGroup  stg){
        this.stg =  stg;
    }

    SelectorThreadGroup(int num){
        //num  线程数
        sts = new SelectorThread[num];
        for (int i = 0; i < num; i++) {
            sts[i] = new SelectorThread(this);
            new Thread(sts[i]).start();
        }
    }

    public void bind(int port) {
        try {
            server =  ServerSocketChannel.open();
            server.configureBlocking(false);
            server.bind(new InetSocketAddress(port));
            //注册到某个selector
            nextSelectorV3(server);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void nextSelectorV3(Channel c) {
        try {
            if(c instanceof  ServerSocketChannel){
                SelectorThread st = nextBossThread();  //listen 选择了 boss组中的一个线程后，要更新这个线程的work组
                st.lbq.put(c);
                st.setWorker(stg);
                st.selector.wakeup();// 使得这个事件在线程st的selector中生效
            }else {
                SelectorThread st = nextWorkerThread();  //在 main线程种，取到堆里的selectorThread对象
                //1,通过队列传递数据 消息
                st.lbq.add(c);
                //2,通过打断阻塞，让对应的线程去自己在打断后完成注册selector
                st.selector.wakeup();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // 返回boss的一个线程
    private SelectorThread nextBossThread() {
        int index = xid.incrementAndGet() % sts.length;
        return sts[index];
    }

    // 返回worker的一个线程
    private SelectorThread nextWorkerThread() {
        int index = xid.incrementAndGet() % stg.sts.length;
        return stg.sts[index];
    }
}
