package io.zdp.node.ui;

import java.awt.BorderLayout;

import javax.annotation.PostConstruct;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.zdp.crypto.Curves;
import io.zdp.node.common.QTextComponentContextMenu;
import io.zdp.node.service.LocalNodeService;

@Component
public class MainPanel extends JPanel {

	private JTabbedPane tabs;

	@Autowired
	private LocalNodeService nodeService;

	@Autowired
	private VNLFilePanel vnlFilePanel;

	@Autowired
	private ValidationNodesPanel validationNodesPanel;

	@Autowired
	private LastTransferPanel lastTransferPanel;

	@PostConstruct
	public void init() {

		this.tabs = new JTabbedPane();

		this.setLayout(new BorderLayout());
		this.add(tabs, BorderLayout.CENTER);

		ServerInfoPanel info = new ServerInfoPanel();
		info.serverHost.setText(nodeService.getNode().getHostname());
		info.serverPort.setText(nodeService.getNode().getHttpPort() + "");
		info.serverUuid.setText(nodeService.getNode().getUuid());
		info.serverType.setText(nodeService.getNode().getNodeType().name());
		info.serverPublicKey.setText(nodeService.getNode().getPublicKey());
		info.serverCurve.setText(Curves.VALIDATION_NODE_CURVE);

		new QTextComponentContextMenu(info.serverHost);
		new QTextComponentContextMenu(info.serverPort);
		new QTextComponentContextMenu(info.serverUuid);
		new QTextComponentContextMenu(info.serverType);
		new QTextComponentContextMenu(info.serverPublicKey);
		new QTextComponentContextMenu(info.serverCurve);

		this.tabs.insertTab("Node Info", new ImageIcon(this.getClass().getResource("/icons/app/16.png")), info, "Node information", 0);
		this.tabs.insertTab("Node System Logs", new ImageIcon(this.getClass().getResource("/icons/report.png")), new NodeLogsPanel(), "Node System Logs", 1);
		this.tabs.insertTab("Node HTTP Logs", new ImageIcon(this.getClass().getResource("/icons/world.png")), new NodeHttpLogsPanel(), "Node HTTP Logs", 2);
		this.tabs.insertTab("Validation Node Logs", new ImageIcon(this.getClass().getResource("/icons/world.png")), new ValidationNodeLogsPanel(), "Validation Node Logs", 3);
		this.tabs.insertTab("VNL File", new ImageIcon(this.getClass().getResource("/icons/brick.png")), vnlFilePanel, "Validation Nodes Network File", 4);
		this.tabs.insertTab("Validation Nodes", new ImageIcon(this.getClass().getResource("/icons/brick.png")), validationNodesPanel.getPanel(), "Validation Nodes", 5);
		this.tabs.insertTab("Last Transfer Info", new ImageIcon(this.getClass().getResource("/icons/app/16.png")), lastTransferPanel.getPanel(), "Last Transfer Info", 6);

	}

}
