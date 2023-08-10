package reservation.api.domain.reservation.repository

import org.springframework.data.jpa.repository.JpaRepository
import reservation.api.domain.reservation.entity.Reservation

interface ReservationRepository : JpaRepository<Reservation?, Long?>, ReservationRepositoryCustom