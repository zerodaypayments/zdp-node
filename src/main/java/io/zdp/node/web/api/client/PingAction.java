package io.zdp.node.web.api.client;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import io.zdp.api.model.v1.PingResponse;
import io.zdp.api.model.v1.Urls;

@RestController
public class PingAction {

	@RequestMapping(path = Urls.URL_PING)
	@ResponseBody
	public PingResponse ping() throws Exception {
		return new PingResponse();
	}
}
