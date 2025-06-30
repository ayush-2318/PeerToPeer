package p2p.controller;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
//import main.java.p2p.service.*;
import jdk.internal.misc.Signal;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import p2p.service.FileSharer;;

public class FileController {
    private final FileSharer fileSharer;
    private final HttpServer httpServer;
    private final String uploadDir;
    private final ExecutorService executorService;

    public FileController(int port) throws IOException {
        this.fileSharer=new FileSharer();
        this.httpServer=HttpServer.create(new InetSocketAddress(port),0);
        this.uploadDir=System.getProperty("java.io.tmpdir")+File.separator +"peer-link-uploads";
        this.executorService=Executors.newFixedThreadPool(10);

        File uploadDirFile=new File(uploadDir);
        if(!uploadDirFile.exists()){
            uploadDirFile.mkdirs();
        }

        httpServer.createContext("/upload",new UploadHandler());
       // httpServer.createContext("/download",new DownloadHandler());
        httpServer.createContext("/", new CORSHandler());
        httpServer.setExecutor(executorService);
    }

    public void start(){
        httpServer.start();
        System.out.println("API Server started on port "+ httpServer.getAddress().getPort());
    }

    public void stop(){
        httpServer.stop(0);
        executorService.shutdown();
        System.out.println("API server has stopped");
    }

    private class CORSHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            Headers headers=exchange.getResponseHeaders();
            headers.add("Access-Control-Allow-Origin","*");
            headers.add("Access-Control-Allow-Methods","GET,POST,OPTIONS");
            headers.add("Access-Control-Allow-Headers","Content-Type-Authorization");

            if(exchange.getRequestMethod().equals("OPTIONS")){
                exchange.sendResponseHeaders(204,-1);
                return;
            }
            String response="NOT FOUND";

            exchange.sendResponseHeaders(404,response.getBytes().length);
            try (OutputStream outputStream=exchange.getResponseBody()){
                outputStream.write(response.getBytes());
            }
        }
    }


}
