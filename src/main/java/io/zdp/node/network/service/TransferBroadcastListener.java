package io.zdp.node.network.service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import io.zdp.node.network.domain.NetworkNode;

@Service
public class TransferBroadcastListener {

	private Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private NetworkConfigurationService networkService;
	
	private List<JmsTemplate> jms= new ArrayList<>();

	@PostConstruct
	public void init() {
		
		log.debug("Starting TransferBroadcastListener");
		
		configureJms();
		
	}
	
    private void configureJms() {

    	List<NetworkNode> nodes = networkService.getNodes();

    	for (NetworkNode node:nodes) {
    		
    		JmsTemplate j = new JmsTemplate();
    		
    		jms.add(j);
    		
    	}
    	
    	
	}

	public void consume(BroadcastedTransfer transfer) {
		
	}
	
	
}
