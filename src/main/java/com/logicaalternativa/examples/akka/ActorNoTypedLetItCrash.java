package com.logicaalternativa.examples.akka;

import scala.concurrent.duration.Duration;
import akka.actor.ActorRef;
import akka.actor.OneForOneStrategy;
import akka.actor.Props;
import akka.actor.SupervisorStrategy;
import akka.actor.SupervisorStrategy.Directive;
import akka.actor.Terminated;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.Function;

public class ActorNoTypedLetItCrash extends UntypedActor  {
	
	protected LoggingAdapter logger = Logging.getLogger( getContext().system(), this );	
	
	private ActorRef actorChild;
	
	private String typeStrategy;
	
	boolean isActorChildAlive;
	
	public ActorNoTypedLetItCrash( Props propsChild, String typeStrategy ) {
		
		actorChild = getContext().actorOf( propsChild, "child" );
		
		isActorChildAlive = true;
		
		getContext().watch( actorChild ); // It is monitored if the actorChild died		
		
		this.typeStrategy = typeStrategy;
		
	}
	
	@Override
	public void onReceive( Object message ) throws Exception {
		
		if ( message != null 
				&& message instanceof Terminated ) {
			
			logger.info ( "From " + getClass().getSimpleName() + "Actor child passed away" );
			
			isActorChildAlive = false;			
			
		}  else if ( isActorChildAlive ) {
			
			actorChild.forward( message, getContext() );
			
		} else {
			
			sender().tell("Actor child passed away", self() );
			
		}
		
		
		
	}
	
	@Override
	public void postStop() throws Exception {
		
		getContext().system().eventStream().unsubscribe( getSelf() );
		
		super.postStop();
	}

	@Override
	public SupervisorStrategy supervisorStrategy() {
		
		if ( "default".equals( typeStrategy )  ) {
			
			return super.supervisorStrategy();
			
		}
		
		return  new OneForOneStrategy
				(
					5,
					Duration.apply("10 seconds"),
					new FunctionStrategy( )
				 );
	}
	
	private class FunctionStrategy implements Function<Throwable, SupervisorStrategy.Directive> {
		
		@Override
		public Directive apply(Throwable exception) throws Exception {
			
			if ( exception instanceof Exception ) {
				
				if ( "stop".equals( typeStrategy ) ){
					
					return SupervisorStrategy.stop();
					
				} else if ( "resume".equals( typeStrategy ) ) {
					
					return SupervisorStrategy.resume();
					
				} else if ( "escalate".equals( typeStrategy ) ) {
					
					return SupervisorStrategy.escalate();
					
				}
				
			}
			
			return SupervisorStrategy.restart();
		}
	}
	
}
