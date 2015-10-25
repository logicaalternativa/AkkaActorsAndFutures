# akka by example

In this Java 8 project, examples are used to illustrate the most akka important concepts.

The tools are dummy implementations and Junit tests that allow for observation of the behavior of:

- message queues
- routers
- types of sending messages
- supervision strategies and actor life cycle
- event bus
- dead letters
- typed actors

## Tests
This is a Maven Java project. There are 16 Junit tests that can be run thanks to 'mvn' command:

### How works the actor's mailbox?
```
mvn -Dtest=com.logicaalternativa.examples.akka.queue.ActorQueueTest test
```

### How works a router?
```
mvn -Dtest=com.logicaalternativa.examples.akka.queue.ActorQueueRouteRoundRobinTest test
```

### Different ways of sending messages between actors

#### Using 'forward'
```
mvn  -Dtest=com.logicaalternativa.examples.akka.message.ActorProxyTest#testForward test 
```

#### Using 'redirect'
```
mvn -Dtest=com.logicaalternativa.examples.akka.message.ActorProxyTest#testRedirectMessageToChild test
```

#### Using futures
```
mvn -Dtest=com.logicaalternativa.examples.akka.message.ActorProxyTest#testFutur test
```

#### Using 'await'
```
mvn -Dtest=com.logicaalternativa.examples.akka.message.ActorProxyTest#testAwaitFutur test
```

### Fault resilience: supervision strategy.

#### Default strategy
```
mvn -Dtest=com.logicaalternativa.examples.akka.supervisorstrategy.ActorLetItCrashTestDefault test
```

#### Escalate exception
```
mvn -Dtest=com.logicaalternativa.examples.akka.supervisorstrategy.ActorLetItCrashTestEscalate test
```

#### 'Resume' supervision strategy
```
mvn -Dtest=com.logicaalternativa.examples.akka.supervisorstrategy.ActorLetItCrashTestResume test
```

#### Stopping supervised actor
```
mvn -Dtest=com.logicaalternativa.examples.akka.supervisorstrategy.ActorLetItCrashTestStop test
```

### How works the event bus?
```
mvn -Dtest=com.logicaalternativa.examples.akka.bus.PublishSimpleSubcribeTest test
```

### Dead letters
```
mvn -Dtest=com.logicaalternativa.examples.akka.bus.DeadLettersTest test
```

### Typed actors

#### Behavior when it's called a method that returns void
```
mvn -Dtest=com.logicaalternativa.examples.akka.typed.TypedActorDummyImpTest#testReturnVoidWithSleep test
```
and
```
mvn -Dtest=com.logicaalternativa.examples.akka.typed.TypedActorDummyImpExceptionsTest#testRuntimeExceptionVoid test
```

#### Behavior when it's called a method that returns a future
```
mvn -Dtest=com.logicaalternativa.examples.akka.typed.TypedActorDummyImpTest#testFutureEcho test
```
and
```
mvn -Dtest=com.logicaalternativa.examples.akka.typed.TypedActorDummyImpExceptionsTest#testRuntimeExceptionFuture test
```

#### Behavior when it's called a method that returns an object
```
mvn -Dtest=com.logicaalternativa.examples.akka.typed.TypedActorDummyImpTest#testEcho test
```
and
```
mvn -Dtest=com.logicaalternativa.examples.akka.typed.TypedActorDummyImpExceptionsTest#testRuntimeExceptionString test
```

*[M.E.](http://www.logicaalternativa.com)*

