package ctd.lebedev.dev

import ctd.lebedev.dev.grammar.Expr
import ctd.lebedev.dev.grammar.Not
import ctd.lebedev.dev.grammar.Var
import ctd.lebedev.dev.grammar.parseExpr
import java.io.File
import java.math.BigInteger

fun main(args: Array<String>) {
    val inputFilePath = "hw3.in"
    val outputFilePath = "hw3.out"
    hw3(inputFilePath, outputFilePath)
}

fun hw3(inputFilePath: String, outputFilePath: String) {
    File(inputFilePath).bufferedReader().use { input ->
        File(outputFilePath).bufferedWriter().use { output ->
            val expr = parseExpr(input.readLine())

            val falseSubstitution = isTautology(expr)
            if (falseSubstitution.isEmpty()) {
                val params: MutableMap<Int, String> = HashMap()
                getVars(expr, params)

                var proofs: MutableList<ProofCreator> = ArrayList()
                for (mask in 0 until 2.pow(params.size)) {
                    val assumptions: MutableList<Expr> = ArrayList()
                    for (j in 0 until params.size) {
                        if (mask.and(2.pow(j)) == 0) {
                            assumptions.add(Var(params[j]!!))
                        } else {
                            assumptions.add(Not(Var(params[j]!!)))
                        }
                    }
                    val proof = ProofCreator(expr, assumptions)
                    makeProof(expr, proof)
                    proofs.add(proof)
                }

                for (i in 1..params.size) {
                    val newProofs: MutableList<ProofCreator> = ArrayList()
                    proofs.chunked(2).forEach { (fst, snd) ->
                        newProofs.add(fst.merge(snd))
                    }
                    proofs = newProofs
                }
                proofs.single().printToFile(output)
            } else {
                output.write("Выражение ложно при ${falseSubstitution.map { (key, value) ->
                    if (value) "$key=И" else "$key=Л"
                }.joinToString(separator = ", ")}")
            }
        }
    }
}

fun Int.pow(exponent: Int): Int = BigInteger(this.toString()).pow(exponent).toInt()
