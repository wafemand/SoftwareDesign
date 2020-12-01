package visitors

import Tokenizer
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

class CalcVisitorTest {
    private var calcVisitor = RPNCalcVisitor()

    @BeforeEach
    fun prepareVisitor() {
        calcVisitor = RPNCalcVisitor()
    }

    @Test
    fun testOneNumberExpression() {
        val num = 22;
        val tokens = listOf(Tokenizer.Token.Number(num), Tokenizer.Token.EOF)
        calcVisitor.visitAll(tokens)
        assertEquals(num, calcVisitor.result.asInt)
    }

    @Test
    fun testWithoutVisiting() {
        assertThrows<NoSuchElementException> {
            calcVisitor.result.asInt
        }
    }

    @Test
    fun testSimpleExpression() {
        val a = 2
        val b = 7
        val tokens = listOf(
            Tokenizer.Token.Number(a),
            Tokenizer.Token.Number(b),
            Tokenizer.Token.Operation.Plus,
            Tokenizer.Token.EOF
        )
        calcVisitor.visitAll(tokens)
        assertEquals(a + b, calcVisitor.result.asInt)
    }

    @Test
    fun testComplexExpression() {
        val tokens = listOf(
            Tokenizer.Token.Number(2),
            Tokenizer.Token.Number(3),
            Tokenizer.Token.Number(5),
            Tokenizer.Token.Operation.Plus,
            Tokenizer.Token.Operation.Minus,
            Tokenizer.Token.Number(2),
            Tokenizer.Token.Operation.Div,
            Tokenizer.Token.EOF
        )
        calcVisitor.visitAll(tokens)
        assertEquals(calcVisitor.result.asInt, -3)
    }

    @Test
    fun testMissingOperand() {
        assertThrows<IllegalStateException> {
            val tokens = listOf(Tokenizer.Token.Number(1), Tokenizer.Token.Operation.Mul)
            calcVisitor.visitAll(tokens)
        }
    }
}