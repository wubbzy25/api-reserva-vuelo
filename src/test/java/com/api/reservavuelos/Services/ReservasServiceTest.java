package com.api.reservavuelos.Services;

import com.api.reservavuelos.DTO.Response.ResponseDTO;
import com.api.reservavuelos.DataProvider;
import com.api.reservavuelos.Models.Reservas;
import com.api.reservavuelos.Repositories.ReservasRepository;
import com.api.reservavuelos.Repositories.UsuarioRepository;
import com.api.reservavuelos.Repositories.VuelosRepository;
import com.api.reservavuelos.Utils.DateFormatter;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.text.ParseException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class ReservasServiceTest {

    @Mock
    VuelosRepository vuelosRepository;
    @Mock
    ReservasRepository reservasRepository;
    @Mock
    UsuarioRepository usuarioRepository;
    @Mock
    HttpServletRequest request;
    @Mock
    DateFormatter dateFormatter;
    @InjectMocks
    ReservasService reservasService;

    @Test
    void LanzarExcepcionSiElUsuarioNoExisteEnreservarVuelo(){
        when(vuelosRepository.findById(anyLong())).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> reservasService.reservarVuelo(1L, DataProvider.obtenerReservaRequestDTO(), request));
        assertEquals("El vuelo no existe", exception.getMessage());

        verify(vuelosRepository, times(1)).findById(anyLong());
     }

     @Test
     void LanzarExcepcionSiElAsientoAreservasYaEstaReservado() throws ParseException {
        when(vuelosRepository.findById(anyLong())).thenReturn(Optional.of(DataProvider.obtenerVuelo()));
        when(reservasRepository.findByNumeroAsientoAndClase(anyInt(), anyString())).thenReturn(Optional.of(DataProvider.obtenerReserva()));
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> reservasService.reservarVuelo(1L, DataProvider.obtenerReservaRequestDTO(), request));

        assertEquals("El asiento está reservado", exception.getMessage());

        verify(vuelosRepository, times(1)).findById(anyLong());
        verify(reservasRepository, times(1)).findByNumeroAsientoAndClase(anyInt(), anyString());
    }

    @Test
    void LanzarExcepcionSiElVueloYaEstaLleno(){
        when(vuelosRepository.findById(anyLong())).thenReturn(Optional.of(DataProvider.obtenerVuelo()));
        when(reservasRepository.findByNumeroAsientoAndClase(anyInt(), anyString())).thenReturn(Optional.empty());
        when(reservasRepository.getBussinessClassByidVuelo(anyLong())).thenReturn(10);
        when(reservasRepository.getEconomyClassByidVuelo(anyLong())).thenReturn(10);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> reservasService.reservarVuelo(1L, DataProvider.obtenerReservaRequestDTO(), request));
        assertEquals("El vuelo está completamente ocupado", exception.getMessage());
        verify(vuelosRepository, times(1)).findById(anyLong());
        verify(reservasRepository, times(1)).findByNumeroAsientoAndClase(anyInt(), anyString());
        verify(reservasRepository, times(1)).getBussinessClassByidVuelo(anyLong());
        verify(reservasRepository, times(1)).getEconomyClassByidVuelo(anyLong());
    }

    @Test
    void LanzarExpcecionSiLaClaseBussinessEstaLlena(){
        when(vuelosRepository.findById(anyLong())).thenReturn(Optional.of(DataProvider.obtenerVuelo()));
        when(reservasRepository.findByNumeroAsientoAndClase(anyInt(), anyString())).thenReturn(Optional.empty());
        when(reservasRepository.getBussinessClassByidVuelo(anyLong())).thenReturn(10);
        when(reservasRepository.getEconomyClassByidVuelo(anyLong())).thenReturn(1);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> reservasService.reservarVuelo(1L, DataProvider.obtenerReservaRequestDTO(), request));

        assertEquals("Los asientos de bussines class ya estan completamente llenos", exception.getMessage());
        verify(vuelosRepository, times(1)).findById(anyLong());
        verify(reservasRepository, times(1)).findByNumeroAsientoAndClase(anyInt(), anyString());
        verify(reservasRepository, times(1)).getBussinessClassByidVuelo(anyLong());
        verify(reservasRepository, times(1)).getEconomyClassByidVuelo(anyLong());
    }

    @Test
    void LanzarExpcecionSiLaClaseEconomyEstaLlena(){
        when(vuelosRepository.findById(anyLong())).thenReturn(Optional.of(DataProvider.obtenerVuelo()));
        when(reservasRepository.findByNumeroAsientoAndClase(anyInt(), anyString())).thenReturn(Optional.empty());
        when(reservasRepository.getBussinessClassByidVuelo(anyLong())).thenReturn(1);
        when(reservasRepository.getEconomyClassByidVuelo(anyLong())).thenReturn(10);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> reservasService.reservarVuelo(1L, DataProvider.obtenerReservaRequestDTO(), request));
        assertEquals("Los asientos economy class ya estan completamente llenos", exception.getMessage());
        verify(vuelosRepository, times(1)).findById(anyLong());
        verify(reservasRepository, times(1)).findByNumeroAsientoAndClase(anyInt(), anyString());
        verify(reservasRepository, times(1)).getBussinessClassByidVuelo(anyLong());
        verify(reservasRepository, times(1)).getEconomyClassByidVuelo(anyLong());
    }

    @Test
    void LanzarExcepcionSiElNumeroDeAsientoNoEsValidoParaLaClaseEconomy(){
        when(vuelosRepository.findById(anyLong())).thenReturn(Optional.of(DataProvider.obtenerVuelo()));
        when(reservasRepository.findByNumeroAsientoAndClase(anyInt(), anyString())).thenReturn(Optional.empty());
        when(reservasRepository.getBussinessClassByidVuelo(anyLong())).thenReturn(1);
        when(reservasRepository.getEconomyClassByidVuelo(anyLong())).thenReturn(1);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> reservasService.reservarVuelo(1L, DataProvider.obtenerReservaRequestDTO(), request));
        assertEquals("Este asiento no existe o no pertenece a la clase economy", exception.getMessage());
        verify(vuelosRepository, times(1)).findById(anyLong());
        verify(reservasRepository, times(1)).findByNumeroAsientoAndClase(anyInt(), anyString());
        verify(reservasRepository, times(1)).getBussinessClassByidVuelo(anyLong());
        verify(reservasRepository, times(1)).getEconomyClassByidVuelo(anyLong());

    }

    @Test
    void LanzarExcepcionSiElNumeroDeAsientoNoEsValidoParaLaClaseBussines(){
        when(vuelosRepository.findById(anyLong())).thenReturn(Optional.of(DataProvider.obtenerVuelo()));
        when(reservasRepository.findByNumeroAsientoAndClase(anyInt(), anyString())).thenReturn(Optional.empty());
        when(reservasRepository.getBussinessClassByidVuelo(anyLong())).thenReturn(1);
        when(reservasRepository.getEconomyClassByidVuelo(anyLong())).thenReturn(1);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> reservasService.reservarVuelo(1L, DataProvider.obtenerReservaRequestDTO(), request));
        assertEquals("Este asiento no existe o no pertenece a la clase bussiness", exception.getMessage());
        verify(vuelosRepository, times(1)).findById(anyLong());
        verify(reservasRepository, times(1)).findByNumeroAsientoAndClase(anyInt(), anyString());
        verify(reservasRepository, times(1)).getBussinessClassByidVuelo(anyLong());
        verify(reservasRepository, times(1)).getEconomyClassByidVuelo(anyLong());

    }
    @Test
    void reservarVuelo() throws ParseException {
        when(vuelosRepository.findById(anyLong())).thenReturn(Optional.of(DataProvider.obtenerVuelo()));
        when(reservasRepository.findByNumeroAsientoAndClase(anyInt(), anyString())).thenReturn(Optional.empty());
        when(reservasRepository.getBussinessClassByidVuelo(anyLong())).thenReturn(1);
        when(reservasRepository.getEconomyClassByidVuelo(anyLong())).thenReturn(1);
        when(usuarioRepository.findById(anyLong())).thenReturn(Optional.of(DataProvider.obtenerUsuarioPorDefectoRolUsuario()));
        when(reservasRepository.save(any(Reservas.class))).thenReturn(DataProvider.obtenerReserva());

        ResponseDTO response = reservasService.reservarVuelo(1L, DataProvider.obtenerReservaRequestDTO(), request);

        assertEquals("P-201", response.getCode());
        assertEquals("Se ha reservado el vuelo correctamente", response.getMessage());

        verify(vuelosRepository, times(1)).findById(anyLong());
        verify(reservasRepository, times(1)).findByNumeroAsientoAndClase(anyInt(), anyString());
        verify(reservasRepository, times(1)).getBussinessClassByidVuelo(anyLong());
        verify(reservasRepository, times(1)).getEconomyClassByidVuelo(anyLong());
        verify(reservasRepository, times(1)).save(any(Reservas.class));
    }
    @Test
    void LanzarExcepcionCuandoLaReservaNoExista(){
        when(reservasRepository.findById(anyLong())).thenReturn(Optional.empty());
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> reservasService.cancelarReserva(1L, request));
        assertEquals("La reserva no existe", exception.getMessage());
        verify(reservasRepository, times(1)).findById(anyLong());

    }
    @Test
    void cancelarReserva() {
        when(reservasRepository.findById(anyLong())).thenReturn(Optional.of(DataProvider.obtenerReserva()));
        doNothing().when(reservasRepository).deleteById(anyLong());

        ResponseDTO response = reservasService.cancelarReserva(1L, request);

        assertEquals("P-200", response.getCode());
        assertEquals("Se ha cancelado la reserva", response.getMessage());

        verify(reservasRepository, times(1)).findById(anyLong());
        verify(reservasRepository, times(1)).deleteById(anyLong());
    }
}