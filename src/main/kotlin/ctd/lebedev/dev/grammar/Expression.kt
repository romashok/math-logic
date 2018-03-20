package ctd.lebedev.dev.grammar

sealed class Expr {
    abstract fun eval(vars: MutableMap<String, Boolean>): Boolean
}

sealed class UnaryExpr(var exp: Expr) : Expr()
sealed class BinaryExpr(var l: Expr, var r: Expr) : Expr()

data class Impl(var lhs: Expr, var rhs: Expr) : BinaryExpr(lhs, rhs) {
    override fun toString() = "($l->$r)"
    override fun eval(vars: MutableMap<String, Boolean>) = !l.eval(vars) || r.eval(vars)
}

data class Or(var lhs: Expr, var rhs: Expr) : BinaryExpr(lhs, rhs) {
    override fun toString() = "($l|$r)"
    override fun eval(vars: MutableMap<String, Boolean>) = l.eval(vars) || r.eval(vars)
}

data class And(var lhs: Expr, var rhs: Expr) : BinaryExpr(lhs, rhs) {
    override fun toString() = "($l&$r)"
    override fun eval(vars: MutableMap<String, Boolean>) = l.eval(vars) && r.eval(vars)
}

data class Not(var expr: Expr) : UnaryExpr(expr) {
    override fun toString() = "(!$exp)"
    override fun eval(vars: MutableMap<String, Boolean>) = !exp.eval(vars)
}

data class Var(var value: String) : Expr() {
    override fun toString() = value
    override fun eval(vars: MutableMap<String, Boolean>): Boolean = vars[value]!!
}
