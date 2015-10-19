/*
 *      AppConfiguration.java
 *      
 *      Copyright 2015 Miguel Rafael Esteban Martín (www.logicaalternativa.com) <miguel.esteban@logicaalternativa.com>
 *      
 *      This program is free software; you can redistribute it and/or modify
 *      it under the terms of the GNU General Public License as published by
 *      the Free Software Foundation; either version 2 of the License, or
 *      (at your option) any later version.
 *      
 *      This program is distributed in the hope that it will be useful,
 *      but WITHOUT ANY WARRANTY; without even the implied warranty of
 *      MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *      GNU General Public License for more details.
 *      
 *      You should have received a copy of the GNU General Public License
 *      along with this program; if not, write to the Free Software
 *      Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 *      MA 02110-1301, USA.
 */

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
			
			___WHEN( "It's called a method that return void and it's thrown "
					+ "an exception" );
			
			typedActorDummy.testRuntimeExceptionVoid();
			
			___THEN( "The exception is not caught because other thread "
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
			
			___WHEN( "It's called a method that return String and it's thrown "
					+ "an exception" );
			
			typedActorDummy.testRuntimeExceptionString();
						
			fail("You wouldn't have to be here!!! ");			
			
		} catch (Exception e) {
			
			___THEN( "The exception is caught because the result of the "
					+ "execution in the other thread is waited. The thread is "
					+ "blocked");
			
			assertTrue( true );
			
		}
		
	}

	@Test
	public void testRuntimeExceptionFuture() {
		
		___GIVEN( "A typed actor (TypedActorDummy) is loaded in a System Actor " );
		
		___WHEN( "It's called a method that return String and it's thrown "
					+ "an exception" );
			
		Future<String> resultfuture = typedActorDummy.testRuntimeExceptionFuture();
		
		__INFO("... And finally it is checked the future result");
		
		try {
		
			Await.result( resultfuture, Duration.create("3 seconds") );	
			
			fail("You wouldn't have to be here!!! ");			
			
		} catch (Exception e) {
			
			___THEN( "The exception is caught because the future execution in "
					+ "the other thread is thrown the exception. "
					+ "The caught exception is the same type that execution "
					+ "is thrown (RuntimeException) ");
			
			assertEquals("RuntimeException", e.getClass().getSimpleName() );			
			
		}
		
	}
	
	@SuppressWarnings("rawtypes")
	@Test
	public void testRuntimeExceptionFuture2() throws Exception {
		
		___GIVEN( "A typed actor (TypedActorDummy) is loaded in a System Actor " );
		
		___WHEN( "It's called a method that return String and it's thrown "
					+ "an exception" );
			
		Future<String> resultfuture = typedActorDummy.testRuntimeExceptionFuture();
		
		initResultBoolean();
		
		resultfuture.onComplete(new OnComplete(){

			@Override
			public void onComplete(Throwable arg0, Object arg1)
					throws Throwable {
				
				___THEN( "The Throwable is caught because the future execution in "
						+ "the other thread is thrown the exception. "
						+ "The caught exception is the same type that execution "
						+ "is thrown (RuntimeException) ");
				
				addResultAndValue( "RuntimeException".equals( arg0.getClass().getSimpleName() ) );
				
			}
			
		}, system.dispatcher() );
		
		__INFO( "It's only for waiting the result of agent" );
		
		Thread.sleep( 200 );
		
		__INFO("... And finally it is checked the future result");
		
		assertTrue( getResultBoolean() );
		
		
	}
	
	@Test
	public void testfutureEcho() throws Exception{
		
		___GIVEN( "A typed actor (TypedActorDummy) is loaded in a System Actor " );
		
		
		___WHEN( "It's called a method that return and future" );
		
		final String message = "Hello";
		
		final Long millisec = 2000L;
		
		final Long now = System.currentTimeMillis();
		
		Future<String> futureEcho = typedActorDummy.futureEcho( millisec, message);
		
		Long time = System.currentTimeMillis() - now;
		
		
		___THEN( "The execution flow don't wait the future result "
				+ "(millisec: "+ millisec +", time: " + time + " )");
		
		assertTrue( millisec >  time );
		
		
		__INFO("... And finally it is checked the future result");
		
		String result = Await.result(futureEcho, Duration.create("3 seconds") );
		
		
		___THEN( "The result of future is 'Echo Hello' (" + result + ")" );
		
		assertEquals( "Echo Hello", result );
		
	}
	
}
