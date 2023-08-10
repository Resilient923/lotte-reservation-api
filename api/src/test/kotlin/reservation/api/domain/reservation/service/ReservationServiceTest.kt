package reservation.api.domain.reservation.service

import jakarta.transaction.Transactional
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import reservation.api.common.entity.WorkType
import reservation.api.common.exception.EntityNotFoundException
import reservation.api.domain.employee.entity.Employee
import reservation.api.domain.employee.repository.EmployeeRepository
import reservation.api.domain.reservation.api.ReservationService
import reservation.api.domain.reservation.dto.ReservationDTO
import reservation.api.domain.reservation.repository.ReservationRepository
import reservation.api.domain.seat.entity.Seat
import reservation.api.domain.seat.repository.SeatRepository
import reservation.api.common.exception.ReservationException
import reservation.api.domain.reservation.entity.Reservation

@SpringBootTest
@Transactional
class ReservationServiceTest(
    @Autowired
    val reservationService: ReservationService,
    @Autowired
    val reservationRepository: ReservationRepository,
    @Autowired
    private val employeeRepository: EmployeeRepository,
    @Autowired
    private val seatRepository: SeatRepository
) {
    lateinit var employee1: Employee
    lateinit var employee2: Employee

    lateinit var seat1: Seat
    lateinit var seat2: Seat

    @BeforeEach
    fun init() {
        employee1 = createTestEmployee(1)
        employee2 = createTestEmployee(2)
        seat1 = createTestSeat(1)
        seat2 = createTestSeat(2)

    }

    @Test
    @DisplayName("예약이 존재하지 않는 경우에는 예약 취소가 불가능하다.")
    fun reservation_cancel_not_possible_if_reservation_does_not_exist() {

        val request: ReservationDTO.CancelReservationRequest = ReservationDTO.CancelReservationRequest(employeeId = 5)

        assertThrows<EntityNotFoundException> {
            reservationService.cancelReservation(request)
        }
    }

    @Test
    @DisplayName("좌석이 모두 예약 완료일 경우, 직원이 좌석 예약을 요청하면 자동으로 재택근무가 지정된다.")
    fun auto_assign_remote_work_when_all_seats_are_full() {

        createData()
        val employee101 = createTestEmployee(3)

        val request: ReservationDTO.ReservationRequest = ReservationDTO.ReservationRequest(employee101.id, seat1.id)
        reservationService.makeReservation(request)

        assertEquals(employee101.workType, WorkType.REMOTE)

    }

    @Test
    @DisplayName("이미 예약한 좌석이 있는 유저는 좌석 예약이 불가능하다.")
    fun reservation_impossible_if_already_reserve_seat() {

        createTestReservation(employee1, seat1)

        val request: ReservationDTO.ReservationRequest = ReservationDTO.ReservationRequest(employee1.id, seat1.id)

        assertThrows<ReservationException> {
            reservationService.makeReservation(request)
        }
    }

    @Test
    @DisplayName("같은 직원은 동일한 좌석 예약이 하루에 한번만 신청 가능하다.")
    fun same_employee_can_reserve_the_same_seat_once_a_day() {

        createTestReservation(employee1, seat1)
        val cancelReservationRequest: ReservationDTO.CancelReservationRequest =
            ReservationDTO.CancelReservationRequest(employee1.id)
        reservationService.cancelReservation(cancelReservationRequest)

        val makeReservationRequest: ReservationDTO.ReservationRequest =
            ReservationDTO.ReservationRequest(employee1.id, seat1.id)

        assertThrows<ReservationException> {
            reservationService.makeReservation(makeReservationRequest)
        }
    }

    @Test
    @DisplayName("직원이 취소한 좌석을 다른 직원은 예약할 수 있습니다.")
    fun employee_can_reserve_seat_when_other_employee_cancel_same_seat() {

        createTestReservation(employee1, seat1)
        val cancelReservationRequest: ReservationDTO.CancelReservationRequest =
            ReservationDTO.CancelReservationRequest(employee1.id)
        reservationService.cancelReservation(cancelReservationRequest)

        val result: Reservation = createTestReservation(employee2, seat2)

        assertEquals(result.employee?.employeeNum, employee2.employeeNum)
    }

    private fun createTestEmployee(employeeNum: Long): Employee {
        val employee = Employee(employeeNum, 0)
        return employeeRepository.save(employee)
    }

    private fun createTestSeat(seatNum: Long): Seat {
        val seat = Seat(seatNum, 0)
        return seatRepository.save(seat)
    }

    private fun createTestReservation(employee: Employee, seat: Seat): Reservation {
        val reservation: Reservation = Reservation(employee, seat, 0)
        reservation.addReservationToEmployeeAndSeat(employee, seat)
        reservation.setReserved()
        return reservationRepository.save(reservation)
    }

    private fun createData() {
        repeat(100) { index ->
            val employee = createTestEmployee((index + 1).toLong())
            val seat = createTestSeat((index + 1).toLong())
            createTestReservation(employee, seat)

        }
    }
}
