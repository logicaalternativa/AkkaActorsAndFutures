/*
 *      ActorNoTypedDummyCheckLifeCycle.java
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

package com.logicaalternativa.examples.akka;

import scala.Option;
import akka.actor.UntypedActor;
import akka.agent.Agent;
import akka.dispatch.Mapper;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class  ActorNoTypedDummyCheckLifeCycle extends UntypedActor  {
	
	private String state;
	
	protected LoggingAdapter logger = Logging.getLogger( getContext().system(), this );
	
	private Agent<String> logAllCycleLife;
	
	public ActorNoTypedDummyCheckLifeCycle() {
		
		initState();
	
	}
	
	public ActorNoTypedDummyCheckLifeCycle(Agent<String> logAllCycleLife) {
		
		this.logAllCycleLife = logAllCycleLife;
		
		initState();
		
	}
	
	private void initState() {
		
		logger.info("From " + getClass().getSimpleName() + " ****** Constructor ***");
		
		logAndConcatState( "CONSTRUCTOR", "constructor" );
		
	}
	

	@Override
	public void onReceive(Object arg0) throws Exception {
		
		if ( arg0 instanceof String) {
			
			logger.info("From " + getClass().getSimpleName() + "  " + arg0 );			
			
			final String res =  ( "state".equals( arg0 ) ) ? state : (String) arg0;	
				
			getSender().tell( res, getSelf() );
			
		}  else if ( arg0 instanceof Exception) {
			
			logger.info("From " + getClass().getSimpleName() + "  throw exception" + ( (Exception) arg0 ).getMessage() );
			
			logAndConcatState( "[[EXCEPTION]]", "onReceive" );
			
			getSender().tell( "I'm going to pass away", getSelf() );
			
			throw (Exception) arg0;
			
		} else {
		
			unhandled( arg0 );
		
		}
		
	}
	
	@Override
	public void aroundPreStart() {
		
		logAndConcatState( "AROUND_PRE_START", "aroundPreStart" );
		
		super.aroundPreStart();
	}
	
	@Override
	public void preStart() throws Exception {
		
		logAndConcatState( "PRE_START", "preStart" );	
		
		super.preStart();
		
	}
	
	
	@Override
	public void aroundPreRestart(Throwable reason, Option<Object> message) {
		
		logAndConcatState( "AROUND_PRE_RESTART", "aroundPreRestart" );	
				
		super.aroundPreRestart( reason, message );
		
	}

	@Override
	public void preRestart(Throwable reason, Option<Object> message) throws Exception {
		
		logAndConcatState( "PRE_RESTART", "preRestart" );
		
		super.preRestart( reason, message );
		
	}
	
	@Override
	public void postRestart(Throwable reason) throws Exception {
		
		logAndConcatState( "POST_RESTART", "postRestart" );
		
		super.postRestart(reason);
	}
	
	@Override
	public void aroundPostRestart(Throwable reason) {
		
		logAndConcatState( "AROUND_POST_RESTART", "aroundPostRestart" );	
		
		super.aroundPostRestart( reason );
		
	}
	
	@Override
	public void aroundPostStop() {
		
		logAndConcatState( "AROUND_POST_STOP", "aroundPostStop" );	
		
		super.aroundPostStop();
		
	}

	@Override
	public void postStop() throws Exception {
		
		logAndConcatState( "POST_STOP", "postStop" );
		
		super.postStop();
	}

	
	private void logAndConcatState( String concatState, String nameMethod ){
		
		state = ( state == null) 
					? concatState : 
						state.concat(" => ").concat( concatState );
		
		logger.info("From " + getClass().getSimpleName() + " calling " + nameMethod + " method. It's added log value: " + concatState  );
		
		logAgent(logAllCycleLife, concatState);
		
	}
	
	private void logAgent( final Agent<String> logAllCycleLife, String concatState ){
		
		if ( logAllCycleLife == null ) {
			
			return;
			
		}
		
		logAllCycleLife.send(new Mapper<String, String>() {
			
			@Override
			public String apply(String valAgent) {
				
				String res = (valAgent !=  null && !"".equals( valAgent.trim() ) 
									? valAgent.concat(" >> ") : 
										"" )
							.concat( concatState );
				
//				logger.info("From " + getClass().getSimpleName() + " state log .....  ".concat(res) );
				
				return res;
				
			}
		});
		
	}
	
}
