package ctd.lebedev.dev

import ctd.lebedev.dev.grammar.*
import java.io.File

fun main(args: Array<String>) {
//    val inputFilePath = "test7.out"
//    val inputFilePath = "test11.out"
    val inputFilePath = "test13.out"

//    val outputFilePath = "hw4.out"
    val outputFilePath = "test7.res"
    hw4(inputFilePath, outputFilePath)
}

fun hw4(inputFilePath: String, outputFilePath: String): Boolean {
    val debugOut: MutableList<String> = ArrayList()
    File(inputFilePath).bufferedReader().use { input ->
        File(outputFilePath).bufferedWriter().use { output ->
            val parser = FormalParser()
            val assumptions: MutableSet<Expr> = HashSet()
            var lastAssumption: Expr? = null

            val firstLine = input.readLine()
            val (strAssumptions, strProve) = firstLine.split("|-")
            if (strAssumptions.trim().isNotEmpty()) {
                val strLast = strAssumptions.split(",").last()
                lastAssumption = parser.parseExpr(strLast)
                assumptions.add(lastAssumption!!)

                val strInit = strAssumptions.split(",").dropLast(1)
                strInit.forEach skip@{ string ->
                    val expr = parser.parseExpr(string)
                    if (expr == null) {
                        println("Не удалось распарсить предположение: $string")
                        return@skip
                    }
                    assumptions.add(expr)
                }
            }

            val statement = parser.parseExpr(strProve)
            val freeVariables: MutableSet<String> = HashSet()
            if (lastAssumption != null)
                getFreeVariables(lastAssumption, HashMap(), freeVariables)

            val approved: MutableSet<Expr> = HashSet()
            val approvedList: MutableList<Expr> = ArrayList()
            val prior: MutableList<Pair<Int, Expr?>> = ArrayList()

            var state = -1
            var stateVal: Expr? = null

            input.readLines().forEachIndexed proof@{ index, line ->
                //                println("${assumptions.single()}|-$statement")

                val lineIndex = index + 1
                if (lineIndex % 1000 == 0) println("Обработано $lineIndex строк")
                var error = "[Недоказаное утверждение]"

                val expr = parser.parseExpr(line)
                        ?: throw RuntimeException("Неудалось распарсить выражение на строке $lineIndex: $line")
                state = -1
                stateVal = null

                if (isAnyAxiom(expr) || isAnyFormalAxiom(expr)) {
                    state = 0
                }

                if (state == -1) {
                    if (expr is Impl && expr.l is And && (expr.l as And).r is All && ((expr.l as And).r as All).expr.single() is Impl) {
                        if (getFreeVariables(expr.r, HashMap(), HashSet()).contains(((expr.l as And).r as All).param.value)
                                && freeSubtract(expr.r, (((expr.l as And).r as All).expr.single() as Impl).r,
                                        Var(((expr.l as And).r as All).param.value)/*todo mistype*/, HashMap(), HashMap())
                                && freeSubtract(expr.r, (expr.l as And).l,
                                        Var(((expr.l as And).r as All).param.value)/*todo mistype*/, HashMap(), HashMap())
                                && expr.r == (((expr.l as And).r as All).expr.single() as Impl).l) {
                            state = 0
                        }
                    }
                }

                if (state == -1 && assumptions.contains(expr)) {
                    state = 1
                }

                if (state == -1) {
                    val size = approvedList.size
                    for (j in 0 until size) {
                        if (approved.contains(Impl(approvedList[size - j - 1], expr))) {
                            state = 2
                            stateVal = approvedList[size - j - 1]
                            break
                        }
                    }
                }

                if (state == -1) {
                    if (expr is Impl && expr.r is All) {
                        val tmp = Impl(expr.l, (expr.r as All).expr.single())
                        if (approved.contains(tmp)) {
                            if (!getFreeVariables(expr.l, HashMap(), HashSet()).contains((expr.r as All).param.value)) {
                                if (!freeVariables.contains((expr.r as All).param.value)) {
                                    state = 3
                                    stateVal = tmp
                                } else {
                                    error = "[Невозможно переделать доказательство. " +
                                            "Применение правил с кватором всеобщности, " +
                                            "используещее свободную переменную ${(expr.r as All).param.value} из предположений.]"
                                }
                            } else {
                                error = "[Ошибка применения правил вывода с квантором всеобщности. " +
                                        "Переменная ${(expr.r as All).param.value} входит свободно.]"
                            }
                        }
                    }
                }

                if (state == -1) {
                    if (expr is Impl && expr.l is Exists) {
                        val tmp = Impl((expr.l as Exists).expr.single(), expr.r)
                        if (approved.contains(tmp)) {
                            if (!getFreeVariables(expr.r, HashMap(), HashSet()).contains((expr.l as Exists).param.value)) {
                                if (!freeVariables.contains((expr.l as Exists).param.value)) {
                                    state = 4
                                    stateVal = tmp
                                } else {
                                    error = "[Невозможно переделать доказательство. " +
                                            "Применение правил с кватором существования, " +
                                            "используещее свободную переменную ${(expr.l as Exists).param.value} из предположений.]"
                                }
                            } else {
                                error = "[Ошибка применения правил вывода с квантором существования. " +
                                        "Переменная ${(expr.l as Exists).param.value} входит свободно.]"
                            }
                        }
                    }
                }

                if (state == -1) {
                    println("Вывод не корректен")
                    output.write("Вывод некорректен, начиная с формулы №$lineIndex: $error\n")
//                    println(line)
                    return false //hw4
                } else {
                    approved.add(expr)
                    approvedList.add(expr)
                    prior.add(Pair(state, stateVal))
                }
            }


            if (state != -1) {
                println("Вывод корректен")
                output.write(assumptions.filter { it != lastAssumption }.joinToString(", "))

                if (assumptions.isNotEmpty()) {
                    val printableAss = assumptions.filter { it != lastAssumption }
                    debugOut.add(printableAss.joinToString(",") + "|-${Impl(lastAssumption!!, statement!!)}\n")
                    output.write("|-${Impl(lastAssumption!!, statement!!)}\n")
                    val anyProof = File("$PROOFS_ROOT/any_rule.proof").bufferedReader().readLines()
                    val existsProof = File("$PROOFS_ROOT/exists_rule.proof").bufferedReader().readLines()

                    for (i in 0 until approvedList.size) {
                        when (prior[i].first) {
                            0 -> {
                                // axiom
                                debugOut.add("${Impl(approvedList[i], Impl(lastAssumption, approvedList[i]))}\n")
                                output.write("${Impl(approvedList[i], Impl(lastAssumption, approvedList[i]))}\n")
                                debugOut.add("${approvedList[i]}\n")
                                output.write("${approvedList[i]}\n")
                                debugOut.add("${Impl(lastAssumption, approvedList[i])}\n")
                                output.write("${Impl(lastAssumption, approvedList[i])}\n")
                            }
                            1 -> {
                                // assumption
                                if (approvedList[i] != lastAssumption) {
                                    debugOut.add("${Impl(approvedList[i], Impl(lastAssumption, approvedList[i]))}\n")
                                    output.write("${Impl(approvedList[i], Impl(lastAssumption, approvedList[i]))}\n")
                                    debugOut.add("${approvedList[i]}\n")
                                    output.write("${approvedList[i]}\n")
                                    debugOut.add("${Impl(lastAssumption, approvedList[i])}\n")
                                    output.write("${Impl(lastAssumption, approvedList[i])}\n")
                                } else {
                                    val tmp1 = Impl(lastAssumption, Impl(lastAssumption, lastAssumption))
                                    debugOut.add("$tmp1\n")
                                    output.write("$tmp1\n")

                                    val tmp2 = Impl(lastAssumption, Impl(Impl(lastAssumption, lastAssumption), lastAssumption))
                                    val tmp3 = Impl(lastAssumption, lastAssumption)
                                    debugOut.add("${Impl(tmp1, Impl(tmp2, tmp3))}\n")
                                    output.write("${Impl(tmp1, Impl(tmp2, tmp3))}\n")
                                    debugOut.add("${Impl(tmp2, tmp3)}\n")
                                    output.write("${Impl(tmp2, tmp3)}\n")
                                    debugOut.add("$tmp2\n")
                                    output.write("$tmp2\n")
                                    debugOut.add("$tmp3\n")
                                    output.write("$tmp3\n")
                                }
                            }
                            2 -> {
                                //MP
                                val tmp = Impl(lastAssumption, Impl(prior[i].second!!, approvedList[i]))
                                val tmp1 = Impl(lastAssumption, prior[i].second!!)
                                val tmp2 = Impl(lastAssumption, approvedList[i])
                                debugOut.add("${Impl(tmp1, Impl(tmp, tmp2))}\n")
                                output.write("${Impl(tmp1, Impl(tmp, tmp2))}\n")
                                debugOut.add("${Impl(tmp, tmp2)}\n")
                                output.write("${Impl(tmp, tmp2)}\n")
                                debugOut.add("$tmp2\n")
                                output.write("$tmp2\n")
                            }
                            3 -> {
                                // Any
                                val subs: MutableMap<String, Expr> = mutableMapOf(
                                        "A" to lastAssumption,
                                        "B" to (approvedList[i] as Impl).l,
                                        "C" to ((approvedList[i] as Impl).r as All).expr.single(),
                                        "x" to ((approvedList[i] as Impl).r as All).param)
                                anyProof.forEach { line ->
                                    debugOut.add("${substite(parser.parseExpr(line)!!, subs)}\n")
                                    output.write("${substite(parser.parseExpr(line)!!, subs)}\n")
                                }
                            }
                            4 -> {
                                // Exists
                                val subs: MutableMap<String, Expr> = mutableMapOf(
                                        "A" to lastAssumption,
                                        "B" to ((approvedList[i] as Impl).l as Exists).expr.single(),
                                        "C" to (approvedList[i] as Impl).r,
                                        "x" to ((approvedList[i] as Impl).l as Exists).param)
                                existsProof.forEach { line ->
                                    debugOut.add("${substite(parser.parseExpr(line)!!, subs)}\n")
                                    output.write("${substite(parser.parseExpr(line)!!, subs)}\n")
                                }
                            }
                        }
                    }
                } else {
                    debugOut.add("|-$statement\n")
                    output.write("|-$statement\n")
                    approvedList.forEach { expr -> output.write("$expr\n") }
                }
                return true //hw4
            } else {
                return false //hw4
            }
        }
    }
}

