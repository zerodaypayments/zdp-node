package io.zdp.node;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.zdp.node.http.HttpServer;
import io.zdp.node.service.network.NetworkNode;
import io.zdp.node.service.network.NetworkNodeType;

public class Node {

	private static final Logger log = LoggerFactory.getLogger( Node.class );

	private static NetworkNode localNode = new NetworkNode();

	public static void main ( String [ ] args ) throws Exception {

		populateLocalNode( args );

		HttpServer http = new HttpServer();
		http.init();
	}

	private static void populateLocalNode ( String [ ] args ) {

		// defaults
		localNode.setNodeType( NetworkNodeType.MONITORING );
		localNode.setHostname( "localhost" );
		localNode.setPort( 8080 );
		localNode.setUuid( UUID.randomUUID().toString() );

		String userHome = System.getProperty( "user.home" );

		for ( String arg : args ) {

			if ( arg.contains( "=" ) ) {

				String [ ] split = arg.split( "=" );
				String key = StringUtils.removeStart( split [ 0 ], "--" );
				String value = split [ 1 ];

				if ( "type".equalsIgnoreCase( key ) ) {
					if ( NetworkNodeType.VALIDATING.name().equals( value ) ) {
						localNode.setNodeType( NetworkNodeType.VALIDATING );
					}
				} else if ( "uuid".equalsIgnoreCase( key ) ) {
					localNode.setUuid( value );
				} else if ( "port".equalsIgnoreCase( key ) ) {
					localNode.setPort( Integer.parseInt( value ) );
				} else if ( "host".equalsIgnoreCase( key ) ) {
					localNode.setHostname( value );
				} else if ( "private-key".equalsIgnoreCase( key ) ) {
					localNode.setPrivateKey( value );

				} else if ( "data-folder".equalsIgnoreCase( key ) ) {

					log.info( "New user home: " + value );
					userHome = value;
				}

			}
		}

		if ( localNode.isValidating() && StringUtils.isBlank( localNode.getPrivateKey() ) ) {
			System.err.println( "Validating node must have a private key (--private-key parameter)" );
			System.exit( 1 );
		}

		// set new user home
		if ( false == userHome.equals( System.getProperty( "user.home" ) ) ) {

			File userHomeFile = new File( userHome + File.separator + localNode.getUuid() );

			log.debug( "New user home: " + userHomeFile.getAbsolutePath() );

			try {
				FileUtils.forceMkdir( userHomeFile );
			} catch ( IOException e ) {
				log.error( "Error: ", e );
				System.exit( 1 );
			}
			System.setProperty( "user.home", userHomeFile.getAbsolutePath() );

		} else {
			System.setProperty( "user.home", userHome );
		}

		log.debug( "Local node:  " + localNode );
	}

	public static NetworkNode getLocalNode ( ) {
		return localNode;
	}

}