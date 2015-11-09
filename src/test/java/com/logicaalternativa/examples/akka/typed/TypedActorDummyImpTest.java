/*
 *      TypedActorDummyImpTest.java
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
		
		final Long millisec = 2000L;
		
		
		___WHEN( "It's called a method with a message ('" + message + "') that "
				+ "return a string. Its implementation sleeps for "
				+ "" + millisec  + " milliseconds");
		
		final Long now = System.currentTimeMillis();
		
		String resultEcho = typedActorDummy.echo( millisec, message);
		
		Long time = System.currentTimeMillis() - now;
		
		
		___THEN( "Although the execution is executed by other thread. The flow  "
				+ "waits the response "
				+ "(millisec: "+ millisec +", time: " + time + ")");
		
		assertTrue( millisec <  time );
		
		
		___THEN( "The result should be 'Echo Hello' (" + resultEcho + ")" );
		
		assertEquals( "Echo Hello", resultEcho );
		
	}
	
	@Test
	public void testFutureEcho() throws Exception{
		
		___GIVEN( "A typed actor (TypedActorDummy) is loaded in a System Actor " );	
		
		final String message = "Hello";
		
		final Long millisec = 2000L;
		
		
		___WHEN( "It's called a method with a message ('" + message + "') that "
				+ "return a future. Its implementation sleeps for "
				+ "" + millisec  + " milliseconds");
		
		final Long now = System.currentTimeMillis();
		
		Future<String> futureEcho = typedActorDummy.futureEcho( millisec, message);
		
		Long time = System.currentTimeMillis() - now;
		
		
		___THEN( "The execution flow doesn't wait the future result "
				+ "(millisec: "+ millisec +", time: " + time + ")");
		
		assertTrue( millisec > time );
		
		
		__INFO("... And finally it is checked the future result");
		
		String result = Await.result(futureEcho, Duration.create("300 seconds") );
		
		
		___THEN( "The result of future is 'Echo Hello' (" + result + ")" );
		
		assertEquals( "Echo Hello", result );
		
	}
	
	@Test
	public void testReturnVoidWithSleep() throws Exception{
		
		___GIVEN( "A typed actor (TypedActorDummy) is loaded in a System Actor " );	
		
		final Long millisec = 2000L;
		
		
		___WHEN( "It's called a method that return a void. Its implementation "
				+ "sleeps for " + millisec  + " milliseconds");
		
		final Long now = System.currentTimeMillis();
		
		typedActorDummy.sleep( millisec );
		
		Long time = System.currentTimeMillis() - now;
		
		
		___THEN( "The execution flow doesn't wait the sleep because the method "
				+ "is executed in a another thread "
				+ "(millisec: "+ millisec +", time: " + time + ")");
		
		assertTrue( time < millisec );
		
	}

	
}
