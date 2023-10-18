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
			byte[] buffer = new byte[2048];
			int n = socket.getInputStream().read(buffer);
			String rawRequest = new String(buffer, 0, n);
			Request request = new Request(rawRequest);
			logger.info("Получен запрос");
			request.show();
			boolean executed = false;
			for (Map.Entry<String, MyWebApplication> e : server.getRouter().entrySet()) {
				if (request.getUri().startsWith(e.getKey())) {
					e.getValue().execute(request, socket.getOutputStream());
					executed = true;
					break;
				}
			}
			if (!executed) {
				socket.getOutputStream().write(("HTTP/1.1 200 OK\r\nContent-Type: text/html\r\n\r\n<html><link rel=\"icon\" href=\"data:,\"><body><h1>Unknown application</h1></body></html>").getBytes(StandardCharsets.UTF_8));

			}
			socket.close();
		} catch (IOException e) {
			logger.error("Ошибка взаимодействия с клиентом " + e);
		}
	}
}