package ru.ibelan;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.Socket;

/**
 * Connection class.
 * По сути это обёртка над сокетом для удобства взаимодействия с игроком.
 */
@Slf4j
public class Connection implements AutoCloseable {
	private final Socket socket;
	@Getter
	private final Player player;
	private final BufferedReader reader;
	private final BufferedWriter writer;

	public Connection(Socket socket) throws IOException {
		this.socket = socket;
		this.player = new Player();
		this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		this.writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
	}

	public void write(String str) throws IOException {
		writer.write(str);
		writer.flush();
	}

	public String read() throws IOException {
		return reader.readLine();
	}

	public boolean isAlive() {
		return !socket.isClosed();
	}

	@Override
	public void close() throws IOException {
		socket.close();
	}
}
