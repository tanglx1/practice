package demo.nio.ultimate;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

public class SelectorThread  extends  ThreadLocal<LinkedBlockingQueue<Channel>>  implements   Runnable{
    // 一个线程对应一个selector，
    // 每个客户端，只绑定到其中一个selector
    Selector selector = null;
    LinkedBlockingQueue<Channel> lbq = get();  //获取initialValue的值
    SelectorThreadGroup stg;

    @Override
    protected LinkedBlockingQueue<Channel> initialValue() {
        return new LinkedBlockingQueue<>();//初始化与当前线程绑定的ThreadLocal的值，通过get可以获取
    }

    SelectorThread(SelectorThreadGroup stg){
        try {
            this.stg = stg;
            selector = Selector.open();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        //Loop
        while (true){
            try {
                int nums = selector.select();  //阻塞，wakeup()可以让select()立即返回
                //2,处理selectkeys
                if(nums>0){
                    Set<SelectionKey> keys = selector.selectedKeys();
                    Iterator<SelectionKey> iter = keys.iterator();
                    while(iter.hasNext()){  //线程处理的过程
                        SelectionKey key = iter.next();
                        iter.remove();
                        if(key.isAcceptable()){  //复杂,接受客户端的过程（接收之后，要注册，多线程下，新的客户端，注册到那里呢？）
                            acceptHandler(key);
                        }else if(key.isReadable()){
                            readHander(key);
                        }else if(key.isWritable()){
                        }
                    }
                }
                //3,处理一些task :  listen  client
                if(!lbq.isEmpty()){   //如果队列不为空，表示有新的连接要注册到selector
                    Channel c = lbq.take();
                    if(c instanceof ServerSocketChannel){
                        ServerSocketChannel server = (ServerSocketChannel) c;
                        server.register(selector,SelectionKey.OP_ACCEPT);
                        System.out.println(Thread.currentThread().getName()+" register listen");
                    }else if(c instanceof SocketChannel){
                        SocketChannel client = (SocketChannel) c;
                        ByteBuffer buffer = ByteBuffer.allocateDirect(4096);
                        client.register(selector, SelectionKey.OP_READ, buffer);
                        System.out.println(Thread.currentThread().getName()+" register client: " + client.getRemoteAddress());
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    private void readHander(SelectionKey key) {
        System.out.println(Thread.currentThread().getName()+" read......");
        ByteBuffer buffer = (ByteBuffer)key.attachment();
        SocketChannel client = (SocketChannel)key.channel();
        buffer.clear();
        while(true){
            try {
                int num = client.read(buffer);
                if(num > 0){
                    buffer.flip();  //将读到的内容翻转，然后直接写出
                    while(buffer.hasRemaining()){
                        client.write(buffer);
                    }
                    buffer.clear();
                }else if(num == 0){
                    break;
                }else if(num < 0 ){
                    //客户端断开了
                    System.out.println("client: " + client.getRemoteAddress()+"closed......");
                    key.cancel();
                    break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void acceptHandler(SelectionKey key) {
        System.out.println(Thread.currentThread().getName()+"   acceptHandler......");

        ServerSocketChannel server = (ServerSocketChannel)key.channel();
        try {
            SocketChannel client = server.accept();
            client.configureBlocking(false);
            stg.nextSelectorV3(client);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 给当前多路复用器设置工作者线程
    public void setWorker(SelectorThreadGroup stgWorker) {
        this.stg =  stgWorker;
    }
}
