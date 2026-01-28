// Taller 1
//Juliana niño Galvis, Miguel Perez
//-------------------1--------------
def insert(e: Int, l: List[Int]): List[Int] =
  l match
    case Nil => List(e)
    case h :: t if e <= h => e :: l
    case h :: t => h :: insert(e, t)
println(insert(1, Nil))
println(insert(10, List(20, 30, 40)))
println(insert(30, List(10, 20, 40, 50)))
println(insert(100, List(10, 20, 40, 50)))
//--------------2-----------

import scala.annotation.tailrec

@tailrec
def insertionSortTail(inputList: List[Int], outputList: List[Int]): List[Int] =
  inputList match {
    case Nil => outputList
    case h :: t => insertionSortTail(t, insert(h, outputList))
  }

//Ocultar el valor incial de la lista de salida: outputList
def insertionInput(inputList: List[Int]): List[Int] =
  insertionSortTail(inputList, Nil)


//-------------3---------------
def menores(l: List[Int], p: Int): List[Int] =

  l match
  case Nil => Nil
  case h :: t =>
    if h < p then h :: menores(t, p)
    else menores(t, p)

def mayores(l: List[Int], p: Int): List[Int] =
  l match
  case Nil => Nil
  case h :: t =>
    if h >= p then h :: mayores(t, p)
    else mayores(t, p)

def quicksortDesc(l: List[Int]): List[Int] =
  l match
  case Nil => Nil
  case x :: Nil => List(x)
  case p :: t =>
    quicksortDesc(mayores(t, p)) ::: (p :: quicksortDesc(menores(t, p)))

def top5(scores: List[Int]): List[Int] =
  quicksortDesc(scores).take(5)

println(top5(List(5, 1, 3, 9, 7, 2, 8)))
println(top5(List(100, 200, 300, 400, 500, 600, 700)))
println(top5(List(42, 42, 42, 42, 42, 42)))

//-----------4---------------

def menoresF(inputList: List[Int], p: Int): List[Int] = inputList match
  case Nil => Nil
  case h :: t =>
    if h < p then h :: menores(t, p)
    else menores(t, p)

def mayoresF(inputList: List[Int], p: Int): List[Int] = inputList match
  case Nil => Nil
  case h :: t =>
    if h >= p then h :: mayores(t, p)
    else mayores(t, p)

//------------5-----------------
def qsort(l: List[Int]): List[Int] =
  l match
  case Nil => Nil
  case x :: Nil => List(x)
  case p :: t =>
    qsort(menores(t, p)) ::: (p :: qsort(mayores(t, p)))








