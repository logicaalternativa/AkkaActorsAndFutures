package com.logicaalternativa.examples.akka.supervisorstrategy;

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
import akka.agent.Agent;
import akka.dispatch.ExecutionContexts;
import akka.dispatch.OnComplete;
import akka.pattern.Patterns;

import com.logicaalternativa.examples.akka.ActorNoTypedDummyCheckLifeCycle;
import com.logicaalternativa.examples.akka.ActorNoTypedLetItCrash;
import com.logicaalternativa.examples.akka.ActorNoTypedLogEvent;
import com.logicaalternativa.examples.akka.testbase.TestBase;

public class ActorLetItCrashTestStop extends TestBase {
	
	@Test	
	public void test() throws Exception {
		
		___GIVEN( "It creates a proxy actor (ActorNoTypedLetItCrash) . It is "
				+ "added with configuration proxied actor. In this case the "
				+ "supervision strategy will be stop the child actor if it's "
				+ "crashed." );
		
		final Agent<String> logAllCycleLife = Agent.create( "", ExecutionContexts.global() );
		
		final Props propsChild = Props.create( ActorNoTypedDummyCheckLifeCycle.class
				, () -> new ActorNoTypedDummyCheckLifeCycle( logAllCycleLife )  );
		
		
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
		
		
		___WHEN("[1] It send a message state");
		
		final Future<Object> future1 = Patterns.ask( actorRef, "state" , 1000 );
		
		future1.onComplete( new OnComplete<Object>(){

			@Override
			public void onComplete(Throwable exception, Object arg1) throws Throwable {
				
				___THEN( "[1] The message received must be equal to:\n "
						+ "INI => AROUND_PRE_START => PRE_START\n"
						+ "(" + arg1 + ")" );
				
				addResultAndValue( exception == null );
				
				addResultAndValue( "CONSTRUCTOR => AROUND_PRE_START => PRE_START".equals(arg1)  );
				
			}
			
		}, system.dispatcher() );		
		
		
		__INFO( "And after..." );
		
		___WHEN(" [2] It send a message Exception that causes a exception in "
				+ "child actor");
		
		final Exception exceptionSent = new Exception( "Exception test" );
		
		Future<Object> future2 = Patterns.ask( actorRef, exceptionSent ,1000 );
		
		final Object result2 = Await.result( future2, Duration.create( "1 second") );
		
		___THEN( "[2] The menssage has to be 'I'm going to pass away'\n "
				+ "(" + result2 +") ");
		
		assertEquals( "I'm going to pass away", result2 );
		
		
		__INFO( "And after..." );		
		
		___WHEN("[3] It send a message 'state' again");
		
		final Future<Object> future3 = Patterns.ask( actorRef, "state" , 3000 );
		
		try {
		
			Await.result( future3, Duration.create( " 1 second") );			
			
			fail( "Ey!!! You don't have to be here!" );
			
		} catch( Exception e ) {
			
			___THEN( " [3] The menssage is lost. The exception must be 'TimeoutException'\n "
					+ "(" + e.getClass().getSimpleName() + ")");
			
			assertEquals( e.getClass().getSimpleName(), TimeoutException.class.getSimpleName() );			
			
		}
		
		___WHEN("[4] It's checked all child actor cycle life. In this case, "
				+ "killing the actor it's not necessary");
		
		final String resAllStateCycleLife = Await.result(logAllCycleLife.future(), Duration.create( "300 sec" ) );
				
		___THEN( "[4] When the exception is throwed, the child actor is not "
				+ "stoped and i's is not restarted. The cycle life has to be:\n"
				+ "CONSTRUCTOR" 
					+ " >> AROUND_PRE_START"
					 + " >> PRE_START"
					  + " >> [[EXCEPTION]]"
					   + " >> AROUND_POST_STOP"
					    + " >> POST_STOP \n(" + resAllStateCycleLife + ")"  );
		
		assertEquals( "CONSTRUCTOR"
					+ " >> AROUND_PRE_START"
					 + " >> PRE_START"
					  + " >> [[EXCEPTION]]"
					   + " >> AROUND_POST_STOP"
					    + " >> POST_STOP", resAllStateCycleLife );
		
		__INFO( "And after..." );		
		
		___WHEN("[5] When It's send a 'lastMessage' to check the dead letters");
		
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
											
				___THEN( "[5] The dead letter message musts be 'state' "
						+ "(" + message + ") and the actor recipient must be"
						+ " 'child' (" + nameActorRef +")" );
				
				addResultAndValue( exception == null );
				
				addResultAndValue( "state".equals( message  )  );
				
				addResultAndValue( "child".equals( nameActorRef  )  );
				
			}
			
		}, system.dispatcher() );
		
		__INFO( "And after..." );		
		
		___WHEN("[6] if send a message 'state' again");
		
		final Future<Object> future5 = Patterns.ask( actorRef, "state" , 1000 );
		
		final Object result5 = Await.result( future5, Duration.create( "1 second") );
		
		___THEN( "[6] The message received must be equal a 'Actor child passed "
				+ "away'\n"
				+ "(" + result5 + ") ");
		
		assertEquals( "Actor child passed away", result5 );		
		
		
		Boolean resultBoolean = getResultBoolean();
		
		__INFO("... and finally, it's going to check all the futur results (" + resultBoolean + ")");
		
		assertEquals( true, resultBoolean );
		
	}
	
	
}


