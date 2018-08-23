package com.dragishak.aerospike

import com.aerospike.client.{Bin, Key}
import monix.execution.Scheduler.Implicits.global
import org.scalatest.WordSpec
import org.scalatest.Matchers._
import org.scalatest.concurrent.ScalaFutures

class AerospikeScalaClientTest extends WordSpec with AerospikeFixture with ScalaFutures {

  "AerospikeScalaClient" should {

    "write and read" in withAerospikeScalaClient { client =>
      val key = new Key(aerospikeNamespace, null, "key1")
      val bin = new Bin("binName", "binValue")

      val task = for {
        _ <- client.put(key, bin)
        r <- client.get(key)
      } yield r

      whenReady(task.runAsync) { res =>
        res.bins should contain key "binName"
        res.bins.get("binName") should be("binValue")
      }

    }
  }

}
