package io.zdp.node.ui;

import java.awt.BorderLayout;

import javax.annotation.PostConstruct;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import io.zdp.node.common.QTextComponentContextMenu;
import io.zdp.node.common.SwingHelper;

@SuppressWarnings ( "serial" )
@Component
public class LastTransferPanel {

	private static final Logger log = LoggerFactory.getLogger( LastTransferPanel.class );

	private JEditorPane textArea;

	private JPanel panel;

	@PostConstruct
	public void init ( ) {

		textArea = new JEditorPane();
		textArea.setEditable( false );
		textArea.setContentType( "text/html" );

		new QTextComponentContextMenu( textArea );

		SwingHelper.setFontForJText( textArea );

		panel = new JPanel();
		panel.setLayout( new BorderLayout() );
		panel.add( new JScrollPane( textArea ), BorderLayout.CENTER );

		refresh();
	}

	@Scheduled ( fixedDelay = 1000 )
	public void refresh ( ) {

		StringBuilder sb = new StringBuilder();
/*
		sb.append( "<html><table border='0' width='80%' align='center'>" );

		sb.append( "<tr style=';padding:10px;'><td>ValidationNetworkClient request: </td><td>" + ValidationNetworkClient.getLastRequest() + "</td></tr>" );
		sb.append( "<tr style=';padding:10px;'><td>ValidationNetworkClient response: </td><td>" + ValidationNetworkClient.getLastResponse() + "</td></tr>" );

		sb.append( "<tr><td colspan='2'><hr>Prepare: </td></tr>" );

		sb.append( "<tr style=';padding:10px;'><td>getLastValidationPrepareTransferRequest: </td><td>" + ValidationTransferAction.getLastValidationPrepareTransferRequest() + "</td></tr>" );
		sb.append( "<tr style=';padding:10px;'><td>getLastValidationPrepareTransferResponse: </td><td>" + ValidationTransferAction.getLastValidationPrepareTransferResponse() + "</td></tr>" );

		sb.append( "<tr><td colspan='2'><hr>Commit: </td></tr>" );

		sb.append( "<tr style=';padding:10px;'><td>getLastValidationCommitRequest: </td><td>" + ValidationTransferAction.getLastValidationCommitRequest() + "</td></tr>" );
		sb.append( "<tr style=';padding:10px;'><td>isLastValidationCommitResponse: </td><td>" + ValidationTransferAction.isLastValidationCommitResponse() + "</td></tr>" );

		sb.append( "<tr><td colspan='2'><hr>Rollback: </td></tr>" );

		sb.append( "<tr style=';padding:10px;'><td>getLastRollbackValidationPrepareTransferRequest: </td><td>" + ValidationTransferAction.getLastRollbackValidationPrepareTransferRequest() + "</td></tr>" );
		sb.append( "<tr style=';padding:10px;'><td>isLastRollbackValidationPrepareTransferResponse: </td><td>" + ValidationTransferAction.isLastRollbackValidationPrepareTransferResponse() + "</td></tr>" );

		sb.append( "</table></html>" );
*/
		textArea.setText( sb.toString() );

	}

	public JPanel getPanel ( ) {
		return panel;
	}

}
