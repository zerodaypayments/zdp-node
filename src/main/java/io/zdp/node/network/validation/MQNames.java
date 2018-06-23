package io.zdp.node.network.validation;

public interface MQNames {

	public static final String TOPIC_TRANSFER_NEW_REQ = "T.TRANSFER.NEW.REQ";

	public static final String QUEUE_TRANSFER_NEW_RESP = "Q.TRANSFER.NEW.RESP";

	public static final String TOPIC_SETTLED_TRANSFER_REQ = "T.SETTLED.TRANSFER.REQ";

	public static final String TOPIC_FAILED_TRANSFER_REQ = "T.FAILED.TRANSFER.REQ";
	
	public static final String TOPIC_GET_BALANCE_REQ = "T.GET.BALANCE.REQ";
	
	public static final String QUEUE_GET_BALANCE_RESP = "Q.GET.BALANCE.RESP";

}
