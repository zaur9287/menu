import org.joda.time.DateTime


DateTime.now




object Planets extends Enumeration {
  val Mercury = Value
  val Venus = Value
  val Earth = Value
  val Mars = Value
  val Jupiter = Value
  val Saturn = Value
  val Uranus = Value
  val Neptune = Value
  val Pluto = Value
}

Planets.Jupiter.id

