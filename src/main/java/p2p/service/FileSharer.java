import java.io.File;
import java.io.FileInputStream;
import java.io.IO;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

import main.java.p2p.utils.UploadUtils;

public class FileSharer {
    private HashMap<Integer,String> avialableFiles;

    public FileSharer(){
        avialableFiles=new HashMap<Integer, String>();
    }

    public int offerFile(String filePath){
        int port;
        while(true){
            port=UploadUtils.generateCode();
            if(!avialableFiles.containsKey(port)){
                avialableFiles.put(port, filePath);
                return port;
            }
        }
    }

    public void startFileServer(int port){
        String filePath=avialableFiles.get(port);

        if(filePath==null){
            System.out.println("No File Associated with port "+ port);
            return;
        }
        try(ServerSocket serverSocket = new ServerSocket(port)){
            System.out.println("Serving file "+ new File(filePath).getName()+"on port"+port);
            Socket clientSocket=serverSocket.accept();
            System.out.println("Client connection"+clientSocket.getInetAddress());
            new Thread(new FileSenderHandler(clientSocket,filePath)).start();
        }catch (IOException e) {
           System.err.println("Error on file serving on port"+port);
        }
    }

    public static class FileSenderHandler implements Runnable{
        private final Socket clientSocket;
        private final String filePath;

        public FileSenderHandler(Socket clientSocket,String filePath){
            this.filePath=filePath;
            this.clientSocket=clientSocket;
        }

        @Override
        public void run(){
            try(FileInputStream fileInputStream =new FileInputStream(filePath)) {
                OutputStream oos=clientSocket.getOutputStream();
                String fileName= new File(filePath).getName();
                String header="Filename: "+fileName+"\n";
                oos.write(header.getBytes());
                byte[] buffer=new byte[4096];
                int byteRead;
                while((byteRead=fileInputStream.read(buffer))!=-1){
                    oos.write(buffer,0,byteRead);
                }
                System.out.println("File"+ fileName + "sent to" + clientSocket.getInetAddress());
            } catch (Exception e) {
                System.err.println("Error in sending file to the client "+e.getMessage());
            }finally{
                try {
                    clientSocket.close();
                } catch (Exception e) {
                   System.err.println("Error in closing Socket :"+e.getMessage());
                }
            }
        }

    }
}