package io.zdp.node.network.validation;

import org.springframework.web.client.RestTemplate;

import io.zdp.node.web.api.validation.model.ValidationCommitRequest;

public class CommitTask implements Runnable {

	private String url;

	private RestTemplate template;

	private ValidationCommitRequest request;

	private boolean response;

	public CommitTask ( String url, RestTemplate template, ValidationCommitRequest request ) {
		super();
		this.url = url;
		this.template = template;
		this.request = request;
	}

	public boolean getResponse ( ) {
		return response;
	}

	@Override
	public void run ( ) {

		response = template.postForObject( url, request, Boolean.class );

	}

}
