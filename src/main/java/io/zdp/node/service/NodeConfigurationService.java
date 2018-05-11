package io.zdp.node.service;

import org.springframework.stereotype.Service;

import io.zdp.node.network.topology.NetworkNode;
import io.zdp.node.network.topology.NetworkNodeType;

@Service
public class NodeConfigurationService {
	
	private NetworkNode node;
	
	public NetworkNodeType getNodeType() {
		return node.getNodeType(); 
	}
	
}
