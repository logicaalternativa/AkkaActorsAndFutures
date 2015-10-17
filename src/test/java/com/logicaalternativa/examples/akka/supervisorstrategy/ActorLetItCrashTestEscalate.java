/*
 *      ActorLetItCrashTestEscalate.java
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

package com.logicaalternativa.examples.akka.supervisorstrategy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.concurrent.TimeoutException;

import org.junit.Assert;
import org.junit.Test;

import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;
import akka.actor.ActorRef;
import akka.actor.DeadLetter;
import akka.actor.PoisonPill;
import akka.actor.Props;
import akka.agent.Agent;
import akka.dispatch.ExecutionContexts;
import akka.dispatch.OnComplete;
import akka.pattern.AskTimeoutException;
import akka.pattern.Patterns;

import com.logicaalternativa.examples.akka.ActorNoTypedDummyCheckLifeCycle;
import com.logicaalternativa.examples.akka.ActorNoTypedLetItCrash;
import com.logicaalternativa.examples.akka.ActorNoTypedLogEvent;
import com.logicaalternativa.examples.akka.testbase.TestBase;

public class ActorLetItCrashTestEscalate extends TestBase {
	
	@Test	
	public void test() throws Exception {
		
		___GIVEN( "It creates a proxy actor (ActorNoTypedLetItCrash). It is "
				+ "added with configuration proxied actor. In this case the "
				+ "supervision strategy is 'escalate'. If child actor is "
				+ "crashed, it  will escalate the erro. The next step will "
				+ "depend on supervisor strategy" );
		

		Agent<String> logAllCycleLife = Agent.create( "", ExecutionContexts.global() );
		
		final Props propsChild = Props.create( ActorNoTypedDummyCheckLifeCycle.class,
				 () -> new ActorNoTypedDummyCheckLifeCycle( logAllCycleLife )  );
		
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
																	"escalate" 
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
						+ "CONSTRUCTOR => AROUND_PRE_START => PRE_START "
						+ "(" + arg1 + ")" );
				
				addResultAndValue( exception == null );
				
				addResultAndValue( "CONSTRUCTOR => AROUND_PRE_START => PRE_START".equals(arg1)  );
				
			}
			
		}, system.dispatcher() );
		
		
		__INFO( "And after..." );
		
		___WHEN("[2] It send a message Exception that causes a exception in "
				+ "child actor");
		
		final Exception exceptionSent = new Exception( "Exception test" );
		
		Future<Object> future2 = Patterns.ask( actorRef, exceptionSent ,1000 );
		
		future2.onComplete( new OnComplete<Object>(){

			@Override
			public void onComplete(Throwable exception, Object arg1) throws Throwable {
				
				___THEN( "[2] The exception must be null and message has to math:\n"
						+ "'I'm going to pass away'\n"
						+ " (exception: " + exception + ", message: "+ arg1 +")" );
				
				addResultAndValue( exception == null );
				
				addResultAndValue( "I'm going to pass away".equals( arg1 ) );
				
			}
			
		}, system.dispatcher() );
		
		
		__INFO( "And after..." );		
		
		___WHEN("[3] It send a message state");
		
		final Future<Object> future3 = Patterns.ask( actorRef, "state" , 1000 );
		
		
		try {
			
			Await.result( future3, Duration.create( "1 second") );			
			
			fail( "Ey!!! You don't have to be here!" );		
			
			
		} catch( Exception e ) {
			
			___THEN( "[3] The message is lost. The exception must be:\n "
					+ "'AskTimeoutException' or 'TimeoutException'\n"
					+ "(" + e.getClass().getSimpleName() + ")");
			
			final String excepSimpleName = e.getClass().getSimpleName();
			
			Assert.assertTrue( 
							  excepSimpleName.equals( AskTimeoutException.class.getSimpleName() )
							  || excepSimpleName.equals( TimeoutException.class.getSimpleName() )
							);	
			
		}
		
		
		__INFO( "And after..." );		
		
		___WHEN("[4] When It's send a 'lastMessage' to check the dead letters");
		
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
											
				___THEN( "[4] The dead letter message must be 'state' "
						+ "(" + message + ") and the actor recipient must be"
						+ " 'child' (" + nameActorRef +")" );
				
				addResultAndValue( exception == null );
				
				addResultAndValue( "state".equals( message  )  );
				
				addResultAndValue( "child".equals( nameActorRef ) );
				
			}
			
		}, system.dispatcher() );	
		
		
		
		__INFO( "And after..." );
		
		___WHEN("[5] If send a message 'state' again. The actor 'let-itcrass' is "
				+ "restarted because the exception is escalated ");
		
		final Future<Object> future5 = Patterns.ask( actorRef, "state" , 1000 );
		
		final Object result5 = Await.result( future5, Duration.create( "1 second") );
		
		___THEN( "[5] The message received must be equal a 'I'm restarted' "
				+ "(" + result5 + ") ");
		
		assertEquals( "I'm restarted", result5 );	
		
		__INFO( "To check all life cycle, It's killed the actor ref. This kills "
				+ "the child actor too" );
		
		actorRef.tell(PoisonPill.getInstance(), ActorRef.noSender());
		
		__INFO( "Sleep. It's only for waiting to terminate life cycle actor" );
		
		Thread.sleep( 200 );	
		
		___WHEN("[6] It's checked all child actor cycle life");
		
		final String resAllStateCycleLife = Await.result(logAllCycleLife.future(), Duration.create( "300 sec" ) );
		
		___THEN( "[6] The child actor it's started two times because its supervisor "
				+ "is restarted [the exception is scalated]. The all life cycle "
				+ "has to be: \n"
				+ "CONSTRUCTOR"
					+ " >> AROUND_PRE_START"
					 + " >> PRE_START"
					  + " >> [[EXCEPTION]]"
					   + " >> AROUND_POST_STOP"
					    + " >> POST_STOP"
					     + " >> CONSTRUCTOR"
					      + " >> AROUND_PRE_START"
					       + " >> PRE_START"
					        + " >> AROUND_POST_STOP"
					         + " >> POST_STOP \n(" + resAllStateCycleLife + ")"  );
		
		assertEquals( "CONSTRUCTOR"
					+ " >> AROUND_PRE_START"
					 + " >> PRE_START"
					  + " >> [[EXCEPTION]]"
					   + " >> AROUND_POST_STOP"
					    + " >> POST_STOP"
					     + " >> CONSTRUCTOR"
					      + " >> AROUND_PRE_START"
					       + " >> PRE_START"
					        + " >> AROUND_POST_STOP"
					         + " >> POST_STOP", resAllStateCycleLife );
		
		
		final Boolean resultBoolean = getResultBoolean();
		
		
		__INFO("... and finally, it's going to check all the futur results (" + resultBoolean + ")");
		
		assertEquals( true, resultBoolean );
		
	}
	
	
}


