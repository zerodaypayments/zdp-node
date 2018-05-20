package io.zdp.node.network.validation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager;
import org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor;
import org.apache.http.nio.reactor.IOReactorException;
import org.eclipse.jetty.util.log.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.zdp.api.model.v1.TransferRequest;
import io.zdp.node.domain.ValidatedTransferRequest;
import io.zdp.node.service.network.NetworkTopologyService;
import io.zdp.node.web.api.validation.Urls;

/**
 * Ask Validation network nodes for the 
 */
@Service
public class ValidationNetworkClient {

	@Autowired
	private NetworkTopologyService networkNodeService;

	private Logger log = LoggerFactory.getLogger(this.getClass());

	private CloseableHttpAsyncClient client;

	@PostConstruct
	public void start() throws IOReactorException {

		PoolingNHttpClientConnectionManager poolingConnManager = new PoolingNHttpClientConnectionManager(new DefaultConnectingIOReactor());

		poolingConnManager.setDefaultMaxPerRoute(16);

		client = HttpAsyncClients.custom().setConnectionManager(poolingConnManager).build();

		client.start();

	}

	/**
	 * True for yes/no for this transfer 
	 */
	public boolean prepare(ValidatedTransferRequest req) {

		log.debug("Prepare " + req);

		if (false == networkNodeService.getNodes().isEmpty()) {

			ask(req);

			log.debug("TEST MODE, REJECT TRANSFER");

			return false;

		} else {

			log.debug("I am the only validation node, I am very agreeable");

			return true;
		}

	}

	@PreDestroy
	public void close() {

		if (client != null) {

			try {
				client.close();
			} catch (IOException e) {
				log.error("Error: ", e);
			}

		}

	}

	private void ask(ValidatedTransferRequest req) {

		List<Future<HttpResponse>> futures = new ArrayList<>();

		networkNodeService.getNodes().forEach(n -> {

			log.debug("Validation node: " + n.getUuid());

			HttpGet request = new HttpGet("https://" + n.getHostname() + ":" + n.getPort() + Urls.URL_VOTE);

			log.debug("request: " + request.getURI());

			// if to account is empty and no nodes know about it, reject

			// go with the latest block hash

			futures.add(client.execute(request, null));

		});

		while (futures.isEmpty() == false) {

			for (Future<HttpResponse> f : futures) {

				if (f.isDone()) {

					try {

						HttpResponse response = f.get();

						System.out.println(IOUtils.toString(response.getEntity().getContent()));

					} catch (UnsupportedOperationException | InterruptedException | ExecutionException | IOException e) {
						log.error("Error: ", e);
					}

					futures.remove(f);
					break;

				}

			}
		}

		log.debug("Finished voting");

	}

	public void rollback(ValidatedTransferRequest enrichedTransferRequest) {
		// TODO Auto-generated method stub

	}

	public void commit(ValidatedTransferRequest enrichedTransferRequest) {
		// TODO Auto-generated method stub

	}
}