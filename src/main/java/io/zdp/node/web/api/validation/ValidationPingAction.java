package io.zdp.node.web.api.validation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import io.zdp.node.service.NodeConfigurationService;
import io.zdp.node.web.api.validation.model.ValidationPingResponse;

@RestController
public class ValidationPingAction {

	@Autowired
	private NodeConfigurationService nodeConfigurationService;

	@RequestMapping(path = Urls.URL_PING)
	@ResponseBody
	public ValidationPingResponse ping() throws Exception {

		ValidationPingResponse resp = new ValidationPingResponse();

		resp.setPublicKey(nodeConfigurationService.getNode().getPublicKey());
		resp.setUuid(nodeConfigurationService.getNode().getUuid());

		return resp;
	}
}
