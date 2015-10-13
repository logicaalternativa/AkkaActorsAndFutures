/*
 *      ActorNoTypedLogEvent.java
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

import java.util.ArrayList;
import java.util.List;

import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class ActorNoTypedLogEvent<T> extends UntypedActor  {
	
	protected LoggingAdapter logger = Logging.getLogger( getContext().system(), this );	
	
	private List<T> listLogFilter;
	
	private Class<T> clas;
	
	public ActorNoTypedLogEvent( Class<T> clas ) {
		
		getContext().system().eventStream().subscribe( getSelf(), clas );
		
		this.clas = clas;
		
		listLogFilter = new ArrayList<T>();
		
	}
	
	

	@SuppressWarnings({ "unchecked" })
	@Override
	public void onReceive( Object message ) throws Exception {
		
		if ("lastMessage".equals( message ) ) {
			
			if ( ! listLogFilter.isEmpty() ) {
				
				final T lastMessage = listLogFilter.get( listLogFilter.size() -1 );
				
				getSender().tell( lastMessage, getSelf() );
				
			} else {
				
				getSender().tell( "NONE", getSelf() );
				
			}			
			
		} else if ( message != null 
				&& clas.equals(message.getClass() ) ) {
			
			final T messageLog = ( T ) message;
			
			logger.info ( "From " + getClass().getSimpleName() + " "
						+ clas.getSimpleName()  +" received... " + messageLog );
			
			listLogFilter.add( messageLog );
			
		} else {
			
			unhandled(message);			
			
		}
		
		
	}
	
	
	
	@Override
	public void postStop() throws Exception {
		
		getContext().system().eventStream().unsubscribe( getSelf() );
		
		super.postStop();
	}
	
	

}
