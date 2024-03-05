package wafna.kdbc

import java.sql.ResultSet

/**
 * Collects parameters for a select statement, which will need a record reader.
 */
abstract class SelectParamReceiver<T> internal constructor() {
    abstract operator fun invoke(vararg params: Any?): RecordReaderReceiver<T>
}

/**
 * Collects parameters for an update statement (UPDATE or DELETE).
 */
abstract class UpdateParamReceiver<T> internal constructor() {
    abstract operator fun invoke(vararg params: Any?): T
}

/**
 * Receives the method for reading records from a result set.
 */
abstract class RecordReaderReceiver<T> {
    abstract fun read(read: (ResultSet) -> T): List<T>
    abstract fun read(reader: RecordReader<T>): List<T>
}

interface RecordReader<T> {
    fun read(resultSet: ResultSet): T
}

/**
 * Receives the method for writing records into a batch.
 */
abstract class RecordWriterReceiver<T> {
    abstract fun write(write: (T) -> List<Any?>): List<Int>
    abstract fun write(writer: RecordWriter<T>): List<Int>
}

interface RecordWriter<T> {
    fun write(record: T): List<Any?>
}
