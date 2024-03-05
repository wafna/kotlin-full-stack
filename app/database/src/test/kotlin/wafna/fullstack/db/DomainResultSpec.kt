package wafna.fullstack.db

import arrow.core.Either
import arrow.core.right
import io.kotest.assertions.fail
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.common.runBlocking
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import org.junit.jupiter.api.Test
import wafna.fullstack.domain.errors.DomainError
import kotlin.coroutines.cancellation.CancellationException

class DomainResultSpec {
    @Test
    fun `domainResult should return right when block does not throw`(): Unit = runBlocking {
        val result = domainResult { 42 }
        result shouldBe 42.right()
    }

    @Test
    fun `domainResult should return left when block throws`(): Unit = runBlocking {
        when (val result = domainResult { throw RuntimeException("wafna/fullstack/test") }) {
            is Either.Left ->
                result.value.shouldBeInstanceOf<DomainError.InternalServerError>()

            else ->
                fail("Expected Left, got $result")
        }
    }

    @Test
    fun `domainResult should rethrow CancellationException`(): Unit = runBlocking {
        shouldThrow<CancellationException> {
            domainResult { throw CancellationException() }
        }
    }
}
