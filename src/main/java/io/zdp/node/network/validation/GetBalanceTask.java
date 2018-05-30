package io.zdp.node.network.validation;

import org.springframework.web.client.RestTemplate;

import io.zdp.api.model.v1.GetBalanceRequest;
import io.zdp.api.model.v1.GetBalanceResponse;

public class GetBalanceTask implements Runnable {

	private String url;

	private RestTemplate template;

	private GetBalanceRequest request;

	private GetBalanceResponse response;

	public GetBalanceTask ( String url, RestTemplate template, GetBalanceRequest request ) {
		super();
		this.url = url;
		this.template = template;
		this.request = request;
	}

	@Override
	public void run ( ) {

		response = template.postForObject( url, request, GetBalanceResponse.class );

	}

	public GetBalanceResponse getResponse ( ) {
		return response;
	}

	@Override
	public String toString ( ) {
		return "GetBalanceTask [url=" + url + ", request=" + request + ", response=" + response + "]";
	}

}
