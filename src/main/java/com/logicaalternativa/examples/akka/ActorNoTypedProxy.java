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
