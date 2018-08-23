package com.dragishak.aerospike

import com.aerospike.client.{AerospikeClient, AerospikeException, Key, Record}
import com.aerospike.client.async.EventLoops
import com.aerospike.client.listener.RecordListener
import com.aerospike.client.policy.{Policy, WritePolicy}
import monix.eval.Task
import monix.execution.Cancelable

class AerospikeScalaClient(client: AerospikeClient, eventLoops: EventLoops) {

  import com.aerospike.client.Bin
  import com.aerospike.client.listener.WriteListener

  def put(key: Task[Key], bin: Task[Bin], writePolicy: Option[WritePolicy] = None): Task[Unit] =
    for {
      k <- key
      b <- bin
      t <- Task.create[Unit] { (_, callback) =>
        val listener = new WriteListener {
          override def onSuccess(key: Key): Unit = callback.onSuccess(Unit)
          override def onFailure(exception: AerospikeException): Unit = callback.onError(exception)
        }
        val loop = eventLoops.next()
        try {
          client.put(loop, listener, writePolicy.orNull, k, b)
        } catch {
          case ex: AerospikeException => callback.onError(ex)
        }
        Cancelable.empty
      }
    } yield t

  def get(key: Task[Key], policy: Option[Policy] = None): Task[Record] =
    for {
      k <- key
      t <- Task.create[Record] { (_, callback) =>
        val handler = new RecordListener {
          override def onSuccess(key: Key, record: Record): Unit = callback.onSuccess(record)
          override def onFailure(exception: AerospikeException): Unit = callback.onError(exception)
        }
        val loop = eventLoops.next()
        try {
          client.get(loop, handler, policy.orNull, k)
        } catch {
          case ex: AerospikeException => callback.onError(ex)
        }
        Cancelable.empty
      }
    } yield t
}

object AerospikeScalaClient {

  def apply(client: AerospikeClient, eventLoops: EventLoops): AerospikeScalaClient =
    new AerospikeScalaClient(client, eventLoops)

}
