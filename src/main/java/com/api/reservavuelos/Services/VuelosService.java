package com.api.reservavuelos.Services;

import com.api.reservavuelos.DTO.Cache.VueloCacheDTO;
import com.api.reservavuelos.DTO.Request.VueloUpdateStateRequestDTO;
import com.api.reservavuelos.DTO.Request.VuelosRequestDTO;
import com.api.reservavuelos.DTO.Response.AsientosResponseDTO;
import com.api.reservavuelos.DTO.Response.ResponseDTO;
import com.api.reservavuelos.DTO.Response.VuelosResponseDTO;
import com.api.reservavuelos.Mappers.VueloMapper;
import com.api.reservavuelos.Models.Reservas;
import com.api.reservavuelos.Models.Vuelos;
import com.api.reservavuelos.Repositories.ReservasRepository;
import com.api.reservavuelos.Repositories.VuelosRepository;
import com.api.reservavuelos.Utils.DateFormatter;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

//definimos la clase VuelosService que se encarga de la logica de nogicio de los vuelos y le colocamos @Service para que spring lo reconozca como uno
@Service
public class VuelosService {

    //declaramos los servicios, repositorios o interfaces necesarios para utilizarlos
    //repositorio para poder realizar las operaciones en la base de datos de los vuelos
    private final VuelosRepository vuelosRepository;
    //componente para obtener la fecha actual formateada
    private final DateFormatter dateFormatter;
    //clase para poder interactucar con redis
    private final RedisTemplate<String, Object>  redisVuelosTemplate;
    //interfaz para poder mapear los objetos necesarios
    private final VueloMapper vueloMapper;
    //repositorio para poder realizar las operaciones en la base de datos de las reservas
    private final ReservasRepository reservasRepository;
    //Aplicamos inyeccion de dependencias mediante el contructor
    @Autowired
    public VuelosService(VuelosRepository vuelosRepository,
                         DateFormatter dateFormatter,
                         RedisTemplate<String, Object>  redisVuelosTemplate,
                         VueloMapper vueloMapper,
                         ReservasRepository reservasRepository) {
           this.vuelosRepository = vuelosRepository;
           this.dateFormatter = dateFormatter;
           this. redisVuelosTemplate =  redisVuelosTemplate;
           this.vueloMapper = vueloMapper;
           this.reservasRepository = reservasRepository;
    }

