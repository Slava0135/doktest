package cases

/**
 * ```kotlin doctest:run
 * val a = 2
 * val b = 1
 * assertEquals(a - b, minus2(a, b))
 * ```
 * bla bla
 * ```kotlin doctest:norun
 * asertEquals(0, minus2(2, 1))
 * ```
 * bla bla
 * ```kotlin doctest:nomain
 * class Foo {
 *     fun Bar(a: Int, b: Int) = minus2(b, a)
 * }
 * ```
 */
@Suppress("unused")
fun minus2(a: Int, b: Int) = a - b
