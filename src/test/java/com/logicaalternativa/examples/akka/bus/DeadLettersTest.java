package com.logicaalternativa.examples.akka.bus;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.concurrent.TimeoutException;

import org.junit.Test;

import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;
import akka.actor.ActorRef;
import akka.actor.DeadLetter;
import akka.actor.Props;
import akka.pattern.AskTimeoutException;
import akka.pattern.Patterns;

import com.logicaalternativa.examples.akka.ActorNoTypedDummy;
import com.logicaalternativa.examples.akka.ActorNoTypedLogEvent;
import com.logicaalternativa.examples.akka.testbase.TestBase;

public class DeadLettersTest extends TestBase {	
	
	
	@Test	
	public void test() throws Exception {
		
		___GIVEN( "It's created an actor and a reader deadletters" );
		
		final Props props = Props.create( ActorNoTypedDummy.class );		
		
		final ActorRef actorRef = system.actorOf( props, "dummy");		
		
		final Props propsReadDeadLetters = Props.create
											( 	
												ActorNoTypedLogEvent.class,
												() -> new ActorNoTypedLogEvent<DeadLetter>( DeadLetter.class )
											);
		
		final ActorRef actorDeadLetters = system.actorOf( propsReadDeadLetters, "dead-letters" );
		
		___WHEN("[1] It is stopped the actor and then it's sent a message");		
		
		system.stop( actorRef );		
		
		Future<Object> future1 = Patterns.ask( actorRef, "bye" , 5000 );
		
		try {
			
			Await.result( future1, Duration.create( "2 second") );			
			
			fail( "Ey!!! You don't have to be here!" );
			
		} catch( Exception e ) {
			
			___THEN( "[1] The menssage is lost. The exception must be 'TimeoutException' "
					+ "(" + e.getClass().getSimpleName() + ")");
			
			assertTrue( e.getClass().getSimpleName().equals(  TimeoutException.class.getSimpleName() )
						|| e.getClass().getSimpleName().equals(  AskTimeoutException.class.getSimpleName() )
						);			
			
		}
		
		__INFO( "And after..." );		
		
		___WHEN("[2] When It's send a 'lastMessage' to check the dead letters");
		
		final Future<Object> future2 = Patterns.ask( actorDeadLetters, "lastMessage" , 3000 );
		
		Object result2 = Await.result( future2, Duration.create( "1 second") );		
		

		final DeadLetter deadLetter = result2 instanceof DeadLetter 
											?( DeadLetter ) result2
													: null;
		
		final String message = deadLetter != null && deadLetter.message() != null && deadLetter.message() instanceof String
									? ( String )deadLetter.message() 
											: null;
									
		final ActorRef recipient = deadLetter != null
									? deadLetter.recipient()
											: null;
									
		final String nameActorRef = recipient != null 
										? recipient.path().name()
												: null;
									
		___THEN( "[2] The dead letter message must be 'bye' "
				+ "(" + message + ") and the actor recipient must be"
				+ " 'dummy' (" + nameActorRef +")" );
		
		assertEquals("bye", message);
		
		assertEquals("dummy", nameActorRef);
		
		
	}

}
