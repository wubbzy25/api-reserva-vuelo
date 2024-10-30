package com.api.reservavuelos.Mappers;

import com.api.reservavuelos.DTO.Cache.VueloCacheDTO;
import com.api.reservavuelos.DTO.Request.VuelosRequestDTO;
import com.api.reservavuelos.DTO.Response.VuelosResponseDTO;
import com.api.reservavuelos.Models.Vuelos;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.List;


@Mapper(componentModel = "spring")
    public interface VueloMapper {

        VueloMapper INSTANCE = Mappers.getMapper(VueloMapper.class);
        VueloCacheDTO vueloToVueloCacheDTO(Vuelos vuelo);
        List<VueloCacheDTO> vuelostoVuelosCacheDTO(List<Vuelos> vuelo);
        VuelosResponseDTO vuelosToVuelosResponseDTO(Vuelos vuelo);
        Vuelos vuelosRequestDTOToVuelos(VuelosRequestDTO vuelo);
        void updateVueloFromDto(VuelosRequestDTO dto, @MappingTarget Vuelos vuelo);
        }
