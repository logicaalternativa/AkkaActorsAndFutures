/*
 *      ActorNoTypedDummy.java
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

import akka.actor.UntypedActor;
import akka.agent.Agent;
import akka.dispatch.Mapper;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class  ActorNoTypedDummy extends UntypedActor  {
	
	protected LoggingAdapter logger = Logging.getLogger( getContext().system(), this ); 
	
	private Agent<String> result;
	
	public ActorNoTypedDummy( Agent<String> result ) {
		
		this.result = result;
		
	}
	
	public ActorNoTypedDummy( ) {
		
		super();
		
	}

	@Override
	public void onReceive(Object arg0) throws Exception {
		
		if ( arg0 instanceof Long ) {
			
			Thread.sleep( ( ( Long ) arg0) ) ;

			logger.info(" ::: From " + getClass().getSimpleName() + " awake " + arg0);
			
		} else if ( arg0 instanceof String) {
			
			logger.info( " ::: From " + getClass().getSimpleName() + "  " + arg0 );
			
			getSender().tell( arg0, getSelf() );
			
			addResult( arg0.toString(), result);
			
		}  
		
	}
	
	private void addResult( String message, Agent<String> result ) {
		
		if ( result == null ) {
			
			return;
		}
		
		result.send(
				new Mapper<String, String>() {

					@Override
					public String apply(String parameter) {
						return parameter.concat(" ").concat(message).trim();
					}
					
				}
		);
		
	}

}
