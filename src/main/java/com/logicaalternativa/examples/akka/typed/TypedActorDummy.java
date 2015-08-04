package com.logicaalternativa.examples.akka.typed;

import scala.concurrent.Future;

public interface TypedActorDummy {

	public abstract void testRuntimeExceptionVoid();
	
	public abstract Future<String> testRuntimeExceptionFuture();

	public abstract String testRuntimeExceptionString();

	public abstract void sleep(Long miliSec);

	public abstract Future<String> futureEcho(Long miliSec, String message);
	
	public abstract String echo(Long miliSec, String message);

}