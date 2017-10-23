package com.abc.interest;

import org.junit.Test;

import com.abc.Account;
import com.abc.DateProvider;
import com.abc.Transaction;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertSame;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class InterestSchemeTest {

	@Test
	public void factoryTestChecking() {
		assertTrue(InterestSchemeFactory.getScheme(Account.AccountType.CHECKING) instanceof CheckingAccountInterest);
	}
	
	@Test
	public void factoryTestSaving() {
		assertTrue(InterestSchemeFactory.getScheme(Account.AccountType.SAVINGS) instanceof SavingsAccountInterest);
	}
	
	@Test
	public void factoryTestMaxi() {
		assertTrue(InterestSchemeFactory.getScheme(Account.AccountType.MAXI_SAVINGS) instanceof MaxiSavingsAccountInterest);
	}
	
	@Test
	public void InterestCheckingSinglton() {
		CheckingAccountInterest i = CheckingAccountInterest.getInstance();
		assertSame(i, CheckingAccountInterest.getInstance());
	}
	
	@Test
	public void InterestChecking() {
		ArrayList<Transaction> a = new ArrayList<>();
		a.add(new Transaction(8888));
		a.add(new Transaction(18));
		a.add(new Transaction(245));
		
		assertTrue(CheckingAccountInterest.getInstance().getInterest(a)
				.compareTo(new BigDecimal("9.151")) == 0);
	}
	
	@Test
	public void InterestSavingsSingleton() {
		SavingsAccountInterest i = SavingsAccountInterest.getInstance();
		assertSame(i, SavingsAccountInterest.getInstance());
	}
	
	@Test
	public void InterestSavings() {
		ArrayList<Transaction> a = new ArrayList<>();
		a.add(new Transaction(33));
		a.add(new Transaction(222));
		a.add(new Transaction(465));
		
		assertTrue(SavingsAccountInterest.getInstance().getInterest(a)
				.compareTo(new BigDecimal("0.72")) == 0);
	}
	
	@Test
	public void InterestSavingsThreshold() {
		ArrayList<Transaction> a = new ArrayList<>();
		a.add(new Transaction(2587));
		a.add(new Transaction(450));
		a.add(new Transaction(1000));
		
		assertTrue(SavingsAccountInterest.getInstance().getInterest(a)
				.compareTo(new BigDecimal("7.074")) == 0);
	}
	
	@Test
	public void InterestMaxiSavingsSingleton() {
		MaxiSavingsAccountInterest i = MaxiSavingsAccountInterest.getInstance();
		assertSame(i, MaxiSavingsAccountInterest.getInstance());
	}
	
	@Test
	public void InterestMaxiSavings() {
		ArrayList<Transaction> a = new ArrayList<>();
		a.add(new Transaction(89));
		a.add(new Transaction(346));
		a.add(new Transaction(410));
		
		assertTrue(MaxiSavingsAccountInterest.getInstance().getInterest(a)
				.compareTo(new BigDecimal("16.9")) == 0);
	}
	
	@Test
	public void InterestMaxiSavingsThreshold1() {
		ArrayList<Transaction> a = new ArrayList<>();
		a.add(new Transaction(56));
		a.add(new Transaction(584));
		a.add(new Transaction(799));
		
		assertTrue(MaxiSavingsAccountInterest.getInstance().getInterest(a)
				.compareTo(new BigDecimal("41.95")) == 0);
	}
	
	@Test
	public void InterestMaxiSavingsThreshold2() {
		ArrayList<Transaction> a = new ArrayList<>();
		a.add(new Transaction(1257));
		a.add(new Transaction(999));
		a.add(new Transaction(15879));
		
		assertTrue(MaxiSavingsAccountInterest.getInstance().getInterest(a)
				.compareTo(new BigDecimal("1683.5")) == 0);
	}
	
	@Test
	public void InterestMaxiSavingsPenalty() {
		ArrayList<Transaction> a = new ArrayList<>();
		a.add(new Transaction(89));
		a.add(new Transaction(346));
		a.add(new Transaction(410));
		a.add(new Transaction(-10));
		
		assertTrue(MaxiSavingsAccountInterest.getInstance().getInterest(a)
				.compareTo(new BigDecimal("0.835")) == 0);
	}
	
	@Test
	public void InterestMaxiSavingsPenaltyPast() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		ArrayList<Transaction> a = new ArrayList<>();
		a.add(new Transaction(89));
		a.add(new Transaction(346));
		a.add(new Transaction(4100));
		Transaction t = new Transaction(-10);
		
		//Using Reflection to edit the date which should usually be uneditable
		Field f = t.getClass().getDeclaredField("TRANSACTION_DATE");
		long time = DateProvider.getInstance().now().getTime() - 
				TimeUnit.DAYS.toMillis(11);
		Date d = new Date(time);
		f.setAccessible(true);
		f.set(t, d);
		
		a.add(t);
		
		assertTrue(MaxiSavingsAccountInterest.getInstance().getInterest(a)
				.compareTo(new BigDecimal("322.5")) == 0);
	}
}