package plots

import kotlin.math.sqrt

data class Stats(
    val n: Int,
    val min: Double,
    val max: Double,
    val avg: Double,
    val med: Double,
    val stdDev: Double
) {
    companion object {
        fun fromDataSet(xs: Iterable<Double>): Stats {
            var n = 0
            var sumX = 0.0
            var sumX2 = 0.0
            var min: Double? = null
            var max: Double? = null
            val list = mutableListOf<Double>()
            for (x in xs) {
                n += 1
                sumX += x
                sumX2 += (x * x)
                list.add(x)
                if (null == min || x < min) min = x
                if (null == max || x > max) max = x
            }
            if (0 == n)
                return Stats(0, 0.0, 0.0, 0.0, 0.0, 0.0)
            val avg = sumX / n
            val median = xs.sorted().run {
                if (1 == size) first()
                else {
                    if (0 == size % 2) {
                        val partition = size / 2
                        val lo = drop(partition - 1).first()
                        val hi = drop(partition).first()
                        (lo + hi) / 2.0
                    } else {
                        drop((size - 1) / 2).first()
                    }
                }
            }
            val stdDev = sqrt((sumX2 / n.toDouble()) - (avg * avg))
            return Stats(n, min ?: 0.0, max ?: 0.0, avg, median, stdDev)
        }
    }
}
