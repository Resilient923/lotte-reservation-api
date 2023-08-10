package reservation.api.domain.reservation.repository

import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.support.PageableExecutionUtils
import org.springframework.stereotype.Repository
import reservation.api.domain.reservation.entity.QReservation.reservation
import reservation.api.domain.reservation.entity.Reservation

@Repository
class ReservationRepositoryCustomImpl(private var query: JPAQueryFactory) : ReservationRepositoryCustom {

    override fun findReservationByEmployeeId(employeeId: Long): Reservation? {
        return query
            .selectFrom(reservation)
            .where(reservation.employee.id.eq(employeeId), reservation.reserved)
            .fetchOne()
    }

    override fun findReservationBySeatId(seatId: Long?): Reservation? {
        return query
            .selectFrom(reservation)
            .where(reservation.seat.id.eq(seatId), reservation.reserved)
            .fetchOne()
    }

    override fun existsReservationByEmployeeIdAndSeatId(employeeId: Long?, seatId: Long?): Boolean {
        return query
            .selectFrom(reservation)
            .where(reservation.employee.id.eq(employeeId), reservation.seat.id.eq(seatId))
            .fetchFirst() != null
    }

    override fun findAllReservation(pageable: Pageable): Page<Reservation?> {
        val result: List<Reservation> = query
            .selectFrom(reservation)
            .where(reservation.reserved.eq(true))
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .fetch()
        val countQuery = query
            .select(reservation.count())
            .from(reservation)
            .where(reservation.reserved)
        return PageableExecutionUtils.getPage(result, pageable) { countQuery.fetchOne()!! }
    }

    override fun countReservedSeat(): Long? {
        return query
            .select(reservation.reserved.count())
            .from(reservation)
            .where(reservation.reserved)
            .fetchOne()
    }
}