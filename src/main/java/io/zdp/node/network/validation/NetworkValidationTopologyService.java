package io.zdp.node.network.validation;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.zdp.model.network.NetworkNode;
import io.zdp.model.network.NetworkTopologyService;
import io.zdp.node.service.NodeConfigurationService;

@Service
public class NetworkValidationTopologyService {

	@Autowired
	private NetworkTopologyService networkTopologyService;

	@Autowired
	private NodeConfigurationService nodeConfigurationService;

	private List < NetworkNode > nodes;

	private Date lastUpdateDate = new Date();

	public List < NetworkNode > getNodes ( ) {

		if ( nodes == null || networkTopologyService.getLastRefreshDate().after( lastUpdateDate ) ) {

			lastUpdateDate = networkTopologyService.getLastRefreshDate();

			nodes = new ArrayList<>( networkTopologyService.getAllNodes() );

			nodes.remove( nodeConfigurationService.getNode() );

		}

		return nodes;
	}

}
