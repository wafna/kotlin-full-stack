package domain

import kotlinx.serialization.Serializable

@Serializable
data class HomePageData(
    val dataBlocks: List<DataBlock>,
)
