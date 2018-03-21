package ctd.lebedev.dev

import org.junit.Test

import java.io.File
import kotlin.test.assertEquals

class Hw5KtTest {
    val root: String = javaClass.getResource("/tests/HW5").file

    @Test
    fun test() {
        val cases = "$root/cases.in"
        val input = "$root/input.in"
        val output = "$root/output.out"

        File(cases).bufferedReader().readLines().filter { it.isNotEmpty() }.forEach { line ->
            File(input).bufferedWriter().use { out -> out.write(line) }
            hw5(input, output)

            val (a, b) = line.split(" ").map { it.trim() }.filter { it.isNotEmpty() }.map { it.toInt() }
            val result = File(output).bufferedReader().readLines().filter { it.isNotEmpty() }.last()
            val (lhsSum, rhsSum) = result.split("=").map { it.count { it == '\'' } }

            assertEquals(a + b, lhsSum)
            assertEquals(a + b, rhsSum)
        }
    }
}