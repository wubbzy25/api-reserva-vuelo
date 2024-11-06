package com.api.reservavuelos.Services;

// Importamos las librerías necesarias
import com.api.reservavuelos.DTO.Request.ReservaRequestDTO;
import com.api.reservavuelos.DTO.Response.ResponseDTO;
import com.api.reservavuelos.Models.Reservas;
import com.api.reservavuelos.Models.Vuelos;
import com.api.reservavuelos.Repositories.ReservasRepository;
import com.api.reservavuelos.Repositories.UsuarioRepository;
import com.api.reservavuelos.Repositories.VuelosRepository;
import com.api.reservavuelos.Utils.DateFormatter;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import java.util.*;

// Definimos la clase ReservasService y la anotamos con @Service para que Spring la reconozca como un servicio
@Service
public class ReservasService {
    // Declaramos los servicios, repositorios o interfaces necesarios para utilizarlos
    private final DateFormatter dateFormatter; // Utilidad para formatear fechas
    private final VuelosRepository vuelosRepository; // Repositorio para manejar la entidad Vuelos
    private final ReservasRepository reservasRepository; // Repositorio para manejar la entidad Reservas
    private final UsuarioRepository usuarioRepository; // Repositorio para manejar la entidad Usuarios

    // Constructor para inyectar las dependencias
    public ReservasService(DateFormatter dateFormatter,
                           VuelosRepository vuelosRepository,
                           ReservasRepository reservasRepository,
                           UsuarioRepository usuarioRepository) {
        this.dateFormatter = dateFormatter;
        this.vuelosRepository = vuelosRepository;
        this.reservasRepository = reservasRepository;
        this.usuarioRepository = usuarioRepository;
    }

    // Metodo para reservar un vuelo
    public ResponseDTO reservarVuelo(Long id_vuelo, ReservaRequestDTO reservaRequestDTO, HttpServletRequest request) {
        // Verificamos si el vuelo existe
        Optional<Vuelos> vueloOptional = vuelosRepository.findById(id_vuelo);
        if (vueloOptional.isEmpty()) {
            throw new IllegalArgumentException("El vuelo no existe");
        }

        Vuelos vuelo = vueloOptional.get();

        // Verificamos si el asiento ya está reservado
        Optional<Reservas> asiento_reserva = reservasRepository.findByNumeroAsientoAndClase(reservaRequestDTO.getNumero_asiento(), reservaRequestDTO.getClase());
        if (asiento_reserva.isPresent()) {
            throw new IllegalArgumentException("El asiento está reservado");
        }

        // Obtenemos la capacidad de las clases Business y Economy del vuelo
        int getBussinessCapacity = vuelo.getBussinessClass();
        int getEconomyCapacity = vuelo.getEconomyClass();
        int maxCapacity = getBussinessCapacity + getEconomyCapacity;

        // Verificamos las reservas actuales por clase
        int reservasBussinesClass = reservasRepository.getBussinessClassByidVuelo(id_vuelo);
        int reservasEconomyClass = reservasRepository.getEconomyClassByidVuelo(id_vuelo);

        // Verificamos si el vuelo está completamente ocupado
        if (reservasBussinesClass + reservasEconomyClass >= maxCapacity) {
            throw new IllegalArgumentException("El vuelo está completamente ocupado");
        }

        // Verificamos si hay asientos disponibles en la clase Business
        if (reservasBussinesClass >= getBussinessCapacity && Objects.equals(reservaRequestDTO.getClase(), "bussiness")) {
            throw new IllegalArgumentException("Los asientos de bussines class ya estan completamente llenos");
        }

        // Verificamos si hay asientos disponibles en la clase Economy
        if (reservasEconomyClass >= getEconomyCapacity && Objects.equals(reservaRequestDTO.getClase(), "economy")) {
            throw new IllegalArgumentException("Los asientos economy class ya estan completamente llenos");
        }

        // Verificamos si el número de asiento es válido para la clase seleccionada
        if (Objects.equals(reservaRequestDTO.getClase(), "economy") && reservaRequestDTO.getNumero_asiento() > getEconomyCapacity) {
            throw new IllegalArgumentException("Este asiento no existe o no pertenece a la clase economy");
        }

        if (Objects.equals(reservaRequestDTO.getClase(), "bussiness") && reservaRequestDTO.getNumero_asiento() > getBussinessCapacity) {
            throw new IllegalArgumentException("Este asiento no existe o no pertenece a la clase bussiness");
        }

        // Creamos una nueva reserva
        Reservas nuevaReserva = new Reservas();
        nuevaReserva.setClase(reservaRequestDTO.getClase());
        nuevaReserva.setNumero_asiento(reservaRequestDTO.getNumero_asiento());
        nuevaReserva.setUsuarios(usuarioRepository.findById(reservaRequestDTO.getId_usuario()).get());
        nuevaReserva.setFecha_reserva(dateFormatter.formatearFecha());
        nuevaReserva.setVuelos(vuelo);
        nuevaReserva.setEstado("reservado");

        // Guardamos la reserva en la base de datos
        reservasRepository.save(nuevaReserva);

        // Devolvemos una respuesta indicando que la reserva se ha realizado correctamente
        return setResponseDTO("P-201", "Se ha reservado el vuelo correctamente", request);
    }

    // Método para cancelar una reserva
    public ResponseDTO cancelarReserva(Long id_reserva, HttpServletRequest request) {
        // Verificamos si la reserva existe
        Optional<Reservas> reservaOptional = reservasRepository.findById(id_reserva);
        if (reservaOptional.isEmpty()) {
            throw new IllegalArgumentException("La reserva no existe");
        }

        // Eliminamos la reserva de la base de datos
        reservasRepository.deleteById(id_reserva);

        // Devolvemos una respuesta indicando que la reserva se ha cancelado
        return setResponseDTO("P-200", "Se ha cancelado la reserva", request);
    }

    // Método privado para crear un ResponseDTO con la fecha, código y mensaje proporcionados
    private ResponseDTO setResponseDTO(String code, String message, HttpServletRequest request) {
        return new ResponseDTO(dateFormatter.formatearFecha(), code, message, request.getRequestURI());
    }
}
