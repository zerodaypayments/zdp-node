package io.zdp.node.ui;

import java.awt.BorderLayout;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import io.zdp.client.ZdpClient;
import io.zdp.node.Node;
import io.zdp.node.common.QTextComponentContextMenu;
import io.zdp.node.common.SwingHelper;
import io.zdp.node.network.validation.NetworkValidationTopologyService;
import io.zdp.node.storage.account.dao.AccountDao;
import io.zdp.node.storage.account.domain.Account;
import io.zdp.node.storage.transfer.dao.TransferHeaderDao;
import io.zdp.node.storage.transfer.domain.TransferHeader;

@Component
@SuppressWarnings ( "serial" )
public class ValidationNodesPanel {

	private JPanel panel;

	private JEditorPane textArea;

	@Autowired
	private NetworkValidationTopologyService networkTopologyService;

	@Autowired
	private ZdpClient zdpClient;

	@Autowired
	private AccountDao accountDao;

	@Autowired
	private TransferHeaderDao transferHeaderDao;

	@Scheduled ( fixedDelay = DateUtils.MILLIS_PER_SECOND * 2 )
	public void refresh ( ) {

		StringBuilder sb = new StringBuilder();

		sb.append( "<html>" );

		sb.append( "<table border='0' width='80%' align='center'><tr style='background:black;color:white;padding:10px;'><td>Server</td><td>Status</td></tr>" );

		networkTopologyService.getNodes().stream().forEach( n -> {

			zdpClient.setNetworkNode( n );

			String active = "<span style='color:green;'>OK</span>";
			try {
				zdpClient.ping();
			} catch ( Exception e ) {
				active = "<span style='color:red;'>Not available</span>";
			}

			sb.append( "<tr style='background:#efefef;'><td>" + n.getHttpBaseUrl() + "</td><td>" + active + "</td></tr>" );

		} );

		zdpClient.setNetworkNode( null );

		sb.append( "</table>" );

		if ( Node.isDebugMode() ) {

			// List of accounts

			sb.append( "<h2>Accounts</h2>" );

			List < Account > accounts = accountDao.findAll();
			sb.append( "Accounts count: " + accounts.size() + "<hr>" );
			for ( Account a : accounts ) {
				sb.append( "Account: " + a + "<hr>" );
			}

			// List of transfers

			sb.append( "<h2>Transfers</h2>" );

			List < TransferHeader > transfers = transferHeaderDao.findAll();
			sb.append( "transfers count: " + transfers.size() + "<hr>" );
			for ( TransferHeader th : transfers ) {
				sb.append( "Transfer: " + th + "<hr>" );
			}

		}

		sb.append( "</html>" );

		textArea.setText( sb.toString() );

	}

	@PostConstruct
	public void init ( ) {

		SwingUtilities.invokeLater( ( ) -> {

			panel = new JPanel();

			textArea = new JEditorPane();
			textArea.setEditable( false );
			textArea.setContentType( "text/html" );

			new QTextComponentContextMenu( textArea );

			SwingHelper.setFontForJText( textArea );
			panel.setLayout( new BorderLayout() );
			panel.add( new JScrollPane( textArea ), BorderLayout.CENTER );

			textArea.setText( "loading..." );
		} );
	}

	public JPanel getPanel ( ) {
		return panel;
	}

}
