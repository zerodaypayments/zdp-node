package io.zdp.node.network.validation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.zdp.api.model.v1.TransferRequest;
import io.zdp.node.domain.ValidatedTransferRequest;
import io.zdp.node.service.network.NetworkTopologyService;

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

		} else {

			log.debug("I am the only validation node, I am very agreeable");

		}

		return true;

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

		networkNodeService.getNodes().forEach(e -> {
			
			log.debug("Validation node: " + e.getUuid());
			
			
			
		});

	}
/*
	{

		HttpGet request = new HttpGet("http://localhost:8080/test.jsp");

		List<Future<HttpResponse>> futures = new ArrayList<>();

		for (int i = 0; i < 6; i++) {

			futures.add(client.execute(request, null));

			//Thread.sleep(RandomUtils.nextLong(1000, 5000));
		}

		while (futures.isEmpty() == false)
			for (Future<HttpResponse> f : futures) {

				if (f.isDone()) {

					HttpResponse response = f.get();

					System.out.println(IOUtils.toString(response.getEntity().getContent()));

					futures.remove(f);
					break;

				}

			}

		client.close();
	}
*/
}