package io.zdp.node.ui;

import java.awt.BorderLayout;

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
import io.zdp.model.network.NetworkTopologyService;
import io.zdp.node.common.QTextComponentContextMenu;
import io.zdp.node.common.SwingHelper;

@Component
@SuppressWarnings("serial")
public class ValidationNodesPanel {

	private JPanel panel;

	private JEditorPane textArea;

	@Autowired
	private NetworkTopologyService networkTopologyService;

	@Autowired
	private ZdpClient zdpClient;

	@Scheduled(fixedDelay = DateUtils.MILLIS_PER_SECOND * 2)
	public void refresh() {

		StringBuilder sb = new StringBuilder();
		sb.append("<html><table border='0' width='80%' align='center'><tr style='background:black;color:white;padding:10px;'><td>Server</td><td>Status</td></tr>");

		networkTopologyService.getNodes().stream().forEach(n -> {

			zdpClient.setHostUrl(n.getHttpBaseUrl());

			String active = "<span style='color:green;'>OK</span>";
			try {
				zdpClient.ping();
			} catch (Exception e) {
				active = "<span style='color:red;'>Not available</span>";
			}

			sb.append("<tr style='background:#efefef;'><td>" + n.getHttpBaseUrl() + "</td><td>" + active + "</td></tr>");

		});

		zdpClient.setHostUrl(null);

		sb.append("</table></html>");

		textArea.setText(sb.toString());

	}

	@PostConstruct
	public void init() {

		SwingUtilities.invokeLater(() -> {

			panel = new JPanel();

			textArea = new JEditorPane();
			textArea.setEditable(false);
			textArea.setContentType("text/html");

			new QTextComponentContextMenu(textArea);

			SwingHelper.setFontForJText(textArea);
			panel.setLayout(new BorderLayout());
			panel.add(new JScrollPane(textArea), BorderLayout.CENTER);

			textArea.setText("loading...");
		});
	}

	public JPanel getPanel() {
		return panel;
	}

}
