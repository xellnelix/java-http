package ru.fisunov.http.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class RequestHandler implements Runnable {
    private final Socket socket;
    private static final Logger logger = LogManager.getLogger(RequestHandler.class.getName());
    private final Server server;


    public RequestHandler(Socket socket, Server server) {
        this.socket = socket;
        this.server = server;
    }

    @Override
    public void run() {
        try {
            Request request = new Request(socket.getInputStream());
            logger.info("Получен запрос");
			request.show();
            boolean executed = false;
            for (Map.Entry<String, MyWebApplication> e : server.getRouter().entrySet()) {
                if (request.getUri() != null && request.getUri().startsWith(e.getKey())) {
                    e.getValue().execute(request, socket.getOutputStream());
                    executed = true;
                    break;
                }
            }
            if (!executed) {
                socket.getOutputStream().write(("HTTP/1.1 404 Not Found\\r\\n\" +\n" +
                        "                                        \"Content-Type: text/html;charset=utf-8\\r\\n\" +\n" +
                        "                                        \"Content-Length: 431\\r\\n\" +\n" +
                        "                                        \"\\r\\n\" +\n" +
                        "                                        \"<!doctype html>\\n\" +\n" +
                        "                                \"<html lang=\\\"en\\\">\\n\" +\n" +
                        "                                \"\\n\" +\n" + "<link rel=\"icon\" href=\"data:,\">" +
                        "                                \"<head>\\n\" +\n" +
                        "                                \"\\t<title>HTTP Status 404 – Not Found</title>\\n\" +\n" +
                        "                                \"\\t<style type=\\\"text/css\\\">\\n\" +\n" +
                        "                                \"\\t\\tbody {\\n\" +\n" +
                        "                                \"\\t\\t\\tfont-family: Tahoma, Arial, sans-serif;\\n\" +\n" +
                        "                                \"\\t\\t}\\n\" +\n" +
                        "                                \"\\n\" +\n" +
                        "                                \"\\t\\th1,\\n\" +\n" +
                        "                                \"\\t\\th2,\\n\" +\n" +
                        "                                \"\\t\\th3,\\n\" +\n" +
                        "                                \"\\t\\tb {\\n\" +\n" +
                        "                                \"\\t\\t\\tcolor: white;\\n\" +\n" +
                        "                                \"\\t\\t\\tbackground-color: #525D76;\\n\" +\n" +
                        "                                \"\\t\\t}\\n\" +\n" +
                        "                                \"\\n\" +\n" +
                        "                                \"\\t\\th1 {\\n\" +\n" +
                        "                                \"\\t\\t\\tfont-size: 22px;\\n\" +\n" +
                        "                                \"\\t\\t}\\n\" +\n" +
                        "                                \"\\n\" +\n" +
                        "                                \"\\t\\th2 {\\n\" +\n" +
                        "                                \"\\t\\t\\tfont-size: 16px;\\n\" +\n" +
                        "                                \"\\t\\t}\\n\" +\n" +
                        "                                \"\\n\" +\n" +
                        "                                \"\\t\\th3 {\\n\" +\n" +
                        "                                \"\\t\\t\\tfont-size: 14px;\\n\" +\n" +
                        "                                \"\\t\\t}\\n\" +\n" +
                        "                                \"\\n\" +\n" +
                        "                                \"\\t\\tp {\\n\" +\n" +
                        "                                \"\\t\\t\\tfont-size: 12px;\\n\" +\n" +
                        "                                \"\\t\\t}\\n\" +\n" +
                        "                                \"\\n\" +\n" +
                        "                                \"\\t\\ta {\\n\" +\n" +
                        "                                \"\\t\\t\\tcolor: black;\\n\" +\n" +
                        "                                \"\\t\\t}\\n\" +\n" +
                        "                                \"\\n\" +\n" +
                        "                                \"\\t\\t.line {\\n\" +\n" +
                        "                                \"\\t\\t\\theight: 1px;\\n\" +\n" +
                        "                                \"\\t\\t\\tbackground-color: #525D76;\\n\" +\n" +
                        "                                \"\\t\\t\\tborder: none;\\n\" +\n" +
                        "                                \"\\t\\t}\\n\" +\n" +
                        "                                \"\\t</style>\\n\" +\n" +
                        "                                \"</head>\\n\" +\n" +
                        "                                \"\\n\" +\n" +
                        "                                \"<body>\\n\" +\n" +
                        "                                \"\\t<h1>HTTP Status 404 – Not Found</h1>\\n\" +\n" +
                        "                                \"</body>\\n\" +\n" +
                        "                                \"\\n\" +\n" +
                        "                                \"</html>").getBytes(StandardCharsets.UTF_8));
//						"HTTP/1.1 200 OK\r\nContent-Type: text/html\r\n\r\n<html><link rel=\"icon\" href=\"data:,\"><body><h1>Unknown application</h1></body></html>").getBytes(StandardCharsets.UTF_8));

            }
            socket.close();
        } catch (IOException e) {
            logger.error("Ошибка взаимодействия с клиентом " + e);
        }
    }
}