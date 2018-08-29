package com.dragishak.aerospike

import com.aerospike.client.{AerospikeClient, AerospikeException, Key, Record}
import com.aerospike.client.async.EventLoops
import com.aerospike.client.listener.{DeleteListener, ExistsListener, RecordListener, WriteListener}
import com.aerospike.client.policy.{Policy, WritePolicy}
import monix.eval.Task
import monix.execution.Cancelable
import com.aerospike.client.Bin

class AerospikeMonixClient(client: AerospikeClient, eventLoops: EventLoops) {

  def put(key: Key, writePolicy: Option[WritePolicy], bins: Bin*): Task[Unit] =
    Task.create[Unit] { (_, callback) =>
      val listener = new WriteListener {
        override def onSuccess(key: Key): Unit = callback.onSuccess(())
        override def onFailure(exception: AerospikeException): Unit = callback.onError(exception)
      }
      val loop = eventLoops.next()
      try {
        client.put(loop, listener, writePolicy.orNull, key, bins: _*)
      } catch {
        case ex: AerospikeException => callback.onError(ex)
      }
      Cancelable.empty
    }

  def put(key: Key, bins: Bin*): Task[Unit] = put(key, None, bins: _*)

  def put(key: Key, writePolicy: WritePolicy, bins: Bin*): Task[Unit] = put(key, Some(writePolicy), bins: _*)

  def get(key: Key, policy: Option[Policy]): Task[Record] =
    Task.create[Record] { (_, callback) =>
      val handler = new RecordListener {
        override def onSuccess(key: Key, record: Record): Unit = callback.onSuccess(record)
        override def onFailure(exception: AerospikeException): Unit = callback.onError(exception)
      }
      val loop = eventLoops.next()
      try {
        client.get(loop, handler, policy.orNull, key)
      } catch {
        case ex: AerospikeException => callback.onError(ex)
      }
      Cancelable.empty
    }

  def get(key: Key): Task[Record] = get(key, None)

  def get(key: Key, policy: Policy): Task[Record] = get(key, Some(policy))

  def get(key: Key, policy: Option[Policy], bins: String*): Task[Record] =
    Task.create[Record] { (_, callback) =>
      val handler = new RecordListener {
        override def onSuccess(key: Key, record: Record): Unit = callback.onSuccess(record)

        override def onFailure(exception: AerospikeException): Unit = callback.onError(exception)
      }
      val loop = eventLoops.next()
      try {
        client.get(loop, handler, policy.orNull, key, bins: _*)
      } catch {
        case ex: AerospikeException => callback.onError(ex)
      }
      Cancelable.empty
    }

  def get(key: Key, bins: String*): Task[Record] = get(key, None, bins: _*)

  def get(key: Key, policy: Policy, bins: String*): Task[Record] = get(key, Some(policy), bins: _*)

  def delete(key: Key, writePolicy: Option[WritePolicy]): Task[Boolean] =
    Task.create[Boolean] { (_, callback) =>
      val listener = new DeleteListener {
        override def onSuccess(key: Key, existed: Boolean): Unit = callback.onSuccess(existed)

        override def onFailure(exception: AerospikeException): Unit = callback.onError(exception)
      }
      val loop = eventLoops.next()
      try {
        client.delete(loop, listener, writePolicy.orNull, key)
      } catch {
        case ex: AerospikeException => callback.onError(ex)
      }
      Cancelable.empty
    }

  def delete(key: Key): Task[Boolean] = delete(key, None)
  def delete(key: Key, writePolicy: WritePolicy): Task[Boolean] = delete(key, Some(writePolicy))

  def exists(key: Key, policy: Option[Policy]): Task[Boolean] =
    Task.create[Boolean] { (_, callback) =>
      val listener = new ExistsListener {
        override def onSuccess(key: Key, exists: Boolean): Unit = callback.onSuccess(exists)
        override def onFailure(exception: AerospikeException): Unit = callback.onError(exception)
      }
      val loop = eventLoops.next()
      try {
        client.exists(loop, listener, policy.orNull, key)
      } catch {
        case ex: AerospikeException => callback.onError(ex)
      }
      Cancelable.empty
    }

  def exists(key: Key): Task[Boolean] = exists(key, None)
  def exists(key: Key, policy: Policy): Task[Boolean] = exists(key, Some(policy))
}

object AerospikeMonixClient {

  def apply(client: AerospikeClient, eventLoops: EventLoops): AerospikeMonixClient =
    new AerospikeMonixClient(client, eventLoops)

}
