package ru.ibelan;

import lombok.extern.slf4j.Slf4j;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Server class.
 */
@Slf4j
public class Server {
	private volatile boolean isRunning = false;

	// Все соединения
	private final List<Connection> connectionPool = new ArrayList<>();

	// Очередь игроков (точнее их соединений) (хотя вообще, это осмысленно только в случае больших очередей,
	// а тут можно заменить на одного ожидающего)
	private final Queue<Connection> queue = new LinkedList<>();

	public void run(int port, int connectionsLimit) {
		Thread serverThread = new Thread(() -> runServer(port, connectionsLimit));
		serverThread.start();
	}

	private void runServer(int port, int connectionsLimit) {
		try (ServerSocket ss = new ServerSocket(port, connectionsLimit)) {
			log.info("RPS-game server has started at port {}.", port);
			isRunning = true;

			while (isRunning) {
				try {
					Socket socket = ss.accept();
					Connection connection = new Connection(socket);
					connectionPool.add(connection);
					log.debug("Connected");
					connection.write("\nWelcome to RPS-game!\n");
					initDialog(connection);
				} catch (Exception e) {
					log.error(e.getMessage());
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage());
		} finally {
			connectionPool.forEach(con -> {
				try {
					con.close();
				} catch (Exception e) {
					log.error(e.getMessage());
				}
			});
		}
	}

	public void initDialog(Connection connection) {
		new Thread(() -> {
			try {
				connection.write("Enter your nickname:\n");
				String nickname = connection.read(); // будет куча ждущих потоков, да, не очень хорошо
				connection.getPlayer().setNickname(nickname);
				log.debug("player nickname is \"{}\"", nickname);
				connection.write("Searching for opponents...\n");
				startGameOrQueue(connection);
			} catch (Exception e) {
				log.error(e.getMessage());
			}
		}).start();
	}

	/**
	 * Начинает игру или ставит игрока в очередь если её невозможно начать.
	 */
	public void startGameOrQueue(Connection connection) {
		clearConnections();
		Connection opponentConnection = queue.poll();
		if (opponentConnection == null) {
			queue.offer(connection);
		} else {
			Game game = new Game(this, connection, opponentConnection);
			game.start();
		}
	}

	public void closeConnection(Connection connection, boolean withError) {
		connectionPool.removeIf(con -> con == connection);
		if (withError) {
			try {
				connection.write("\nUNEXPECTED SERVER ERROR!\n");
			} catch (Exception ex) {
				log.error(ex.getMessage());
			}
		}
		try {
			connection.close();
			log.debug("connection closed");
		} catch (Exception ex) {
			log.error(ex.getMessage());
		}
	}

	public void clearConnections() {
		connectionPool.removeIf(it -> !it.isAlive());
		queue.removeIf(it -> !it.isAlive());
	}

	public void stop() {
		isRunning = false;
		log.info("RPS-game server has stopped.");
	}
}
