package io.zdp.node.service.validation.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.zdp.crypto.Signing;
import io.zdp.model.network.NetworkTopologyService;
import io.zdp.node.service.LocalNodeService;
import io.zdp.node.service.validation.model.NetworkBaseSignedObject;

@Service
public class ValidationNodeSigner {

	@Autowired
	private LocalNodeService nodeConfig;

	@Autowired
	private NetworkTopologyService networkTopologyService;

	public void sign(NetworkBaseSignedObject o) throws Exception {
		o.setServerUuid(nodeConfig.getNode().getUuid());
		o.setServerSignature(Signing.sign(nodeConfig.getNode().getECPrivateKey(), o.toHash()));
	}

	public boolean isValidSignature(NetworkBaseSignedObject o) {
		boolean validServerRequest = networkTopologyService.isValidServerRequest(o.getServerUuid(), o.toHash(), o.getServerSignature());
		return validServerRequest;
	}
}
