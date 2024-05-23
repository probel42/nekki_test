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

	public static void main(String[] args) throws ConfigurationException {
		// load properties
		PropertiesConfiguration config = new PropertiesConfiguration();
		config.load(PROPERTIES);
		int port = config.getInt(SERVER_PORT);
		int connectionsLimit = config.getInt(CONNECTIONS_LIMIT);

		// start server
		Server server = new Server();
		server.run(port, connectionsLimit);
	}
}
