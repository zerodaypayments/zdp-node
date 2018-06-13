package io.zdp.node.network.validation;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.zdp.model.network.NetworkNode;
import io.zdp.model.network.NetworkTopologyListener;
import io.zdp.model.network.NetworkTopologyService;
import io.zdp.node.service.LocalNodeService;

@Service
public class TopologyService implements NetworkTopologyListener {

	@Autowired
	private NetworkTopologyService networkTopologyService;

	@Autowired
	private LocalNodeService nodeConfigurationService;

	private List<NetworkNode> nodes;

	@PostConstruct
	public void init() {
		this.networkTopologyService.addChangeListener(this);
	}

	public List<NetworkNode> getNodes() {

		if (nodes == null) {
			onChange();
		}

		return nodes;

	}

	@Override
	public void onChange() {

		nodes = new ArrayList<>(networkTopologyService.getAllNodes());

		nodes.remove(nodeConfigurationService.getNode());

	}

}
