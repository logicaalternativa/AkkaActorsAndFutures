package com.logicaalternativa.examples.akka.typed.imp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;

import com.logicaalternativa.examples.akka.testbase.TestBaseTypedActor;

public class TypedActorDummyImpTest extends TestBaseTypedActor {
	

	
	@Test
	public void testEcho() throws Exception{
		
		___GIVEN( "A typed actor (TypedActorDummy) is loaded in a System Actor " );	
		
		final String message = "Hello";
		
		final Long miliSec = 2000L;
		
		
		___WHEN( "It's called a method with a message ('" + message + "') that "
				+ "return a string. Its implementation sleeps for "
				+ "" + miliSec  + " miliseconds");
		
		final Long now = System.currentTimeMillis();
		
		String resultEcho = typedActorDummy.echo( miliSec, message);
		
		Long time = System.currentTimeMillis() - now;
		
		
		___THEN( "Although the execution is executed by other thread. The flow  "
				+ "waits the future result "
				+ "(miliSec: "+ miliSec +", time: " + time + " )");
		
		assertTrue( miliSec <  time );
		
		
		___THEN( "The result of future is 'Echo Hello' (" + resultEcho + ")" );
		
		assertEquals( "Echo Hello", resultEcho );
		
	}
	
	@Test
	public void testFutureEcho() throws Exception{
		
		___GIVEN( "A typed actor (TypedActorDummy) is loaded in a System Actor " );	
		
		final String message = "Hello";
		
		final Long miliSec = 2000L;
		
		
		___WHEN( "It's called a method with a message ('" + message + "') that "
				+ "return a future. Its implementation sleeps for "
				+ "" + miliSec  + " miliseconds");
		
		final Long now = System.currentTimeMillis();
		
		Future<String> futureEcho = typedActorDummy.futureEcho( miliSec, message);
		
		Long time = System.currentTimeMillis() - now;
		
		
		___THEN( "The execution flow doesn't wait the future result "
				+ "(miliSec: "+ miliSec +", time: " + time + " )");
		
		assertTrue( miliSec >  time );
		
		
		__INFO("... And finally it is checked the future result");
		
		String result = Await.result(futureEcho, Duration.create("3 seconds") );
		
		
		___THEN( "The result of future is 'Echo Hello' (" + result + ")" );
		
		assertEquals( "Echo Hello", result );
		
	}
	
	@Test
	public void testSleep() throws Exception{
		
		___GIVEN( "A typed actor (TypedActorDummy) is loaded in a System Actor " );	
		
		final Long miliSec = 2000L;
		
		
		___WHEN( "It's called a method that return a void. Its implementation "
				+ "sleeps for " + miliSec  + " miliseconds");
		
		final Long now = System.currentTimeMillis();
		
		typedActorDummy.sleep( miliSec );
		
		Long time = System.currentTimeMillis() - now;
		
		
		___THEN( "The execution flow doesn't wait the sleep because the method "
				+ "is executed in a another thread "
				+ "(miliSec: "+ miliSec +", time: " + time + " )");
		
		assertTrue( miliSec >  time );
		
	}

	
}
