package demo.io;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;

public class IOClient {
    public static void main(String[] args) throws Exception{
        Socket socket = new Socket();

        socket.connect(new InetSocketAddress("localhost",8081));
        System.out.println("connected success:"+socket.getRemoteSocketAddress());

        CompletableFuture.runAsync(() -> {
                try {
                    read(socket);
                } catch (Exception e) {
                    e.printStackTrace();
                }

        });

        PrintWriter writer = new PrintWriter(socket.getOutputStream(),true);

        Scanner scanner = new Scanner(System.in);
        System.out.println("please begin chat to server:");
        while(scanner.hasNext()){
            String say=scanner.nextLine();
            writer.println(say);
        }

    }

    private static void read(Socket socket) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }

    }


}
