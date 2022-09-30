package demo.nio;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;

public class NIOClient {
    public static void main(String[] args) throws Exception{
        Selector selector = Selector.open();
        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.configureBlocking(false);
        socketChannel.connect(new InetSocketAddress("localhost", 8081));
        socketChannel.register(selector, SelectionKey.OP_CONNECT);

        CompletableFuture.runAsync(()->{

            while(true){
                try {
                    int readyCount = selector.select();
                    if (readyCount > 0) {
                        Iterator<SelectionKey> it = selector.selectedKeys().iterator();
                        while(it.hasNext()){
                            SelectionKey selectionKey=it.next();
                            it.remove();
                            if(selectionKey.isReadable()){
                                    read(selectionKey,selector);
                            }else if(selectionKey.isConnectable()){
                                System.out.println("connection ing ");
                                SocketChannel s=(SocketChannel) selectionKey.channel();
                                if(s.finishConnect()){
                                    selectionKey.interestOps(SelectionKey.OP_READ );
                                    System.out.println("connection successful");
                                }
                            }

                        }

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

        });

        Scanner scanner = new Scanner(System.in);
        while(true){
            if(scanner.hasNextLine()){
                String in = scanner.nextLine();

                socketChannel.write(ByteBuffer.wrap(in.getBytes()));
            }

        }


    }

    private static void read(SelectionKey selectionKey,Selector selector)throws Exception{
        SocketChannel socketChannel=(SocketChannel) selectionKey.channel();
        ByteBuffer byteBuffer=ByteBuffer.allocate(1024);

        int count;
        while((count=socketChannel.read(byteBuffer))>0){
            byteBuffer.flip();
            String line= Charset.forName("UTF-8").decode(byteBuffer).toString();
            System.out.println(line);
            byteBuffer.clear();
        }

        if(count==-1){
            selectionKey.cancel();
        }

      }
}
