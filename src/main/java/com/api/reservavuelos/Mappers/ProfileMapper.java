package com.api.reservavuelos.Mappers;

import com.api.reservavuelos.DTO.Cache.ProfileCacheDTO;
import com.api.reservavuelos.DTO.Request.ProfileRequestDTO;
import com.api.reservavuelos.Models.Usuarios;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;
import org.springframework.context.annotation.Profile;

@Mapper(componentModel = "spring")
public interface ProfileMapper {
    ProfileMapper INSTANCE = Mappers.getMapper(ProfileMapper.class);
    ProfileCacheDTO profileToProfileCacheDTO(Profile profile);
    void updateProfileFromDto(ProfileRequestDTO profileRequestDTO, @MappingTarget Usuarios profile);

}
