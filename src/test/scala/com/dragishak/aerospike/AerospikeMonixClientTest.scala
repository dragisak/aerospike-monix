package com.dragishak.aerospike

import com.aerospike.client.{BatchRead, Operation}
import monix.execution.Scheduler.Implicits.global
import org.scalacheck.Gen
import org.scalatest.WordSpec
import org.scalatest.Matchers._
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.prop.GeneratorDrivenPropertyChecks

import scala.collection.JavaConverters._

class AerospikeMonixClientTest
    extends WordSpec
    with AerospikeFixture
    with ScalaFutures
    with GeneratorDrivenPropertyChecks {

  "AerospikeScalaClient" should {

    "write and read" in withAerospikeScalaClient { client =>
      forAll(genKey, Gen.nonEmptyListOf(genBin)) { (key, bins) =>
        val task = for {
          _ <- client.put(key, bins: _*)
          exists <- client.exists(key)
          record <- client.get(key)
          deleted <- client.delete(key)
          notExists <- client.exists(key)
        } yield (exists, record, deleted, notExists)

        val expected = bins.map(b => b.name -> b.value.getObject).toMap

        whenReady(task.runAsync) {
          case (exists, record, deleted, notExists) =>
            exists should be(true)
            record.bins.asScala.toList should contain theSameElementsAs expected
            deleted should be(true)
            notExists should be(false)
        }

      }

    }

    "operate" in withAerospikeScalaClient { client =>
      forAll(genKey, Gen.nonEmptyListOf(genBin)) { (key, bins) =>
        val ops = bins.map(Operation.put) ::: Operation.get() :: Nil
        val task = for {
          record <- client.operate(key, ops: _*)
          exists <- client.exists(key)
          _ <- client.delete(key)
        } yield (record, exists)

        val expected = bins.map(b => b.name -> b.value.getObject).toMap

        whenReady(task.runAsync) {
          case (record, exists) =>
            record.bins.asScala.toList should contain theSameElementsAs expected
            exists should be(true)
        }

      }

    }

    "batch get" in withAerospikeScalaClient { client =>
      forAll(genKey, Gen.nonEmptyListOf(genBin)) { (key, bins) =>
        val ops = bins.map(Operation.put)
        val task = for {
          _ <- client.operate(key, ops: _*)
          reads <- client.get(List(new BatchRead(key, true)))
          _ <- client.delete(key)
        } yield reads

        val expected = bins.map(b => b.name -> b.value.getObject).toMap

        whenReady(task.runAsync) { reads =>
          reads.flatMap(_.record.bins.asScala.toList) should contain theSameElementsAs expected
        }

      }

    }
  }

}
