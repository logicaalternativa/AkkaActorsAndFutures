/*
 *      ActorNoTypedWhoIam.java
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

import java.util.HashMap;
import java.util.Map;

import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class  ActorNoTypedWhoIam extends UntypedActor  {
	
	protected LoggingAdapter logger = Logging.getLogger( getContext().system(), this ); 

	@Override
	public void onReceive(Object arg0) throws Exception {
		
		
		if ( ! ( arg0 instanceof String ) ) {
			
			unhandled( arg0 );
			
		}
		
		final String message = ( String ) arg0;
		
		final String name = getSelf().path().name();
		
		logger.info("From " + getClass().getSimpleName() + " name: " + name + ", message: " + arg0);
		
		final Map<String,String> mapResult = new HashMap<String, String>();
		
		mapResult.put( "name", name );
		mapResult.put( "message", message );
		
		getSender().tell( mapResult, getSelf() );
			
	}	

}
