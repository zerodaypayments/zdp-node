package io.zdp.node.service;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import io.zdp.model.network.NetworkNode;
import io.zdp.model.network.NetworkNodeType;
import io.zdp.node.Node;

/**
 *	Local node configuration
 */
@Component("localNodeService")
public class LocalNodeService {

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
