package io.zdp.node.domain;

import java.io.Serializable;

public class LocalNode implements Serializable {

	private String nodeModeString;

	private String nodeUuid;

	private String nodeKey;

	private long height;

	private String blockChainHash;

	public long getHeight() {
		return height;
	}

	public void setHeight(long height) {
		this.height = height;
	}

	public String getBlockChainHash() {
		return blockChainHash;
	}

	public void setBlockChainHash(String blockChainHash) {
		this.blockChainHash = blockChainHash;
	}

	public String getNodeModeString() {
		return nodeModeString;
	}

	public void setNodeModeString(String nodeModeString) {
		this.nodeModeString = nodeModeString;
	}

	public String getNodeUuid() {
		return nodeUuid;
	}

	public void setNodeUuid(String nodeUuid) {
		this.nodeUuid = nodeUuid;
	}

	public String getNodeKey() {
		return nodeKey;
	}

	public void setNodeKey(String nodeKey) {
		this.nodeKey = nodeKey;
	}

}
