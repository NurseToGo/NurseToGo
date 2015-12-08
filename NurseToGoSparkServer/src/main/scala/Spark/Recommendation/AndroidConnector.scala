package Spark.Recommendation

/**
 * Created by hastimal on 12/6/2015.
 */
import java.io.{DataInputStream, IOException}
import java.net.{InetAddress, Socket}

object AndroidConnector {
  def findIpAdd():String =
  {
    val localhost = InetAddress.getLocalHost
    val localIpAddress = localhost.getHostAddress

    return localIpAddress
  }
  def recieveCommandForRobot(string: String)
  {
    // Simple server

    try {


      lazy val address: Array[Byte] = Array(10.toByte, 205.toByte, 0.toByte, 68.toByte)  //iOS server
      val ia = InetAddress.getByAddress(address)
      val socket = new Socket(ia, 4094)
     // val out = new PrintStream(socket.getOutputStream)
      val in = new DataInputStream(socket.getInputStream())

     print(s"I am getting ${in.toString}")
      //in..flush()

      //out.close()
     // in.close()
      //socket.close()
    }
    catch {
      case e: IOException =>
        e.printStackTrace()
    }
  }

}
