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


val strFormalAxioms = listOf(
        "a=b->a'=b'",
        "a=b->a=c->b=c",
        "a'=b'->a=b",
        "!(a'=0)",
        "a+b'=(a+b)'",
        "a+0=a",
        "a*0=0",
        "a*b'=a*b+a")

val axiomsExprs = strAxioms.map(::parseExpr)
fun isAxiom(expr: Expr, axiom: Expr) = match(expr, axiom, HashMap())
fun isAnyAxiom(expr: Expr) = axiomsExprs.any { axiom -> isAxiom(expr, axiom) }
val formalAxiomsExprs: List<Expr> by lazy {
    val parser = FormalParser()
    strFormalAxioms.map { parser.parseExpr(it)!! }
}

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
            is UnaryExpr -> return match((expr as UnaryExpr).expr.single(), axiom.expr.single(), vars)
            is BinaryExpr -> return match((expr as BinaryExpr).l, axiom.l, vars) && match((expr).r, axiom.r, vars)
        }
    }
    return false
}

fun getVars(expr: Expr, vars: MutableMap<Int, String>) {
    fun extract(expr: Expr, vars: MutableMap<String, Int>) {
        when (expr) {
            is Var -> vars.putIfAbsent(expr.value, vars.size)
            is UnaryExpr -> extract(expr.expr.single(), vars)
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


fun getFreeVariables(expr: Expr, dictionary: MutableMap<Expr, Int>, freeVariables: MutableSet<String>): Set<String> {
    when (expr) {
        is Var -> if (!dictionary.containsKey(expr)) freeVariables.add(expr.value)
        is Pred -> expr.expr.forEach { getFreeVariables(it, dictionary, freeVariables) }
        is Exists -> {
            val cnt = dictionary.getOrDefault(expr.param, 0)
            dictionary.put(expr.param, cnt)
            getFreeVariables(expr.param, dictionary, freeVariables)
            if (cnt == 0) dictionary.remove(expr.param) else dictionary.put(expr.param, cnt)
        }
        is All -> {
            val cnt = dictionary.getOrDefault(expr.param, 0)
            dictionary.put(expr.param, cnt)
            getFreeVariables(expr.param, dictionary, freeVariables)
            if (cnt == 0) dictionary.remove(expr) else dictionary.put(expr.param, cnt)
        }
        is UnaryExpr -> {
            getFreeVariables(expr.expr.single(), dictionary, freeVariables)
        }
        is BinaryExpr -> {
            getFreeVariables(expr.l, dictionary, freeVariables)
            getFreeVariables(expr.r, dictionary, freeVariables)
        }
    }
    return freeVariables
}

fun freeSubtract(template: Expr, expr: Expr, param: Var, locked: MutableMap<String, Int>, dict: MutableMap<Expr, Expr>): Boolean {
    if (template is Var) {
        if (template != param) return template == expr
        if (locked.containsKey(template.value)) {
            return template == expr
        } else {
            if (dict.containsKey(template)) {
                return dict[template] == expr
            } else {
                val tmp: MutableSet<String> = HashSet()
                getFreeVariables(expr, HashMap(), tmp)
                if (tmp.intersect(locked.keys).size != 0) return false
                dict[template] = expr
                return true
            }
        }
    } else if (template::class == expr::class) {
        when (template) {
            is All -> {
                val cnt = locked.getOrDefault(template.param.value, 0)
                locked.put(template.param.value, cnt + 1)
                if (expr !is UnaryExpr) {
//                    println("Real class: ${expr::class}")
                    // todo check type of expr
                    return false
                }
//                val result = freeSubtract(template.param, (expr as All).expr.single(), param, locked, dict)
                val result = freeSubtract(template.expr.single(), (expr as All).expr.single(), param, locked, dict)
                if (cnt == 0) locked.remove(template.param.value) else locked.put(template.param.value, cnt)
                return result
            }
            is Exists -> {
                val cnt = locked.getOrDefault(template.param.value, 0)
                locked.put(template.param.value, cnt + 1)
                if (expr !is UnaryExpr) {
//                    println("Real class: ${expr::class}")
                    // todo check type of expr
                    return false
                }
                val result = freeSubtract(template.param, (expr as Exists).expr.single(), param, locked, dict)
                if (cnt == 0) locked.remove(template.param.value) else locked.put(template.param.value, cnt)
                return result
            }
            is Pred -> {
                if (template.expr.size != (expr as Pred).expr.size) return false
                for (i in 0 until template.expr.size) {
                    if (!freeSubtract(template.expr[i], expr.expr[i], param, locked, dict)) return false
                }
                return true
            }
            is UnaryExpr -> return freeSubtract(template.expr.single(), (expr as UnaryExpr).expr.single(), param, locked, dict)
            is BinaryExpr -> return freeSubtract(template.l, (expr as BinaryExpr).l, param, locked, dict)
                    && freeSubtract(template.r, (expr).r, param, locked, dict)
            else -> return false
        }
    } else return false
}


fun isAxiomAny(expr: Expr): Boolean {
    if (expr is Impl && expr.l is All) {
        return freeSubtract((expr.l as All).expr.single(), expr.r, (expr.l as All).param, HashMap(), HashMap())
    } else return false
}

fun isAxiomExist(expr: Expr): Boolean {
    if (expr is Impl && expr.r is Exists) {
        return freeSubtract((expr.r as Exists).expr.single(), expr.l, (expr.r as Exists).param, HashMap(), HashMap())
    } else return false
}


fun newMatch(template: Expr, expr: Expr, locked: MutableSet<Expr>, dict: MutableMap<Expr, Expr>): Boolean {
    if (template is Var) {
        if (locked.contains(template)) {
            return template == expr
        } else {
            if (dict.containsKey(template)) {
                return dict[template] == expr
            } else {
                dict[template] = expr
                return true
            }
        }
    } else if (template::class == expr::class) {
        when (template) {
            is All -> {
                locked.add(template.param)
                val result = newMatch(template.expr.single(), (expr as All).expr.single(), locked, dict)
                locked.remove(template.param)
                return result
            }
            is Exists -> {
                locked.add(template.param)
                val result = newMatch(template.expr.single(), (expr as Exists).expr.single(), locked, dict)
                locked.remove(template.param)
                return result
            }
            is Pred -> {
                if (template.expr.size != (expr as Pred).expr.size) return false
                template.expr.zip(expr.expr).forEach { (fst: Expr, snd: Expr) ->
                    if (!newMatch(fst, snd, locked, dict)) return false
                }
                return true
            }
            is UnaryExpr -> return newMatch(template.expr.single(), (expr as UnaryExpr).expr.single(), locked, dict)
            is BinaryExpr -> return newMatch(template.l, (expr as BinaryExpr).l, locked, dict) &&
                    newMatch(template.r, expr.r, locked, dict)
            else -> return false
        }
    } else return false
}

fun isAnyFormalAxiom(expr: Expr): Boolean {
    if (formalAxiomsExprs.any { axiom -> newMatch(axiom, expr, HashSet(), HashMap()) }) return true
    if (isAxiomAny(expr) || isAxiomExist(expr)) return true
    return false
}
