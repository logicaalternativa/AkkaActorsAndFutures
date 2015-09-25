package com.logicaalternativa.examples.akka.notyped05.supervisorstrategy;

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

public class ActorLetItCrashTestResume extends TestBase {
	
	@Test	
	public void test() throws Exception {
		
		___GIVEN( "It creates a proxy actor (ActorNoTypedLetItCrash). It is "
				+ "added with configuration proxied actor. In this case "
				+ "supervision strategy is 'resume'. If child actor is crashed, "
				+ "the exception is not propaged and the actor is not "
				+ "restarted." );
		

		final Agent<String> logAllCycleLife = Agent.create( "", ExecutionContexts.global() );
		
		final Props propsChild = Props.create( ActorNoTypedDummyCheckLifeCycle.class
				, () -> new ActorNoTypedDummyCheckLifeCycle( logAllCycleLife )  );
		
		final Props props = Props.create
								( 
									ActorNoTypedLetItCrash.class, 
									() -> new ActorNoTypedLetItCrash( 
																	propsChild,
																	"resume" 
																  ) 
											
								);
		
		final ActorRef actorRef = system.actorOf( props, "let-itcrass" );	
		
		initResultBoolean();
		
		
		___WHEN("[1] It send a message state");
		
		final Future<Object> future1 = Patterns.ask( actorRef, "state" , 1000 );
		
		future1.onComplete( new OnComplete<Object>(){

			@Override
			public void onComplete(Throwable exception, Object arg1) throws Throwable {
				
				___THEN( "[1] The message received must be equal to:\n"
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
				
				___THEN( "[2] The exception must be null and message have to match "
						+ "'I'm going to pass away'\n"
						+ " (exception: " + exception + ", message: "+ arg1 +")" );
				
				addResultAndValue( exception == null );
				
				addResultAndValue( "I'm going to pass away".equals( arg1 ) );
				
			}
			
		}, system.dispatcher() );
		
		
		__INFO( "And after..." );		
		
		___WHEN("[3] It send a message state");
		
		final Future<Object> future3 = Patterns.ask( actorRef, "state" , 1000 );
		
		final Object result3 = Await.result( future3, Duration.create( "3 second") );
		
		___THEN( "[3] The message received must be equal to:\n "
				+ "CONSTRUCTOR => AROUND_PRE_START => PRE_START => [[EXCEPTION]] "
				+ "(" + result3 + ") ");
		
		assertEquals( "CONSTRUCTOR => AROUND_PRE_START => PRE_START => [[EXCEPTION]]", result3 );
		
		__INFO( "To check all life cycle, It's killed the actor ref. This kills "
				+ "the child actor too" );
		
		actorRef.tell(PoisonPill.getInstance(), ActorRef.noSender());
		
		__INFO( "Sleep. It's only for waiting to terminate life cycle actor" );
		
		Thread.sleep( 200 );
		
		___WHEN("[4] It's checked all child actor cycle life");
		
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
		
		
		final Boolean resultBoolean = getResultBoolean();
		
		__INFO("... and finally, it's going to check all the futur results (" + resultBoolean + ")");
		
		assertEquals( true,resultBoolean);
		
	}
	
	
}


