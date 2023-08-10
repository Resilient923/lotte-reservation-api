package reservation.api.domain.employee.entity

import jakarta.persistence.*
import reservation.api.common.entity.BaseTimeEntity
import reservation.api.common.entity.WorkType
import reservation.api.domain.reservation.entity.Reservation

@Entity
@Table(name = "employee")
class Employee(
    employeeNumber: Long,
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    var id: Long
) : BaseTimeEntity() {

    var employeeNum: Long? = null

    var workType: WorkType? = null

    @OneToMany(mappedBy = "employee", cascade = [CascadeType.REMOVE], orphanRemoval = true)
    var reservationList: MutableList<Reservation> = ArrayList()

}