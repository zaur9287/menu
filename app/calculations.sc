import net.minidev.asm.ex.ConvertException
import org.joda.time.DateTime
import utils.Hashids


var number = "0554733458"

number.slice(2,number.length)


if (number.take(2) == "05" || number.take(2)=="07") "00994"+number.slice(1,number.length)







var s = "0504733458"
if (s.take(1)=="+") s= s.replace(s.take(1),"00")
s

val hashids = new Hashids
var smsID=0
val id:Int = 5
var st = ""
try
  st= hashids.encode(id)
catch{
  case ex:ConvertException=>println("There is error")
}
st
//st = "Mh"

try
  hashids.decode(st)(0)
catch{
  case ex:ConvertException=>println("There is error")
}
DateTime.now.toString
//2018-03-30T15:40:39.463+04:00
//2018-03-27 14:40:13.803000 +04:00
//val t = DateTime.parse( "2018-03-27 14:40:13.803 +04:00")
val a:Option[Boolean] = None
if (a.isDefined){a.get}else{a.getOrElse(false)}


protected def normalizePhoneNumber(number:String):String ={
  var tempNumber = ""
  if (number.take(1)=="+")                            tempNumber = number.replace(number.take(1),"00")
  if (number.take(2) == "05" || number.take(2)=="07") tempNumber = "00994"+number.slice(1,number.length)
  tempNumber
}


normalizePhoneNumber("+994504733458")
