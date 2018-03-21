package ctd.lebedev.dev

import ctd.lebedev.dev.grammar.parseExpr
import org.junit.Assert
import org.junit.Test

import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class Hw3KtTest {
    val root: String = javaClass.getResource("/tests/HW3").file

    @Test
    fun false1() {
        val input = "$root/false1.in"
        val output = "$root/false1.out"
        hw3(input, output)
        Assert.assertTrue(File(output).bufferedReader().readLine().startsWith("Выражение ложно при"))
    }

    @Test
    fun true1() {
        val input = "$root/true1.in"
        val inter = "$root/true1-inter.out"
        val output = "$root/true1.out"
        hw3(input, inter)
        val expectedStatement = parseExpr(File(input).bufferedReader().readLine())
        val actualStatement = parseExpr(File(inter).bufferedReader().readLines().last())
        assertEquals(expectedStatement, actualStatement)

        hw1(inter, output)
        assertTrue(File(output).bufferedReader().lines().allMatch { line -> !line.contains("Не доказано") })
    }

    @Test
    fun true2() {
        val input = "$root/true2.in"
        val inter = "$root/true2-inter.out"
        val output = "$root/true2.out"
        hw3(input, inter)
        val expectedStatement = parseExpr(File(input).bufferedReader().readLine())
        val actualStatement = parseExpr(File(inter).bufferedReader().readLines().last())
        assertEquals(expectedStatement, actualStatement)

        hw1(inter, output)
        assertTrue(File(output).bufferedReader().lines().allMatch { line -> !line.contains("Не доказано") })
    }

    @Test
    fun true3() {
        val input = "$root/true3.in"
        val inter = "$root/true3-inter.out"
        val output = "$root/true3.out"
        hw3(input, inter)
        val expectedStatement = parseExpr(File(input).bufferedReader().readLine())
        val actualStatement = parseExpr(File(inter).bufferedReader().readLines().last())
        assertEquals(expectedStatement, actualStatement)

        hw1(inter, output)
        assertTrue(File(output).bufferedReader().lines().allMatch { line -> !line.contains("Не доказано") })
    }

    @Test
    fun true4() {
        val input = "$root/true4.in"
        val inter = "$root/true4-inter.out"
        val output = "$root/true4.out"
        hw3(input, inter)
        val expectedStatement = parseExpr(File(input).bufferedReader().readLine())
        val actualStatement = parseExpr(File(inter).bufferedReader().readLines().last())
        assertEquals(expectedStatement, actualStatement)

        hw1(inter, output)
        assertTrue(File(output).bufferedReader().lines().allMatch { line -> !line.contains("Не доказано") })
    }

    @Test
    fun true5() {
        val input = "$root/true5.in"
        val inter = "$root/true5-inter.out"
        val output = "$root/true5.out"
        hw3(input, inter)
        val expectedStatement = parseExpr(File(input).bufferedReader().readLine())
        val actualStatement = parseExpr(File(inter).bufferedReader().readLines().last())
        assertEquals(expectedStatement, actualStatement)

        hw1(inter, output)
        assertTrue(File(output).bufferedReader().lines().allMatch { line -> !line.contains("Не доказано") })
    }

    @Test
    fun true6() {
        val input = "$root/true6.in"
        val inter = "$root/true6-inter.out"
        val output = "$root/true6.out"
        hw3(input, inter)
        val expectedStatement = parseExpr(File(input).bufferedReader().readLine())
        val actualStatement = parseExpr(File(inter).bufferedReader().readLines().last())
        assertEquals(expectedStatement, actualStatement)

        hw1(inter, output)
        assertTrue(File(output).bufferedReader().lines().allMatch { line -> !line.contains("Не доказано") })
    }

    @Test
    fun true7() {
        val input = "$root/true7.in"
        val inter = "$root/true7-inter.out"
        val output = "$root/true7.out"
        hw3(input, inter)
        val expectedStatement = parseExpr(File(input).bufferedReader().readLine())
        val actualStatement = parseExpr(File(inter).bufferedReader().readLines().last())
        assertEquals(expectedStatement, actualStatement)

        hw1(inter, output)
        assertTrue(File(output).bufferedReader().lines().allMatch { line -> !line.contains("Не доказано") })
    }
}