package io.zdp.node.ui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.zdp.node.Node;

/**
 * Simple local configuration manager (via java properties) 
 */
public class Props {

	private static final Logger log = LoggerFactory.getLogger( Props.class );

	private static final Properties props = new Properties();

	private static File file = new File( System.getProperty( Node.USER_HOME ) + File.separator + ".zdp" + File.separator + "conf" + File.separator + "ui.properties" );

	static {

		try {
			FileUtils.forceMkdirParent( file );
		} catch ( IOException e1 ) {
			log.error( "Error: ", e1 );
		}

		if ( file.exists() ) {
			try ( InputStream is = new FileInputStream( file ) ) {

				props.loadFromXML( is );

				log.info( props.toString() );

			} catch ( Exception e ) {
				log.error( "Error: ", e );
			}
		}

	}

	public static int getInt ( String key, int def ) {
		return NumberUtils.toInt( props.getProperty( key ), def );
	}

	public static String getString ( String key, String def ) {
		return StringUtils.defaultIfBlank( props.getProperty( key ), def );
	}

	public static Properties getProps ( ) {
		return props;
	}

	public static void add ( String key, Integer v ) {
		props.put( key, Integer.toString( v ) );
		save();
	}

	public static void add ( String key, String v ) {
		props.put( key, v );
		save();
	}

	private static void save ( ) {
		try ( OutputStream os = new FileOutputStream( file ) ) {
			props.storeToXML( os, "" );
		} catch ( Exception e ) {
			log.error( "Error: ", e );
		}
	}

	public static boolean contains ( String key ) {
		return props.containsKey( key );
	}

}
