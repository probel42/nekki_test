package ru.ibelan;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

/**
 * Main class.
 */
public class App {
	private static final String PROPERTIES = "app.properties";
	private static final String SERVER_PORT = "server.port";
	private static final String CONNECTIONS_LIMIT = "server.connectionsLimit";

	private static final int DEFAULT_PORT = 23;
	private static final int DEFAULT_CONNECTIONS_LIMIT = 100;

	public static void main(String[] args) {
		// load properties
		int port = DEFAULT_PORT;
		int connectionsLimit = DEFAULT_CONNECTIONS_LIMIT;
		try {
			PropertiesConfiguration config = new PropertiesConfiguration();
			config.load(PROPERTIES);
			port = config.getInt(SERVER_PORT);
			connectionsLimit = config.getInt(CONNECTIONS_LIMIT);
		} catch (ConfigurationException ignore) {
		}

		// start server
		Server server = new Server();
		server.run(port, connectionsLimit);
	}
}
