package com.logicaalternativa.examples.akka.typed;

import static org.junit.Assert.*;

import org.junit.Test;

import com.logicaalternativa.examples.akka.testbase.TestBaseTypedActor;

import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;
import akka.dispatch.OnComplete;

@SuppressWarnings("unchecked")
public class TypedActorDummyImpExceptionsTest extends TestBaseTypedActor {
	
	@Test
	public void testRuntimeExceptionVoid() {
		
		___GIVEN( "A typed actor (TypedActorDummy) is loaded in a System Actor " );
		
		try {
			
			___WHEN( "It's called a method that return void and it's throwed "
					+ "an exception" );
			
			typedActorDummy.testRuntimeExceptionVoid();
			
			___THEN( "The exception is not catched because other thread "
					+ "executed the implementation (FIRE & FORGET) "
					+ "[It's like a 'tell']");
			
			assertTrue( true);
			
		} catch (Exception e) {
			
			fail("You wouldn't have to be here!!! ");
		}
		
	}

	@Test
	public void testRuntimeExceptionString() {
		
		___GIVEN( "A typed actor (TypedActorDummy) is loaded in a System Actor " );
		
		try {
			
			___WHEN( "It's called a method that return String and it's throwed "
					+ "an exception" );
			
			typedActorDummy.testRuntimeExceptionString();
						
			fail("You wouldn't have to be here!!! ");			
			
		} catch (Exception e) {
			
			___THEN( "The exception is catched because the result of the "
					+ "execution in the other thread is waited. The thread is "
					+ "blocked");
			
			assertTrue( true );
			
		}
		
	}


	@Test
	public void testRuntimeExceptionFuture() {
		
		___GIVEN( "A typed actor (TypedActorDummy) is loaded in a System Actor " );
		
		___WHEN( "It's called a method that return String and it's throwed "
					+ "an exception" );
			
		Future<String> resultFuture = typedActorDummy.testRuntimeExceptionFuture();
		
		__INFO("... And finally it is checked the future result");
		
		try {
		
			Await.result( resultFuture, Duration.create("3 seconds") );	
			
			fail("You wouldn't have to be here!!! ");			
			
		} catch (Exception e) {
			
			___THEN( "The exception is catched because the futur execution in "
					+ "the other thread is throwed the exception. "
					+ "The catched exception is the same type that execution "
					+ "is throwed (RuntimeException) ");
			
			assertEquals("RuntimeException", e.getClass().getSimpleName() );			
			
		}
		
	}
	
	
	@SuppressWarnings("rawtypes")
	@Test
	public void testRuntimeExceptionFuture2() throws Exception {
		
		___GIVEN( "A typed actor (TypedActorDummy) is loaded in a System Actor " );
		
		___WHEN( "It's called a method that return String and it's throwed "
					+ "an exception" );
			
		Future<String> resultFuture = typedActorDummy.testRuntimeExceptionFuture();
		
		initResultBoolean();
		
		resultFuture.onComplete(new OnComplete(){

			@Override
			public void onComplete(Throwable arg0, Object arg1)
					throws Throwable {
				
				___THEN( "The Throwable is catched because the futur execution in "
						+ "the other thread is throwed the exception. "
						+ "The catched exception is the same type that execution "
						+ "is throwed (RuntimeException) ");
				
				addResultAndValue( "RuntimeException".equals( arg0.getClass().getSimpleName() ) );
				
			}
			
		}, system.dispatcher() );
		
		__INFO( "It's only for waiting the result of agent" );
		
		Thread.sleep( 200 );
		
		__INFO("... And finally it is checked the future result");
		
		assertTrue( getResultBoolean() );
		
		
	}
	
	
	
	@Test
	public void testFutureEcho() throws Exception{
		
		___GIVEN( "A typed actor (TypedActorDummy) is loaded in a System Actor " );
		
		
		___WHEN( "It's called a method that return and future" );
		
		final String message = "Hello";
		
		final Long miliSec = 2000L;
		
		final Long now = System.currentTimeMillis();
		
		Future<String> futureEcho = typedActorDummy.futureEcho( miliSec, message);
		
		Long time = System.currentTimeMillis() - now;
		
		
		___THEN( "The execution flow don't wait the future result "
				+ "(miliSec: "+ miliSec +", time: " + time + " )");
		
		assertTrue( miliSec >  time );
		
		
		__INFO("... And finally it is checked the future result");
		
		String result = Await.result(futureEcho, Duration.create("3 seconds") );
		
		
		___THEN( "The result of future is 'Echo Hello' (" + result + ")" );
		
		assertEquals( "Echo Hello", result );
		
	}

	
}
