package reservation.api.common.exception

import java.util.ArrayList

class ErrorResponse private constructor(code: ErrorCode?) {
    val message: String
    val errors: List<FieldError>

    init {
        message = code?.message ?: ""
        errors = ArrayList()
    }

    class FieldError private constructor(private val field: String, private val value: String, private val reason: String)

    companion object {
        fun of(code: ErrorCode?): ErrorResponse {
            return ErrorResponse(code)
        }
    }
}