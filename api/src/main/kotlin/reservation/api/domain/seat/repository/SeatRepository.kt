package reservation.api.domain.seat.repository

import org.springframework.data.jpa.repository.JpaRepository
import reservation.api.domain.seat.entity.Seat

interface SeatRepository : JpaRepository<Seat?, Long?>