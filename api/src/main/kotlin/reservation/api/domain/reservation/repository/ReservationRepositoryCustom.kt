package reservation.api.domain.reservation.repository

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import reservation.api.domain.reservation.entity.Reservation

interface ReservationRepositoryCustom {
    fun findReservationByEmployeeId(employeeId: Long): Reservation?
    fun findReservationBySeatId(seatId: Long?): Reservation?
    fun existsReservationByEmployeeIdAndSeatId(employeeId: Long?, seatId: Long?): Boolean
    fun findAllReservation(pageable: Pageable): Page<Reservation?>
    fun countReservedSeat(): Long?
}