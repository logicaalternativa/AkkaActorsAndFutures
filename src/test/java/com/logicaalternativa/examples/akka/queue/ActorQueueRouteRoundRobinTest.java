/*
 *      ActorQueueRouteRoundRobinTest.java
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

package com.logicaalternativa.examples.akka.queue;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import org.junit.Test;

import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.agent.Agent;
import akka.dispatch.Mapper;
import akka.dispatch.OnComplete;
import akka.pattern.Patterns;
import akka.routing.RoundRobinPool;

import com.logicaalternativa.examples.akka.ActorNoTypedWhoIam;
import com.logicaalternativa.examples.akka.testbase.TestBase;

public class ActorQueueRouteRoundRobinTest extends TestBase {

	@Test	
	public void test() throws Exception {
		
		int numberMessages = 10;
		
		int numberActors = 5;
		
		int messageForActor = numberMessages / numberActors;
		
		long now = System.currentTimeMillis();
		
		
		___GIVEN( "It's created an router actor with " + numberActors + " rotee" );
		
		final Props routeeProps = Props.create( ActorNoTypedWhoIam.class );
		
		RoundRobinPool roundRobinPool = new RoundRobinPool( numberActors );
		
		final Props propRouter = roundRobinPool.props( routeeProps );
		
		final ActorRef actorRef = system.actorOf( propRouter, "router");
		
		
		___WHEN(" It is sent a " + numberMessages +  " messages ");
		
		Agent<Map<String, List<String>>> resultRequest = Agent.create( new LinkedHashMap<String, List<String>>(), system.dispatcher() );
		
		initResultBoolean();	
	
		for ( int i = 0; i < numberMessages; i++ ) {
			
			final String orderSent = "Message " + i;
			
			Future<Object> future = Patterns.ask( actorRef, orderSent , 5000 );
			
			__INFO(" It was sent message: " + i );
			
			future.onComplete( new OnComplete<Object>() {

				@SuppressWarnings("unchecked")
				@Override
				public void onComplete(Throwable arg0, final Object messageResponse )
						throws Throwable {
					
					
					final Map<String, String> response = ( Map<String, String> ) messageResponse;
					
					loadValueAgentRequest( resultRequest, response );
					
					___THEN( "The message received must be equal to message sent (" + messageResponse + ", " + orderSent + ")" );
					
					addResultAndValue( orderSent.equals( response.get( "message" ) ) );
					
				}			
				
			}, system.dispatcher() );			
			
		    
		}	
		
		__INFO("Only for waiting all result");
		
		Thread.sleep( ( System.currentTimeMillis() - now ) * 2 );
		
		
		Boolean resultBoolean = getResultBoolean();
				
		__INFO("... and then, it is going to check all the futur result (" + resultBoolean + ")");
		
		assertEquals( true, resultBoolean );
		
			
		__INFO("... and finally, it is checked the values of the list");
		
		Map<String, List<String>> mapRequest = Await.result( resultRequest.future(), Duration.create( "300 sec" ) );
		
		
		___THEN("Actor number (" + numberActors + ") have to be the same "
				+ "that the count of keys that each has (" + messageForActor + ") messages ");
		
		Stream<Integer> filter = getFilterMessageForActor( messageForActor, mapRequest );
		
		assertEquals( numberActors, filter.count() );
		
		
	}
	
	/**
	 * Request actor data filter. It returns and stream with all  map keys that
	 * every one has the same number that messages sent to an actor
	 * @param messageForActor
	 * @param mapRequest
	 * @return
	 */
	protected Stream<Integer> getFilterMessageForActor(int messageForActor, Map<String, List<String>> mapRequest) {
		
		__INFO("This is information of messages sent to the actors");
		
		return mapRequest.keySet().stream()
					.peek( p -> loggerActor.info( "\n      TRACE: " + p + " >> " + mapRequest.get( p ) ) )
					.map( p -> mapRequest.get(p).size() )
					.filter( p -> p == messageForActor );
	}

	/**
	 * Auxiliary method
	 * @param resultRequest
	 * @param response
	 */
	protected void loadValueAgentRequest(final Agent<Map<String, List<String>>> resultRequest, final Map<String, String> response) {
		
		resultRequest.send( new Mapper<Map<String, List<String>>, Map<String, List<String>>>() {
			
			 public  Map<String, List<String>> apply( Map<String, List<String>> valAgent) {
				 
				 final Optional<Map<String, String>> opResponse = Optional
						 											.ofNullable( response );
				 
				 Optional<String> key = opResponse
										 .map( r -> r.get("name") )
										 ;
				 
				if( ! key.isPresent()
						|| valAgent == null ){
					
					return valAgent;
				} 
				 
				final List<String> list = key
										  .map( r -> valAgent.get( r ) )
										  .orElse( new ArrayList<String>() );
						
				final String message = opResponse
										 .map( r -> r.get("message") )
										 .orElse( null );
				list.add( message );
				valAgent.put( key.get(), list );
				
				return new LinkedHashMap<String, List<String>>( valAgent );							 
				 
			 }
			 
		});
		
	}
	
}
