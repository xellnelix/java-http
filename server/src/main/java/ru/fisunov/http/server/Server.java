package ru.fisunov.http.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
	private static final Logger logger = LogManager.getLogger(Server.class.getName());
	private final int port;
	private final Map<String, MyWebApplication> router;

	public Map<String, MyWebApplication> getRouter() {
		return router;
	}

	Server(int port) {
		this.port = port;
		this.router = new HashMap<>();
	}

	public void start() {
		ExecutorService pool = Executors.newFixedThreadPool(10);
		try (ServerSocket serverSocket = new ServerSocket(port)) {
			router.put("/calculator", new CalculatorWebApplication());
			router.put("/greetings", new GreetingsWebApplication());
			logger.info("Сервер запущен");
			while (!serverSocket.isClosed()) {
				Socket socket = serverSocket.accept();
				logger.info("Клиент подключился");
				pool.execute(new RequestHandler(socket, this));
			}
		} catch (IOException e) {
			logger.error("Ошибка запуска сервера " + e);
			pool.shutdown();
		}
	}
}