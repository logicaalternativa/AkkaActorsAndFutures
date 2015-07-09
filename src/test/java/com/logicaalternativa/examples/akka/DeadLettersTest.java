package com.logicaalternativa.examples.akka;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.concurrent.TimeoutException;

import org.junit.Test;

import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.Terminated;
import akka.dispatch.OnComplete;
import akka.dispatch.sysmsg.Terminate;
import akka.pattern.Patterns;
import akka.routing.NoRouter;
import akka.routing.RouterConfig;

import com.logicaalternativa.examples.akka.testbase.TestBase;

public class DeadLettersTest extends TestBase {	
	
	
	@Test	
	public void test() throws Exception {
		
		___GIVEN( "It's created an actor and a reader deadletters" );
		
		final Props props = Props.create( ActorNoTypedDummyII.class );
		
		
		final ActorRef actorRef = system.actorOf( props, "dummy");
				
		
		___WHEN(" It is sent an terminate message");		

		
		system.stop( actorRef );		
		
		Future<Object> future = Patterns.ask( actorRef, "bye" , 5000 );
		
		final Object res = Await.result( future, Duration.create( "1 second") );
		
		try {
			
			Await.result( future, Duration.create( " 1 second") );			
			
			fail( "Ey!!! You don't have to be here!" );
			
		} catch( Exception e ) {
			
			___THEN( "The menssage is loosed The exception must be 'TimeoutException' "
					+ "(" + e.getClass().getSimpleName() + ")");
			
			assertEquals( e.getClass().getSimpleName(), TimeoutException.class.getSimpleName() );			
			
		}	
		
		
	}

}
