/*
 *      PublishSimpleSuscribeTest.java
 *      
 *      Copyright 2016 Miguel Rafael Esteban Martín (www.logicaalternativa.com) <miguel.esteban@logicaalternativa.com>
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

package com.logicaalternativa.examples.akka.bus;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import scala.concurrent.Future;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.dispatch.OnComplete;
import akka.pattern.Patterns;

import com.logicaalternativa.examples.akka.ActorNoTypedLogEvent;
import com.logicaalternativa.examples.akka.testbase.TestBase;

public class PublishSimpleSuscribeTest extends TestBase {
	
	@Test	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void test() throws Exception {
		
		final Integer messageInteger = 1;
		
		long now = System.currentTimeMillis();
		
		___GIVEN( "It's created two sucriber actors that the general event"
				+ "bus of akka " );
		
		final Props propsSuscriber = Props.create
				( 	
					ActorNoTypedLogEvent.class,
					() -> new ActorNoTypedLogEvent<Integer>( Integer.class )
				);
		
		ActorRef actorSuscriber1 = system.actorOf( propsSuscriber, "suscriber1" );
		ActorRef actorSuscriber2 = system.actorOf( propsSuscriber, "suscriber2" );
		
		___WHEN( "It's published a message into event bus" );

		system.eventStream().publish( messageInteger );
		
		___THEN( "The even is read by the susbriber actors" );
		
		initResultBoolean();		
			
		Future future1 = Patterns.ask( actorSuscriber1, "lastMessage" , 5000 );
		Future future2 = Patterns.ask( actorSuscriber2, "lastMessage" , 5000 );
		
		future1.onComplete( new OnCompleteSimpeSusbriber( messageInteger ), system.dispatcher() );
		future2.onComplete( new OnCompleteSimpeSusbriber( messageInteger ), system.dispatcher() );
		
		__INFO("Only for waiting all agent results ");
		
		Thread.sleep( ( System.currentTimeMillis() - now ) * 12  );
		
		
		__INFO("... and finally, it is going to check all the futur result");
		
		assertEquals( true, getResultBoolean() );
		
	}
	
	
	/**
	 * Private class to check the result of future
	 * @author miguel
	 *
	 */
	private class OnCompleteSimpeSusbriber extends OnComplete<Integer> {
		
		private Integer mssg;
		
		public OnCompleteSimpeSusbriber( Integer mssg ) {
			
			this.mssg = mssg;
			
		}

		@Override
		public void onComplete(Throwable arg0, Integer arg1) throws Throwable {
			
			__INFO( "Message received has to be the same (" + mssg + ", " + arg1 + ")" );
			
			addResultAndValue( mssg.equals( arg1 ) );
			
		}
		
		
		
	}

		
}


