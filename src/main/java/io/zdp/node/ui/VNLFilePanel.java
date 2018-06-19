package io.zdp.node.ui;

import java.awt.BorderLayout;

import javax.annotation.PostConstruct;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import io.zdp.model.network.NetworkTopologyService;
import io.zdp.node.common.QTextComponentContextMenu;

@Component
@SuppressWarnings("serial")
public class VNLFilePanel extends JPanel {

	private JTextArea textArea;

	@Autowired
	private NetworkTopologyService networkTopologyService;

	@Scheduled(fixedDelay = DateUtils.MILLIS_PER_SECOND * 5)
	public void refresh() {

		String content = networkTopologyService.getVnlFileContent();
		if (content != null && false == content.equals(textArea.getText())) {
			textArea.setText(content);
		}
	}

	@PostConstruct
	public void init() {

		textArea = new JTextArea();
		textArea.setEditable(false);
		new QTextComponentContextMenu(textArea);

		this.setLayout(new BorderLayout());
		this.add(new JScrollPane(textArea), BorderLayout.CENTER);

		textArea.setText(networkTopologyService.getVnlFileContent());

	}

}
