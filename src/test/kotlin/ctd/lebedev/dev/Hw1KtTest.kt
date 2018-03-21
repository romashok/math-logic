package ctd.lebedev.dev

import org.junit.Assert.*
import org.junit.Test
import java.io.File

class Hw1KtTest {
    val root: String = javaClass.getResource("/tests/HW1").file

    @Test
    fun good1() {
        val input = "$root/good1.in"
        val output = "$root/good1.out"
        println(input)
        println(output)
        hw1(input, output)
        assertTrue(File(output).bufferedReader().lines().allMatch { line -> !line.contains("Не доказано") })
    }

    @Test
    fun good3() {
        val input = "$root/good3.in"
        val output = "$root/good3.out"
        hw1(input, output)
        assertTrue(File(output).bufferedReader().lines().allMatch { line -> !line.contains("Не доказано") })
    }

    @Test
    fun good4() {
        val input = "$root/good4.in"
        val output = "$root/good4.out"
        hw1(input, output)
        assertTrue(File(output).bufferedReader().lines().allMatch { line -> !line.contains("Не доказано") })
    }

    @Test
    fun good5() {
        val input = "$root/good5.in"
        val output = "$root/good5.out"
        hw1(input, output)
        assertTrue(File(output).bufferedReader().lines().allMatch { line -> !line.contains("Не доказано") })
    }

    @Test
    fun good6() {
        val input = "$root/good6.in"
        val output = "$root/good6.out"
        hw1(input, output)
        assertTrue(File(output).bufferedReader().lines().allMatch { line -> !line.contains("Не доказано") })
    }

    @Test
    fun wrong1() {
        val input = "$root/wrong1.in"
        val output = "$root/wrong1.out"
        hw1(input, output)
        assertTrue(File(output).bufferedReader().lines().anyMatch { line -> line.contains("Не доказано") })
    }

    @Test
    fun wrong2() {
        val input = "$root/wrong2.in"
        val output = "$root/wrong2.out"
        hw1(input, output)
        assertTrue(File(output).bufferedReader().lines().anyMatch { line -> line.contains("Не доказано") })
    }

    @Test
    fun wrong3() {
        val input = "$root/wrong3.in"
        val output = "$root/wrong3.out"
        hw1(input, output)
        assertTrue(File(output).bufferedReader().lines().anyMatch { line -> line.contains("Не доказано") })
    }

    @Test
    fun wrong4() {
        val input = "$root/wrong4.in"
        val output = "$root/wrong4.out"
        hw1(input, output)
        assertTrue(File(output).bufferedReader().lines().anyMatch { line -> line.contains("Не доказано") })
    }

    @Test
    fun wrong5() {
        val input = "$root/wrong5.in"
        val output = "$root/wrong5.out"
        hw1(input, output)
        assertTrue(File(output).bufferedReader().lines().anyMatch { line -> line.contains("Не доказано") })
    }

    @Test
    fun wrong6() {
        val input = "$root/wrong6.in"
        val output = "$root/wrong6.out"
        hw1(input, output)
        assertTrue(File(output).bufferedReader().lines().anyMatch { line -> line.contains("Не доказано") })
    }
}