    //metodo para obtener todos los vuelos disponibles
    public List<VueloCacheDTO> obtenerVuelos(){
        try {
            //obtenemos los vuelos en redis
            List<VueloCacheDTO> vuelosCache = (List<VueloCacheDTO>)  redisVuelosTemplate.opsForValue().get("VuelosCache");
            //si vuelosCache es diferente a null significa que hay cache entonces lo devolvemos eso mapeado a VuelosCacheDTO
            if (vuelosCache != null ){
                return vuelosCache;
            }
            //obtenemos los vuelos de la base de datos
           List<Vuelos> vuelos = vuelosRepository.findAll();
            //mapeamos los vuelos a vuelosCacheDTO
            List<VueloCacheDTO> vuelosCacheDTO = vueloMapper.vuelostoVuelosCacheDTO(vuelos);
            //seteamos los vuelos en redis con un tiempo de vida de una hora
            redisVuelosTemplate.opsForValue().set("VuelosCache", vuelosCacheDTO, 1, TimeUnit.HOURS);
            //retornamos los vuelosCacheDTO
           return vuelosCacheDTO;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    //metodo para obtener un vuelo por su id
    public VuelosResponseDTO obtenerVuelo(Long id_vuelo){
        //buscamos en la base de datos el vuelo por su id
        Optional<Vuelos> vueloOptional = vuelosRepository.findById(id_vuelo);
        //si esta vacio significa que no existe entonces lanzamos una excepcion
        if (vueloOptional.isEmpty()){
            throw new IllegalArgumentException("El vuelo no existe");
        }
        //obtenemos el vuelo
        Vuelos vuelo = vueloOptional.get();
        //lo mapeamos y lo devolvemos
        return vueloMapper.vuelosToVuelosResponseDTO(vuelo);
    }
    //metodo para crear un vuelo
    public ResponseDTO crearVuelo(VuelosRequestDTO vueloRequest, HttpServletRequest request){
        try {
            //obtenemos el vuelo a crear con el numero de vuelo
           Optional<Vuelos> vueloOptional = vuelosRepository.getVuelosByNumeroVuelo(vueloRequest.getNumeroVuelo());
            //si vueloOptional no esta vacio significa que el vuelo ya existe entonces lanzamos una excepcion
           if (vueloOptional.isPresent()){
               throw new IllegalArgumentException("El vuelo ya existe");
           }
           //mapeamos vueloRequest a vuelos para poder crearlo
           Vuelos vuelo = vueloMapper.vuelosRequestDTOToVuelos(vueloRequest);
           //guardamos el vuelo
           vuelosRepository.save(vuelo);
           //actualizamos el cache de vuelos
           setListVueloCache();
           //retornamos un mensaje de exito y el request para poder capturarlo en el log
           return setResponseDTO("P-201", "Vuelo creado correctamente", request);
        } catch (Exception e){
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    //metodo para obtener los asientos de un vuelo
    public List<AsientosResponseDTO> obtenerAsientos(Long id_vuelo){
        //buscamos el vuelo por su id
        Optional<Vuelos> vueloOptional = vuelosRepository.findById(id_vuelo);
        //si vueloOptional esta vacio significa que no existe entonces lanzamos una excepcion
        if (vueloOptional.isEmpty()){
            throw new IllegalArgumentException("El vuelo no existe");
        }
        //obtenemos el vuelo
        Vuelos vuelo = vueloOptional.get();
        //definimos dos lista para poder llenarlas con los asientos disponibles y ocupados
        List<AsientosResponseDTO> asientosOcupados = new ArrayList<>();
        List<AsientosResponseDTO> asientosDisponibles = new ArrayList<>();
        //obtenemos la capacidad de cada clase de asientos del vuelo
        int BussinesCapacity =  vuelo.getBussinessClass();
        int EconomyCapacity = vuelo.getEconomyClass();
        //implementamos un bucle para llenar las listas con los asientos disponibles y ocupados
        for (int i = 1; i <= BussinesCapacity; i++) {
            //obtenemos la reserva del asiento y su clase
            Optional<Reservas> reservaOptional = reservasRepository.findByNumeroAsientoAndClase(i, "bussiness");
            //si reservaOptional no esta vacio significa que el asiento esta ocupado
            if (reservaOptional.isPresent()) {
                    asientosOcupados.add(new AsientosResponseDTO(i, "Bussiness", "Ocupado"));
            } else {
                asientosDisponibles.add(new AsientosResponseDTO(i, "Bussiness", "Disponible"));
            }
        }
        //implementamos un bucle para llenar las listas con los asientos disponibles y ocupados
        for (int i = 1; i <= EconomyCapacity; i++) {
            //obtenemos la reserva del asiento y su clase
            Optional<Reservas> reservaOptional = reservasRepository.findByNumeroAsientoAndClase(i, "economy");
            //si reservaOptional no esta vacio significa que el asiento esta ocupado
            if (reservaOptional.isPresent()) {
                    asientosOcupados.add(new AsientosResponseDTO(i, "Economy", "Ocupado"));
            } else {
                asientosDisponibles.add(new AsientosResponseDTO(i, "Economy", "Disponible"));
            }
        }
        //juntamos las dos lista para devolverlas
        List<AsientosResponseDTO> asientosResponse = new ArrayList<>();
        asientosResponse.addAll(asientosOcupados);
        asientosResponse.addAll(asientosDisponibles);
        return asientosResponse;
    }
    //metodo para actualizar el estado del vuelo
    public ResponseDTO actualizarEstadoVuelo(Long id_vuelo, VueloUpdateStateRequestDTO estado, HttpServletRequest request){
        //obtenemos el vuelo por su id
        Optional<Vuelos> vueloOptional = vuelosRepository.findById(id_vuelo);
        //si vueloOptional esta vacio significa que no existe entonces lanzamos una excepcion
        if (vueloOptional.isEmpty()){
            throw new IllegalArgumentException("El vuelo no existe");
        }
        //obtenemos el vuelo
        Vuelos vuelo = vueloOptional.get();
        //actualizamos el estado del vuelo
        vuelo.setEstadoVuelo(estado.getEstado());
        //guardamos el vuelo
        vuelosRepository.save(vuelo);
        //actualizamos el cache de vuelos
        setListVueloCache();
        //retornamos un mensaje de exito
      return setResponseDTO("P-200", "El estado del vuelo fue actualizado correctamente", request);
     }

     //metodo para actualizar la informacion del vuelo
     public ResponseDTO actualizarInformacionVuelo(Long id_vuelo, VuelosRequestDTO vueloDTO, HttpServletRequest request){
        //obtenemos el vuelo por su id
        Optional<Vuelos> VueloOptional = vuelosRepository.findById(id_vuelo);
        //si VueloOptional esta vacio significa que no existe entonces lanzamos una excepcion
        if (VueloOptional.isEmpty()){
            throw new IllegalArgumentException("El vuelo no existe");
        }
        //obtenemos el vuelo
        Vuelos vueloResult = VueloOptional.get();
        //mapeamos el vueloDTO a vueloResult para que se guarde la informacion actualizada
         vueloMapper.updateVueloFromDto(vueloDTO, vueloResult);
         //guardamos el vuelo actualizado
        vuelosRepository.save(vueloResult);
        //actualizamos el cache de vuelos
        setListVueloCache();
         //retornamos un mensaje de exito
         return setResponseDTO("P-200", "La informacion del vuelo fue actualizada correctamente", request);
     }

     //metodo para setear el ResponseDTO
    private ResponseDTO setResponseDTO(String code, String message, HttpServletRequest request){
        return new ResponseDTO(dateFormatter.formatearFecha(), code, message, request.getRequestURI());
    }
    //metodo para actualizar el cache de vuelos
  private void setListVueloCache() {
     try {
         //buscamos los vuelos en redis
         List<VueloCacheDTO> VuelosCache = (List<VueloCacheDTO>) redisVuelosTemplate.opsForValue().get("VuelosCache");
         //si VuelosCache no esta vacio significa que hay cache entonces lo eliminamos y lo reemplazamos con los vuelos actuales
         if (VuelosCache != null) {
             redisVuelosTemplate.delete("VuelosCache");
             List<Vuelos> vuelosActualizados = vuelosRepository.findAll();
             redisVuelosTemplate.opsForValue().set("VuelosCache", vuelosActualizados,1, TimeUnit.HOURS);
         }
     } catch (Exception e) {
         throw  new RuntimeException(e);
     }
 }
 //metodo para eliminar un vuelo
    public ResponseDTO eliminarVuelo(Long id_vuelo, HttpServletRequest request){
        try {
            //buscamos el vuelo por su id
            Optional<Vuelos> vueloOptional = vuelosRepository.findById(id_vuelo);
            //si vueloOptional esta vacio significa que no existe entonces lanzamos una excepcion
            if (vueloOptional.isEmpty()){
                throw new IllegalArgumentException("Este vuelo no existe");
            }
            //eliminamos el vuelo
                vuelosRepository.deleteById(id_vuelo);
            //actualizamos el cache de vuelos
                setListVueloCache();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        //retornamos un mensaje de exito
        return setResponseDTO("P-200", "El vuelo fue eliminado correctamente", request);
    }
}