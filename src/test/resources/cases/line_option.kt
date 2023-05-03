package cases

/**
 * ```kotlin doctest:run
 * val a = 2
 * val b = 1
 * assertNotEquals(a - b, minus4(a, b))
 * ```
 * bla bla
 * ```kotlin doctest:norun
 * asertEquals(0, minus4(2, 1))
 * ```
 * bla bla
 * ```kotlin doctest:nomain
 * class Foo {
 *     fun Bar(a: Int, b: Int) = minus4(b, a)
 * }
 * ```
 */
@Suppress("unused")
fun minus4(a: Int, b: Int) = a - b