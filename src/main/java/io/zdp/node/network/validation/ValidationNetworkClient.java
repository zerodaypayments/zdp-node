package io.zdp.node.network.validation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
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

import io.zdp.api.model.v1.GetBalanceRequest;
import io.zdp.api.model.v1.GetBalanceResponse;
import io.zdp.crypto.Signing;
import io.zdp.model.network.NetworkNode;
import io.zdp.node.domain.ValidatedTransferRequest;
import io.zdp.node.service.NodeConfigurationService;
import io.zdp.node.storage.account.domain.Account;
import io.zdp.node.web.api.validation.Urls;
import io.zdp.node.web.api.validation.model.ValidationPrepareTransferRequest;
import io.zdp.node.web.api.validation.model.ValidationPrepareTransferResponse;
import io.zdp.node.web.api.validation.model.ValidationPrepareTransferResponse.Status;

/**
 * Ask Validation network nodes for the 
 */
@Service
public class ValidationNetworkClient {

	private Logger log = LoggerFactory.getLogger( this.getClass() );

	@Autowired
	private NetworkValidationTopologyService networkNodeService;

	private static final int HTTP_CLIENT_MAX_PER_ROUTE = 2000;

	private static final int HTTP_CLIENT_MAX_TOTAL = 20000;

	private static final int HTTP_TIMEOUT = 2000;

	private RestTemplate restTemplate;

	private final ExecutorService rollbackThreadPool = Executors.newFixedThreadPool( 32 );

	@Autowired
	private NodeConfigurationService nodeConfig;

	private static ValidatedTransferRequest lastRequest;
	private static ValidationPrepareTransferResponse lastResponse;

	@PostConstruct
	public void init ( ) {

		synchronized ( this ) {

			close();

			restTemplate = new RestTemplate( Collections.singletonList( new MappingJackson2HttpMessageConverter() ) );

			HttpClient httpClient = HttpClientBuilder.create() //
					.setMaxConnTotal( HTTP_CLIENT_MAX_TOTAL ) //
					.setMaxConnPerRoute( HTTP_CLIENT_MAX_PER_ROUTE ) //
					.build();

			restTemplate.setRequestFactory( new HttpComponentsClientHttpRequestFactory( httpClient ) );

		}
	}

	/**
	 * True for yes/no for this transfer 
	 */
	public ValidationPrepareTransferResponse prepare ( ValidatedTransferRequest req ) {

		log.debug( "Prepare " + req );

		lastRequest = req;

		ValidationPrepareTransferResponse resp;

		if ( false == networkNodeService.getNodes().isEmpty() ) {

			resp = ask( req );

		} else {

			log.debug( "I am the only validation node, I am very agreeable" );

			resp = new ValidationPrepareTransferResponse();
			resp.setFromAccount( req.getFromAccount() );
			resp.setToAccount( req.getToAccount() );
			resp.setStatus( Status.APPROVED );

		}

		lastResponse = resp;

		return resp;

	}

	@PreDestroy
	public void close ( ) {

	}

	private ValidationPrepareTransferResponse ask ( ValidatedTransferRequest req ) {

		final ValidationPrepareTransferResponse resp = new ValidationPrepareTransferResponse();
		resp.setFromAccount( req.getFromAccount() );
		resp.setStatus( Status.APPROVED );
		resp.setToAccount( req.getToAccount() );

		final List < NetworkNode > nodes = networkNodeService.getNodes();

		// Pool for the vote call
		final ExecutorService threadPool = Executors.newFixedThreadPool( nodes.size() );

		// Prepare request
		final ValidationPrepareTransferRequest restRequest = toRequest( req );

		try {
			restRequest.setSignedRequest( Signing.sign( nodeConfig.getNode().getECPrivateKey(), restRequest.toHashData() ) );
		} catch ( Exception e ) {
			log.error( "Error: ", e );
		}

		// Create tasks
		final List < PrepareTask > tasks = new ArrayList<>( networkNodeService.getNodes().size() );

		// Run them in parallel
		networkNodeService.getNodes().forEach( n -> {

			log.debug( "Validation node: " + n.getUuid() );

			final PrepareTask task = new PrepareTask( n.getHttpBaseUrl() + Urls.URL_VOTE, restTemplate, restRequest );

			tasks.add( task );

			threadPool.submit( task );

		} );

		try {
			threadPool.awaitTermination( 5, TimeUnit.SECONDS );
		} catch ( InterruptedException e ) {
			log.error( "Error: ", e );
		}

		log.debug( "Finished voting: " );

		// Filter out un-fullfilled requests
		filterOutFailedTasks( tasks );

		// Any rejections?
		boolean anyRejections = anyRejections( tasks );

		if ( anyRejections ) {
			log.info( "There was a rejection, cancel transfer" );
			resp.setStatus( Status.REJECTED );
		} else {

			// find latest from and to account and settle transfer
			findLatestAccount( tasks, resp );

		}

		return resp;

	}

