package com.logicaalternativa.examples.akka.typed.imp;

import akka.dispatch.Futures;
import scala.concurrent.Future;
import scala.concurrent.Promise;



public class TypedActorDummyImp {
	
	public void testRuntimeException(){
		
		throw new RuntimeException();
		
	}
	
	public void sleep( final Long miliSec ) {
		
		_sleep(miliSec);
		
	}

	private void _sleep( final Long miliSec ) {
		try {
			Thread.sleep( miliSec );
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	
	public Future<String> futureEcho( final Long miliSec, final String message ) {
		
		Promise<String> promise = Futures.promise();
		
		_sleep( miliSec );
		
		promise.success( "Echo ".concat( message ) );
		
		return promise.future();		
	}

}
