//import org.joda.time.DateTime
//DateTime.now
//Map("up"->DateTime.now,"de"-> true)
//
//var arr = Array("s", "a", "l", "a", "m")
//def echo(args: String*) = for (arg <- args) print(arg)
//echo(arr: _*)
//arr.mkString


class ChecksumAccumulator {
  private val sum = 0
  def add(b: Byte) = sum + b
  def checksum() = ~(sum & 0xFF) + 1
}

val test = new ChecksumAccumulator
test.add(5)
test.add(5)
test.checksum()
255 & 0xFF + 200
0xFF