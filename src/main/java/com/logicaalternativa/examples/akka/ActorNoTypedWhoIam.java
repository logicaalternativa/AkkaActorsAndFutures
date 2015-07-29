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
