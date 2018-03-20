package ctd.lebedev.dev.grammar

typealias Token = String
typealias ParseResult = Pair<Expr, List<Token>>

fun addSpaces(s: String): String {
    var spaced = s
    listOf("->", "|", "&", "!", "(", ")").forEach { spaced = spaced.replace(it, " $it ") }
    return spaced.trim()
}

fun splitToTokens(s: String): List<String> = addSpaces(s).split(" ").filter { it.isNotBlank() }

fun parseExpr(s: String): Expr {
    val tokens = splitToTokens(s)
    val (tree, _) = parseImpl(tokens)
    return tree
}

fun parseImpl(tokens: List<Token>): ParseResult {
    var (tree, tail) = parseOr(tokens)
    while (!tail.isEmpty() && tail.first() == "->") {
        val (nextTree, nextTail) = parseImpl(tail.drop(1))
        tree = Impl(tree, nextTree)
        tail = nextTail
    }
    return Pair(tree, tail)
}

fun parseOr(tokens: List<Token>): ParseResult {
    var (tree, tail) = parseAnd(tokens)
    while (!tail.isEmpty() && tail.first() == "|") {
        val (nextTree, nextTail) = parseAnd(tail.drop(1))
        tree = Or(tree, nextTree)
        tail = nextTail
    }
    return Pair(tree, tail)
}

fun parseAnd(tokens: List<Token>): ParseResult {
    var (tree, tail) = parseNot(tokens)
    while (!tail.isEmpty() && tail.first() == "&") {
        val (nextTree, nextTail) = parseNot(tail.drop(1))
        tree = And(tree, nextTree)
        tail = nextTail
    }
    return Pair(tree, tail)
}

fun parseNot(tokens: List<Token>): ParseResult = when (tokens.first()) {
    "!" -> {
        val (tree, tail) = parseNot(tokens.drop(1))
        Pair(Not(tree), tail)
    }
    else -> parseUnary(tokens)
}

fun parseUnary(tokens: List<Token>): ParseResult = when (tokens.first()) {
    "(" -> {
        val (tree, tail) = parseImpl(tokens.drop(1))
        Pair(tree, tail.drop(1))
    }
    else -> Pair(Var(tokens.first()), tokens.drop(1))
}
