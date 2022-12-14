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

        // ????????????????????????????????????Runtime.getRuntime().freeMemory()?????????????????????
        // ???????????????????????????JVM??????????????????
        ByteBuffer buffer = ByteBuffer.allocate(102400);
        System.out.println("buffer = " + buffer);

        System.out.println("after alocate:"
                + Runtime.getRuntime().freeMemory());

        // ?????????????????????????????????????????????JVM?????????????????????
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
        // 1.?????????????????????1024
        ByteBuffer buf = ByteBuffer.allocate(1024);
        System.out.print("init 1024 byte:");
        System.out.println("position="+buf.position()+";limit="+buf.limit()+";capacity="+buf.capacity()+"\n");

        // 2.??????????????????5?????????
        buf.put("link1".getBytes());
        System.out.print("after put 5 byte(link1):");
        System.out.println("position="+buf.position()+";limit="+buf.limit()+";capacity="+buf.capacity()+"\n");

        // 3.???????????????
        // 	flip()???????????????;position=0;?????????position??????;?????????limit??????
        buf.flip();
        System.out.print("after ???????????????flip():");
        System.out.println("position="+buf.position()+";limit="+buf.limit()+";capacity="+buf.capacity()+"\n");

        int readableCount=buf.limit();
        byte[] bytes1 = new byte[readableCount-2];
        byte[] bytes2 = new byte[2];

        buf.get(bytes1);
        System.out.print("after ??????3?????????:");
        System.out.println("position="+buf.position()+";limit="+buf.limit()+";capacity="+buf.capacity()+"\n");

        buf.flip();
        System.out.println("position="+buf.position()+";limit="+buf.limit()+";capacity="+buf.capacity()+"\n");

        buf.get(bytes2);
        System.out.print("after ????????????2?????????:");
        System.out.println("position="+buf.position()+";limit="+buf.limit()+";capacity="+buf.capacity()+"\n");

        byte[] bytes = new byte[readableCount];
        System.arraycopy(bytes1,0,bytes,0,bytes1.length);
        System.arraycopy(bytes2,0,bytes,bytes1.length,bytes2.length);
        System.out.println("the input="+new String(bytes, 0, bytes.length));


        // 4.?????????????????????
        // 	rewind()????????????;position=0
        buf.rewind();
        System.out.print("???????????????after invoke rewind():");
        System.out.println("position="+buf.position()+";limit="+buf.limit()+";capacity="+buf.capacity()+"\n");

        byte[] bytes3 = new byte[buf.limit()];
        buf.get(bytes3);
        System.out.print("after ??????"+buf.limit()+"???????????????????????????:");
        System.out.println("position="+buf.position()+";limit="+buf.limit()+";capacity="+buf.capacity()+"\n");
        System.out.println("the input="+new String(bytes3, 0, bytes3.length));

        // 5.clean ???????????????  ??????????????????,????????????????????????
        buf.clear();
        System.out.print("after ???????????????:");
        System.out.println("position="+buf.position()+";limit="+buf.limit()+";capacity="+buf.capacity()+"\n");


        System.out.println("after clear??? ????????????????????????:");
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
