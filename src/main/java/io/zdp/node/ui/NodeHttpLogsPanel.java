package io.zdp.node.ui;

import java.awt.BorderLayout;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.Tailer;
import org.apache.commons.io.input.TailerListenerAdapter;
import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.zdp.node.common.QTextComponentContextMenu;

@SuppressWarnings ( "serial" )
public class NodeHttpLogsPanel extends JPanel {

	private static final Logger log = LoggerFactory.getLogger( NodeHttpLogsPanel.class );

	private File file;

	private JTextArea textArea;

	public NodeHttpLogsPanel ( ) {

		file = new File( SystemUtils.getUserHome(), "/.zdp/logs/http/http.log" );
		textArea = new JTextArea();
		textArea .setEditable( false );
		new QTextComponentContextMenu( textArea );
		
		this.setLayout( new BorderLayout() );
		this.add( new JScrollPane( textArea ), BorderLayout.CENTER );

		Tailer.create( file, new TailerListenerAdapter() {

			@Override
			public void init ( Tailer tailer ) {
				try {
					log.debug( "Logs: " + file );
					textArea.setText( FileUtils.readFileToString( file, StandardCharsets.UTF_8 ) );
				} catch ( IOException e ) {
					log.error( "Error:" + e.getMessage() );
				}
			}

			@Override
			public void handle ( String line ) {
				textArea.append( line );
				textArea.append( IOUtils.LINE_SEPARATOR );
				textArea.setCaretPosition( textArea.getText().length() );
			}
		} );
	}

}
