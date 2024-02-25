package wafna.dbexplorer.db

import arrow.core.Either
import arrow.core.right
import io.kotest.assertions.fail
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.common.runBlocking
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import wafna.dbexplorer.domain.errors.DomainError
import kotlin.coroutines.cancellation.CancellationException

class DomainResultSpec : StringSpec({
    "domainResult should return right when block does not throw" {
        val result = domainResult { 42 }
        result shouldBe 42.right()
    }

    "domainResult should return left when block throws" {
        when (val result = domainResult { throw RuntimeException("test") }) {
            is Either.Left ->
                result.value.shouldBeInstanceOf<DomainError.InternalServerError>()

            else ->
                fail("Expected Left, got $result")
        }
    }

    "domainResult should rethrow CancellationException" {
        shouldThrow<CancellationException> {
            runBlocking {
                domainResult { throw CancellationException() }
            }
        }
    }
})
