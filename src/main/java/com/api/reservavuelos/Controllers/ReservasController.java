package com.api.reservavuelos.Controllers;

import com.api.reservavuelos.DTO.Request.ReservaRequestDTO;
import com.api.reservavuelos.DTO.Response.ResponseDTO;
import com.api.reservavuelos.Services.ReservasService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("api/v1/reservas")
public class ReservasController {

    private final ReservasService reservasService;

    public ReservasController(ReservasService reservasService){
        this.reservasService = reservasService;
    }

    @PostMapping("/reservar/{id_vuelo}")
    public ResponseEntity<ResponseDTO> reservarVuelo(@PathVariable Long id_vuelo, @Valid @RequestBody ReservaRequestDTO reservaRequestDTO, HttpServletRequest request){
        return new ResponseEntity<>(reservasService.reservarVuelo(id_vuelo, reservaRequestDTO, request), HttpStatus.OK);
    }

    @DeleteMapping("/cancelar/{id_reserva}")
    public ResponseEntity<ResponseDTO> cancelarReserva(@PathVariable Long id_reserva, HttpServletRequest request)  {
        return new ResponseEntity<>(reservasService.cancelarReserva(id_reserva, request), HttpStatus.OK);
    }

}
