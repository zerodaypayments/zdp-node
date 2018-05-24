package io.zdp.node.ui;

import java.awt.BorderLayout;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.input.Tailer;
import org.apache.commons.io.input.TailerListener;
import org.apache.commons.io.input.TailerListenerAdapter;
import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.zdp.node.service.NodeConfigurationService;
import sun.rmi.runtime.Log;

import javax.swing.plaf.nimbus.NimbusLookAndFeel;

@Component
public class MainWindow {

	private Logger log = LoggerFactory.getLogger( this.getClass() );

	@Autowired
	private NodeConfigurationService nodeService;

	@PostConstruct
	public void init ( ) {

		try {
			UIManager.setLookAndFeel( new NimbusLookAndFeel() );
		} catch ( Exception e ) {
			e.printStackTrace();
		}

		SwingUtilities.invokeLater( ( ) -> {

			JFrame frame = new JFrame();
			frame.setTitle( "ZDP Node" );
			frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
			frame.setSize( 800, 600 );
			frame.setLayout( new BorderLayout() );

			final MainPanel panel = new MainPanel();
			panel.serverUuid.setText( nodeService.getNode().getUuid() );
			panel.serverPort.setText( nodeService.getNode().getPort() + "" );
			panel.serverType.setText( nodeService.getNodeType().name() );

			File nodeLogFile = new File( SystemUtils.getUserHome(), "/.zdp/logs/node.log" );
			Tailer.create( nodeLogFile, new TailerListenerAdapter() {

				@Override
				public void init ( Tailer tailer ) {
					try {
						panel.nodeLogs.setText( FileUtils.readFileToString( nodeLogFile, StandardCharsets.UTF_8 ) );
					} catch ( IOException e ) {
						log.error( "Error:" + e.getMessage() );
					}
				}

				@Override
				public void handle ( String line ) {
					panel.nodeLogs.append( line );
				}
			} );

			// http requests log
			File httpLog = new File( SystemUtils.getUserHome(), "/.zdp/logs/http/http.log" );

			Tailer.create( httpLog, new TailerListenerAdapter() {

				@Override
				public void init ( Tailer tailer ) {
					try {
						panel.httpLogs.setText( FileUtils.readFileToString( httpLog, StandardCharsets.UTF_8 ) );
					} catch ( IOException e ) {
						log.error( "Error: " + e.getMessage() );
					}
				}

				@Override
				public void handle ( String line ) {
					panel.nodeLogs.append( line );
				}
			} );

			frame.add( panel );
			frame.setLocationByPlatform( true );

			List < Image > icons = new ArrayList<>();

			icons.add( new ImageIcon( this.getClass().getResource( "/icons/app/32.png" ) ).getImage() );
			icons.add( new ImageIcon( this.getClass().getResource( "/icons/app/64.png" ) ).getImage() );
			icons.add( new ImageIcon( this.getClass().getResource( "/icons/app/128.png" ) ).getImage() );
			icons.add( new ImageIcon( this.getClass().getResource( "/icons/app/256.png" ) ).getImage() );
			icons.add( new ImageIcon( this.getClass().getResource( "/icons/app/512.png" ) ).getImage() );

			frame.setIconImages( icons );

			frame.setVisible( true );

		} );
	}

}
