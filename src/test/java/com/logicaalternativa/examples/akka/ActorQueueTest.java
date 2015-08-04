package com.logicaalternativa.examples.akka;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.dispatch.OnComplete;
import akka.pattern.Patterns;

import com.logicaalternativa.examples.akka.testbase.TestBase;

public class ActorQueueTest extends TestBase {	
	
	@Test	
	public void test() throws Exception {
		
		final Long sleep = 200L;
		
		int numberMessages = 10;
		
		___GIVEN( "It's created an actor that returs the same message or It "
				+ "sleeps for a time" );
		
		final Props props = Props.create( ActorNoTypedDummy.class );
		
		final ActorRef actorRef = system.actorOf( props, "proxied");
				
		
		___WHEN(" It is sent an 'sleep' 200 and then it is sent a 10 messages ");		 
				
		Patterns.ask( actorRef, sleep, 5000 );
		
		__INFO(" It was sent sleep: " + sleep );	
		
		initResultBoolean();		
		
		for ( int i=0; i <= numberMessages; i++ ) {
			
			final String messageSent = "Hello " + i;
			
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
		
		__INFO("Only for waiting all result");
		
		Future<Object> future = Patterns.ask( actorRef, "bye" , 5000 );
		
		final Object res = Await.result( future, Duration.create( "1 second") );
		
		assertEquals( "bye", res );
		
		__INFO("... and finally, it is going to check all the futur result");
		
		assertEquals( true, getResultBoolean() );
		
	}

}
