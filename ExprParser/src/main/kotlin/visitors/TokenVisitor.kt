package visitors

interface TokenVisitor {
    fun visit(token: Tokenizer.Token.Number)
    fun visit(token: Tokenizer.Token.Brace)
    fun visit(token: Tokenizer.Token.Operation)
    fun visit(token: Tokenizer.Token.EOF)
    fun visitAll(tokens: List<Tokenizer.Token>) {
        tokens.forEach { it.accept(this) }
    }
}