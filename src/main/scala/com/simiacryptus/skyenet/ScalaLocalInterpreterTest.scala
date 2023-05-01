package com.simiacryptus.skyenet

import com.simiacryptus.skyenet.heart.ScalaLocalInterpreter

import java.io.File
import scala.reflect.runtime.universe._

object ScalaLocalInterpreterTest {
  def main(args: Array[String]): Unit = {
    // Print the full classpath, the # of entries, and # of chars
    val classpath = System.getProperty("java.class.path")
    println("Classpath: " + classpath)
    println("Classpath # of entries: " + classpath.split(File.pathSeparator).length)
    println("Classpath # of chars: " + classpath.length)

    val interpreter: ScalaLocalInterpreter = new ScalaLocalInterpreter(
      getTypeTagTuple("message", "hello"),
      getTypeTagTuple("function", (x: Int) => x * x)
    )
    interpreter.run("System.out.println(message)")
    interpreter.run("System.out.println(function(5))")
    test()
  }

  def getTypeTagTuple[T](name: String, value: T)(implicit tt: TypeTag[T]) = (name, value, tt.tpe)

  class TestObject {
    def square(x: Int): Int = x * x
  }

  trait TestInterface {
    def square(x: Int): Int
  }

  def test(): Unit = {
    {
      val interpreter = new ScalaLocalInterpreter(getTypeTagTuple("message", "hello"))
      test("hello", interpreter.run("message"))
    }
    {
      val interpreter = new ScalaLocalInterpreter(getTypeTagTuple("function", new TestObject()))
      test(25, interpreter.run("function.square(5)"))
    }
    {
      val testImpl = new TestInterface() {
        override def square(x: Int): Int = {
          x * x
        }
      }
      val interpreter = new ScalaLocalInterpreter(getTypeTagTuple("function", testImpl))
      test(25, interpreter.run("function.square(5)"))
    }
  }

  private def test[T <: Any](expected: T, actual: T): Unit = {
    require(expected == actual, actual.toString)
  }
}
