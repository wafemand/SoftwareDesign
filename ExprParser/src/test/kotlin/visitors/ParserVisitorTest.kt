package visitor

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import visitors.ParserVisitor
import visitors.TokenVisitor
import kotlin.test.assertEquals

class ParserVisitorTest {
    private var visitor = ParserVisitor()

    @BeforeEach
    fun prepare() {
        visitor = ParserVisitor()
    }

    @Test
    fun testSimple() {
        val a = Tokenizer.Token.Number(1)
        val b = Tokenizer.Token.Number(2)
        val op = Tokenizer.Token.Operation.Plus
        visitor.visitAll(listOf(a, op, b, Tokenizer.Token.EOF))
        assertEquals(listOf(a, b, op, Tokenizer.Token.EOF), visitor.result)
    }

    @Test
    fun testSimpleParenthesis() {
        val a = Tokenizer.Token.Number(1)
        val b = Tokenizer.Token.Number(2)
        val c = Tokenizer.Token.Number(3)
        val tokens = listOf(
            Tokenizer.Token.Brace.Open,
            a,
            Tokenizer.Token.Operation.Plus,
            b,
            Tokenizer.Token.Brace.Close,
            Tokenizer.Token.Operation.Mul,
            c,
            Tokenizer.Token.EOF
        )
        visitor.visitAll(tokens)
        assertEquals(
            listOf(
                a, b,
                Tokenizer.Token.Operation.Plus,
                c,
                Tokenizer.Token.Operation.Mul,
                Tokenizer.Token.EOF
            ),
            visitor.result
        )
    }

    @Test
    fun testDifferentPriorities() {
        val a = Tokenizer.Token.Number(1)
        val b = Tokenizer.Token.Number(2)
        val c = Tokenizer.Token.Number(3)
        val d = Tokenizer.Token.Number(4)
        val e = Tokenizer.Token.Number(5)
        val tokens = listOf(
            a,
            Tokenizer.Token.Operation.Plus,
            b,
            Tokenizer.Token.Operation.Mul,
            c,
            Tokenizer.Token.Operation.Div,
            d,
            Tokenizer.Token.Operation.Plus,
            e,
            Tokenizer.Token.EOF
        )
        visitor.visitAll(tokens)
        assertEquals(
            listOf(
                a,
                b,
                c,
                Tokenizer.Token.Operation.Mul,
                d,
                Tokenizer.Token.Operation.Div,
                Tokenizer.Token.Operation.Plus,
                e,
                Tokenizer.Token.Operation.Plus,
                Tokenizer.Token.EOF
            ),
            visitor.result
        )
    }

    @Test
    fun noMatchingBracketTest() {
        assertThrows<IllegalStateException> {
            val tokens = listOf(
                Tokenizer.Token.Brace.Open,
                Tokenizer.Token.Number(2),
                Tokenizer.Token.Operation.Plus,
                Tokenizer.Token.Number(3),
                Tokenizer.Token.EOF
            )
            visitor.visitAll(tokens)
        }
    }
}