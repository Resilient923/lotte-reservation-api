package reservation.api.domain.reservation.api

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import reservation.api.domain.reservation.dto.ReservationDTO

interface ReservationService {
    fun makeReservation(request: ReservationDTO.ReservationRequest?): ReservationDTO.ReservationResponse?
    fun cancelReservation(request: ReservationDTO.CancelReservationRequest?): ReservationDTO.CancelReservationResponse
    fun getAllEmployeesWithWorkType(pageable: Pageable): Page<ReservationDTO.ReservationResponse>
}