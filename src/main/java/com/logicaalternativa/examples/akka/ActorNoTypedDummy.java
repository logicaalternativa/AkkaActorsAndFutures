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
