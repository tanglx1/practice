package demo.nio;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;

public class BufferPractice {
    public static void main(String[] args) {

        fileChanelMapTest();


    }
    private static void fileChanelMapTest(){
        try {
            FileChannel fileChannel= new FileInputStream("D:\\temp\\debug.txt").getChannel();
            System.out.println("before alocate:" + Runtime.getRuntime().freeMemory());
            ByteBuffer byteBuffer = fileChannel.map(FileChannel.MapMode.READ_ONLY,0,fileChannel.size());
            System.out.println("after alocate:" + Runtime.getRuntime().freeMemory());


            System.out.println(Charset.forName("gb2312").decode(byteBuffer));

//            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//
//            while(byteBuffer.hasRemaining()){
//                byte[] bytes5=new byte[byteBuffer.limit()];
//                byteBuffer.get(bytes5);
//                byteArrayOutputStream.write(bytes5);
//            }
//            System.out.println(new String(byteArrayOutputStream.toByteArray(),"gb2312"));


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void fileChanelTest(){
        try {
            FileChannel fileChannel= new FileInputStream("D:\\temp\\debug.txt").getChannel();
            ByteBuffer byteBuffer = ByteBuffer.allocate(40);

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            while(fileChannel.read(byteBuffer)!=-1){
                byteBuffer.flip();
                byte[] bytes5=new byte[byteBuffer.limit()];
                byteBuffer.get(bytes5);
                byteArrayOutputStream.write(bytes5);
                byteBuffer.clear();
            }
            System.out.println(new String(byteArrayOutputStream.toByteArray(),"gb2312"));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void directTest(){

        System.out.println("----------Test allocate--------");
        System.out.println("before alocate:"
                + Runtime.getRuntime().freeMemory());

        // 如果分配的内存过小，调用Runtime.getRuntime().freeMemory()大小不会变化？
        // 要超过多少内存大小JVM才能感觉到？
        ByteBuffer buffer = ByteBuffer.allocate(102400);
        System.out.println("buffer = " + buffer);

        System.out.println("after alocate:"
                + Runtime.getRuntime().freeMemory());

        // 这部分直接用的系统内存，所以对JVM的内存没有影响
        ByteBuffer directBuffer = ByteBuffer.allocateDirect(102400);
        System.out.println("directBuffer = " + directBuffer);
        System.out.println("after direct alocate:"
                + Runtime.getRuntime().freeMemory());

        System.out.println("----------Test wrap--------");
        byte[] bytes = new byte[32];
        buffer = ByteBuffer.wrap(bytes);
        System.out.println(buffer);

        buffer = ByteBuffer.wrap(bytes, 10, 10);
        System.out.println(buffer);
    }



    private static void normalTest(){
        // 1.指定缓冲区大小1024
        ByteBuffer buf = ByteBuffer.allocate(1024);
        System.out.print("init 1024 byte:");
        System.out.println("position="+buf.position()+";limit="+buf.limit()+";capacity="+buf.capacity()+"\n");

        // 2.向缓冲区存放5个数据
        buf.put("link1".getBytes());
        System.out.print("after put 5 byte(link1):");
        System.out.println("position="+buf.position()+";limit="+buf.limit()+";capacity="+buf.capacity()+"\n");

        // 3.开启读模式
        // 	flip()开启读模式;position=0;读取时position增加;读取时limit减少
        buf.flip();
        System.out.print("after 开启读模式flip():");
        System.out.println("position="+buf.position()+";limit="+buf.limit()+";capacity="+buf.capacity()+"\n");

        int readableCount=buf.limit();
        byte[] bytes1 = new byte[readableCount-2];
        byte[] bytes2 = new byte[2];

        buf.get(bytes1);
        System.out.print("after 读取3个数据:");
        System.out.println("position="+buf.position()+";limit="+buf.limit()+";capacity="+buf.capacity()+"\n");

        buf.flip();
        System.out.println("position="+buf.position()+";limit="+buf.limit()+";capacity="+buf.capacity()+"\n");

        buf.get(bytes2);
        System.out.print("after 继续读取2个数据:");
        System.out.println("position="+buf.position()+";limit="+buf.limit()+";capacity="+buf.capacity()+"\n");

        byte[] bytes = new byte[readableCount];
        System.arraycopy(bytes1,0,bytes,0,bytes1.length);
        System.arraycopy(bytes2,0,bytes,bytes1.length,bytes2.length);
        System.out.println("the input="+new String(bytes, 0, bytes.length));


        // 4.开启重复读模式
        // 	rewind()重复读取;position=0
        buf.rewind();
        System.out.print("读取完后，after invoke rewind():");
        System.out.println("position="+buf.position()+";limit="+buf.limit()+";capacity="+buf.capacity()+"\n");

        byte[] bytes3 = new byte[buf.limit()];
        buf.get(bytes3);
        System.out.print("after 使用"+buf.limit()+"个字节数组读取数据:");
        System.out.println("position="+buf.position()+";limit="+buf.limit()+";capacity="+buf.capacity()+"\n");
        System.out.println("the input="+new String(bytes3, 0, bytes3.length));

        // 5.clean 清空缓冲区  数据依然存在,只不过数据被遗忘
        buf.clear();
        System.out.print("after 清空缓冲区:");
        System.out.println("position="+buf.position()+";limit="+buf.limit()+";capacity="+buf.capacity()+"\n");


        System.out.println("after clear后 ，再读取一次返回:");
        byte[] bytes4 = new byte[10];
        buf.get(bytes4);
        System.out.println((char)bytes4[0]);
        System.out.println((char)bytes4[1]);
        System.out.println((char)bytes4[2]);
        System.out.println((char)bytes4[3]);
        System.out.println((char)bytes4[4]);
        System.out.println(bytes4[5]);
        System.out.println((char)buf.get());
        System.out.println((char)buf.get());
        System.out.println(buf.get());
        System.out.println(buf.get());
    }


}
