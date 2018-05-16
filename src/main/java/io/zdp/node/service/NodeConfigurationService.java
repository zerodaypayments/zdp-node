package io.zdp.node.service;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Service;

import io.zdp.crypto.Curves;
import io.zdp.crypto.key.ZDPKeyPair;
import io.zdp.node.service.network.NetworkNode;
import io.zdp.node.service.network.NetworkNodeType;

/**
 *
 */
@Service
public class NodeConfigurationService {

	private NetworkNode node;

	@PostConstruct
	public void init() {
		node = new NetworkNode();
		node.setUuid("test-local-node");
		node.setNodeType(NetworkNodeType.VALIDATING);

		ZDPKeyPair kp = ZDPKeyPair.createRandom(Curves.DEFAULT_CURVE);

		node.setPublicKey(kp.getPublicKeyAsBase58());
		node.setPrivateKey(kp.getPrivateKeyAsBase58());
	}

	public NetworkNodeType getNodeType() {
		return node.getNodeType();
	}

	public NetworkNode getNode() {
		return node;
	}

}
