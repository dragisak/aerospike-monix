[![Build Status](https://travis-ci.org/dragisak/aerospike-monix.svg?branch=master)](https://travis-ci.org/dragisak/aerospike-monix)
[![codecov](https://codecov.io/gh/dragisak/aerospike-monix/branch/master/graph/badge.svg)](https://codecov.io/gh/dragisak/aerospike-monix)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.dragishak/aerospike-monix_2.12/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.dragishak/aerospike-monix_2.12)

## Aerospike Monix Client

### Installation

```sbtshell
libraryDependencies += "com.dragishak" %% "aerospike-monix" % "0.0.3"
```

### Code examples

```scala
import com.dragishak.aerospike._
import com.aerospike.client.{AerospikeClient, Bin, Key}
import com.aerospike.client.async.{EventPolicy, NioEventLoops}
import com.aerospike.client.policy.ClientPolicy


val eventPolicy = new EventPolicy()
val eventLoops = new NioEventLoops(eventPolicy, 0)
val clientPolicy = new ClientPolicy()
clientPolicy.eventLoops = eventLoops

val aerospikeClient = new AerospikeClient(clientPolicy, "localhost", 3000)
val client = AerospikeMonixClient(aerospikeClient, eventLoops)

val key = new Key("test", null, "key1")
val bin = new Bin("bin1", "value2")

val task = for {
  _        <- client.put(key, bin)
  res      <- client.get(key)
  existsed <- client.delete(key)
} yield (res, existed)

```