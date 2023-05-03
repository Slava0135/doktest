package cases

/**
 * ```kotlin doctest:run
 * val a = 2
 * val b = 1
 * assertEquals(a - b, minus3(a, b))
 * ```
 * bla bla
 * ```kotlin doctest:norun
 * assertEquals(0, minus3(2, 1))
 * ```
 * bla bla
 * ```kotlin doctest:nomain
 * class Foo {
 *     fun Bar(a: Int, b: Int) = Minus3(b, a)
 * }
 * ```
 */
@Suppress("unused")
fun minus3(a: Int, b: Int) = a - b