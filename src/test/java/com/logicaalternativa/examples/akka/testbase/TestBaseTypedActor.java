/*
 *      TypedActorDummy.java
 *      
 *      Copyright 2015 Miguel Rafael Esteban Martín (www.logicaalternativa.com) <miguel.esteban@logicaalternativa.com>
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