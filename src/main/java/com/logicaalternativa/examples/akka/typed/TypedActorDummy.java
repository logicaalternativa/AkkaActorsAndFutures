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

package com.logicaalternativa.examples.akka.typed;

import scala.concurrent.Future;

public interface TypedActorDummy {

	public abstract void testRuntimeExceptionVoid();
	
	public abstract Future<String> testRuntimeExceptionFuture();

	public abstract String testRuntimeExceptionString();

	public abstract void sleep(Long millisec);

	public abstract Future<String> futureEcho(Long millisec, String message);
	
	public abstract String echo(Long millisec, String message);

}