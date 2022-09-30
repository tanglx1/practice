package demo.netty;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;

public class NIOServer {
    public static void main(String[] args) throws Exception{
        Selector selector = Selector.open();
        ServerSocketChannel serverSocketChannel=ServerSocketChannel.open();
        serverSocketChannel.bind(new InetSocketAddress(8081));
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        System.out.println("register");


        while(true){
            int readyChannel=selector.select();

            System.out.println("ready count:"+readyChannel);
            if(readyChannel>0){
                Iterator<SelectionKey> it=selector.selectedKeys().iterator();
                while(it.hasNext()){
                    SelectionKey selectionKey = it.next();
                    it.remove();
                    if(selectionKey.isAcceptable()){
                        accept(serverSocketChannel, selector);
                    }else if(selectionKey.isReadable()){
                        read(selectionKey, selector);
                    }else if(selectionKey.isWritable()){
                        String s=(String)selectionKey.attachment();
                        if(s!=null){
                            SocketChannel socketChannel=(SocketChannel) selectionKey.channel();
                            socketChannel.write(Charset.forName("utf-8").encode(s));
                        }

                        selectionKey.interestOps(selectionKey.interestOps() - SelectionKey.OP_WRITE);


                    }
                }
            }
        }


    }

    private static void accept(ServerSocketChannel serverSocketChannel,Selector selector) throws Exception{
        try {
            SocketChannel socketChannel=serverSocketChannel.accept();
            socketChannel.configureBlocking(false);
            socketChannel.register(selector, SelectionKey.OP_READ);
            socketChannel.write(ByteBuffer.wrap("welcome".getBytes("utf-8")));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static void read(SelectionKey selectionKey,Selector selector)throws Exception{
        try {
            SocketChannel socketChannel=(SocketChannel)selectionKey.channel();
            ByteBuffer byteBuffer=ByteBuffer.allocate(1024);

            int count;
            String line="";
            while((count=socketChannel.read(byteBuffer))>0){
                byteBuffer.flip();
                 line=Charset.forName("UTF-8").decode(byteBuffer).toString();
                byteBuffer.clear();

                System.out.println("rec:"+line);
//                socketChannel.write(ByteBuffer.wrap(("hello:"+line).getBytes("utf-8")));

            }
            selectionKey.interestOps(selectionKey.interestOps() + SelectionKey.OP_WRITE);
            selectionKey.attach(line);


            if(count==-1){
                selectionKey.cancel();
            }


        } catch (Exception e) {
            e.printStackTrace();
            selectionKey.cancel();
        }

    }



}
