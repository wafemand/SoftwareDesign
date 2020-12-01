package visitors

class PrintVisitor : TokenVisitor {
    override fun visit(token: Tokenizer.Token.Number) = print("NUMBER(${token.value}) ")

    override fun visit(token: Tokenizer.Token.Brace) = print(
        when (token) {
            Tokenizer.Token.Brace.Open -> "LEFT"
            Tokenizer.Token.Brace.Close -> "RIGHT"
        } + " "
    )

    override fun visit(token: Tokenizer.Token.Operation) = print(
        when (token) {
            Tokenizer.Token.Operation.Plus -> "PLUS"
            Tokenizer.Token.Operation.Minus -> "MINUS"
            Tokenizer.Token.Operation.Mul -> "MUL"
            Tokenizer.Token.Operation.Div -> "DIV"
        } + " "
    )

    override fun visit(token: Tokenizer.Token.EOF) = println()
}