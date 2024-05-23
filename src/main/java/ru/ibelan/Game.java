package ru.ibelan;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Game class.
 */
@Slf4j
public class Game {
	private final Server server;
	private final Connection connection1;
	private final Connection connection2;

	private final AtomicInteger roundNumber = new AtomicInteger(1);
	private final ConcurrentMap<Player, RPS> decisions = new ConcurrentHashMap<>();

	public Game(Server server, Connection connection1, Connection connection2) {
		this.server = server;
		this.connection1 = connection1;
		this.connection2 = connection2;
	}

	public void start() {
		try {
			startGame(connection1, connection2.getPlayer());
			startGame(connection2, connection1.getPlayer());
			do {
				Thread thread1 = startAnswerThread(connection1);
				Thread thread2 = startAnswerThread(connection2);
				thread1.join();
				thread2.join();
				RPS decision1 = decisions.get(connection1.getPlayer());
				RPS decision2 = decisions.get(connection2.getPlayer());
				showResult(connection1, decision1, decision2);
				showResult(connection2, decision2, decision1);
				boolean isDraw = RPS.compare(decision1, decision2) == 0;
				if (!isDraw) {
					break;
				}
				roundNumber.incrementAndGet();
			} while (true);
			endGame(connection1);
			endGame(connection2);
			closeConnections(false);
		} catch (Exception e) {
			log.error(e.getMessage());
			closeConnections(true);
		}
	}

	private void startGame(Connection connection, Player opponent) throws IOException {
		connection.write("\nOpponent has been found! Your opponent is " + opponent.getNickname() + ".\n" +
				"The game has begun.\n");
	}

	private Thread startAnswerThread(Connection connection) {
		Thread thread = new Thread(() -> {
			try {
				connection.write("\n========== ROUND " + roundNumber.get() + " ==========\n");
				RPS answer = askDecision(connection);
				decisions.put(connection.getPlayer(), answer);
				connection.write("You chose \"" + answer + "\"\n");
			} catch (Exception e) {
				log.error(e.getMessage());
				closeConnections(true);
			}
		});
		thread.start();
		return thread;
	}

	private RPS askDecision(Connection connection) throws IOException {
		do {
			connection.write("""
					Select 1, 2 or 3:
					1). ROCK.
					2). PAPER.
					3). SCISSORS.
					""");
			try {
				String answer = connection.read();
				int rpsNumber = Integer.parseInt(answer);
				return switch (rpsNumber) {
					case 1 -> RPS.ROCK;
					case 2 -> RPS.PAPER;
					case 3 -> RPS.SCISSORS;
					default -> throw new NumberFormatException();
				};
			} catch(NumberFormatException e) {
				connection.write("Can't parse your answer, try again:\n");
			}
		} while (true);
	}

	private void showResult(Connection connection, RPS decision, RPS opponentDecision) throws IOException {
		connection.write("Your opponent chose \"" + opponentDecision + "\"\n");

		switch (RPS.compare(decision, opponentDecision)) {
			case 1 -> connection.write("=== YOU WIN! ===\n");
			case -1 -> connection.write("=== YOU LOSE ===\n");
			case 0 -> connection.write("=== DRAW ===\n");
		}
	}

	private void endGame(Connection connection) throws IOException {
		connection.write("\nGood bye.\n");
	}

	private void closeConnections(boolean withError) {
		server.closeConnection(connection1, withError);
		server.closeConnection(connection2, withError);
	}
}
