/*
 *      AppConfiguration.java
 *      
 *      Copyright 2016 Miguel Rafael Esteban Martín (www.logicaalternativa.com) <miguel.esteban@logicaalternativa.com>
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
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class  ActorNoTypedRuntimeExceptionConstructor extends UntypedActor  {
	
	private String state;
	
	protected LoggingAdapter logger = Logging.getLogger( getContext().system(), this );
	
	public ActorNoTypedRuntimeExceptionConstructor() {
		
		super();
		
		throw new RuntimeException("Exception from constructor");
	
	}
	
	@Override
	public void onReceive(Object arg0) throws Exception {
		
		getSender().tell( arg0, getSelf() );
			
		
	}
	
	@Override
	public void aroundPostStop() {
		
		logAndConcatState( "AROUND_POST_STOP", "aroundPostStop" );	
		
		super.aroundPostStop();
		
	}

	@Override
	public void preStart() throws Exception {
		
		logAndConcatState( "PRE_START", "preStart" );	
		
		super.preStart();
		
	}
	
	@Override
	public void aroundPostRestart(Throwable reason) {
		
		logAndConcatState( "AROUND_POST_RESTART", "aroundPostRestart" );	
		
		super.aroundPostRestart( reason );
		
	}



	@Override
	public void aroundPreRestart(Throwable reason, Option<Object> message) {
		
		logAndConcatState( "AROUND_PRE_RESTART", "aroundPreRestart" );	
				
		super.aroundPreRestart( reason, message );
		
	}


	@Override
	public void aroundPreStart() {
		
		logAndConcatState( "AROUND_PRE_START", "aroundPreStart" );
		
		super.aroundPreStart();
	}



	@Override
	public void postStop() throws Exception {
		
		logAndConcatState( "POST_STOP", "postStop" );
		
		super.postStop();
	}

	@Override
	public void postRestart(Throwable reason) throws Exception {
		
		logAndConcatState( "POST_RESTART", "postRestart" );
		
		super.postRestart(reason);
	}

	@Override
	public void preRestart(Throwable reason, Option<Object> message) throws Exception {
		
		logAndConcatState( "PRE_RESTART", "preRestart" );
		
		super.preRestart( reason, message );
		
	}
	
	
	private void logAndConcatState( String concatState, String nameMethod ){
		
		state = ( state == null) 
					? concatState : 
						state.concat(" => ").concat( concatState );
		
		logger.info("From " + getClass().getSimpleName() + " calling " + nameMethod + " method. It's added log value: " + concatState  );
		
	}
	
}
