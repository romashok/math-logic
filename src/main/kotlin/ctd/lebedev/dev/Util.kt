package ctd.lebedev.dev

import ctd.lebedev.dev.grammar.*

val strAxioms = listOf(
        "A -> (B -> A)",
        "(A -> B) -> (A -> B -> C) -> (A -> C)",
        "A -> B -> A & B",
        "A & B -> A",
        "A & B -> B",
        "A -> A | B",
        "A -> B | A",
        "(A -> B) -> (C -> B) -> (A | C -> B)",
        "(A -> B) -> (A -> !B) -> !A",
        "!!A -> A")

val axiomsExprs = strAxioms.map(::parseExpr)
fun isAxiom(expr: Expr, axiom: Expr) = match(expr, axiom, HashMap())
fun isAnyAxiom(expr: Expr) = axiomsExprs.any { axiom -> isAxiom(expr, axiom) }

fun match(expr: Expr, axiom: Expr, vars: MutableMap<Expr, Expr>): Boolean {
    if (axiom is Var) {
        if (vars.containsKey(axiom)) {
            return vars[axiom]!! == expr
        } else {
            vars.put(axiom, expr)
            return true
        }
    } else if (expr::class == axiom::class) {
        when (axiom) {
            is UnaryExpr -> return match((expr as UnaryExpr).expr, axiom.expr, vars)
            is BinaryExpr -> return match((expr as BinaryExpr).l, axiom.l, vars) && match((expr).r, axiom.r, vars)
        }
    }
    return false
}

fun getVars(expr: Expr, vars: MutableMap<Int, String>) {
    fun extract(expr: Expr, vars: MutableMap<String, Int>) {
        when (expr) {
            is Var -> vars.putIfAbsent(expr.value, vars.size)
            is UnaryExpr -> extract(expr.expr, vars)
            is BinaryExpr -> {
                extract(expr.l, vars)
                extract(expr.r, vars)
            }
        }
    }

    val swapVars: MutableMap<String, Int> = HashMap()
    extract(expr, swapVars)
    swapVars.forEach { param, i -> vars.put(i, param) }
}

fun isTautology(expr: Expr): MutableMap<String, Boolean> {
    val params: MutableMap<Int, String> = HashMap()
    getVars(expr, params)

    for (mask in 0 until 2.pow(params.size)) {
        val paramValues: MutableMap<String, Boolean> = HashMap()

        mask.fixedSizeBits(params.size).forEachIndexed { index, c ->
            paramValues.put(params[index]!!, c == '1')
        }
        if (!expr.eval(paramValues)) return paramValues
    }
    return HashMap()
}

fun Int.fixedSizeBits(length: Int) = this.toString(2).padStart(length, '0')
