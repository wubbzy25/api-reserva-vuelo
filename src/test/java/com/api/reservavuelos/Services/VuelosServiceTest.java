package com.api.reservavuelos.Services;

import com.api.reservavuelos.DTO.Cache.VueloCacheDTO;
import com.api.reservavuelos.DTO.Request.VueloUpdateStateRequestDTO;
import com.api.reservavuelos.DTO.Request.VuelosRequestDTO;
import com.api.reservavuelos.DTO.Response.AsientosResponseDTO;
import com.api.reservavuelos.DTO.Response.ResponseDTO;
import com.api.reservavuelos.DTO.Response.VuelosResponseDTO;
import com.api.reservavuelos.DataProvider;
import com.api.reservavuelos.Mappers.VueloMapper;
import com.api.reservavuelos.Models.Reservas;
import com.api.reservavuelos.Models.Vuelos;
import com.api.reservavuelos.Repositories.ReservasRepository;
import com.api.reservavuelos.Repositories.VuelosRepository;
import com.api.reservavuelos.Utils.DateFormatter;
import com.api.reservavuelos.Utils.VueloEstado;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VuelosServiceTest {

    @Mock
    VuelosRepository vuelosRepository;
    @Mock
    DateFormatter dateFormatter;
    @Mock
    RedisTemplate<String, Object> redisVuelosTemplate;
    @Mock
    VueloMapper vueloMapper;
    @Mock
    ReservasRepository reservasRepository;
    @Mock
    ValueOperations<String, Object> valueOperations;
    @Mock
    HttpServletRequest request;
    @InjectMocks
    VuelosService vuelosService;

    @Test
    void LanzarExcepcionSiAlgoSaleMal() {
        when(vuelosRepository.findAll()).thenThrow(RuntimeException.class);
        when(redisVuelosTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(anyString())).thenReturn(null);
        assertThrows(RuntimeException.class, () -> vuelosService.obtenerVuelos());
        verify(vuelosRepository, times(1)).findAll();
    }

    @Test
    void obtenerVuelos() {
        when(redisVuelosTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(anyString())).thenReturn(null);
        when(vuelosRepository.findAll()).thenReturn(DataProvider.obtenerVuelos());
        when(vueloMapper.vuelostoVuelosCacheDTO(any(List.class))).thenReturn(DataProvider.obtenerVuelosCacheDTO());
        doNothing().when(valueOperations).set(eq("VuelosCache"), anyList(), eq(1L), eq(TimeUnit.HOURS));

        List<VueloCacheDTO> vuelos = vuelosService.obtenerVuelos();
        assertEquals(1L, vuelos.get(0).getIdVuelo());
        assertEquals(1, vuelos.get(0).getNumeroVuelo());
        assertEquals("Avianca", vuelos.get(0).getAerolinea());
        assertEquals("BOG", vuelos.get(0).getOrigen());
        assertEquals("MEX", vuelos.get(0).getDestino());
        assertEquals(10, vuelos.get(0).getEconomyClass());
        assertEquals(5, vuelos.get(0).getBussinessClass());
        verify(valueOperations, times(1)).set(eq("VuelosCache"), anyList(), eq(1L), eq(TimeUnit.HOURS));
        verify(vuelosRepository, times(1)).findAll();
        verify(vueloMapper, times(1)).vuelostoVuelosCacheDTO(any(List.class));
        verify(valueOperations, times(1)).get(anyString());

    }

    @Test
    void VerificarQueObtengaLosDatosDeRedis(){
        when(redisVuelosTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(anyString())).thenReturn(DataProvider.obtenerVuelosCacheDTO());

       vuelosService.obtenerVuelos();

        verify(valueOperations, times(1)).get(anyString());
        verify(vuelosRepository, never()).findAll();

    }

    @Test
    void LanzarExcepcionSiNoExisteElVueloEnObtenerVuelo() {
        when(vuelosRepository.findById(anyLong())).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> vuelosService.obtenerVuelo(1L));

        assertEquals("El vuelo no existe", exception.getMessage());
        verify(vuelosRepository, times(1)).findById(anyLong());

    }

    @Test
    void obtenerVuelo() {
        when(vuelosRepository.findById(anyLong())).thenReturn(Optional.of(DataProvider.obtenerVuelo()));
        when(vueloMapper.vuelosToVuelosResponseDTO(any(Vuelos.class))).thenReturn(DataProvider.obtenerVueloResponseDTO());

        VuelosResponseDTO vuelo = vuelosService.obtenerVuelo(1L);

        assertEquals(1L, vuelo.getIdVuelo());
        assertEquals(1, vuelo.getNumeroVuelo());
        assertEquals("Avianca", vuelo.getAerolinea());
        assertEquals("BOG", vuelo.getOrigen());
        assertEquals("MEX", vuelo.getDestino());
        assertEquals(10, vuelo.getEconomyClass());
        assertEquals(5, vuelo.getBussinessClass());
        verify(vuelosRepository, times(1)).findById(anyLong());
        verify(vueloMapper, times(1)).vuelosToVuelosResponseDTO(any(Vuelos.class));
    }

    @Test
    void LanzarExcepcionSiExisteElVueloEnCrearVuelo() {
        when(vuelosRepository.getVuelosByNumeroVuelo(anyInt())).thenReturn(Optional.of(DataProvider.obtenerVuelo()));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> vuelosService.crearVuelo(DataProvider.obtenerVuelosRequestDTO(), request));

        assertEquals("El vuelo ya existe", exception.getMessage());

        verify(vuelosRepository, times(1)).getVuelosByNumeroVuelo(anyInt());
        verify(valueOperations, never()).get(anyString());
    }
    
    @Test
    void crearVuelo() {
        when(vuelosRepository.getVuelosByNumeroVuelo(anyInt())).thenReturn(Optional.empty());
        when(redisVuelosTemplate.opsForValue()).thenReturn(valueOperations);
        when(vueloMapper.vuelosRequestDTOToVuelos(any(VuelosRequestDTO.class))).thenReturn(DataProvider.obtenerVuelo());
        when(vuelosRepository.save(any(Vuelos.class))).thenReturn(DataProvider.obtenerVuelo());

        ResponseDTO response = vuelosService.crearVuelo(DataProvider.obtenerVuelosRequestDTO(), request);

        assertEquals("P-201", response.getCode());
        assertEquals("Vuelo creado correctamente", response.getMessage());

        verify(vuelosRepository, times(1)).getVuelosByNumeroVuelo(anyInt());
        verify(redisVuelosTemplate, times(1)).opsForValue();
        verify(vuelosRepository, times(1)).save(any(Vuelos.class));
        verify(vueloMapper, times(1)).vuelosRequestDTOToVuelos(any(VuelosRequestDTO.class));

    }

    @Test
    void LanzarExcepcionSiExisteElVueloEnObtenerAsientos() {
        when(vuelosRepository.findById(anyLong())).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> vuelosService.obtenerAsientos(1L));

        assertEquals("El vuelo no existe", exception.getMessage());

        verify(vuelosRepository, times(1)).findById(anyLong());
        verify(valueOperations, never()).get(anyString());
    }

    @Test
    void obtenerAsientos() {
        Vuelos vuelo = new Vuelos();
        vuelo.setIdVuelo(1L);
        vuelo.setBussinessClass(3);
        vuelo.setEconomyClass(2);


        when(vuelosRepository.findById(1L)).thenReturn(Optional.of(vuelo));


        when(reservasRepository.findByNumeroAsientoAndClase(1, "bussiness")).thenReturn(Optional.of(new Reservas()));

        when(reservasRepository.findByNumeroAsientoAndClase(2, "bussiness")).thenReturn(Optional.empty());
        when(reservasRepository.findByNumeroAsientoAndClase(3, "bussiness")).thenReturn(Optional.empty());


        when(reservasRepository.findByNumeroAsientoAndClase(1, "economy")).thenReturn(Optional.of(new Reservas()));

        when(reservasRepository.findByNumeroAsientoAndClase(2, "economy")).thenReturn(Optional.empty());


        List<AsientosResponseDTO> asientosResponse = vuelosService.obtenerAsientos(1L);


        assertEquals(5, asientosResponse.size());


        assertEquals("Bussiness", asientosResponse.get(0).getClaseAsiento());
        assertEquals("Ocupado", asientosResponse.get(0).getEstadoAsiento());


        assertEquals("Economy", asientosResponse.get(1).getClaseAsiento());
        assertEquals("Ocupado", asientosResponse.get(1).getEstadoAsiento());

        assertEquals("Bussiness", asientosResponse.get(2).getClaseAsiento());
        assertEquals("Disponible", asientosResponse.get(2).getEstadoAsiento());


        assertEquals("Bussiness", asientosResponse.get(3).getClaseAsiento());
        assertEquals("Disponible", asientosResponse.get(3).getEstadoAsiento());


        assertEquals("Economy", asientosResponse.get(4).getClaseAsiento());
        assertEquals("Disponible", asientosResponse.get(4).getEstadoAsiento());


        verify(vuelosRepository, times(1)).findById(1L);
        verify(reservasRepository, times(1)).findByNumeroAsientoAndClase(1, "bussiness");
        verify(reservasRepository, times(1)).findByNumeroAsientoAndClase(2, "bussiness");
        verify(reservasRepository, times(1)).findByNumeroAsientoAndClase(3, "bussiness");
        verify(reservasRepository, times(1)).findByNumeroAsientoAndClase(1, "economy");
        verify(reservasRepository, times(1)).findByNumeroAsientoAndClase(2, "economy");
    }

    @Test
    void LanzarExcepcionSiElVueloNoExisteEnactualizarEstadoVuelo(){
        when(vuelosRepository.findById(anyLong())).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> vuelosService.actualizarEstadoVuelo(1L, DataProvider.obtenerVueloUpdateStateDTO(), request));

        assertEquals("El vuelo no existe", exception.getMessage());

        verify(vuelosRepository, times(1)).findById(anyLong());
        verify(vuelosRepository, never()).save(any(Vuelos.class));
        verify(valueOperations, never()).get(anyString());
    }
    @Test
    void actualizarEstadoVuelo() {
    when(vuelosRepository.findById(anyLong())).thenReturn(Optional.of(DataProvider.obtenerVuelo()));
    when(redisVuelosTemplate.opsForValue()).thenReturn(valueOperations);
    when(vuelosRepository.save(any(Vuelos.class))).thenReturn(DataProvider.obtenerVuelo());
    ResponseDTO response = vuelosService.actualizarEstadoVuelo(1L, DataProvider.obtenerVueloUpdateStateDTO(), request);

    assertEquals("P-200", response.getCode());
    assertEquals("El estado del vuelo fue actualizado correctamente", response.getMessage());
    verify(vuelosRepository, times(1)).findById(anyLong());
    verify(vuelosRepository, times(1)).save(any(Vuelos.class));
    verify(redisVuelosTemplate, times(1)).opsForValue();

    }

    @Test
    void LanzarExcepcionSiElVueloNoExisteEnactualizarInformacionVuelo(){
        when(vuelosRepository.findById(anyLong())).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> vuelosService.actualizarInformacionVuelo(1L, DataProvider.obtenerVuelosRequestDTO(), request));

        assertEquals("El vuelo no existe", exception.getMessage());

        verify(vuelosRepository, times(1)).findById(anyLong());
        verify(vuelosRepository, never()).save(any(Vuelos.class));
        verify(valueOperations, never()).get(anyString());
    }

    @Test
    void actualizarInformacionVuelo() {
        when(vuelosRepository.findById(anyLong())).thenReturn(Optional.of(DataProvider.obtenerVuelo()));
        when(redisVuelosTemplate.opsForValue()).thenReturn(valueOperations);
        when(vuelosRepository.save(any(Vuelos.class))).thenReturn(DataProvider.obtenerVuelo());
        ResponseDTO response = vuelosService.actualizarInformacionVuelo(1L, DataProvider.obtenerVuelosRequestDTO(), request);

        assertEquals("P-200", response.getCode());
        assertEquals("La informacion del vuelo fue actualizada correctamente", response.getMessage());

        verify(vuelosRepository, times(1)).findById(anyLong());
        verify(vuelosRepository, times(1)).save(any(Vuelos.class));
        verify(redisVuelosTemplate, times(1)).opsForValue();
    }

    @Test
    void LanzarExcepcionCuandoElVueloNoExistaEneliminarVuelo() {

        when(vuelosRepository.findById(anyLong())).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> vuelosService.eliminarVuelo(1L, request));

        assertEquals("Este vuelo no existe", exception.getMessage());

        verify(vuelosRepository, times(1)).findById(anyLong());
    }

    @Test
    void eliminarVuelo() {
        Long idVuelo = 1L;
        Vuelos vuelo = new Vuelos();
        when(vuelosRepository.findById(idVuelo)).thenReturn(Optional.of(DataProvider.obtenerVuelo()));
        when(redisVuelosTemplate.opsForValue()).thenReturn(valueOperations);


        ResponseDTO response = vuelosService.eliminarVuelo(idVuelo, request);


        verify(vuelosRepository, times(1)).deleteById(idVuelo);
        verify(vuelosRepository, times(1)).findById(idVuelo);
        assertEquals("P-200", response.getCode());
        assertEquals("El vuelo fue eliminado correctamente", response.getMessage());
    }
}