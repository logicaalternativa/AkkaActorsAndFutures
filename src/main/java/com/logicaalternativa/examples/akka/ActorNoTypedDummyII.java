package com.logicaalternativa.examples.akka;

import scala.Option;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class  ActorNoTypedDummyII extends UntypedActor  {
	
	private String state;
	
	protected LoggingAdapter logger = Logging.getLogger( getContext().system(), this );
	
	public ActorNoTypedDummyII() {
		
		super();
		
		logger.info("From " + getClass().getSimpleName() + " ****** Constructor ***");
		
		logAndConcatState( "INI", "constructor" );
	
	}
	

	@Override
	public void onReceive(Object arg0) throws Exception {
		
		if ( arg0 instanceof String) {
			
			logger.info("From " + getClass().getSimpleName() + "  " + arg0 );			
			
			final String res =  ( "state".equals( arg0 ) ) ? state : (String) arg0;	
				
			getSender().tell( res, getSelf() );
			
		}  else if ( arg0 instanceof Exception) {
			
			logger.info("From " + getClass().getSimpleName() + "  throw exception" + ( (Exception) arg0 ).getMessage() );
			
			logAndConcatState( "EXCEPTION", "onReceive" );
			
			getSender().tell( "I'm going to pass away", getSelf() );
			
			throw (Exception) arg0;
			
		} else {
		
			unhandled( arg0 );
		
		}
		
	}
	
	@Override
	public void aroundPostStop() {
		
		logAndConcatState( "AROUND_POST_STOP", "aroundPostStop" );	
		
		super.aroundPostStop();
		
	}

	@Override
	public void preStart() throws Exception {
		
		logAndConcatState( "PRE_START", "preStart" );	
		
		super.preStart();
		
	}
	
	@Override
	public void aroundPostRestart(Throwable reason) {
		
		logAndConcatState( "AROUND_POST_RESTART", "aroundPostRestart" );	
		
		super.aroundPostRestart( reason );
		
	}



	@Override
	public void aroundPreRestart(Throwable reason, Option<Object> message) {
		
		logAndConcatState( "AROUND_PRE_RESTART", "aroundPreRestart" );	
				
		super.aroundPreRestart( reason, message );
		
	}


	@Override
	public void aroundPreStart() {
		
		logAndConcatState( "AROUND_PRE_START", "aroundPreStart" );
		
		super.aroundPreStart();
	}



	@Override
	public void postStop() throws Exception {
		
		logAndConcatState( "POST_STOP", "postStop" );
		
		super.postStop();
	}

	@Override
	public void postRestart(Throwable reason) throws Exception {
		
		logAndConcatState( "POST_RESTART", "postRestart" );
		
		super.postRestart(reason);
	}

	@Override
	public void preRestart(Throwable reason, Option<Object> message) throws Exception {
		
		logAndConcatState( "PRE_RESTART", "preRestart" );
		
		super.preRestart( reason, message );
		
	}
	
	
	private void logAndConcatState( String concatState, String nameMethod ){
		
		state = ( state == null) 
					? concatState : 
						state.concat(" => ").concat( concatState );
		
		logger.info("From " + getClass().getSimpleName() + " calling " + nameMethod + " method. It's added log value: " + concatState  );
		
	}
	
}
