package visitors

import java.lang.IllegalStateException
import java.util.*
import kotlin.collections.ArrayList

class ParserVisitor : TokenVisitor {
    private val stack = Stack<Tokenizer.Token>()
    val result = ArrayList<Tokenizer.Token>()

    override fun visit(token: Tokenizer.Token.Number) {
        result.add(token)
    }

    override fun visit(token: Tokenizer.Token.Brace) {
        when (token) {
            Tokenizer.Token.Brace.Open -> stack.push(token)
            Tokenizer.Token.Brace.Close -> {
                while (!stack.empty()) {
                    when (val lastToken = stack.pop()) {
                        is Tokenizer.Token.Operation -> {
                            result.add(lastToken)
                            continue
                        }
                        Tokenizer.Token.Brace.Open -> break
                        else -> throw IllegalStateException("Wrong stack state: ${stack.toList()}")
                    }
                }
            }
        }
    }

    override fun visit(token: Tokenizer.Token.Operation) {
        while (!stack.empty()) {
            when (val lastToken = stack.peek()) {
                is Tokenizer.Token.Operation -> when {
                    lastToken.priority >= token.priority -> {
                        result.add(lastToken)
                        stack.pop()
                    }
                    else -> break
                }
                Tokenizer.Token.Brace.Open -> break
                else -> throw IllegalStateException("Wrong stack state: ${stack.toList()}")
            }
        }
        stack.push(token)
    }

    override fun visit(token: Tokenizer.Token.EOF) {
        while (!stack.empty()) {
            when (val lastToken = stack.pop()) {
                is Tokenizer.Token.Operation -> result.add(lastToken)
                else -> throw IllegalStateException(
                    "No matching closing bracket\n" +
                            "Stack state: ${stack.toList()}"
                )
            }
        }
        result.add(token)
    }
}