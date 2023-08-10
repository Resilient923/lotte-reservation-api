package reservation.api.domain.reservation.dto

import jakarta.validation.constraints.NotBlank
import org.springframework.data.domain.Page
import reservation.api.common.dto.PagingResponse
import reservation.api.common.entity.WorkType
import reservation.api.domain.reservation.entity.Reservation

class ReservationDTO {

    data class EmployeeWorkTypeListResponse(
        val employeeWorkTypeResponseList: List<ReservationResponse>,
        val pagingResponse: PagingResponse
    ) {
        constructor(employeeWorkTypeResponseList: List<ReservationResponse>, page: Page<*>) : this(
            employeeWorkTypeResponseList, PagingResponse(page)
        )
    }

    data class ReservationResponse(
        val employeeNum: Long?,
        val workType: WorkType?,
        val seatNum: Long?
    ) {
        constructor(reservation: Reservation) : this(
            employeeNum = reservation.employee?.employeeNum,
            workType = reservation.employee?.workType,
            seatNum = reservation.seat?.seatNum
        )
    }

    data class CancelReservationResponse(
        val id: Long?,
        val employeeNum: Long?,
        val seatNum: Long?
    ) {
        constructor(reservation: Reservation) : this(
            id = reservation.id,
            employeeNum = reservation.employee?.employeeNum,
            seatNum = reservation.seat?.seatNum
        )
    }

    data class CancelReservationRequest(
        @NotBlank(message = "employeeId를 입력하세요.") val employeeId: Long?
    )

    data class ReservationRequest(
        @NotBlank(message = "employeeId를 입력하세요.") val employeeId: Long?,
        @NotBlank(message = "seatId를 입력하세요.") val seatId: Long?

    )

    data class ReservationErrorResponse(private val message: String) {
        val reservationId: Long? = null
        val employeeNum: Long? = null
        val workType: WorkType? = null
    }
}