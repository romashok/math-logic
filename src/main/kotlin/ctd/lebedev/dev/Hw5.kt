package ctd.lebedev.dev

import java.io.File

fun main(args: Array<String>) {
    val inputFilePath = "hw5.in"
    val outputFilePath = "hw5.out"
    hw5(inputFilePath, outputFilePath)
}

fun replaceAll(s: String, replacements: Map<String, String>): String {
    var resultString = s
    replacements.forEach { old, new ->
        resultString = resultString.replace(old, new)
    }
    return resultString
}

fun hw5(inputFilePath: String, outputFilePath: String) {
    File(inputFilePath).bufferedReader().use { input ->
        File(outputFilePath).bufferedWriter().use { output ->
            val (a, b) = input.readLine().split(" ").map { it.toInt() }
            val res = a + b
            val lhs = "0" + "'".repeat(a)
            val rhs = "0" + "'".repeat(b)
            val total = "0" + "'".repeat(res)
            output.write("|-$lhs+$rhs=$total\n")

            proofPrefix.forEach { line ->
                output.write(line)
                output.newLine()
            }

            var cur0 = "0"
            var curA = "a"
            for (i in 0 until b) {
                proofStep.forEach { line ->
                    output.write(replaceAll(line, mapOf("o" to cur0, "d" to curA)))
                    output.newLine()
                }
                cur0 += "'"
                curA += "'"
            }

            val curQ = "0" + "'".repeat(a)
            val curR = "0" + "'".repeat(res)
            proofSuffix.forEach { line ->
                output.write(replaceAll(line, mapOf("d" to curA, "o" to cur0, "q" to curQ, "f" to curR)))
                output.newLine()
            }
        }
    }
}

val proofPrefix = listOf("0=0->0=0->0=0",
        "a=b->a=c->b=c",
        "(a=b->a=c->b=c)->(0=0->0=0->0=0)->(a=b->a=c->b=c)",
        "(0=0->0=0->0=0)->(a=b->a=c->b=c)",
        "(0=0->0=0->0=0)->@c(a=b->a=c->b=c)",
        "(0=0->0=0->0=0)->@b@c(a=b->a=c->b=c)",
        "(0=0->0=0->0=0)->@a@b@c(a=b->a=c->b=c)",
        "@c(a=b->a=c->b=c)",
        "@c(a=b->a=c->b=c)->(a=b->a=a->b=a)",
        "a=b->a=a->b=a",
        "@a@b@c(a=b->a=c->b=c)",
        "@a@b@c(a=b->a=c->b=c)->@b@c(a+0=b->a+0=c->b=c)",
        "(0=0->0=0->0=0)->@a@b@c(a=b->a=c->b=c)",
        "@b@c(a+0=b->a+0=c->b=c)->@c(a+0=a->a+0=c->a=c)",
        "@b@c(a+0=b->a+0=c->b=c)",
        "@c(a+0=a->a+0=c->a=c)",
        "@c(a+0=a->a+0=c->a=c)->(a+0=a->a+0=a->a=a)",
        "a+0=a->a+0=a->a=a",
        "a+0=a",
        "a+0=a->a=a",
        "a=a",
        "a=a->a=b->a=a",
        "a=b->a=a",
        "(a=b->a=a)->(a=b->a=a->b=a)->(a=b->b=a)",
        "(a=b->a=a->b=a)->(a=b->b=a)",
        "a=b->b=a",
        "(a=b->b=a)->(0=0->0=0->0=0)->(a=b->b=a)",
        "(0=0->0=0->0=0)->(a=b->b=a)",
        "(0=0->0=0->0=0)->@b(a=b->b=a)",
        "(0=0->0=0->0=0)->@a@b(a=b->b=a)",
        "@a@b(a=b->b=a)",
        "@a@b(a=b->b=a)->@b(x=b->b=x)",
        "@b(x=b->b=x)",
        "@b(x=b->b=x)->(x=y->y=x)",
        "x=y->y=x",
        "(x=y->y=x)->(0=0->0=0->0=0)->(x=y->y=x)",
        "(0=0->0=0->0=0)->(x=y->y=x)",
        "(0=0->0=0->0=0)->@y(x=y->y=x)",
        "(0=0->0=0->0=0)->@x@y(x=y->y=x)",
        "@x@y(x=y->y=x)")

val proofStep = listOf("a+b'=(a+b)'",
        "a+b'=(a+b)'->(A->B->A)->a+b'=(a+b)'",
        "(A->B->A)->a+b'=(a+b)'",
        "(A->B->A)->@b(a+b'=(a+b)')",
        "A->B->A",
        "@b(a+b'=(a+b)')",
        "@b(a+b'=(a+b)')->(a+o'=(a+o)')",
        "a+o'=(a+o)'",
        "@x@y(x=y->y=x)->@y((a+o')=y->y=(a+o'))",
        "@y((a+o')=y->y=(a+o'))",
        "@y((a+o')=y->y=(a+o'))->(a+o')=(a+o)'->(a+o)'=(a+o')",
        "(a+o')=(a+o)'->(a+o)'=(a+o')",
        "(a+o)'=(a+o')",
        "a=b->a=c->b=c",
        "(a=b->a=c->b=c)->(A->B->A)->(a=b->a=c->b=c)",
        "(A->B->A)->(a=b->a=c->b=c)",
        "(A->B->A)->@c(a=b->a=c->b=c)",
        "(A->B->A)->@b@c(a=b->a=c->b=c)",
        "(A->B->A)->@a@b@c(a=b->a=c->b=c)",
        "(A->B->A)",
        "@a@b@c(a=b->a=c->b=c)",
        "@a@b@c(a=b->a=c->b=c)->@b@c((a+o)'=b->(a+o)'=c->b=c)",
        "@b@c((a+o)'=b->(a+o)'=c->b=c)",
        "@b@c((a+o)'=b->(a+o)'=c->b=c)->@c((a+o)'=(a+o')->(a+o)'=c->(a+o')=c)",
        "@c((a+o)'=(a+o')->(a+o)'=c->(a+o')=c)",
        "@c((a+o)'=(a+o')->(a+o)'=c->(a+o')=c)->((a+o)'=(a+o')->(a+o)'=d'->(a+o')=d')",
        "((a+o)'=(a+o')->(a+o)'=d'->(a+o')=d')",
        "(a+o)'=d'->(a+o')=d'",
        "a+o=d",
        "a=b->a'=b'",
        "(a=b->a'=b')->(A->B->A)->(a=b->a'=b')",
        "(A->B->A)->(a=b->a'=b')",
        "(A->B->A)->@b(a=b->a'=b')",
        "(A->B->A)->@a@b(a=b->a'=b')",
        "@a@b((a=b)->(a'=b'))",
        "@a@b((a=b)->(a'=b'))->@b((a+o=b)->(a+o)'=b')",
        "@b((a+o=b)->(a+o)'=b')",
        "@b((a+o=b)->(a+o)'=b')->((a+o=d)->(a+o)'=d')",
        "(a+o=d)->(a+o)'=d'",
        "(a+o)'=d'",
        "a+o'=d'")

val proofSuffix = listOf("a+o=d",
        "(a+o=d)->(A->B->A)->(a+o=d)",
        "(A->B->A)->(a+o=d)",
        "(A->B->A)->@a(a+o=d)",
        "(A->B->A)",
        "@a(a+o=d)",
        "@a(a+o=d)->(q+o=f)",
        "q+o=f")