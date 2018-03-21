package ctd.lebedev.dev

import org.junit.Assert.*
import org.junit.Test
import java.io.File

class Hw2KtTest {
    val root: String = javaClass.getResource("/tests/HW2").file

    @Test
    fun contra() {
        val input = "$root/contra.in"
        val inter = "$root/contra-inter.out"
        val output = "$root/contra.out"
        hw2(input, inter)
        hw1(inter, output)
        assertTrue(File(output).bufferedReader().lines().allMatch { line -> !line.contains("Не доказано") })
    }
    @Test
    fun contra1() {
        val input = "$root/contra1.in"
        val inter = "$root/contra1-inter.out"
        val output = "$root/contra1.out"
        hw2(input, inter)
        hw1(inter, output)
        assertTrue(File(output).bufferedReader().lines().allMatch { line -> !line.contains("Не доказано") })
    }
    @Test
    fun contra2() {
        val input = "$root/contra2.in"
        val inter = "$root/contra2-inter.out"
        val output = "$root/contra2.out"
        hw2(input, inter)
        hw1(inter, output)
        assertTrue(File(output).bufferedReader().lines().allMatch { line -> !line.contains("Не доказано") })
    }
}