package com.logicaalternativa.examples.akka.testbase;

import org.junit.After;
import org.junit.Before;

import akka.actor.ActorSystem;
import akka.agent.Agent;
import akka.dispatch.ExecutionContexts;
import akka.dispatch.Mapper;
import akka.event.Logging;
import akka.event.LoggingAdapter;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class TestBase {
	
	protected LoggingAdapter loggerActor;
	
	protected ActorSystem system = null;
	
	private Agent<Boolean> resultBoolean;
	
	@Before
	public void setUp(){
		
		final String simpleNameClass = getClass().getSimpleName();		
		
		final String adicionalConfig = setAditionalConfig();		
		
		createSystemActor( simpleNameClass, adicionalConfig );		
		
		loggerActor = Logging.getLogger( system, this ); 
		
	}
	
	protected String setAditionalConfig() {
		
		return null;
		
	}	
	

	private void createSystemActor (final String simpleNameClass, final String adicionalConfig) {
		
		
//		system = ActorSystem.create( simpleNameClass, null, this.getClass().getClassLoader(), ExecutionContexts.fromExecutor( Executors.newFixedThreadPool( 1 ) ) );
		
		
		if ( adicionalConfig != null ) {
				
			Config config = ConfigFactory.parseString( adicionalConfig );
			
			system = ActorSystem.create( simpleNameClass, config  );
			
		} else {
			
			system = ActorSystem.create( simpleNameClass  );			
			
		}
		
	}	
	
	
	@After
	public void tearDown(){
		
		if ( system != null ) {
			
			system.shutdown();
			
		}
		
	}
	
	
	protected void initResultBoolean(){
		
		resultBoolean = Agent.create( null, ExecutionContexts.global() );
		
	}
	
	protected void addResultAndValue( boolean value ) {
		
		resultBoolean.send(new Mapper<Boolean, Boolean>() {
			  
			public Boolean apply(Boolean valAgent) {
				  
				  if ( ! value ) {
					
					  loggerActor.info( this.getClass().getSimpleName() + " value is false ");
					  
				  }
				  
				  
				  final Boolean res = valAgent == null 
						  				? value 
						  					:  valAgent && value;
				  
//				  loggerActor.info( this.getClass().getSimpleName() + ""
//							+ "  value: " + value  + " res: " + res );
				  
					
				 return res;
			  }
			});
		
	}
	
	protected Boolean getResultBoolean() {
		
		return resultBoolean.get();
	}
	
	
	private void log( String text ) {
		
		
		if ( loggerActor != null ) {
			
			loggerActor.info( text );
			
		}
		
	}
		
	protected void ___GIVEN(String text) {
		
		log( "___ GIVEN : " + text );		
		
	}	
	
	protected void ___WHEN(String text) {
		
		log( "___  WHEN : " + text );
		
	}
	

	protected void ___THEN( String text) {
		
		log( "___   THEN : " + text );
		 
	}
	
	protected void __INFO( String text) {
		
		log( "__      INFO : " + text );
		
	}
	

}
