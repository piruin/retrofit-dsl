package retrofit2.dsl

import net.jodah.concurrentunit.Waiter

fun Waiter.assert(value: Boolean, lazyMessage: () -> String) {
    if (!value)
        println(lazyMessage())

    assertTrue(value)
}

fun waiter(time: Long = 3000, block: Waiter.() -> Unit) {
    val waiter = Waiter()
    waiter.block()
    waiter.await(time)
}
