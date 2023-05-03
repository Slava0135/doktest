package cases

/**
 * ```kotlin doctest:run
 * val a = 2
 * val b = 1
 * assertEquals(a - b, minus(a, b))
 * ```
 * bla bla
 * ```kotlin doctest:norun
 * assertEquals(0, minus(2, 1))
 * ```
 * bla bla
 * ```kotlin doctest:nomain
 * class Foo {
 *     fun Bar(a: Int, b: Int) = minus(b, a)
 * }
 * ```
 */
fun minus(a: Int, b: Int) = a - b
