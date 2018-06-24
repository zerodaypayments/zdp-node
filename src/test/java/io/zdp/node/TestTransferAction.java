package io.zdp.node;

import java.math.BigDecimal;
import java.util.UUID;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import io.zdp.api.model.v1.GetBalanceRequest;
import io.zdp.api.model.v1.GetBalanceResponse;
import io.zdp.api.model.v1.GetNewAccountRequest;
import io.zdp.api.model.v1.GetNewAccountResponse;
import io.zdp.api.model.v1.TransferRequest;
import io.zdp.api.model.v1.TransferResponse;
import io.zdp.crypto.Curves;
import io.zdp.crypto.Hashing;
import io.zdp.crypto.key.ZDPKeyPair;
import io.zdp.crypto.mnemonics.Mnemonics.Language;
import io.zdp.node.storage.account.service.AccountService;
import io.zdp.node.web.api.client.AccountAction;
import io.zdp.node.web.api.client.TransferAction;
import junit.framework.TestCase;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:/com/zerodaypayments/central/spring-context.xml")
public class TestTransferAction extends TestCase {

	@Autowired
	private TransferAction transferAction;

	@Autowired
	private AccountAction accountAction;

	@Autowired
	private AccountService accountService;

	/*
		@Test
		public void testErrors() throws Exception {
	
			{
				SubmitTransactionRequest request = new SubmitTransactionRequest();
				SubmitTransactionResponse response = transferAction.transfer(request);
	
				System.out.println(response);
	
				assertNotNull(response);
				assertNull(response.getUuid());
				assertEquals(SubmitTransactionResponse.ERROR_INVALID_FROM_ACCOUNT, response.getError());
			}
	
			{
				SubmitTransactionRequest request = new SubmitTransactionRequest();
				request.setFrom("AAA");
	
				SubmitTransactionResponse response = transferAction.transfer(request);
	
				System.out.println(response);
	
				assertNotNull(response);
				assertNull(response.getUuid());
				assertEquals(SubmitTransactionResponse.ERROR_INVALID_FROM_ACCOUNT, response.getError());
			}
	
			{
				SubmitTransactionRequest request = new SubmitTransactionRequest();
	
				ZDPKeyPair kp = ZDPKeyPair.createRandom(Curves.DEFAULT_CURVE);
	
				request.setFrom(kp.getZDPAccount().getUuid());
	
				SubmitTransactionResponse response = transferAction.transfer(request);
	
				System.out.println(response);
	
				assertNotNull(response);
				assertEquals(SubmitTransactionResponse.ERROR_INVALID_TO_ACCOUNT, response.getError());
			}
	
		}
	
		@Test
		public void testInvalidAmounts() throws Exception {
	
			String[] invalidAmounts = { "", null, "23s", "234,4234", "234.234/234", "3.4.2", "-34" };
	
			for (String amt : invalidAmounts) {
	
				// Create 2 accounts
				GetNewAccountResponse acc1 = accountAction.getNewAccount(new GetNewAccountRequest(Language.ENGLISH, Curves.DEFAULT_CURVE));
				GetNewAccountResponse acc2 = accountAction.getNewAccount(new GetNewAccountRequest(Language.ENGLISH, Curves.DEFAULT_CURVE));
	
				SubmitTransactionRequest request = new SubmitTransactionRequest();
	
				request.setFrom(acc1.getAccountUuid());
				request.setTo(acc2.getAccountUuid());
				request.setAmount(amt);
	
				SubmitTransactionResponse response = transferAction.transfer(request);
	
				System.out.println(response);
	
				assertNotNull(response);
				assertEquals(SubmitTransactionResponse.ERROR_INVALID_AMOUNT, response.getError());
	
			}
	
		}
	*/
	@Test
	public void testValid() throws Exception {
/*
		// Create 2 accounts
		GetNewAccountResponse from = accountAction.getNewAccount(new GetNewAccountRequest(Language.ENGLISH, Curves.DEFAULT_CURVE));
		GetNewAccountResponse to = accountAction.getNewAccount(new GetNewAccountRequest(Language.ENGLISH, Curves.DEFAULT_CURVE));

		GetBalanceRequest req = new GetBalanceRequest();
		req.setAccountUuid(from.getAccountUuid());

		GetBalanceResponse balance = accountService.getBalance(req);

		assertTrue(new BigDecimal(balance.getAmount()).longValue() > 0);

		TransferRequest request = new TransferRequest();

		request.setPublicKey(from.getPublicKey());
		request.setRequestUuid(UUID.randomUUID().toString());
		request.setFrom(from.getAccountUuid());
		request.setTo(to.getAccountUuid());
		request.setAmount("10");

		byte[] signature = Hashing.hashTransactionSignature(from.getAccountUuid() + request.getAmount() + to.getAccountUuid());
		ZDPKeyPair kp = ZDPKeyPair.createFromPrivateKeyBase58(from.getPrivateKey(), from.getCurve());
		request.setSignature(kp.sign(signature));

		TransferResponse response = transferAction.transfer(request);

		System.out.println(response);

		assertNotNull(response);

		assertNull(response.getError());

		assertNotNull(response.getUuid());

		// get fee
		BigDecimal fee = new BigDecimal( transferAction.getFee().getFee() );
		
		// balance for FROM should increase by amount+fee
		GetBalanceResponse afterbalance = accountService.getBalance(req);
		
		assertEquals(new BigDecimal("89.9999"), new BigDecimal(afterbalance.getAmount()));
		
		System.out.println(afterbalance);
*/
	}

}
