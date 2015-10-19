/*
 *      ActorLetItCrashConstructorTestDefault.java
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
import akka.actor.Props;
import akka.dispatch.OnComplete;
import akka.pattern.Patterns;

import com.logicaalternativa.examples.akka.ActorNoTypedDummyCheckLifeCycle;
import com.logicaalternativa.examples.akka.ActorNoTypedLetItCrash;
import com.logicaalternativa.examples.akka.ActorNoTypedRuntimeExceptionConstructor;
import com.logicaalternativa.examples.akka.testbase.TestBase;

public class ActorLetItCrashConstructorTestDefault extends TestBase {
	
	@Test	
	public void test() throws Exception {
		
		___GIVEN( "It creates a proxy actor (ActorNoTypedLetItCrash) . It is "
				+ "added with configuration proxied actor. In this case the "
				+ "supervision strategy is default. If child actor crashes, it "
				+ "will be restarted" );
		

		final Props propsChild = Props.create( ActorNoTypedRuntimeExceptionConstructor.class );
		
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
		
		___WHEN(" It send a message state");
		
		final Future<Object> future1 = Patterns.ask( actorRef, "state" , 1000 );
		
		future1.onComplete( new OnComplete<Object>(){

			@Override
			public void onComplete(Throwable exception, Object arg1) throws Throwable {
				
				___THEN( "The exception must be null and message has to math "
				+ "'Actor child passed away'"
				+ " (exception: " + exception + ", message: "+ arg1 +")" );
		
				addResultAndValue( exception == null );
				
				addResultAndValue( "Actor child passed away".equals( arg1 ) );

				
			}
			
		}, system.dispatcher() );
		
		
		__INFO( "It's only for waiting the result of agent" );
		
		Thread.sleep( 1000 );
		
		Boolean resultBoolean = getResultBoolean();
		
		
		__INFO("... and finally, it's going to check all the future results (" + resultBoolean + ")");
		
		assertEquals( true, resultBoolean );
		
	}
	
	
}


