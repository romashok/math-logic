package ctd.lebedev.dev

import ctd.lebedev.dev.grammar.Expr
import ctd.lebedev.dev.grammar.Impl
import ctd.lebedev.dev.grammar.parseExpr
import java.io.File

fun main(args: Array<String>) {
    val inputFilePath = "hw2.in"
    val outputFilePath = "hw2.out"
    hw2(inputFilePath, outputFilePath)
}

fun hw2(inputFilePath: String, outputFilePath: String) {
    File(inputFilePath).bufferedReader().use { input ->
        File(outputFilePath).bufferedWriter().use { output ->
            val (strAssumptions, strProve) = input.readLine().split("|-")
            val strLast = strAssumptions.split(",").last()
            val strInit = strAssumptions.split(",").dropLast(1)
            output.write("${strInit.joinToString(separator = ",")}|-($strLast)->($strProve)\n")

            val lastAssumption = parseExpr(strLast)
            val assumptions = strInit.mapIndexed { index, s -> Pair(parseExpr(s), index + 1) }.toMap()

            val approvedList: MutableList<Expr> = ArrayList()
            val approvedListAtLine: MutableMap<Expr, Int> = HashMap()
            var lineNumber = 1
            input.forEachLine { line ->
                val expr = parseExpr(line)

                val newLines = deductionStep(expr, lineNumber, lastAssumption, assumptions, approvedList, approvedListAtLine)
                newLines.forEach { exprOfModifiedLine ->
                    output.write(exprOfModifiedLine.toString())
                    output.newLine()
                }

                lineNumber++
            }
            approvedList.forEach {
                println(it)
            }
        }
    }
}

fun deductionStep(expr: Expr, lineNumber: Int,
                  lastAssumption: Expr, assumptions: Map<Expr, Int>,
                  approvedList: MutableList<Expr>,
                  approvedListAtLine: MutableMap<Expr, Int>): List<Expr> {
    fun addAnnotaion() {
        approvedListAtLine.put(expr, lineNumber)
        approvedList.add(expr)
    }

    val newLines: MutableList<Expr> = ArrayList()

    if (lastAssumption == expr) {
        newLines.addAll(lemmaAImplA(expr))
        addAnnotaion()
        return newLines
    }

    // Проверка на предположение
    if (assumptions.containsKey(expr)) {
        newLines.addAll(assumptionOrAxiom(expr, lastAssumption))
        addAnnotaion()
        return newLines
    }

    // Проверка на аксиому
    axiomsExprs.mapIndexed { index, axiom ->
        if (isAxiom(expr, axiom)) {
            newLines.addAll(assumptionOrAxiom(expr, lastAssumption))
            addAnnotaion()
            return newLines
        }
    }

    // Проверка на ModusPonens
    approvedList.reversed().forEach { proved ->
        val cur = Impl(proved, expr)
        if (approvedListAtLine.containsKey(cur)) {
            val first = Impl(lastAssumption, proved)
            val second = Impl(lastAssumption, cur)
            val third = Impl(lastAssumption, expr)
            newLines.addAll(listOf(
                    Impl(first, Impl(second, third)),
                    Impl(second, third),
                    third
            ))
            addAnnotaion()
            return newLines
        }
    }
    return listOf()
}

fun lemmaAImplA(e: Expr): List<Expr> {
    val first = Impl(e, Impl(e, e))
    val second = Impl(e, Impl(Impl(e, e), e))
    val third = Impl(e, e)

    return listOf(
            first,
            Impl(first, Impl(second, third)),
            Impl(second, third),
            second,
            third)
}

fun assumptionOrAxiom(e: Expr, lastAssumption: Expr): List<Expr> = listOf(
        Impl(e, Impl(lastAssumption, e)),
        e,
        Impl(lastAssumption, e)
)