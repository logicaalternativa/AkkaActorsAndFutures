package com.logicaalternativa.examples.akka.testbase;

import akka.actor.TypedActor;
import akka.actor.TypedProps;

import com.logicaalternativa.examples.akka.typed.TypedActorDummy;
import com.logicaalternativa.examples.akka.typed.imp.TypedActorDummyImp;

public abstract class TestBaseTypedActor extends TestBase {

	protected TypedActorDummy typedActorDummy;

	public TestBaseTypedActor() {
		super();
	}

	@Override
	public void setUp() {
		
		 super.setUp();
		 
		 typedActorDummy =
			      TypedActor.get(system).typedActorOf(
			        new TypedProps<TypedActorDummyImp>(TypedActorDummy.class, TypedActorDummyImp.class));
		 
	}

}