package reservation.api.domain.reservation.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import reservation.api.common.dto.PagingResponse
import reservation.api.domain.reservation.api.ReservationService
import reservation.api.domain.reservation.dto.ReservationDTO
import java.lang.Exception

@Tag(name = "좌석 예약 API", description = "Reservation Controller")
@RestController
@RequestMapping("/api/reservation")
class ReservationController(private val reservationService: ReservationService) {

    @ApiResponses(
        value = [ApiResponse(
            responseCode = "200",
            description = "성공",
            content = [Content(schema = Schema(implementation = ReservationDTO.EmployeeWorkTypeListResponse::class))]
        )
        ]
    )
    @Operation(summary = "전 직원 근무형태 현황 조회", description = "150명 직원의 근무형태 현황을 조회한다.")
    @GetMapping("")
    fun getAllEmployeesWithWorkType(@PageableDefault(size = 20) pageable: Pageable): ResponseEntity<ReservationDTO.EmployeeWorkTypeListResponse> {
        val pageResult = reservationService.getAllEmployeesWithWorkType(pageable)
        return ResponseEntity(
            ReservationDTO.EmployeeWorkTypeListResponse(
                pageResult.content,
                PagingResponse(pageResult)
            ), HttpStatus.OK
        )
    }

    @ApiResponses(
        value = [ApiResponse(
            responseCode = "200",
            description = "성공",
            content = [Content(schema = Schema(implementation = ReservationDTO.ReservationResponse::class))]
        )]
    )
    @Operation(summary = "좌석 예약", description = "좌석을 예약 한다.")
    @PostMapping("")
    @Throws(
        Exception::class
    )
    fun makeReservation(@RequestBody @Valid request: ReservationDTO.ReservationRequest?): ResponseEntity<*> {
        val response = reservationService.makeReservation(request)
        return if (response != null) {
            ResponseEntity(response, HttpStatus.OK)
        } else {
            // 좌석이 모두 예약되는 경우, 예약하지 못한 직원은 예약 요청을 보낼 경우 자동으로 재택근무로 지정이됩니다.
            val errorResponse =
                ReservationDTO.ReservationErrorResponse("Reservation failed: All seats are already reserved. Automatically selected for remote work")
            ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(errorResponse)
        }
    }

    @ApiResponses(
        value = [ApiResponse(
            responseCode = "200",
            description = "성공",
            content = [Content(schema = Schema(implementation = ReservationDTO.CancelReservationResponse::class))]
        )
        ]
    )
    @Operation(summary = "예약 취소", description = "좌석 예약을 취소한다.")
    @PostMapping("/cancel")
    fun cancelReservation(@RequestBody @Valid request: ReservationDTO.CancelReservationRequest?): ResponseEntity<ReservationDTO.CancelReservationResponse?> {
        val response = reservationService.cancelReservation(request)
        return ResponseEntity(response, HttpStatus.OK)
    }
}