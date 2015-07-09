package com.logicaalternativa.examples.akka;

import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class  ActorNoTypedDummy extends UntypedActor  {
	
//	protected Logger logger = Logger.getLogger( getClass().toString() );
	
	protected LoggingAdapter logger = Logging.getLogger( getContext().system(), this ); 

	@Override
	public void onReceive(Object arg0) throws Exception {
		
		if ( arg0 instanceof Long ) {
			
			Thread.sleep( ( ( Long ) arg0) ) ;

			logger.info("From " + getClass().getSimpleName() + " awake " + arg0);
			
		} else if ( arg0 instanceof String) {
			
			logger.info("From " + getClass().getSimpleName() + "  " + arg0 );			
				
			getSender().tell( arg0, getSelf() );
			
		}  
		
	}	

}
