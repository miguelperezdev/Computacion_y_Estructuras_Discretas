@main

def sumaPares(n: Int): Int = n

x match
  case 0 => 0
  case x if x % 2 == 0 => x + sumaPares(x - 1)
  case x => sumaPares(x - 1)

