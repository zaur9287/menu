import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat

import scala.collection.mutable.Map
import scala.collection.mutable.ListMap

val tupletest = (1, "elnur", "Zaur", 5.00f)
val testDate = DateTime.now().toString()

val myTestDate = DateTime.parse(testDate).toDateTime()
myTestDate
















val mymap:Map[String,String] =
  Map(
  "id"->"3",
  "name"->"Tural",
  "desc"->"deyisdirilecek user data",
  "created_at"->"sffsd 2018-03-09T22:46:05.648+04:00"
)
var old = Map("id"->"3","name"->"Tural","desc"->"user data","expireDate"->"46546s4a6d6asd")
var myMapNew = Map("id"->"3","name"->"Tural Agayev","desc"->"yeni user data")
//mymap("id").asInstanceOf[Int]
mymap("desc")

val testSeq = Seq(1,2,3,4,5,6, 7)
testSeq.patch(4, Seq(123,34), 2)


var l = new ListMap[String,String]


myMapNew foreach{
  case (key,value)=>
    l++=Map(key->value)
}
l

l("name")
l("id")

//v.clear()
//l.clear()
//l
//v
old("name")
try
old foreach{
  case(key,value) =>
    if (l(key).isEmpty==true)
   old.update(key,l(key))
}
catch {
  case _: Throwable => println("some error")
}


old("name")

//mymap foreach{
//  case (key,keyvalue) =>keyvalue match {
//    case keyvalue:String=>mymap(key)
//    case keyvalue:Int=>mymap(key)
//    case _=>mymap(key)
//  }
//}
//







//val op:Option[String] = Some("Cavad Bayramov")
//
//op.get
//
//
//op.toString



//myMapNew.map(r=>
//  val t:Any = ""
//
//  t match{
//    case t:String=>println("string")
//    case _=>"bla bla"
//  }


//myMapNew foreach{
//  case (key,value)=>
//    key match {
//      case key if ((key =="id")||(key=="name")||(key=="desc"))=>myMapNew(key)
////      case "name"=>myMapNew(key)
////      case "desc"=>myMapNew(key)
//    }
//}


//myMap("name")
//myMap("desc")

//myMapNew foreach{
//  case (key,value)=>
//    myMap.update(key,value)
//}
//
//myMap("name")
//myMap("desc")




val defde:Some[String]= Some("Cavadov Huseyn")
//defde.toString
