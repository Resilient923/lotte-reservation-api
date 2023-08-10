package reservation.api.common.exception

import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody

@ControllerAdvice
class ExceptionManager {

    private val log = LoggerFactory.getLogger(ExceptionManager::class.java)

    @ExceptionHandler(ReservationException::class)
    @ResponseBody
    fun handleReservationLogicException(e: ReservationException): ResponseEntity<ErrorResponse> {
        log.error("[handleReservationException]", e)
        val errorCode = e.errorCode
        val response = ErrorResponse.of(errorCode)
        return ResponseEntity(response, HttpStatus.valueOf(errorCode.status))
    }
}