	private void findLatestAccount ( final List < PrepareTask > tasks, final ValidationPrepareTransferResponse resp ) {

		for ( final PrepareTask task : tasks ) {

			final ValidationPrepareTransferResponse tr = task.getResponse();

			// From
			Account from = tr.getFromAccount();

			if ( resp.getFromAccount() == null ) {
				resp.setFromAccount( from );
			} else if ( from != null && resp.getFromAccount().getHeight() > from.getHeight() ) {
				resp.getFromAccount().setHeight( from.getHeight() );
				resp.getFromAccount().setTransferHash( from.getTransferHash() );
				resp.getFromAccount().setBalance( from.getBalance() );
			}

			// To
			Account to = tr.getToAccount();

			if ( resp.getToAccount() == null ) {
				resp.setToAccount( to );
			} else if ( resp.getToAccount().getHeight() < to.getHeight() ) {
				resp.getToAccount().setHeight( to.getHeight() );
				resp.getToAccount().setTransferHash( to.getTransferHash() );
				resp.getToAccount().setBalance( to.getBalance() );
			}

		}

	}

	private void filterOutFailedTasks ( List < PrepareTask > tasks ) {

		Iterator < PrepareTask > it = tasks.iterator();

		while ( it.hasNext() ) {

			PrepareTask task = it.next();

			if ( task.getResponse() == null || task.getResponse().getStatus() == null ) {
				log.debug( "Filter out: " + task );
				it.remove();
			}

		}

	}

	private boolean anyRejections ( List < PrepareTask > tasks ) {

		for ( final PrepareTask task : tasks ) {

			log.debug( "Response:  " + task.getResponse() );

			if ( false == task.getResponse().getStatus().equals( Status.APPROVED ) ) {
				return true;
			}
		}

		return false;
	}

	private ValidationPrepareTransferRequest toRequest ( ValidatedTransferRequest req ) {
		final ValidationPrepareTransferRequest restRequest = new ValidationPrepareTransferRequest();
		restRequest.setFromAccountUuid( req.getFromAccountUuid().getUuid() );
		restRequest.setRequestUuid( UUID.randomUUID().toString() );
		restRequest.setServerUuid( nodeConfig.getNode().getUuid() );
		restRequest.setToAccountUuid( req.getFromAccountUuid().getUuid() );
		restRequest.setTransferUuid( req.getTransactionUuid() );
		return restRequest;
	}

	public void commit ( ValidatedTransferRequest enrichedTransferRequest ) {

	}

	public void rollback ( ValidatedTransferRequest enrichedTransferRequest ) {

		// 	submit to rollbackThreadPool

	}

	public static ValidatedTransferRequest getLastRequest ( ) {
		return lastRequest;
	}

	public static ValidationPrepareTransferResponse getLastResponse ( ) {
		return lastResponse;
	}

	public GetBalanceResponse getBalance ( GetBalanceRequest request, GetBalanceResponse resp ) {

		// Pool for the vote call
		final List < NetworkNode > nodes = networkNodeService.getNodes();

		final ExecutorService threadPool = Executors.newFixedThreadPool( nodes.size() );

		final List < GetBalanceTask > tasks = new ArrayList<>( networkNodeService.getNodes().size() );

		networkNodeService.getNodes().forEach( n -> {

			log.debug( "Get balance from validation node: " + n.getUuid() );

			final GetBalanceTask task = new GetBalanceTask( n.getHttpBaseUrl() + io.zdp.api.model.v1.Urls.URL_GET_BALANCE, restTemplate, request );

			tasks.add( task );

			threadPool.submit( task );

		} );

		try {
			threadPool.awaitTermination( 5, TimeUnit.SECONDS );
		} catch ( InterruptedException e ) {
			log.error( "Error: ", e );
		}

		log.debug( "Finished getting balance: " );

		for ( final GetBalanceTask task : tasks ) {

			final GetBalanceResponse tr = task.getResponse();

			if ( tr != null ) {
				if ( resp.getAmount() == null || tr.getHeight() > resp.getHeight() ) {
					resp.setAmount( tr.getAmount() );
					resp.setHeight( tr.getHeight() );
					resp.setChainHash( tr.getChainHash() );
				}
			}

		}

		return resp;

	}

}