package com.dragishak.aerospike

import com.aerospike.client.{AerospikeException, Key, Value}
import monix.eval.Task

trait AerospikeKey[T] {

  @throws[AerospikeException]
  def toAerospikeKey(key: T, namespace: String, setName: Option[String] = None): Task[Key]

}

object AerospikeKey {

  def apply[T](toKey: (T, String, Option[String]) => Task[Key]): AerospikeKey[T] = new AerospikeKey[T] {
    @throws[AerospikeException]
    override def toAerospikeKey(key: T, namespace: String, setName: Option[String] = None): Task[Key] =
      toKey(key, namespace, setName)
  }

  def eval[T](toKey: (T, String, Option[String]) => Key): AerospikeKey[T] =
    apply((key: T, namespace: String, setName: Option[String]) => Task.eval(toKey(key, namespace, setName)))

  def evalOnce[T](toKey: (T, String, Option[String]) => Key): AerospikeKey[T] =
    apply((key: T, namespace: String, setName: Option[String]) => Task.evalOnce(toKey(key, namespace, setName)))

}

trait AerospikeKeys {

  implicit val aerospikeStringKey: AerospikeKey[String] = AerospikeKey.evalOnce(
    (key, namespace, setName) => new Key(namespace, setName.orNull, Value.get(key))
  )

  implicit val aerospikeIntKey: AerospikeKey[Int] = AerospikeKey.evalOnce(
    (key, namespace, setName) => new Key(namespace, setName.orNull, Value.get(key))
  )

  implicit val aerospikeLongKey: AerospikeKey[Long] = AerospikeKey.evalOnce(
    (key, namespace, setName) => new Key(namespace, setName.orNull, Value.get(key))
  )

  implicit val aerospikeBooleanKey: AerospikeKey[Boolean] = AerospikeKey.evalOnce(
    (key, namespace, setName) => new Key(namespace, setName.orNull, Value.get(key))
  )

  implicit val aerospikeDoubleKey: AerospikeKey[Double] = AerospikeKey.evalOnce(
    (key, namespace, setName) => new Key(namespace, setName.orNull, Value.get(key))
  )

  implicit val aerospikeFloatKey: AerospikeKey[Float] = AerospikeKey.evalOnce(
    (key, namespace, setName) => new Key(namespace, setName.orNull, Value.get(key))
  )

  implicit val aerospikeByteKey: AerospikeKey[Byte] = AerospikeKey.evalOnce(
    (key, namespace, setName) => new Key(namespace, setName.orNull, Value.get(key))
  )

  implicit val aerospikeByteArrayKey: AerospikeKey[Array[Byte]] = AerospikeKey.evalOnce(
    (key, namespace, setName) => new Key(namespace, setName.orNull, Value.get(key))
  )
}

object AerospikeKeys extends AerospikeKeys
