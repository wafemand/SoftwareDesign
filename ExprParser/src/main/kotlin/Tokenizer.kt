import visitors.TokenVisitor

class Tokenizer {
    sealed class Token {
        abstract fun accept(visitor: TokenVisitor)
        sealed class Operation(val priority: Int) : Token() {
            override fun accept(visitor: TokenVisitor) = visitor.visit(this)

            object Plus : Operation(0)
            object Minus : Operation(0)
            object Mul : Operation(1)
            object Div : Operation(1)
        }

        sealed class Brace : Token() {
            override fun accept(visitor: TokenVisitor) = visitor.visit(this)

            object Open : Brace()
            object Close : Brace()
        }

        data class Number(val value: Int) : Token() {
            override fun accept(visitor: TokenVisitor) = visitor.visit(this)
        }

        object EOF : Token() {
            override fun accept(visitor: TokenVisitor) = visitor.visit(this)
        }
    }

    private val tokens = ArrayList<Token>()
    private var curState: State = StartState()

    fun tokenize(s: String): List<Token> {
        curState = StartState()
        s.forEach { curState.handle(it) }
        curState.handleEOF()
        require(curState is EOFState)
        tokens.add(Token.EOF)
        return tokens
    }

    private abstract inner class State {
        abstract fun handle(c: Char)
        abstract fun handleEOF()
    }

    private inner class EOFState : State() {
        override fun handle(c: Char) {
            throw UnsupportedOperationException("Expected EOF")
        }

        override fun handleEOF() {}
    }

    private inner class NumericState : State() {
        private var number = 0;

        override fun handle(c: Char) {
            when (c) {
                in '0'..'9' -> {
                    number = number * 10 + (c - '0')
                }
                else -> {
                    tokens.add(Token.Number(number))
                    curState = StartState()
                    curState.handle(c)
                }
            }
        }

        override fun handleEOF() {
            tokens.add(Token.Number(number))
            curState = EOFState()
        }
    }

    private inner class StartState : State() {
        override fun handle(c: Char) {
            when (c) {
                '(' -> tokens.add(Token.Brace.Open)
                ')' -> tokens.add(Token.Brace.Close)
                '+' -> tokens.add(Token.Operation.Plus)
                '-' -> tokens.add(Token.Operation.Minus)
                '*' -> tokens.add(Token.Operation.Mul)
                '/' -> tokens.add(Token.Operation.Div)
                in '0'..'9' -> {
                    curState = NumericState()
                    curState.handle(c)
                }
                else -> {
                    require(c.isWhitespace()) { "Unexpected symbol '$c'" }
                }
            }
        }

        override fun handleEOF() {
            curState = EOFState()
        }
    }
}