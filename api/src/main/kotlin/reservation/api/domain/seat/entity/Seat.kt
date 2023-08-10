package reservation.api.domain.seat.entity

import jakarta.persistence.*
import reservation.api.common.entity.BaseTimeEntity
import reservation.api.domain.reservation.entity.Reservation

@Entity
@Table(name = "seat")
class Seat(
    seatNum: Long,
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    val id: Long
) : BaseTimeEntity() {

    @Column
    val seatNum: Long? = null

    @OneToMany(mappedBy = "seat", cascade = [CascadeType.REMOVE], orphanRemoval = true)
    var reservationList: MutableList<Reservation> = ArrayList()

}