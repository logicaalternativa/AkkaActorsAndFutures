package com.logicaalternativa.examples.akka.queue;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import scala.concurrent.Future;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.agent.Agent;
import akka.dispatch.OnComplete;
import akka.pattern.Patterns;

import com.logicaalternativa.examples.akka.ActorNoTypedDummy;
import com.logicaalternativa.examples.akka.testbase.TestBase;

public class ActorQueueTest extends TestBase {	
	
	@Test	
	public void test() throws Exception {
		
		final Long sleep = 1000L;
		
		int numberMessages = 10;
		
		long now = System.currentTimeMillis();
		
		___GIVEN( "It's created an actor that returs the same message or It "
				+ "sleeps for a time" );
		
		Agent<String> orderResult = Agent.create("", system.dispatcher() );
		
		final Props props = Props.create( ActorNoTypedDummy.class, orderResult );
		
		ActorRef actorRef = null;
		
		actorRef = system.actorOf( props, "proxied");
		
		
		___WHEN(" It is sent an 'sleep' " + sleep + " and then it is sent a 10 messages ");		 
				
		Patterns.ask( actorRef, sleep, 5000 );
		
		__INFO(" It was sent sleep: " + sleep );
		
		String orderMessage = "";
		
		initResultBoolean();	
		
		for ( int i=0; i <= numberMessages; i++ ) {
			
			final String messageSent = "Hello " + i;
			
			orderMessage = orderMessage.concat(" ").concat( messageSent ).trim();
			
			Future<Object> future = Patterns.ask( actorRef, messageSent , 5000 );
			
			__INFO(" It was sent message: " + i );
			
			future.onComplete(new OnComplete<Object>() {

				@Override
				public void onComplete(Throwable arg0, Object messageResponse )
						throws Throwable {
					
					___THEN( "The menssage recibied must be equal to message sent (" + messageResponse + ", " + messageSent + ")" );
					
					 addResultAndValue( messageSent.equals( messageResponse ) );
					
				}
				
			}, system.dispatcher() );
			
		}	
		
		
		__INFO("Only for waiting all agent results ");
		
		Thread.sleep( sleep + (System.currentTimeMillis() - now ) * 10  );
		
		final String orderResultString = orderResult.get();
		
		___THEN( "The sent message order and received message order have to be "
				+ "the same ('" + orderMessage + "', '"  + orderResultString +"')" );
		
		assertEquals(orderMessage, orderResultString );
		
		
		__INFO("... and finally, it is going to check all the futur result");
		
		assertEquals( true, getResultBoolean() );
		
	}	

}
