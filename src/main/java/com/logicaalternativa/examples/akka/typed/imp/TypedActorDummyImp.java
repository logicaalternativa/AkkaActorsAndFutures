/*
 *      TypedActorDummyImp.java
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

package com.logicaalternativa.examples.akka.typed.imp;

import com.logicaalternativa.examples.akka.typed.TypedActorDummy;

import akka.dispatch.Futures;
import scala.concurrent.Future;
import scala.concurrent.Promise;

public class TypedActorDummyImp implements TypedActorDummy {
	
	/* (non-Javadoc)
	 * @see com.logicaalternativa.examples.akka.typed.TypedActorDummy#testRuntimeException()
	 */
	@Override
	public void testRuntimeExceptionVoid(){
		
		throw new RuntimeException();
		
	}
	
	@Override
	public Future<String> testRuntimeExceptionFuture() {
	
		throw new RuntimeException();
		
	}

	@Override
	public String testRuntimeExceptionString() {

		throw new RuntimeException();
		
	}
	
	/* (non-Javadoc)
	 * @see com.logicaalternativa.examples.akka.typed.TypedActorDummy#sleep(java.lang.Long)
	 */
	@Override
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
	
	
	/* (non-Javadoc)
	 * @see com.logicaalternativa.examples.akka.typed.TypedActorDummy#futureEcho(java.lang.Long, java.lang.String)
	 */
	@Override
	public Future<String> futureEcho( final Long miliSec, final String message ) {
		
		Promise<String> promise = Futures.promise();
		
		_sleep( miliSec );
		
		promise.success( "Echo ".concat( message ) );
		
		return promise.future();		
	}

	/* (non-Javadoc)
	 * @see com.logicaalternativa.examples.akka.typed.TypedActorDummy#echo(java.lang.Long, java.lang.String)
	 */
	@Override
	public String echo(Long miliSec, String message) {
		
		sleep( miliSec );
		
		return "Echo ".concat( message ) ;
	}

	

}
