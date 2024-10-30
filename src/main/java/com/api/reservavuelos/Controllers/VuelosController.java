package com.api.reservavuelos.Controllers;

import com.api.reservavuelos.DTO.Cache.VueloCacheDTO;
import com.api.reservavuelos.DTO.Request.VueloUpdateStateRequestDTO;
import com.api.reservavuelos.DTO.Request.VuelosRequestDTO;
import com.api.reservavuelos.DTO.Response.AsientosResponseDTO;
import com.api.reservavuelos.DTO.Response.ResponseDTO;
import com.api.reservavuelos.DTO.Response.VuelosResponseDTO;
import com.api.reservavuelos.Services.VuelosService;
import com.api.reservavuelos.Utils.DateFormatter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("api/v1/vuelos")
public class VuelosController {

    private final VuelosService vuelosService;
    private final DateFormatter dateFormatter;

    public VuelosController(VuelosService vuelosService, DateFormatter dateFormatter) {
        this.vuelosService = vuelosService;
        this.dateFormatter = dateFormatter;
    }

    @GetMapping()
    public List<VueloCacheDTO> getVuelos(){
        List<VueloCacheDTO> vuelos = vuelosService.obtenerVuelos();
        return vuelos;
    }

    @GetMapping("/vuelo/{id_vuelo}")
    public ResponseEntity<VuelosResponseDTO> getVueloPorId(@PathVariable Long id_vuelo){
        return new ResponseEntity<>(vuelosService.obtenerVuelo(id_vuelo), HttpStatus.OK);
    }
    @GetMapping("vuelo/asientos/{id_vuelo}")
    public ResponseEntity<?> getVuelosConAsientosDisponibles(@PathVariable Long id_vuelo){
        return new ResponseEntity<>(vuelosService.obtenerAsientos(id_vuelo), HttpStatus.OK);
    }

    //admin controllers
    @PostMapping("/vuelo/crear")
    public ResponseEntity<ResponseDTO> crearVuelo(@Valid @RequestBody VuelosRequestDTO vuelo, HttpServletRequest request){
        return new ResponseEntity<>(vuelosService.crearVuelo(vuelo,request), HttpStatus.OK);
    }

    @PutMapping("/vuelo/editar/{id_editar}")
    public ResponseEntity<ResponseDTO> editarVuelo(@PathVariable Long id_editar, @Valid @RequestBody VuelosRequestDTO vueloUpdateDto, HttpServletRequest request){
        vuelosService.actualizarInformacionVuelo(id_editar,vueloUpdateDto,request);
        return new ResponseEntity<>(new ResponseDTO(dateFormatter.formatearFecha(), "P-200", "La informacion del vuelo se actualizo correctamente", request.getRequestURI()),HttpStatus.OK);
    }

    @PutMapping("vuelo/actualizar-estado/{id_editar}")
    public ResponseEntity<ResponseDTO> actualizarEstadoVuelo(@PathVariable Long id_editar, @Valid @RequestBody VueloUpdateStateRequestDTO estado, HttpServletRequest request){
        return new ResponseEntity<>(vuelosService.actualizarEstadoVuelo(id_editar, estado, request), HttpStatus.OK);
    }

    @DeleteMapping("/vuelo/eliminar/{id_eliminar}")
    public ResponseEntity<ResponseDTO> eliminarVuelo(@PathVariable Long id_eliminar, HttpServletRequest request){
        return new ResponseEntity<>(vuelosService.eliminarVuelo(id_eliminar, request), HttpStatus.OK);
    }
}
