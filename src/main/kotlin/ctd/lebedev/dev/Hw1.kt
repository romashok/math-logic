package ctd.lebedev.dev

import ctd.lebedev.dev.grammar.Expr
import ctd.lebedev.dev.grammar.Impl
import ctd.lebedev.dev.grammar.parseExpr
import java.io.File

fun main(args: Array<String>) {
    val inputFilePath = "hw1.in"
    val outputFilePath = "hw1.out"
    hw1(inputFilePath, outputFilePath)
}

fun hw1(inputFilePath: String, outputFilePath: String) {
    File(inputFilePath).bufferedReader().use { input ->
        File(outputFilePath).bufferedWriter().use { output ->
            val (strAssumptions, strProve) = input.readLine().split("|-")
            output.write("$strAssumptions|-$strProve\n")

            val annotated: MutableList<Expr> = ArrayList()
            val annotatedAtLine: MutableMap<Expr, Int> = HashMap()
            val assumptions = strAssumptions.split(",").filter { it.isNotEmpty() }
                    .mapIndexed { index, s -> Pair(parseExpr(s), index + 1) }.toMap()

            var lineNumber = 1
            input.forEachLine { line ->
                val expr = parseExpr(line)

                val annotaion = getAnnotaion(expr, assumptions, annotated, annotatedAtLine, lineNumber)
                output.write("($lineNumber) $line (${annotaion.msg})\n")
                lineNumber++
            }
        }
    }
}

private sealed class Annotation(val msg: String)
private class NotProven : Annotation("Не доказано")
private class Assumption(index: Int) : Annotation("Предп. $index")
private class AxiomScheme(index: Int) : Annotation("Сх. акс. $index")
private class ModusPonens(fst: Int, snd: Int) : Annotation("М.Р. $fst, $snd")

private fun getAnnotaion(expr: Expr, assumptions: Map<Expr, Int>, annotated: MutableList<Expr>,
                         annotatedAtLine: MutableMap<Expr, Int>, lineNumber: Int): Annotation {
    fun addAnnotaion() {
        annotatedAtLine.put(expr, lineNumber)
        annotated.add(expr)
    }

    // Проверка на предположение
    if (assumptions.containsKey(expr)) {
        addAnnotaion()
        return Assumption(assumptions[expr]!!)
    }

    // Проверка на аксиому
    axiomsExprs.mapIndexed { index, axiom ->
        if (isAxiom(expr, axiom)) {
            addAnnotaion()
            return AxiomScheme(index + 1)
        }
    }

    // Проверка на ModusPonens
    annotated.reversed().forEach { proved ->
        val cur = Impl(proved, expr)
        if (annotatedAtLine.containsKey(cur)) {
            addAnnotaion()
            return ModusPonens(annotatedAtLine[proved]!!, annotatedAtLine[cur]!!)
        }
    }

    return NotProven()
}