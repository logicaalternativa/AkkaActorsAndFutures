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
