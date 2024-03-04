package wafna.kdbc

import java.sql.ResultSet

/**
 * Provides the syntax that separates the SQL from the parameters.
 */
abstract class ParamCollector<T> internal constructor() {
    abstract operator fun invoke(vararg params: Any?): T
}

/**
 * Collects parameters with a follow-on reader.
 */
abstract class SelectParamCollector<T> internal constructor() {
    abstract operator fun invoke(vararg params: Any?): ResultSetReceiver<T>
}

/**
 * Receives the method for reading records from a result set.
 */
abstract class ResultSetReceiver<T> {
    abstract fun read(read: (ResultSet) -> T): List<T>
    abstract fun read(reader: RecordReader<T>): List<T>
}

interface RecordReader<T> {
    fun read(resultSet: ResultSet): T
}

/**
 * Receives the method for writing records into a batch.
 */
abstract class BatchReceiver<T> {
    abstract fun write(write: (T) -> List<Any?>): List<Int>
    abstract fun write(writer: BatchWriter<T>): List<Int>
}

interface BatchWriter<T> {
    fun write(record: T): List<Any?>
}
