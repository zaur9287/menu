def multiply(x: Int, y: Int) = x * y
(multiply _).isInstanceOf[Function2[_, _, _]]
val multiplyCurried = (multiply _).curried
multiply(4, 5)
multiplyCurried(3)(2)
val multiplyCurriedFour = multiplyCurried(4)
multiplyCurriedFour(2)
multiplyCurriedFour(4)