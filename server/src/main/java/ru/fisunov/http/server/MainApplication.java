package ru.fisunov.http.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainApplication {
    private static final Logger logger = LogManager.getLogger(MainApplication.class.getName());
    public static final int PORT = 8189;

    // + К домашнему задания:
    // Добавить логирование!!!

    public static void main(String[] args) {
        ExecutorService pool = Executors.newFixedThreadPool(10);
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            Map<String, MyWebApplication> router = new HashMap<>();
            router.put("/calculator", new CalculatorWebApplication());
            router.put("/greetings", new GreetingsWebApplication());
            logger.info("Сервер запущен");
            while (!serverSocket.isClosed()) {
                Socket socket = serverSocket.accept();
                logger.info("Клиент подключился");
                logger.info(socket.getLocalAddress());

                pool.execute(new ConnectionHandler(socket, router));
//                } catch (IOException e) {
//                    logger.error("Ошибка соединения с клиентом " + e);
//                    pool.shutdown();
//                }
            }
        } catch (IOException e) {
            logger.error("Ошибка запуска сервера " + e);
        }
    }
}
