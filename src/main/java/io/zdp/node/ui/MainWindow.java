package io.zdp.node.ui;

import java.awt.BorderLayout;
import java.awt.Image;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;

import org.pushingpixels.substance.api.skin.SubstanceChallengerDeepLookAndFeel;
import org.pushingpixels.substance.api.skin.SubstanceDustCoffeeLookAndFeel;
import org.pushingpixels.substance.api.skin.SubstanceGraphiteGlassLookAndFeel;
import org.pushingpixels.substance.api.skin.SubstanceNebulaBrickWallLookAndFeel;
import org.pushingpixels.substance.api.skin.SubstanceNebulaLookAndFeel;
import org.pushingpixels.substance.api.skin.SubstanceOfficeBlack2007LookAndFeel;
import org.pushingpixels.substance.api.skin.SubstanceOfficeBlue2007LookAndFeel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.zdp.node.service.NodeConfigurationService;

@Component
public class MainWindow {

	private Logger log = LoggerFactory.getLogger( this.getClass() );

	@Autowired
	private NodeConfigurationService nodeService;

	@Autowired
	private MainPanel mainPanel;

	@PostConstruct
	public void init ( ) {

		try {
//			UIManager.setLookAndFeel(new NimbusLookAndFeel());
			
			UIManager.setLookAndFeel(new SubstanceOfficeBlack2007LookAndFeel());
			
			JFrame.setDefaultLookAndFeelDecorated( true );
			JDialog.setDefaultLookAndFeelDecorated( true );
		} catch ( Exception e ) {
			e.printStackTrace();
		}

		SwingUtilities.invokeLater( ( ) -> {

			JFrame frame = new JFrame();

			frame.setTitle( "ZDP Node (" + System.getProperty( "user.home" ) + ")" );
			frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
			frame.setSize( 800, 600 );

			frame.setLayout( new BorderLayout() );

			frame.add( mainPanel );

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
