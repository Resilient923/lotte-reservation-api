package reservation.api.common.exception

import com.fasterxml.jackson.annotation.JsonFormat

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
enum class ErrorCode(val status: Int, val message: String) {
    INVALID_INPUT_VALUE(400, "요청값이 유효하지 않습니다."),
    ENTITY_NOT_FOUND(404, "존재하지 않는 값입니다.")
}