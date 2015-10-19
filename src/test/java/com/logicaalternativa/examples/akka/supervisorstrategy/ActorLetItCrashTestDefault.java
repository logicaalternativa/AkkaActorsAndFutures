/*
 *      ActorLetItCrashTestDefault.java
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

import org.junit.Test;

import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;
import akka.actor.ActorRef;
import akka.actor.PoisonPill;
import akka.actor.Props;
import akka.agent.Agent;
import akka.dispatch.ExecutionContexts;
import akka.dispatch.OnComplete;
import akka.pattern.Patterns;

import com.logicaalternativa.examples.akka.ActorNoTypedDummyCheckLifeCycle;
import com.logicaalternativa.examples.akka.ActorNoTypedLetItCrash;
import com.logicaalternativa.examples.akka.testbase.TestBase;

public class ActorLetItCrashTestDefault extends TestBase {
	
	@Test	
	public void test() throws Exception {
		
		___GIVEN( "It creates a proxy actor (ActorNoTypedLetItCrash). It is "
				+ "added with configuration proxied actor. In this case the "
				+ "supervision strategy is default. If child actor is crashed, it "
				+ "will be restarted" );
		
		final Agent<String> logAllCycleLife = Agent.create( "", ExecutionContexts.global() );
		
		final Props propsChild = Props.create( ActorNoTypedDummyCheckLifeCycle.class
				, () -> new ActorNoTypedDummyCheckLifeCycle( logAllCycleLife )  );
		
		final Props props = Props.create
								( 
									ActorNoTypedLetItCrash.class, 
									() -> new ActorNoTypedLetItCrash( 
																	propsChild,
																	"default" 
																  ) 
											
								);
		
		
		final ActorRef actorRef = system.actorOf( props, "let-itcrass" );	
		
		initResultBoolean();
		
		
		___WHEN("[1] It send a message state ");
		
		final Future<Object> future1 = Patterns.ask( actorRef, "state" , 1000 );
		
		future1.onComplete( new OnComplete<Object>(){

			@Override
			public void onComplete(Throwable exception, Object arg1) throws Throwable {
				
				___THEN( "[1] The message received must be equal to: \n "
						+ "CONSTRUCTOR => AROUND_PRE_START => PRE_START\n "
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
				
				___THEN( "[2] The exception must be null and message has to math "
						+ "'I'm going to pass away'"
						+ " (exception: " + exception + ", message: "+ arg1 +")" );
				
				addResultAndValue( exception == null );
				
				addResultAndValue( "I'm going to pass away".equals( arg1 ) );
				
			}
			
		}, system.dispatcher() );
		
		
		__INFO( "And after..." );		
		
		___WHEN("[3] It send a message state");
		
		final Future<Object> future3 = Patterns.ask( actorRef, "state" , 1000 );
		
		final Object result3 = Await.result( future3, Duration.create( "3 second") );
		
		___THEN( "[3] The message received must be equal to \n"
				+ "CONSTRUCTOR => AROUND_POST_RESTART => POST_RESTART => PRE_START \n"
				+ "(" + result3 + ") ");
		
		assertEquals( "CONSTRUCTOR => AROUND_POST_RESTART => POST_RESTART => PRE_START", result3 );
				
		__INFO( "To check all life cycle, It's killed the actor ref. This kills "
				+ "the child actor too" );
		
		actorRef.tell(PoisonPill.getInstance(), ActorRef.noSender());
		
		
		__INFO( "Sleep. It's only for waiting to terminate life cycle actor" );
		
		Thread.sleep( 200 );
		
		___WHEN("[4] It's checked all child actor cycle life");
		
		final String resAllStateCycleLife = Await.result(logAllCycleLife.future(), Duration.create( "300 sec" ) );
		
		___THEN( "[4] The all life cycle has to be: \n"
				+ "CONSTRUCTOR"
					+ " >> AROUND_PRE_START"
					  + " >> PRE_START"
					   + " >> [[EXCEPTION]]"
					    + " >> AROUND_PRE_RESTART"
					     + " >> PRE_RESTART"
					      + " >> POST_STOP"
					       + " >> CONSTRUCTOR"
					        + " >> AROUND_POST_RESTART"
					         + " >> POST_RESTART"
					          + " >> PRE_START"
					           + " >> AROUND_POST_STOP"
					            + " >> POST_STOP\n"
				+ " (" + resAllStateCycleLife + ")"  );
		
		assertEquals( "CONSTRUCTOR"
					+ " >> AROUND_PRE_START"
					  + " >> PRE_START"
					   + " >> [[EXCEPTION]]"
					    + " >> AROUND_PRE_RESTART"
					     + " >> PRE_RESTART"
					      + " >> POST_STOP"
					       + " >> CONSTRUCTOR"
					        + " >> AROUND_POST_RESTART"
					         + " >> POST_RESTART"
					          + " >> PRE_START"
					           + " >> AROUND_POST_STOP"
					            + " >> POST_STOP", resAllStateCycleLife );
		
		
		
		final Boolean resultBoolean = getResultBoolean();
		
		__INFO("... and finally, it's going to check all the future results (" + resultBoolean + ")");
		
		assertEquals( true, resultBoolean );
		
	}
	
	
}


