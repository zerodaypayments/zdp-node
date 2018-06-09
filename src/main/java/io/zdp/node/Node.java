package io.zdp.node;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.zdp.model.network.NetworkNode;
import io.zdp.model.network.NetworkNodeType;
import io.zdp.node.http.HttpServer;

public class Node {

	private static final String PARAM_DATA_FOLDER = "data-folder";

	private static final String PARAM_TYPE = "type";

	private static final String PARAM_PRIVATE_KEY = "private-key";

	private static final String PARAM_HOST = "host";

	private static final String PARAM_HTTP_PORT = "http-port";

	private static final String PARAM_DEBUG = "debug";

	private static final String PARAM_UUID = "uuid";

	private static final String PARAM_TCP_V_PORT = "tcp-validation-port";

	private static final String PARAM_TCP_V_HOST = "tcp-validation-host";

	private static final String DDASH = "--";

	private static final String EQUALS = "=";

	public static final String USER_HOME = "user.home";

	private Logger log;

	private static NetworkNode localNode;

	private static final String RANDOM_UUID = UUID.randomUUID().toString();

	private static boolean debugMode = false;

	public static void main ( String... args ) throws Exception {

		// Sort out user home
		setUserHomeVarialbe( args );

		new Node().run( args );

	}

	private void run ( String... args ) throws Exception {

		log = LoggerFactory.getLogger( Node.class );

		populateLocalNode( args );

		HttpServer http = new HttpServer();
		http.init();

	}

	private void populateLocalNode ( String [ ] args ) {

		localNode = new NetworkNode();

		// defaults
		localNode.setNodeType( NetworkNodeType.MONITORING );
		localNode.setHostname( "localhost" );
		localNode.setHttpPort( 8080 );
		localNode.setUuid( RANDOM_UUID );

		for ( String arg : args ) {

			if ( arg.contains( EQUALS ) ) {

				String [ ] split = arg.split( EQUALS );
				String key = StringUtils.removeStart( split [ 0 ], DDASH );
				String value = split [ 1 ];

				if ( PARAM_TYPE.equalsIgnoreCase( key ) ) {
					if ( NetworkNodeType.VALIDATING.name().equals( value ) ) {
						localNode.setNodeType( NetworkNodeType.VALIDATING );
					}
				} else if ( PARAM_UUID.equalsIgnoreCase( key ) ) {
					localNode.setUuid( value );
				} else if ( PARAM_HTTP_PORT.equalsIgnoreCase( key ) ) {
					localNode.setHttpPort( Integer.parseInt( value ) );
				} else if ( PARAM_HOST.equalsIgnoreCase( key ) ) {
					localNode.setHostname( value );
				} else if ( PARAM_PRIVATE_KEY.equalsIgnoreCase( key ) ) {
					localNode.setPrivateKey( value );
				} else if ( PARAM_DEBUG.equalsIgnoreCase( key ) ) {
					debugMode = Boolean.parseBoolean( value );
				} else if ( PARAM_TCP_V_HOST.equalsIgnoreCase( key ) ) {
					System.setProperty( PARAM_TCP_V_HOST, value );
				} else if ( PARAM_TCP_V_PORT.equalsIgnoreCase( key ) ) {
					System.setProperty( PARAM_TCP_V_PORT, value );
				}

			}
		}

		if ( localNode.isValidating() && StringUtils.isBlank( localNode.getPrivateKey() ) ) {
			System.err.println( "Validating node must have a private key (--private-key parameter)" );
			System.exit( 1 );
		}

		log.debug( "Local node:  " + localNode );
	}

	private static void setUserHomeVarialbe ( String [ ] args ) {

		String userHome = System.getProperty( USER_HOME );
		String uuid = RANDOM_UUID;

		for ( String arg : args ) {

			if ( arg.contains( EQUALS ) ) {

				String [ ] split = arg.split( EQUALS );
				String key = StringUtils.removeStart( split [ 0 ], DDASH );
				String value = split [ 1 ];

				if ( PARAM_DATA_FOLDER.equalsIgnoreCase( key ) ) {
					System.out.println( "New user home: " + value );
					userHome = value;
				} else if ( PARAM_UUID.equalsIgnoreCase( key ) ) {
					uuid = value;
				}

			}
		}

		// set new user home
		if ( false == userHome.equals( System.getProperty( USER_HOME ) ) ) {

			File userHomeFile = new File( userHome + File.separator + uuid );

			System.out.println( "New user home: " + userHomeFile.getAbsolutePath() );

			try {
				FileUtils.forceMkdir( userHomeFile );
			} catch ( IOException e ) {
				System.out.println( "Error: " + e.getMessage() );
				System.exit( 1 );
			}
			System.setProperty( USER_HOME, userHomeFile.getAbsolutePath() );

		} else {
			System.setProperty( USER_HOME, userHome );
		}

	}

	public static NetworkNode getLocalNode ( ) {
		return localNode;
	}

	public static boolean isDebugMode ( ) {
		return debugMode;
	}

}