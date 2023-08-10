package reservation.api.common.dto

import org.springframework.data.domain.Page
import reservation.api.domain.reservation.entity.Reservation

data class PagingResponse(
    val currentPage: Int,
    val totalPages: Int,
    val pageSize: Int,
    val totalElements: Long,
    val hasNext: Boolean,
    val hasPrevious: Boolean
) {
    constructor(page: Page<*>) : this(
        currentPage = page.number + 1,
        totalPages = page.totalPages,
        pageSize = page.size,
        totalElements = page.totalElements,
        hasNext = page.hasNext(),
        hasPrevious = page.hasPrevious()
    )
}