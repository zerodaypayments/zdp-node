package io.zdp.node.ui;

import java.awt.BorderLayout;
import java.awt.Image;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.pushingpixels.substance.api.skin.SubstanceOfficeBlack2007LookAndFeel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.zdp.node.service.NodeConfigurationService;

@Component
public class MainWindow {

	private static final String WINDOW_Y = "window.y";

	private static final String WINDOW_X = "window.x";

	private static final String WINDOW_HEIGHT = "window.height";

	private static final String WINDOW_WIDTH = "window.width";

	private Logger log = LoggerFactory.getLogger( this.getClass() );

	@Autowired
	private NodeConfigurationService nodeService;

	@Autowired
	private MainPanel mainPanel;

	private JButton btnOnline;

	@PostConstruct
	public void init ( ) {

		try {
			//			UIManager.setLookAndFeel(new NimbusLookAndFeel());

			UIManager.setLookAndFeel( new SubstanceOfficeBlack2007LookAndFeel() );

			JFrame.setDefaultLookAndFeelDecorated( true );
			JDialog.setDefaultLookAndFeelDecorated( true );
		} catch ( Exception e ) {
			e.printStackTrace();
		}

		SwingUtilities.invokeLater( ( ) -> {

			JFrame frame = new JFrame();

			frame.setTitle( "ZDP Node (" + System.getProperty( "user.home" ) + ")" );
			frame.setDefaultCloseOperation( JFrame.DO_NOTHING_ON_CLOSE );

			frame.addWindowListener( new WindowAdapter() {

				@Override
				public void windowClosing ( WindowEvent e ) {
					Props.add( WINDOW_WIDTH, frame.getWidth() );
					Props.add( WINDOW_HEIGHT, frame.getHeight() );
					Props.add( WINDOW_X, frame.getX() );
					Props.add( WINDOW_Y, frame.getY() );

					System.exit( 0 );
				}

			} );

			frame.setSize( Props.getInt( WINDOW_WIDTH, 800 ), Props.getInt( WINDOW_HEIGHT, 600 ) );

			if ( Props.contains( WINDOW_X ) ) {
				frame.setLocation( Props.getInt( WINDOW_X, 0 ), Props.getInt( WINDOW_Y, 0 ) );
			} else {
				frame.setLocationRelativeTo( null );
			}

			frame.setLayout( new BorderLayout() );

			frame.add( mainPanel );

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
