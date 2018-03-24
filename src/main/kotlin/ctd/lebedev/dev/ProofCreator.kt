package ctd.lebedev.dev

import ctd.lebedev.dev.grammar.*
import java.io.BufferedWriter
import java.io.File

val PROOFS_ROOT: String = "src/main/kotlin/ctd/lebedev/dev/proofs"

class ProofCreator(var proofExpr: Expr,
                   var assumptions: MutableList<Expr>,
                   var expressions: MutableList<Expr> = ArrayList()) {
    fun deduction() {
        val newExprs: MutableList<Expr> = ArrayList()
        expressions.forEachIndexed nextPoof@{ i, expr ->
            if (expr == assumptions.first()) {
                val sub = mapOf("A" to expr)
                addProof(newExprs, "$PROOFS_ROOT/A_Implication_A.proof", sub)
                return@nextPoof
            }

            if (assumptions.contains(expr) || isAnyAxiom(expr)) {
                val sub = mapOf("A" to expr, "B" to assumptions.first())
                newExprs.add(expr)
                newExprs.add(makeExpr("A->B->A", sub))
                newExprs.add(makeExpr("B->A", sub))
                return@nextPoof
            }

            for (j in i - 1 downTo 0) {
                val tmp = Impl(expressions[j], expr)
                if (expressions.contains(tmp)) {
                    val sub = mapOf("A" to assumptions.first(), "B" to expressions[j], "C" to expr, "D" to tmp)
                    newExprs.add(makeExpr("(A->B)->(A->D)->(A->C)", sub))
                    newExprs.add(makeExpr("(A->D)->(A->C)", sub))
                    newExprs.add(makeExpr("A->C", sub))
                    break
                }
            }
        }

        proofExpr = Impl(assumptions.first(), proofExpr)
        assumptions = assumptions.takeLast(assumptions.size - 1).toMutableList()
        expressions = newExprs
    }

    fun merge(other: ProofCreator): ProofCreator {
        val sub = mapOf("A" to assumptions.first(), "B" to other.assumptions.first(), "C" to proofExpr).toMutableMap()

        this.deduction()
        other.deduction()

        sub["D"] = proofExpr
        sub["E"] = other.proofExpr

        expressions.addAll(other.expressions)
        expressions.add(makeExpr("D->E->(A|B->C)", sub))
        expressions.add(makeExpr("E->(A|B->C)", sub))
        expressions.add(makeExpr("(A|B->C)", sub))

        addProof(expressions, "$PROOFS_ROOT/Aor!A.proof", sub)
        expressions.add(sub["C"]!!)
        proofExpr = sub["C"]!!
        return this
    }

    fun printToFile(output: BufferedWriter) {
        output.use { out ->
            out.write(assumptions.joinToString(", "))
            out.write("|-$proofExpr")
            out.newLine()

            expressions.forEach { expr -> out.write("$expr\n") }
        }
    }
}

fun addProof(proof: MutableList<Expr>, path: String, substitutions: Map<String, Expr>) {
    File(path).bufferedReader().lines().forEach { line ->
        proof.add(makeExpr(line.trim(), substitutions))
    }
}

fun makeProof(expr: Expr, proof: ProofCreator): Boolean {
    when (expr) {
        is Var -> if (proof.assumptions.contains(expr)) {
            proof.expressions.add(expr)
            return true
        } else {
            proof.expressions.add(Not(expr))
            return false
        }
        is Not -> {
            val A = makeProof(expr.expr.single(), proof)
            val sub = mapOf("A" to expr.expr.single())
            if (A) addProof(proof.expressions, "$PROOFS_ROOT/From_A_To_!!A.proof", sub)
            return !A
        }
        is BinaryExpr -> {
            val A = makeProof(expr.l, proof)
            val B = makeProof(expr.r, proof)
            val op = when (expr) {
                is Or -> "or/"
                is And -> "and/"
                is Impl -> "implication/"
                else -> TODO("unreachable code for simple grammar")
            }
            val suffix = "a_b"
                    .replace("a", if (A) "A" else "nA")
                    .replace("b", if (B) "B" else "nB")

            val path = "$PROOFS_ROOT/$op/$suffix.proof"
            val sub = mapOf("A" to expr.l, "B" to expr.r)
            addProof(proof.expressions, path, sub)

            return proof.expressions.last() == expr
        }
        else -> TODO("unreachable code for simple grammar")
    }
}

fun makeExpr(strExpr: String, substitutions: Map<String, Expr>): Expr {
    return substite(parseExpr(strExpr), substitutions)
}

fun substite(expr: Expr, substitutions: Map<String, Expr>): Expr {
    when (expr) {
        is Var -> return substitutions[expr.value]!!
        is Pred -> {
            if (substitutions.containsKey(expr.name)) {
                return substitutions[expr.name]!!
            }
//            for (i in 0..expr.expr.size) {
//                expr.expr[i] = substite(expr.expr[i], substitutions)
//            }
        }
        is UnaryExpr -> {
            expr.expr = arrayOf(substite(expr.expr.single(), substitutions))
        }
        is BinaryExpr -> {
            expr.l = substite(expr.l, substitutions)
            expr.r = substite(expr.r, substitutions)
        }
    }
    return expr
}
