package io.zdp.node.network.validation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import io.zdp.crypto.Signing;
import io.zdp.model.network.NetworkNode;
import io.zdp.model.network.NetworkTopologyService;
import io.zdp.node.domain.ValidatedTransferRequest;
import io.zdp.node.service.NodeConfigurationService;
import io.zdp.node.web.api.validation.Urls;
import io.zdp.node.web.api.validation.model.ValidationPrepareTransferRequest;
import io.zdp.node.web.api.validation.model.ValidationPrepareTransferResponse;
import io.zdp.node.web.api.validation.model.ValidationPrepareTransferResponse.Status;

/**
 * Ask Validation network nodes for the 
 */
@Service
public class ValidationNetworkClient {

	private Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private NetworkTopologyService networkNodeService;

	private static final int HTTP_CLIENT_MAX_PER_ROUTE = 2000;

	private static final int HTTP_CLIENT_MAX_TOTAL = 20000;

	private static final int HTTP_TIMEOUT = 2000;

	private RestTemplate restTemplate;

	private final ExecutorService rollbackThreadPool = Executors.newFixedThreadPool(32);

	@Autowired
	private NodeConfigurationService nodeConfig;

	@PostConstruct
	public void init() {

		synchronized (this) {

			close();

			restTemplate = new RestTemplate(Collections.singletonList(new MappingJackson2HttpMessageConverter()));

			HttpClient httpClient = HttpClientBuilder.create() //
					.setMaxConnTotal(HTTP_CLIENT_MAX_TOTAL) //
					.setMaxConnPerRoute(HTTP_CLIENT_MAX_PER_ROUTE) //
					.build();

			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(httpClient));

		}
	}

	/**
	 * True for yes/no for this transfer 
	 */
	public ValidationPrepareTransferResponse prepare(ValidatedTransferRequest req) {

		log.debug("Prepare " + req);

		ValidationPrepareTransferResponse resp;

		if (false == networkNodeService.getNodes().isEmpty()) {

			resp = ask(req);

		} else {

			log.debug("I am the only validation node, I am very agreeable");

			resp = new ValidationPrepareTransferResponse();
			resp.setFromAccount(req.getFromAccount());
			resp.setToAccount(req.getToAccount());
			resp.setStatus(Status.APPROVED);

		}

		return resp;

	}

	@PreDestroy
	public void close() {

	}

	private ValidationPrepareTransferResponse ask(ValidatedTransferRequest req) {

		final ValidationPrepareTransferResponse resp = new ValidationPrepareTransferResponse();

		final List<NetworkNode> nodes = networkNodeService.getNodes();

		// Pool for the vote call
		final ExecutorService threadPool = Executors.newFixedThreadPool(nodes.size());

		// Prepare request
		final ValidationPrepareTransferRequest restRequest = toRequest(req);

		try {
			restRequest.setSignedRequest(Signing.sign(nodeConfig.getNode().getECPrivateKey(), restRequest.toHashData()));
		} catch (Exception e) {
			log.error("Error: ", e);
		}

		// Create tasks
		final List<PrepareTask> tasks = new ArrayList<>(networkNodeService.getNodes().size());

		// Run them in parallel
		networkNodeService.getNodes().forEach(n -> {

			log.debug("Validation node: " + n.getUuid());

			PrepareTask task = new PrepareTask(n.getHttpBaseUrl() + Urls.URL_VOTE, restTemplate, restRequest);

			tasks.add(task);

			threadPool.submit(task);

		});

		try {
			threadPool.awaitTermination(5, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			log.error("Error: ", e);
		}

		log.debug("Finished voting: ");

		for (PrepareTask task : tasks) {
			log.debug("Response:  " + task.getResponse());
		}

		return resp;

	}

	private ValidationPrepareTransferRequest toRequest(ValidatedTransferRequest req) {
		final ValidationPrepareTransferRequest restRequest = new ValidationPrepareTransferRequest();
		restRequest.setFromAccountUuid(req.getFromAccountUuid().getUuid());
		restRequest.setRequestUuid(UUID.randomUUID().toString());
		restRequest.setServerUuid(nodeConfig.getNode().getUuid());
		restRequest.setToAccountUuid(req.getFromAccountUuid().getUuid());
		restRequest.setTransferUuid(req.getTransactionUuid());
		return restRequest;
	}

	public void commit(ValidatedTransferRequest enrichedTransferRequest) {

	}

	public void rollback(ValidatedTransferRequest enrichedTransferRequest) {

		// 	submit to rollbackThreadPool

	}

}