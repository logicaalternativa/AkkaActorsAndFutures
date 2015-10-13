/*
 *      TestBase.java
 *      
 *      Copyright 2016 Miguel Rafael Esteban Martín (www.logicaalternativa.com) <miguel.esteban@logicaalternativa.com>
 *      
 *      This program is free software; you can redistribute it and/or modify
 *      it under the terms of the GNU General Public License as published by
 *      the Free Software Foundation; either version 2 of the License, or
 *      (at your option) any later version.
 *      
 *      This program is distributed in the hope that it will be useful,
 *      but WITHOUT ANY WARRANTY; without even the implied warranty of
 *      MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *      GNU General Public License for more details.
 *      
 *      You should have received a copy of the GNU General Public License
 *      along with this program; if not, write to the Free Software
 *      Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 *      MA 02110-1301, USA.
 */

package com.logicaalternativa.examples.akka.testbase;

import org.junit.After;
import org.junit.Before;

import scala.concurrent.Await;
import scala.concurrent.duration.Duration;
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
			  
			@Override
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
	
	protected Boolean getResultBoolean() throws Exception {
		
		return Await.result( resultBoolean.future(), Duration.create("300 sec") );
	}
	
	
	private void log( String text ) {
		
		
		if ( loggerActor != null ) {
			
			loggerActor.info( text );
			
		}
		
	}
		
	protected void ___GIVEN(String text) {
		
		log( indentText(" _ GIVEN", text ) );
		
	}	
	
	protected void ___WHEN(String text) {
		
		log( indentText(" __ WHEN: ", text ) );
		
	}
	

	protected void ___THEN( String text) {
		
		log( indentText(" ___ THEN: ", text ) );
		 
	}
	
	protected void __INFO( String text) {
		
		log( "\n     INFO : " + text );
		
	}
	
	protected String indentText( String typeTraze, String text ) {
		
		final String ident = "\n     ";
		final StringBuilder res = new StringBuilder( "\n" );
		
		res.append( ident )
		    .append( typeTraze )
		    .append( ident )
		    .append( ident );
		
		String[] splitReturnline = text.split("[\\n\\r]");
		
		for (String line : splitReturnline) {
			
			final String[] words = line.split("[ ]");
			
			StringBuilder lineIdent = indentLine( res, words, ident);
			
			res.append( lineIdent ).append( ident );
		}
		
		res.append( "\n"  );
		
		return res.toString();		
		
	}

	private StringBuilder indentLine(
			final StringBuilder res, final String[] words, final String ident) {
		
		int lenghtLine = 75;

		StringBuilder lineIdent = new StringBuilder();
		
		for (String word : words) {
			
			if ( lineIdent.length() + word.length() < lenghtLine ) {
				
				if ( lineIdent.length() > 0) {
					
					lineIdent.append(" ");
					
				}
					
				lineIdent.append( word );
				
			} else {
				
				res.append( lineIdent ).append(ident );
				
				lineIdent = new StringBuilder();
				
				lineIdent.append( word );
			}
			
		}
		
		return lineIdent;
	}
	

}
