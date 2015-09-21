package com.logicaalternativa.examples.akka.message;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.pattern.Patterns;

import com.logicaalternativa.examples.akka.ActorNoTypedDummy;
import com.logicaalternativa.examples.akka.ActorNoTypedProxy;
import com.logicaalternativa.examples.akka.testbase.TestBase;

public class ActorProxyTest extends TestBase {
	
	@Test	
	public void testForward() throws Exception {
		
		___GIVEN( "It creates a proxy actor. It is added whith configuration "
				+ "proxied actor. The proxy will redirect the messge to "
				+ "proxied actor" );
		
		final Props propsChild = Props.create( ActorNoTypedDummy.class );
		
		final Props props = Props.create( ActorNoTypedProxy.class, propsChild );
		
		final ActorRef actorRef = system.actorOf( props, "my-proxy" );				
		
		
		___WHEN(" It send a message ('forward') (Using forward message method )");
		
		final String messageSent = "forward";
		
		Future<Object> future = Patterns.ask( actorRef, messageSent , 5000 );
		
		final Object messageResponse = Await.result( future, Duration.create( "10 second") );		
		
		
		___THEN( "The message received must be equal to sent message (" + messageResponse + ", " + messageSent + ")" );
		
		assertEquals( messageSent, messageResponse );	

	}
	
	@Test	
	public void testRedirectMessageToChild() throws Exception {
		
		___GIVEN( "It creates a proxy actor. It is added whith configuration "
				+ "proxied actor. The proxy will redirect the messge to "
				+ "proxied actor" );
		
		final Props propsChild = Props.create( ActorNoTypedDummy.class );
		
		final Props props = Props.create( ActorNoTypedProxy.class, propsChild );
		
		final ActorRef actorRef = system.actorOf( props, "my-proxy" );				
		
		
		___WHEN(" It send a message ('redirect') (Send message to a child actor. "
				+ "The sender will be this, not the Proxy Actor )");
		
		final String messageSent = "redirect";
		
		Future<Object> future = Patterns.ask( actorRef, messageSent , 5000 );
		
		final Object messageResponse = Await.result( future, Duration.create( "10 second") );		
		
		
		___THEN( "The message received must be equal to sent message (" + messageResponse + ", " + messageSent + ")" );
		
		assertEquals( messageSent, messageResponse );	

	}
	
	@Test	
	public void testAwaitFutur() throws Exception {
		
		___GIVEN( "It creates a proxy actor. It is added whith configuration "
				+ "proxied actor. The proxy will redirect the messge to "
				+ "proxied actor" );
		
		final Props propsChild = Props.create( ActorNoTypedDummy.class );
		
		final Props props = Props.create( ActorNoTypedProxy.class, propsChild );
		
		final ActorRef actorRef = system.actorOf( props, "my-proxy" );				
		
		
		___WHEN(" It send a message (not 'forward') (Proxy that futurs is been "
				+ "waiting )");
		
		final String messageSent = "await";
		
		Future<Object> future = Patterns.ask( actorRef, messageSent , 5000 );
		
		final Object messageResponse = Await.result( future, Duration.create( "10 second") );
		
		
		___THEN( "The message received must be equal to sent message "
				+ "(" + messageResponse + ", " + messageSent + ")" );
		
		assertEquals( messageSent, messageResponse );			

	}
	
	@SuppressWarnings("unchecked")
	@Test	
	public void testFutur() throws Exception {
		
		___GIVEN( "It creates a proxy actor. It is added whith configuration "
				+ "proxied actor. The proxy will redirect the message to "
				+ "proxied actor" );
		
		final Props propsChild = Props.create( ActorNoTypedDummy.class );
		
		final Props props = Props.create( ActorNoTypedProxy.class, propsChild );
		
		final ActorRef actorRef = system.actorOf( props, "my-proxy" );				
		
		
		___WHEN(" It send a message ('futur') (Using futur message method, not "
				+ "blocking )");
		
		final String messageSent = "future";
		
		Future<Object> future = Patterns.ask( actorRef, messageSent , 5000 );
		
		final Future<Object> futureChild = (Future<Object>) Await.result( future, Duration.create( "10 second") );	
		
		final Object messageResponse = Await.result( futureChild, Duration.create( "10 second") );
		
		
		___THEN( "The message received must be equal to sent message "
				+ "(" + messageResponse + ", " + messageSent + ")" );
		
		assertEquals( messageSent, messageResponse );	

	}
	
}


