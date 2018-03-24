package ctd.lebedev.dev.grammar

class FormalParser() {
    var string: String = ""
    var index: Int = 0

    fun parseExpr(exprStr: String): Expr? {
        string = exprStr
        index = 0
        return parseImpl()
    }

    fun parse(): Expr? {
        if (index >= string.length) return null
        return parseImpl()
    }

    fun readVarName(): String {
        var j = index
        while (j < string.length && (string[j].isDigit() || string[j].isLetter() && string[j].isLowerCase())) j++
        val result = string.slice(index until j)
        index = j
        return result
    }

    fun readPredName(): String {
        var j = index
        if (string[j].isLetter() && string[j].isLowerCase()) return ""

        while (j < string.length && (string[j].isDigit() || string[j].isLetter() && string[j].isUpperCase())) j++
        val result = string.slice(index until j)
        index = j
        return result
    }

    fun parseImpl(): Expr {
        val result = parseOr()
        if (index < string.length && string[index] == '-') {
            index += 2
            val tmp = parseImpl()
            return Impl(result, tmp)
        } else return result
    }

    fun parseOr(): Expr {
        var result = parseAnd()
        while (index < string.length && string[index] == '|') {
            index++
            val tmp = parseAnd()
            result = Or(result, tmp)
        }
        return result
    }

    fun parseAnd(): Expr {
        var result = parseUnary()
        while (index < string.length && string[index] == '&') {
            index++
            val tmp = parseUnary()
            result = And(result, tmp)
        }
        return result
    }

    fun parseUnary(): Expr {
        if (string[index] == '!') {
            index++
            val tmp = parseUnary()
            return Not(tmp)
        } else if (string[index] == '@' || string[index] == '?') {
            val symbol = string[index]
            index++
            val word = readVarName()
            val tmp = parseUnary()
            return when (symbol) {
                '@' -> All(Var(word), tmp)
                else -> Exists(Var(word), tmp)
            }
        }

        var result = parsePred()
        if (result != null) return result

        if (index < string.length && string[index] == '(') {
            index++
            result = parseImpl()
            index++
            return result
        }

        val tmp = readVarName()
        return Var(tmp)
    }

    fun parsePred(): Expr? {
        val word = readPredName()
        if (word.isNotEmpty()) {
            val args = parseArgs()
            return Pred(word, *args.toTypedArray())
        } else {
            val save = index
            val result = parseTerm()
            if (index >= string.length || string[index] != '=') {
                index = save
                return null
            }
            index++
            return Equals(result, parseTerm())
        }
    }

    fun parseArgs(): MutableList<Expr> {
        val result: MutableList<Expr> = ArrayList()
        if (index >= string.length || string[index] != '(') return result

        index++
        result.add(parseTerm())
        while (index < string.length && string[index] != ')') {
            index++
            result.add(parseTerm())
        }
        index++
        return result
    }


    fun parseTerm(): Expr {
        var result = parseSum()
        while (index < string.length && string[index] == '+') {
            index++
            val tmp = parseSum()
            result = Sum(result, tmp)
        }
        return result
    }

    fun parseSum(): Expr {
        var result = parseMul()
        while (index < string.length && string[index] == '*') {
            index++
            val tmp = parseMul()
            result = Mul(result, tmp)
        }
        return result
    }

    fun parseMul(): Expr {
        var result: Expr? = null
        if (index < string.length && string[index] == '(') {
            index++
            result = parseTerm()
            index++
            return parseNext(result)
        }
        val word = readVarName()
        if (index < string.length && string[index] == '(') {
            val values = parseArgs()
            result = Pred(word, *values.toTypedArray())
        } else {
            result = Var(word)
        }
        return parseNext(result)
    }

    fun parseNext(value: Expr): Expr {
        var newValue = value
        while (index < string.length && string[index] == '\'') {
            index++
            newValue = Next(newValue)
        }
        return newValue
    }
}