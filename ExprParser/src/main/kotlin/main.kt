import visitors.ParserVisitor
import visitors.PrintVisitor
import visitors.RPNCalcVisitor

fun main() {
    val input = readLine() ?: throw IllegalArgumentException("Input is null")
    try {
        val tokens = Tokenizer().tokenize(input)
        val printVisitor = PrintVisitor()
        println("Tokens:")
        printVisitor.visitAll(tokens)

        val parserVisitor = ParserVisitor()
        parserVisitor.visitAll(tokens)
        val RPNTokens = parserVisitor.result
        println("Reverse Polish notation:")
        printVisitor.visitAll(RPNTokens)

        val calcVisitor = RPNCalcVisitor()
        calcVisitor.visitAll(RPNTokens)
        val calcResult = calcVisitor.result.orElseThrow{
            IllegalStateException("RPNCalcVisitor.result is not ready (EOF expected)")
        }
        println("Result: $calcResult")
    } catch (e: Throwable) {
        println("Error occurred during execution: ${e.message}")
    }
}