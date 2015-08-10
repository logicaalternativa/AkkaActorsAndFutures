package com.logicaalternativa.examples.akka.notyped05.supervisorstrategy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.concurrent.TimeoutException;

import org.junit.Test;

import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;
import akka.actor.ActorRef;
import akka.actor.DeadLetter;
import akka.actor.Props;
import akka.dispatch.OnComplete;
import akka.pattern.Patterns;

import com.logicaalternativa.examples.akka.ActorNoTypedDummyII;
import com.logicaalternativa.examples.akka.ActorNoTypedLetItCrash;
import com.logicaalternativa.examples.akka.ActorNoTypedLogEvent;
import com.logicaalternativa.examples.akka.testbase.TestBase;

public class ActorLetItCrashTestStop extends TestBase {
	
	@Test	
	public void test() throws Exception {
		
		___GIVEN( "It creates a proxy actor (ActorNoTypedLetItCrash) . It is "
				+ "added with configuration proxied actor. In this case the "
				+ "supervision strategy will be stop if it's crashed the child "
				+ "actor." );
		
		final Props propsChild = Props.create( ActorNoTypedDummyII.class );
		
		final Props propsReadDeadLetters = Props.create
											( 	
												ActorNoTypedLogEvent.class,
												() -> new ActorNoTypedLogEvent<DeadLetter>( DeadLetter.class )
											);
		
		final Props props = Props.create
								( 
									ActorNoTypedLetItCrash.class, 
									() -> new ActorNoTypedLetItCrash( 
																	propsChild,
																	"stop" 
																  ) 
											
								);
		
		final ActorRef actorRef = system.actorOf( props, "let-itcrass" );	
		
		final ActorRef actorDeadLetters = system.actorOf( propsReadDeadLetters, "dead-letters" );
		
		initResultBoolean();
		
		
		__INFO( "**************************************************************" );
		
		___WHEN(" It send a message state");
		
		final Future<Object> future1 = Patterns.ask( actorRef, "state" , 1000 );
		
		future1.onComplete( new OnComplete<Object>(){

			@Override
			public void onComplete(Throwable exception, Object arg1) throws Throwable {
				
				___THEN( "The message recibied must be equal a "
						+ "INI => AROUND_PRE_START => PRE_START "
						+ "(" + arg1 + ")" );
				
				addResultAndValue( exception == null );
				
				addResultAndValue( "INI => AROUND_PRE_START => PRE_START".equals(arg1)  );
				
			}
			
		}, system.dispatcher() );		
		
		
		__INFO( "**************************************************************" );
		
		__INFO( "And after..." );
		
		___WHEN(" It send a message Exception that causes a exception in "
				+ "child actor");
		
		final Exception exceptionSent = new Exception( "Exception test" );
		
		Future<Object> future2 = Patterns.ask( actorRef, exceptionSent ,1000 );
		
		final Object result2 = Await.result( future2, Duration.create( "1 second") );
		
		___THEN( "The menssage has to be 'I'm going to pass away' "
				+ "(" + result2 +") ");
		
		assertEquals( "I'm going to pass away", result2 );
		
		
		__INFO( "**************************************************************" );
		
		__INFO( "And after..." );		
		
		___WHEN(" It send a message 'state' again");
		
		final Future<Object> future3 = Patterns.ask( actorRef, "state" , 3000 );
		
		try {
		
			Await.result( future3, Duration.create( " 1 second") );			
			
			fail( "Ey!!! You don't have to be here!" );
			
		} catch( Exception e ) {
			
			___THEN( "The menssage is loosed The exception must be 'TimeoutException' "
					+ "(" + e.getClass().getSimpleName() + ")");
			
			assertEquals( e.getClass().getSimpleName(), TimeoutException.class.getSimpleName() );			
			
		}
		
		
		__INFO( "**************************************************************" );
		
		__INFO( "And after..." );		
		
		___WHEN(" When It's send a 'lastMessage' to check the dead letters");
		
		final Future<Object> future4 = Patterns.ask( actorDeadLetters, "lastMessage" , 3000 );
		
		future4.onComplete( new OnComplete<Object>(){

			@Override
			public void onComplete(Throwable exception, Object arg1) throws Throwable {
				
				
				final DeadLetter deadLetter = arg1 instanceof DeadLetter 
													?( DeadLetter ) arg1
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
											
				___THEN( "The dead letter message musts be 'state' "
						+ "(" + message + ") and the actor recipient must be"
						+ " 'child' (" + nameActorRef +")" );
				
				addResultAndValue( exception == null );
				
				addResultAndValue( "state".equals( message  )  );
				
				addResultAndValue( "child".equals( nameActorRef  )  );
				
			}
			
		}, system.dispatcher() );
		
		
		
		__INFO( "**************************************************************" );
		
		__INFO( "And after..." );		
		
		___WHEN(" if send a message 'state' again");
		
		final Future<Object> future5 = Patterns.ask( actorRef, "state" , 1000 );
		
		final Object result5 = Await.result( future5, Duration.create( "1 second") );
		
		___THEN( "The message recibied must be equal a 'Actor child passed away' "
				+ "(" + result5 + ") ");
		
		assertEquals( "Actor child passed away", result5 );	
	
		
		__INFO( "**************************************************************" );
		
		__INFO( "It's only for waiting the result of agent" );
		
		Thread.sleep( 1000 );
		
		Boolean resultBoolean = getResultBoolean();
		
		__INFO("... and finally, it's going to check all the futur results (" + resultBoolean + ")");
		
		assertEquals( true, resultBoolean );
		
	}
	
	
}


