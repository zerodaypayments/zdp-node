package io.zdp.node;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

import io.zdp.api.model.v1.TransferRequest;
import io.zdp.api.model.v1.TransferResponse;
import io.zdp.api.model.v1.Urls;
import io.zdp.crypto.Curves;
import io.zdp.crypto.key.ZDPKeyPair;
import junit.framework.TestCase;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:/test-spring-context.xml")
public class TestTransferErrorAction extends TestCase {

	private RestTemplate restTemplate = new RestTemplate();
	
	@Value("${zdp.api.host}")
	private String zdpApiHost;
	
	@Test
	public void testErrors() throws Exception {

		{
			
//			PingResponse ping = restTemplate.getForObject(zdpApiHost + Urls.URL_PING, PingResponse.class);
			
			TransferResponse response = restTemplate.postForObject(zdpApiHost + Urls.URL_TRANSFER,new TransferRequest(), TransferResponse.class);

			System.out.println(response);

			assertNotNull(response);
			assertNull(response.getUuid());
			assertEquals(TransferResponse.ERROR_INVALID_FROM_ACCOUNT, response.getError());
			
		}

		{
			TransferRequest request = new TransferRequest();
			request.setFrom("AAA");

			TransferResponse response = restTemplate.postForObject(zdpApiHost + Urls.URL_TRANSFER,request, TransferResponse.class);

			System.out.println(response);

			assertNotNull(response);
			assertNull(response.getUuid());
			assertEquals(TransferResponse.ERROR_INVALID_FROM_ACCOUNT, response.getError());
		}

		{
			TransferRequest request = new TransferRequest();

			ZDPKeyPair kp = ZDPKeyPair.createRandom(Curves.DEFAULT_CURVE);

			request.setFrom(kp.getZDPAccount().getUuid());

			TransferResponse response = restTemplate.postForObject(zdpApiHost + Urls.URL_TRANSFER,request, TransferResponse.class);
			
			System.out.println(response);

			assertNotNull(response);
			assertEquals(TransferResponse.ERROR_INVALID_TO_ACCOUNT, response.getError());
		}

	}
/*
	@Test
	public void testInvalidAmounts() throws Exception {

		String[] invalidAmounts = { "", null, "23s", "234,4234", "234.234/234", "3.4.2", "-34" };

		for (String amt : invalidAmounts) {

			// Create 2 accounts
			GetNewAccountResponse acc1 = accountAction.getNewAccount(new GetNewAccountRequest(Language.ENGLISH, Curves.DEFAULT_CURVE));
			GetNewAccountResponse acc2 = accountAction.getNewAccount(new GetNewAccountRequest(Language.ENGLISH, Curves.DEFAULT_CURVE));

			TransferRequest request = new TransferRequest();

			request.setFrom(acc1.getAccountUuid());
			request.setTo(acc2.getAccountUuid());
			request.setAmount(amt);

			TransferResponse response = transferAction.transfer(request);

			System.out.println(response);

			assertNotNull(response);
			assertEquals(TransferResponse.ERROR_INVALID_AMOUNT, response.getError());

		}

	}
*/
}
