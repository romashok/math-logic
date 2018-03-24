package ctd.lebedev.dev.grammar

sealed class Expr {
    abstract fun eval(vars: MutableMap<String, Boolean>): Boolean
}

sealed class UnaryExpr(vararg var expr: Expr) : Expr() {
    override fun equals(other: Any?): Boolean {
        if (other is UnaryExpr) {
            return expr.size == other.expr.size &&
                    expr.zip(other.expr).all { (fst, snd) -> fst == snd }
        }
        return super.equals(other)
    }

    override fun hashCode(): Int {
        val mod = 1e7 + 19
        return expr.fold(0, { acc, e -> (acc + e.hashCode()) % mod.toInt() })
    }
}

sealed class BinaryExpr(var l: Expr, var r: Expr) : Expr() {
    override fun equals(other: Any?): Boolean {
        if (other is BinaryExpr) return l == other.l && r == other.r
        return super.equals(other)
    }

    override fun hashCode(): Int {
        var result = l.hashCode()
        result = 31 * result + r.hashCode()
        return result
    }
}

class Impl(l: Expr, r: Expr) : BinaryExpr(l, r) {
    override fun toString() = "($l->$r)"
    override fun eval(vars: MutableMap<String, Boolean>) = !l.eval(vars) || r.eval(vars)
}

class Or(l: Expr, r: Expr) : BinaryExpr(l, r) {
    override fun toString() = "($l|$r)"
    override fun eval(vars: MutableMap<String, Boolean>) = l.eval(vars) || r.eval(vars)
}

class And(l: Expr, r: Expr) : BinaryExpr(l, r) {
    override fun toString() = "($l&$r)"
    override fun eval(vars: MutableMap<String, Boolean>) = l.eval(vars) && r.eval(vars)
}

class Not(expr: Expr) : UnaryExpr(expr) {
    override fun toString() = if (expr.size == 1) "(!${expr.single()})" else "(!${expr.contentDeepToString()})"
    override fun eval(vars: MutableMap<String, Boolean>) = !expr.first().eval(vars)
}

class Var(var value: String) : Expr() {
    override fun toString() = value
    override fun eval(vars: MutableMap<String, Boolean>): Boolean = vars[value]!!
    override fun equals(other: Any?): Boolean {
        if (other is Var) return value == other.value
        return super.equals(other)
    }

    override fun hashCode(): Int {
        return value.hashCode()
    }
}

open class All(var param: Var, expr: Expr) : UnaryExpr(expr) {
    override fun toString() = "(@$param${expr.joinToString(",")})"

    override fun eval(vars: MutableMap<String, Boolean>) = TODO("eval All")
}

open class Exists(var param: Var, expr: Expr) : UnaryExpr(expr) {
    override fun toString() = "(?$param${expr.joinToString(",")})"


    override fun eval(vars: MutableMap<String, Boolean>) = TODO("eval Exists")
}


class Pred(var name: String, vararg values: Expr) : UnaryExpr(*values) {
    override fun toString() = if (expr.isEmpty()) name else "$name(${expr.joinToString(",")})"

    override fun eval(vars: MutableMap<String, Boolean>) = TODO("eval Pred")
}


class Equals(l: Expr, r: Expr) : BinaryExpr(l, r) {
    override fun toString() = "($l=$r)"

    override fun eval(vars: MutableMap<String, Boolean>) = TODO("eval Equals")
}


class Sum(l: Expr, r: Expr) : BinaryExpr(l, r) {
    override fun toString() = "($l+$r)"

    override fun eval(vars: MutableMap<String, Boolean>) = TODO("eval Sum")
}

class Mul(l: Expr, r: Expr) : BinaryExpr(l, r) {
    override fun toString() = "($l*$r)"

    override fun eval(vars: MutableMap<String, Boolean>) = TODO("eval Mul")
}


class Next(var value: Expr) : Expr() {
    override fun toString() = "$value'"

    override fun eval(vars: MutableMap<String, Boolean>) = TODO("eval Mul")
}
