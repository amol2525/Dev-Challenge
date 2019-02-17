package com.db.awmd.challenge;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.exception.DuplicateAccountIdException;
import com.db.awmd.challenge.exception.InsufficientFundsException;
import com.db.awmd.challenge.exception.InvalidAccountException;
import com.db.awmd.challenge.exception.InvalidTransferAmountException;
import com.db.awmd.challenge.service.AccountsService;
import com.db.awmd.challenge.service.NotificationService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AccountsServiceTest {

	@Mock
	NotificationService notificationService;

	@Autowired
	@InjectMocks
	private AccountsService underTest;

	private static boolean isSetupRun = false;

	@Before
	public void setup() {
		if (isSetupRun == false) {

			underTest.createAccount(new Account("Id-126", new BigDecimal("1000")));
			underTest.createAccount(new Account("Id-127", new BigDecimal("500")));
			isSetupRun = true;
		}
	}

	@Test
	public void should_add_new_account_with_balance_successfully() throws Exception {
		//given a user wants to add an account with balance
		Account account = new Account("Id-123");
		account.setBalance(new BigDecimal(1000));
		//when 
		underTest.createAccount(account);
       
		//then the account is added successfully
		assertThat(underTest.getAccount("Id-123")).isEqualTo(account);
	}

	@Test
	public void should_not_add_a_duplicate_account() throws Exception {
		//given that an account is added with unique Id
		String uniqueId = "Id-" + System.currentTimeMillis();
		Account account = new Account(uniqueId);
		underTest.createAccount(account);

		try {
			//when account with already existing Id is added
			underTest.createAccount(account);
			//then account addition fails with an exception
			fail("Should have failed when adding duplicate account");
		} catch (DuplicateAccountIdException ex) {
			assertThat(ex.getMessage()).isEqualTo("Account id " + uniqueId + " already exists!");
		}

	}

	@Test
	public void should_transfer_amount_successfully_and_notification_is_sent_to_both_account_users()
			throws InsufficientFundsException, InterruptedException, InvalidAccountException, InvalidTransferAmountException {
          //given that amount is transferred between valid accounts
		//when the transfer is completed successfully
		boolean isTransferSuccessful = underTest.transferAmount("Id-126", "Id-127", new BigDecimal("500"));
		//then verify that amount is credited to receiving account and notification is sent to both accounts
		assertTrue(isTransferSuccessful == true);
		assertTrue(underTest.getAccount("Id-127").getBalance().equals(new BigDecimal("1000")));
		verify(notificationService,times(1)).notifyAboutTransfer(Mockito.any(Account.class),Mockito.eq("Account Id-126 debited with amount 500"));
		verify(notificationService,times(1)).notifyAboutTransfer(Mockito.any(Account.class),Mockito.eq("Account Id-127 credited with amount 500"));
		
		

	}

	@Test
	(expected = InvalidAccountException.class)
	public void shoul_not_transfer_amount_with_null_account_id()
			throws InsufficientFundsException, InterruptedException, InvalidAccountException, InvalidTransferAmountException {
        //give that from accountId is null
		//when amount is transferred from a null accountId
		boolean isTransferSuccesful = underTest.transferAmount(null, "Id-125", new BigDecimal("500"));
		//then transfer should fail
		assertTrue(isTransferSuccesful == false);

	}
	
	@Test
	(expected = InvalidAccountException.class)
	public void should_not_transfer_amout_to_null_account()
			throws InsufficientFundsException, InterruptedException, InvalidAccountException, InvalidTransferAmountException {
        
		 //give that from accountId is null
		//when amount is transferred to a null accountId
		boolean isTransferSuccesful = underTest.transferAmount( "Id-125",null, new BigDecimal("500"));
		//then transfer should fail
		assertTrue(isTransferSuccesful == false);

	}
	
	@Test
	(expected = InvalidAccountException.class)
	public void should_fail_trasfer_with_ivalid_account_details()
			throws InsufficientFundsException, InterruptedException, InvalidAccountException, InvalidTransferAmountException {
        //given that account id does not exist 
		//when amount transfer is initiated from non existing account
		boolean isTransferSuccesful = underTest.transferAmount( "Id-128","Id-125", new BigDecimal("500"));
		//then transfer fails with invalid account exception
		assertTrue(isTransferSuccesful == false);

	}
	
	@Test
	(expected = InvalidAccountException.class)
	public void should_fail_with_invalid_account_to_be_credited()
			throws InsufficientFundsException, InterruptedException, InvalidAccountException, InvalidTransferAmountException {
        
		//given that account id does not exist 
				//when amount transfer is initiated to a non existing account
		boolean isTransferSuccesful = underTest.transferAmount( "Id-125","Id-130", new BigDecimal("500"));
		//then transfer fails with invalid account exception
		assertTrue(isTransferSuccesful == false);

	}
	
	@Test
	(expected = InvalidTransferAmountException.class)
	public void should_fail_with_null_transfer_amount()
			throws InsufficientFundsException, InterruptedException, InvalidAccountException, InvalidTransferAmountException {
		//Given that from and to accounts are valid
		//when amount is negative or zero
		boolean isTransferSuccesful = underTest.transferAmount( "Id-125","Id-124", null);
		//then transfer should fail with invalid amount exception
		assertTrue(isTransferSuccesful == false);

	}

	@Test
	public void should_fail_trasferwith_ifnsufficient_balance()
			throws InsufficientFundsException, InterruptedException, InvalidAccountException, InvalidTransferAmountException {
		try {
			//given that teh acocunts are valid
			//when transfer is initiated and from account has less balance that transfer amount
			underTest.transferAmount("Id-126", "Id-127", new BigDecimal("5000"));
			// then transfer should fail with an exception 
			fail("should fail as amount is greater than balance");
		} catch (InsufficientFundsException ex) {
			assertThat(ex.getMessage()).isEqualTo("Available balance is less that amount to transfer" + 5000);
		}

	}

}
