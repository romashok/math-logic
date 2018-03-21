package ctd.lebedev.dev.grammar

sealed class Expr {
    abstract fun eval(vars: MutableMap<String, Boolean>): Boolean
}

sealed class UnaryExpr(var expr: Expr) : Expr() {
    override fun equals(other: Any?): Boolean {
        if (other is UnaryExpr) return expr == other.expr
        return super.equals(other)
    }

    override fun hashCode(): Int {
        return expr.hashCode()
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
    override fun toString() = "(!$expr)"
    override fun eval(vars: MutableMap<String, Boolean>) = !expr.eval(vars)
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
