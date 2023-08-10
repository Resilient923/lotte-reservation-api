package reservation.api.common.exception

import java.lang.RuntimeException

open class ReservationException(message: String?, val errorCode: ErrorCode) : RuntimeException(message) {
}