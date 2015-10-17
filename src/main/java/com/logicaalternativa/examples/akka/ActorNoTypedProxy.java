/*
 *      ActorNoTypedProxy.java
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

import static akka.pattern.Patterns.ask;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class ActorNoTypedProxy extends UntypedActor  {
	
	protected LoggingAdapter logger = Logging.getLogger( getContext().system(), this );	
	
	private ActorRef actorChild;
	
	public ActorNoTypedProxy( Props props ) {
		
		 actorChild = getContext().actorOf( props, "proxied" );		
	}

	@Override
	public void onReceive(Object arg0) throws Exception {
		
		if ( arg0 instanceof String) {
		
			if ( "forward".equals( arg0 ) ) {
				
				logger.info("Execute method forward");
			
				actorChild.forward( arg0, getContext() );
			
			} else if ( "redirect".equals( arg0 ) ) {
			
				logger.info("Execute tell like a redirect ");
			
				actorChild.tell( arg0, getSender() );
			
			} else if ( "future".equals( arg0 ) ) {
			
				logger.info("Execute method  ask future");
			
				Future<Object> future = ask( actorChild, arg0, 5000 );
				
				getSender().tell( future, getSelf() );		
			
			} else  if ( "await".equals( arg0 ) ) {
			
				logger.info("Execute waiting futur");

				Future<Object> futurs = ask( actorChild, arg0, 5000 );
				
				Object res = Await.result( futurs, Duration.create( "5 second") );
	
				getSender().tell( res, getSelf() );			
			
			} else {
				
				unhandled( arg0 );
			}
			
		} else {
		
			unhandled( arg0 );
		
		}
		
		
	}

}
