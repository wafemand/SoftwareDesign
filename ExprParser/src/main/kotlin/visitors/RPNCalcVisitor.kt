package visitors

import java.lang.IllegalArgumentException
import java.util.*

class RPNCalcVisitor : TokenVisitor {
    private val stack = Stack<Int>()
    var result = OptionalInt.empty()

    override fun visit(token: Tokenizer.Token.Number) {
        stack.push(token.value)
    }

    override fun visit(token: Tokenizer.Token.Brace) {
        throw IllegalArgumentException("Reverse Polish notation cannot contain braces")
    }

    override fun visit(token: Tokenizer.Token.Operation) {
        if (stack.size < 2) {
            throw IllegalStateException("Stack underflow while computing")
        }
        val right = stack.pop()
        val left = stack.pop()
        stack.push(
            when (token) {
                Tokenizer.Token.Operation.Plus -> left + right
                Tokenizer.Token.Operation.Minus -> left - right
                Tokenizer.Token.Operation.Mul -> left * right
                Tokenizer.Token.Operation.Div -> left / right
            }
        )
    }

    override fun visit(token: Tokenizer.Token.EOF) {
        if (stack.size != 1) {
            throw IllegalStateException("Expected only one element in the stack: ${stack.toList()}")
        }
        result = OptionalInt.of(stack.pop())
    }
}