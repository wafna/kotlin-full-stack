package wafna.kdbc

import java.sql.ResultSet

/**
 * Collects parameters for a select statement.
 */
interface SelectParamReceiver<T> {
    operator fun invoke(vararg params: Any?): RecordReaderReceiver<T>
}

/**
 * Collects parameters for an update statement (UPDATE or DELETE).
 */
interface UpdateParamReceiver<T> {
    operator fun invoke(vararg params: Any?): T
}

/**
 * Receives the method for reading records from a result set.
 */
interface RecordReaderReceiver<T> {
    fun read(read: (ResultSet) -> T): List<T>
    fun read(reader: RecordReader<T>): List<T>
}

interface RecordReader<T> {
    fun read(resultSet: ResultSet): T
}

/**
 * Receives the method for writing records into a batch.
 */
interface RecordWriterReceiver<T> {
    fun write(write: (T) -> List<Any?>): List<Int>
    fun write(writer: RecordWriter<T>): List<Int>
}

interface RecordWriter<T> {
    fun write(record: T): List<Any?>
}
