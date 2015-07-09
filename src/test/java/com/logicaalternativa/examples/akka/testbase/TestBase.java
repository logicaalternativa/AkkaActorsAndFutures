package com.logicaalternativa.examples.akka.testbase;

import org.junit.After;
import org.junit.Before;

import akka.actor.ActorSystem;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class TestBase {
	
	protected LoggingAdapter loggerActor;
	
	protected ActorSystem system = null;
	
	
	@Before
	public void before(){
		
		if ( system == null ) {
			
			system = ActorSystem.create( getClass().getSimpleName()  );
			
		}		
		
		loggerActor = Logging.getLogger( system, this ); 
		
	}
	
	
	@After
	public void afterForAllTest(){
		
		if ( system != null ) {
			
			system.shutdown();
			
		}
		
	}
	
	
	private void log( String text ) {
		
		
		if ( loggerActor != null ) {
			
			loggerActor.info( text );
			
		}
		
	}
		
	protected void ___GIVEN(String text) {
		
		log( " ___ GIVEN :" + text );		
		
	}	
	
	protected void ___WHEN(String text) {
		
		log( " ___ WHEN :" + text );
		
	}
	

	protected void ___THEN( String text) {
		
		log( "___ THEN :" + text );
		
	}
	
	protected void __INFO( String text) {
		
		log( "__ INFO :" + text );
		
	}
	
	
	protected class BooleanAnd {
		
		private Boolean res = null;		
		
		public BooleanAnd() {
		
			super();
		
		}		

		public void and( Boolean value ) {
			
			res = res == null ? value :  res && value;
			
		}
		
		public Boolean getRes() {
			
			return res;
			
		}
		
	}
	
	

}
