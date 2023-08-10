package reservation.api.domain.reservation.api

import mu.KotlinLogging
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reservation.api.common.entity.WorkType
import reservation.api.common.exception.EntityNotFoundException
import reservation.api.common.exception.ErrorCode
import reservation.api.common.exception.ReservationException
import reservation.api.domain.employee.entity.Employee
import reservation.api.domain.employee.repository.EmployeeRepository
import reservation.api.domain.reservation.dto.ReservationDTO
import reservation.api.domain.reservation.entity.Reservation
import reservation.api.domain.reservation.repository.ReservationRepository
import reservation.api.domain.seat.entity.Seat
import reservation.api.domain.seat.repository.SeatRepository

private val logger = KotlinLogging.logger {}

@Service
class ReservationServiceImpl(
    private val reservationRepository: ReservationRepository,
    private val employeeRepository: EmployeeRepository,
    private val seatRepository: SeatRepository
) : ReservationService {

    // 전 직원 근무형태 현황 조회 API
    override fun getAllEmployeesWithWorkType(pageable: Pageable): Page<ReservationDTO.ReservationResponse> {
        val allReservation: Page<Reservation?> = pageable.let { reservationRepository.findAllReservation(it) }
        val reservationResponseList: MutableList<ReservationDTO.ReservationResponse> = allReservation
            .mapNotNull { reservation -> reservation?.let { ReservationDTO.ReservationResponse(it) } }
            .toMutableList()

        return PageImpl(reservationResponseList, pageable, allReservation.totalElements)
    }

    // 좌석 예약 API
    @Transactional
    override fun makeReservation(request: ReservationDTO.ReservationRequest?): ReservationDTO.ReservationResponse? {
        val employeeId: Long = request!!.employeeId!!
        val seatId: Long = request.seatId!!
        val employee: Employee? = getEmployee(employeeId)
        val seat: Seat? = getSeat(seatId)
        logger.info("employeeId info : {}, seatId info : {}", employeeId, seatId)

        // 좌석이 모두 예약되는 경우, 예약하지 못한 직원은 자동으로 재택근무 형태가 지정됩니다.
        return if (reservedAllSeat()) {
            // 재택 지정
            if (employee != null) {
                employee.workType = WorkType.REMOTE
            }
            logger.info("All seats are already reserved. employeeId : {}", employeeId)
            null
        } else {
            // 이미 예약한 자리가 있는 유저는 좌석예약이 불가능합니다.
            if (isEmployeeAlreadyReserved(employeeId)) {
                logger.info("The employee has already reserved a seat. employeeId : {}", employeeId)
                throw ReservationException("The employee has already reserved a seat", ErrorCode.INVALID_INPUT_VALUE)
            }
            // 같은 직원이 동일한 좌석예약이 하루에 한번만 신청 가능합니다.
            if (reservationRepository.existsReservationByEmployeeIdAndSeatId(employeeId, seatId)) {
                logger.info("The same seat can only be reserved once a day. seatId : {}", seatId)
                throw ReservationException(
                    "The same seat can only be reserved once a day.",
                    ErrorCode.INVALID_INPUT_VALUE
                )
            }
            // 취소된 좌석을 예약하는 경우
            // 여러 직원이 동시에 같은 좌석을 예약할 수 없습니다.
            val checkReservation: Reservation? = reservationRepository.findReservationBySeatId(seatId)
            if (checkReservation != null && checkReservation.reserved == true) {
                logger.info("The seat already reserved. seatId : {}", seatId)
                throw ReservationException("The seat already reserved.", ErrorCode.INVALID_INPUT_VALUE)
            }
            val reservation = Reservation(employee, seat, 0)
            reservation.setReserved()
            // Employee-Seat 의 연관관계를 맺어줍니다.
            reservation.addReservationToEmployeeAndSeat(employee, seat)
            if (employee != null) {
                employee.workType = WorkType.OFFICE
            }
            val saved: Reservation = reservationRepository.save(reservation)
            logger.info("make reservation is complete. reservationId : {}", saved.id)
            ReservationDTO.ReservationResponse(saved)
        }
    }

    // 예약 취소 API
    @Transactional
    override fun cancelReservation(request: ReservationDTO.CancelReservationRequest?): ReservationDTO.CancelReservationResponse {
        val reservation: Reservation = reservationRepository.findReservationByEmployeeId(request!!.employeeId!!)
            ?: throw EntityNotFoundException("Reservation does not exist.")
        reservation.cancelReserved()
        val employee: Employee? = reservation.employee
        if (employee != null) {
            employee.workType = null
        }
        return ReservationDTO.CancelReservationResponse(reservation)
    }

    private fun getEmployee(employeeId: Long): Employee? {
        return employeeRepository.findById(employeeId).orElseThrow {
            logger.error("employee does not exist. employeeId : {}", employeeId)
            EntityNotFoundException("Employee does not exist.")
        }
    }

    private fun getSeat(seatId: Long): Seat? {
        return seatRepository.findById(seatId).orElseThrow {
            logger.error("seat does not exist. seatId : {}", seatId)
            EntityNotFoundException("Seat does not exist.")
        }
    }

    private fun reservedAllSeat(): Boolean {
        return reservationRepository.countReservedSeat() == MAX_SEAT
    }

    private fun isEmployeeAlreadyReserved(employeeId: Long): Boolean {
        val reservation: Reservation? = reservationRepository.findReservationByEmployeeId(employeeId)
        return reservation?.reserved == true
    }

    companion object {
        private const val MAX_SEAT = 100L
    }
}