package com.dragishak.aerospike

import monix.execution.Scheduler.Implicits.global
import org.scalacheck.Gen
import org.scalatest.WordSpec
import org.scalatest.Matchers._
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import scala.collection.JavaConverters._

class AerospikeScalaClientTest
    extends WordSpec
    with AerospikeFixture
    with ScalaFutures
    with GeneratorDrivenPropertyChecks {

  "AerospikeScalaClient" should {

    "write and read" in withAerospikeScalaClient { client =>
      forAll(genKey, Gen.nonEmptyListOf(genBin)) { (key, bins) =>
        val task = for {
          _ <- client.put(key, bins: _*)
          r <- client.get(key)
        } yield r

        val expected = bins.map(b => b.name -> b.value.getObject).toMap

        whenReady(task.runAsync) { res =>
          res.bins.asScala.toList should contain theSameElementsAs expected
        }

      }

    }
  }

}
