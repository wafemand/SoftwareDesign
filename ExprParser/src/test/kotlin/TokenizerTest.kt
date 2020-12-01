import Tokenizer
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals
import kotlin.test.assertFails

class TokenizerTest {
    private var tokenizer = Tokenizer();

    @BeforeEach
    fun prepareTokenizer() {
        tokenizer = Tokenizer()
    }

    @Test
    fun testSimpleExpression() {
        val s = "2 + 2"
        val tokens = tokenizer.tokenize(s)
        assertEquals(
            listOf(
                Tokenizer.Token.Number(2),
                Tokenizer.Token.Operation.Plus,
                Tokenizer.Token.Number(2),
                Tokenizer.Token.EOF
            ), tokens
        )
    }

    @Test
    fun testSimpleExpressionWithoutWhiteSpaces() {
        val s = "31+7*4"
        val tokens = tokenizer.tokenize(s)
        assertEquals(
            listOf(
                Tokenizer.Token.Number(31),
                Tokenizer.Token.Operation.Plus,
                Tokenizer.Token.Number(7),
                Tokenizer.Token.Operation.Mul,
                Tokenizer.Token.Number(4),
                Tokenizer.Token.EOF
            ),
            tokens
        )
    }

    @Test
    fun testParenthesis() {
        val s = "9 / (4- 1)"
        val tokens = tokenizer.tokenize(s)
        assertEquals(
            listOf(
                Tokenizer.Token.Number(9),
                Tokenizer.Token.Operation.Div,
                Tokenizer.Token.Brace.Open,
                Tokenizer.Token.Number(4),
                Tokenizer.Token.Operation.Minus,
                Tokenizer.Token.Number(1),
                Tokenizer.Token.Brace.Close,
                Tokenizer.Token.EOF
            ),
            tokens
        )
    }

    @Test
    fun incorrectTest() {
        val s = "1 / 2 + j"
        assertThrows<IllegalArgumentException> { tokenizer.tokenize(s) }
    }
}