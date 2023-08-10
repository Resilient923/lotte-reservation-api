package reservation.api.common.exception

// 엔티티 조회 시 존재하지 않는 결과로 인한 예외 처리
class EntityNotFoundException(value: String?) : ReservationException(value, ErrorCode.ENTITY_NOT_FOUND) {
    var entityErrorCode: ErrorCode? = null
}