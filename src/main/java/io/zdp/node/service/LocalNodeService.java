package io.zdp.node.service;

import org.springframework.stereotype.Service;

import io.zdp.node.domain.LocalNode;

@Service
public class LocalNodeService {

	private LocalNode localNode;

	public LocalNode getLocalNode() {
		return localNode;
	}

	public void setLocalNode(LocalNode localNode) {
		this.localNode = localNode;
	}

}
