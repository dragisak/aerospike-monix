package com.dragishak.aerospike
import com.aerospike.client.AerospikeClient
import com.aerospike.client.async.{EventPolicy, NioEventLoops}
import com.aerospike.client.policy.ClientPolicy
import com.dimafeng.testcontainers.{ForAllTestContainer, GenericContainer}
import org.scalatest.Suite
import org.testcontainers.containers.wait.strategy.Wait

import scala.concurrent.duration._

trait AerospikeFixture extends ForAllTestContainer {
  this: Suite =>

  protected val aerospikeTimeout: FiniteDuration = 5.seconds
  protected val aerospikeNamespace: String = "test" // default Aerospike namespace

  override val container: GenericContainer = GenericContainer(
    "aerospike:4.3.0.2",
    exposedPorts = Seq(3000),
    waitStrategy = Wait.forLogMessage(""".*\{test\} migrations: complete.*\n""", 1)
  )

  private val eventPolicy = new EventPolicy()
  eventPolicy.minTimeout = aerospikeTimeout.toMillis.toInt
  private val eventLoops = new NioEventLoops(eventPolicy, 0)
  private val clientPolicy = new ClientPolicy()
  clientPolicy.eventLoops = eventLoops
  clientPolicy.failIfNotConnected = false
  clientPolicy.writePolicyDefault.setTimeout(aerospikeTimeout.toMillis.toInt)

  def withAerospikeScalaClient[T](f: AerospikeScalaClient => T): Unit = {
    val aerospikeClient = new AerospikeClient(clientPolicy, container.containerIpAddress, container.mappedPort(3000))

    val client = AerospikeScalaClient(aerospikeClient, eventLoops)

    f(client)
  }

}
