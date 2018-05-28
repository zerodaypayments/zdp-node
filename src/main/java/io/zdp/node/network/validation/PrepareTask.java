package io.zdp.node.network.validation;

import org.springframework.web.client.RestTemplate;

import io.zdp.node.web.api.validation.model.ValidationPrepareTransferRequest;
import io.zdp.node.web.api.validation.model.ValidationPrepareTransferResponse;

public class PrepareTask implements Runnable {

	private String url;

	private RestTemplate template;

	private ValidationPrepareTransferRequest request;

	private ValidationPrepareTransferResponse response;

	public PrepareTask ( String url, RestTemplate template, ValidationPrepareTransferRequest request ) {
		super();
		this.url = url;
		this.template = template;
		this.request = request;
	}

	public ValidationPrepareTransferResponse getResponse ( ) {
		return response;
	}

	@Override
	public void run ( ) {

		response = template.postForObject( url, request, ValidationPrepareTransferResponse.class );

	}

	@Override
	public String toString ( ) {
		return "PrepareTask [url=" + url + ", response=" + response + "]";
	}

}
