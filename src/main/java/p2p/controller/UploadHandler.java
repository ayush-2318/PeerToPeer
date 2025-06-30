package p2p.controller;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.apache.tomcat.util.http.fileupload.IOUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class UploadHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Headers requesrHeaders=exchange.getRequestHeaders();
        String contentType=requesrHeaders.getFirst("Content-Type");
        if(contentType==null||!contentType.startsWith("multipart/form-data")){
            String response="Bad Request: Content-Type must be multipart/form-data";
            exchange.sendResponseHeaders(400,response.getBytes().length);
            try (OutputStream outputStream=exchange.getResponseBody()){
                outputStream.write(response.getBytes());
            }
            return;
        }

        try {
            String boundary=contentType.substring(contentType.indexOf("boundary=")+9);
            ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
            IOUtils.copy(exchange.getRequestBody(),byteArrayOutputStream);
            byte[] requestData=byteArrayOutputStream.toByteArray();

            //Multiparser parser = new Multiparser(requestData,boundary);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
