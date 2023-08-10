package reservation.api.domain.reservation.repository

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.Import
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import reservation.api.config.TestConfig
import reservation.api.domain.employee.entity.Employee
import reservation.api.domain.employee.repository.EmployeeRepository
import reservation.api.domain.reservation.entity.Reservation
import reservation.api.domain.seat.entity.Seat
import reservation.api.domain.seat.repository.SeatRepository

@DataJpaTest
@Import(TestConfig::class)
class ReservationRepositoryTest(
    @Autowired
    private val reservationRepository: ReservationRepository,

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
    @DisplayName("모든 좌석 예약 현황을 조회 한다.")
    fun find_all_reservation() {

        val employee3: Employee = createTestEmployee(3)
        val employee4: Employee = createTestEmployee(4)
        val seat3: Seat = createTestSeat(3)
        val seat4: Seat = createTestSeat(4)

        createTestReservation(employee1, seat1)
        createTestReservation(employee2, seat2)
        createTestReservation(employee3, seat3)
        createTestReservation(employee4, seat4)

        val firstPage: Pageable = PageRequest.of(0, 2)
        val secondPage: Pageable = PageRequest.of(1, 2)

        val firstPageReservation: Page<Reservation?> = reservationRepository.findAllReservation(firstPage)
        val secondPageReservation: Page<Reservation?> = reservationRepository.findAllReservation(secondPage)

        val totalResultSize = firstPageReservation.content.size + secondPageReservation.content.size

        assertEquals(4, totalResultSize)
    }

    @Test
    @DisplayName("직원 아이디를 이용해서 좌석 예약 현황을 조회한다.")
    fun find_reservation_by_employee_id() {

        val reservation = createTestReservation(employee1, seat1)

        val result: Reservation? = employee1.id.let { reservationRepository.findReservationByEmployeeId(it) }

        assertEquals(reservation, result)

    }

    @Test
    @DisplayName("좌석 아이디를 이용해서 좌석 예약 현황을 조회한다.")
    fun find_reservation_by_seat_id() {

        val reservation = createTestReservation(employee1, seat1)

        val result: Reservation? = seat1.id.let { reservationRepository.findReservationBySeatId(it) }

        assertEquals(reservation, result)
    }

    @Test
    @DisplayName("직원 아이디와 좌석 아이디를 이용해서 당일 동일 좌석 예약 여부를 확인한다.")
    fun exists_reservation_by_employee_id_and_seat_id() {

        createTestReservation(employee1, seat1)

        val result: Boolean = reservationRepository.existsReservationByEmployeeIdAndSeatId(employee1.id, seat1.id)

        assertEquals(true, result)
    }

    @Test
    @DisplayName("좌석이 모두 예약된 경우 확인을 위해, 예약좌석 수를 카운트 한다.")
    fun count_reservation_seat() {

        createTestReservation(employee1, seat1)
        createTestReservation(employee2, seat2)

        val result: Long? = reservationRepository.countReservedSeat()

        assertEquals(2, result)
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

}