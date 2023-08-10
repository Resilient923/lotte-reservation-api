package reservation.api.domain.reservation.entity

import jakarta.persistence.*
import reservation.api.common.entity.BaseTimeEntity
import reservation.api.domain.employee.entity.Employee
import reservation.api.domain.seat.entity.Seat

@Entity
@Table(name = "reservation")
class Reservation(
    employee: Employee?,
    seat: Seat?,
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    val id: Long,

    ) : BaseTimeEntity() {

    @ManyToOne(fetch = FetchType.LAZY)
    var employee: Employee? = null

    @ManyToOne(fetch = FetchType.LAZY)
    var seat: Seat? = null

    var reserved: Boolean? = false

    fun setReserved() {
        this.reserved = true;
    }

    fun cancelReserved() {
        this.reserved = false
    }

    fun addReservationToEmployeeAndSeat(employee: Employee?, seat: Seat?) {
        addReservationToEmployee(employee)
        addReservationToSeat(seat)
        this.employee = employee
        this.seat = seat
    }

    private fun addReservationToEmployee(employee: Employee?) {
        if (!employee?.reservationList?.contains(this)!!) {
            employee.reservationList.add(this)
        }
    }

    private fun addReservationToSeat(seat: Seat?) {
        if (!seat?.reservationList?.contains(this)!!) {
            seat.reservationList.add(this)
        }
    }
}