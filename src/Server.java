import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private ServerSocket serverSocket;
    private int port;

    public Server(int port){
        try{
            this.serverSocket= new ServerSocket(port);
            this.port= port;
        }catch (Exception e){
            System.err.println("Error: No connection");
            System.exit(1);
        }
    }


    public static void main(String[] args) throws IOException {
        //open graphDB server
        ThreadForGraphDBServer threadForGraphDBServer= new ThreadForGraphDBServer();
        threadForGraphDBServer.start();

        //open web application
        String htmlFilePath = "dashboard.html";
        File htmlFile = new File(htmlFilePath);
        // Check if Desktop is supported
        if (Desktop.isDesktopSupported()) {
            Desktop desktop = Desktop.getDesktop();
            try {
                desktop.browse(htmlFile.toURI());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Desktop is not supported on this platform.");
        }

        Server server= new Server(5000);
        while(true){
            Socket clientSocket= server.serverSocket.accept();
            ServerThread serverThread= new ServerThread(clientSocket);
            serverThread.start();
        }
    }


}
