package io.zdp.node.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import io.zdp.api.model.v1.GetFeeResponse;
import io.zdp.api.model.v1.TransferRequest;
import io.zdp.api.model.v1.TransferResponse;
import io.zdp.api.model.v1.Urls;
import io.zdp.node.error.TransferException;
import io.zdp.node.service.TransferService;

@RestController
public class TransferAction {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private TransferService txService;

	@RequestMapping(path = Urls.URL_GET_TX_FEE)
	@ResponseBody
	public GetFeeResponse getFee() {
		return new GetFeeResponse(TransferService.TX_FEE.toPlainString());
	}

	@RequestMapping(path = Urls.URL_TRANSFER)
	@ResponseBody
	public TransferResponse transfer(@RequestBody TransferRequest request) {
		try {
			return txService.transfer(request);
		} catch (TransferException e) {
			log.error("Error: ", e);
			return TransferResponse.error(e.getError());
		}
	}

}
