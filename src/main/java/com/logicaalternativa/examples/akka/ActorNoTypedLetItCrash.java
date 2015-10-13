/*
 *      ActorNoTypedLetItCrash.java
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

import scala.concurrent.duration.Duration;
import akka.actor.ActorRef;
import akka.actor.OneForOneStrategy;
import akka.actor.Props;
import akka.actor.SupervisorStrategy;
import akka.actor.SupervisorStrategy.Directive;
import akka.actor.Terminated;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.Function;

public class ActorNoTypedLetItCrash extends UntypedActor  {
	
	protected LoggingAdapter logger = Logging.getLogger( getContext().system(), this );	
	
	private ActorRef actorChild;
	
	private String typeStrategy;
	
	boolean isActorChildAlive;
	
	boolean isRestarted;
	
	public ActorNoTypedLetItCrash( Props propsChild, String typeStrategy ) {
		
		actorChild = getContext().actorOf( propsChild, "child" );
		
		isActorChildAlive = true;
		
		isRestarted = false;
		
		getContext().watch( actorChild ); // It is monitored if the actorChild died		
		
		this.typeStrategy = typeStrategy;
		
	}
	
	@Override
	public void onReceive( Object message ) throws Exception {
		
		if ( message != null 
				&& message instanceof Terminated ) {
			
			logger.info ( "From " + getClass().getSimpleName() + "Actor child passed away" );
			
			isActorChildAlive = false;			
			
		}  else if ( isRestarted ) {
			
			sender().tell("I'm restarted", self() );
			
		} else if ( isActorChildAlive ) {
			
			actorChild.forward( message, getContext() );
			
		} else {
			
			sender().tell("Actor child passed away", self() );
			
		}
		
	}
	
	
	
	@Override
	public void aroundPostRestart(Throwable reason) {
		
		logger.error( reason, "Restarted actor");
		
		isRestarted = true;
		
		super.aroundPostRestart(reason);
	}

	@Override
	public void postStop() throws Exception {
		
		getContext().system().eventStream().unsubscribe( getSelf() );
		
		super.postStop();
	}

	@Override
	public SupervisorStrategy supervisorStrategy() {
		
		if ( "default".equals( typeStrategy )  ) {
			
			return super.supervisorStrategy();
			
		}
		
		return  new OneForOneStrategy
				(
					5,
					Duration.apply("10 seconds"),
					new FunctionStrategy( )
				 );
	}
	
	private class FunctionStrategy implements Function<Throwable, SupervisorStrategy.Directive> {
		
		@Override
		public Directive apply(Throwable exception) throws Exception {
			
			logger.error(exception, "exception on Function Stategy");
			
			if ( exception instanceof Exception ) {
				
				if ( "stop".equals( typeStrategy ) ){
					
					return SupervisorStrategy.stop();
					
				} else if ( "resume".equals( typeStrategy ) ) {
					
					return SupervisorStrategy.resume();
					
				} else if ( "escalate".equals( typeStrategy ) ) {
					
					return SupervisorStrategy.escalate();
					
				}
				
			}
			
			return SupervisorStrategy.restart();
		}
	}
	
}
