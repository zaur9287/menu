5+5

import org.joda.time.{DateTime, Seconds}



var a1 = "2018-04-23 18:33:29.735000 +04:00"
var t1 = DateTime.parse(a1).toDateTime
var a2 = "2018-04-23 18:37:58.932000 +04:00"
var t2 = DateTime.parse(a2).toDateTime

Seconds.secondsBetween(t1,t2).getSeconds

