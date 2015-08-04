package com.logicaalternativa.examples.akka;

import java.util.ArrayList;
import java.util.List;

import akka.actor.DeadLetter;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class ActorNoTypedReadDeadLetter extends UntypedActor  {
	
	protected LoggingAdapter logger = Logging.getLogger( getContext().system(), this );	
	
	private List<DeadLetter> listDeadLetters;
	
	public ActorNoTypedReadDeadLetter( ) {
		
		getContext().system().eventStream().subscribe( getSelf(), DeadLetter.class );
		
		listDeadLetters = new ArrayList<DeadLetter>();
		
	}
	
	

	@Override
	public void onReceive( Object message ) throws Exception {
		
		if ( message != null 
				&& message instanceof DeadLetter ) {
			
			final DeadLetter deadLetter = ( DeadLetter ) message;
			
			logger.info ( "From " + getClass().getSimpleName() + "Dead letter received... " + deadLetter );
			
			listDeadLetters.add( deadLetter );
			
		} else if ("lastDeadLetter".equals( message ) ) {
			
			if ( ! listDeadLetters.isEmpty() ) {
				
				final DeadLetter deadLetter = listDeadLetters.get( listDeadLetters.size() -1 );
				
				getSender().tell( deadLetter, getSelf() );
				
			} else {
				
				getSender().tell( "NONE", getSelf() );
				
			}			
			
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
