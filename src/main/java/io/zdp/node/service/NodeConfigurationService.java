package io.zdp.node.service;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Service;

import io.zdp.node.Node;
import io.zdp.node.service.network.NetworkNode;
import io.zdp.node.service.network.NetworkNodeType;

/**
 *	Local node configuration
 */
@Service
public class NodeConfigurationService {

	private NetworkNode node;

	@PostConstruct
	public void init ( ) {
		node = Node.getLocalNode();
	}

	public NetworkNodeType getNodeType ( ) {
		return node.getNodeType();
	}

	public NetworkNode getNode ( ) {
		return node;
	}

}
