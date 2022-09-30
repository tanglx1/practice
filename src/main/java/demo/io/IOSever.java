package demo.io;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CompletableFuture;

public class IOSever {
    public static void main(String[] args) throws Exception {
        ServerSocket serverSocket = new ServerSocket(8081);

        while (true) {
            Socket clientSocket=serverSocket.accept();
            System.out.println("client:"+clientSocket.getRemoteSocketAddress()+" connected");

            CompletableFuture.runAsync(() -> {
                try {
                    read(clientSocket);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

        }

    }


    private static void read(Socket socket) throws Exception{
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(),"utf-8"));
        PrintWriter writer = new PrintWriter(socket.getOutputStream(),true);

        String line;
        while((line=reader.readLine())!=null){
            System.out.println("client say:"+line);

            writer.println("hello:" + line);
        }


    }


}
