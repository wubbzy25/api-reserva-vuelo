package com.api.reservavuelos.Controllers;

import com.api.reservavuelos.DTO.Cache.ProfileCacheDTO;
import com.api.reservavuelos.DTO.Request.ProfileRequestDTO;
import com.api.reservavuelos.DTO.Response.ProfileResponseDTO;
import com.api.reservavuelos.DTO.Response.ResponseDTO;
import com.api.reservavuelos.Services.ProfileService;
import com.api.reservavuelos.Utils.DateFormatter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RequestMapping("api/v1/profile")
@RestController
public class ProfileController {
    private final ProfileService profileService;
    private final DateFormatter dateFormatter;

    @Autowired
    public ProfileController(ProfileService profileService, DateFormatter dateFormatter) {
        this.profileService = profileService;
        this.dateFormatter = dateFormatter;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProfileResponseDTO> getProfile(@PathVariable Long id, HttpServletRequest request){
        ProfileCacheDTO profile = profileService.getProfile(id);
        ProfileResponseDTO profileResponseDTO = new ProfileResponseDTO();
        profileResponseDTO.setTimeStamp(dateFormatter.formatearFecha());
        profileResponseDTO.setCode("P-200");
        profileResponseDTO.setId_usuario(profile.getId_usuario());
        profileResponseDTO.setUrl_imagen(profile.getUrl_imagen());
        profileResponseDTO.setPrimer_nombre(profile.getPrimer_nombre());
        profileResponseDTO.setSegundo_nombre(profile.getSegundo_nombre());
        profileResponseDTO.setPrimer_apellido(profile.getPrimer_apellido());
        profileResponseDTO.setSegundo_apellido(profile.getSegundo_apellido());
        profileResponseDTO.setNombre_completo(profile.getNombre_completo());
        profileResponseDTO.setEmail(profile.getEmail());
        profileResponseDTO.setTelefono(profile.getTelefono());
        profileResponseDTO.setFecha_nacimiento(profile.getFecha_nacimiento());
        profileResponseDTO.setGenero(profile.getGenero());
        profileResponseDTO.setUrl(request.getRequestURI());
        return new ResponseEntity<>(profileResponseDTO, HttpStatus.OK);

    }

    @PostMapping("/upload-image")
    public ResponseEntity<ResponseDTO> getProfileImage(@RequestParam("Image") MultipartFile multipartFile, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return new ResponseEntity<>(profileService.uploadImage(multipartFile, request, response), HttpStatus.OK);
    }

    @PutMapping("edit-profile/{id}")
    public ResponseEntity<ResponseDTO> updateProfile(@PathVariable Long id, @RequestBody ProfileRequestDTO profileRequestDTO, HttpServletRequest request) {
        profileService.updateProfile(id, profileRequestDTO);
        return new ResponseEntity<>(new ResponseDTO(dateFormatter.formatearFecha(), "P-200", "El perfil fue actualizado correctamente", request.getRequestURI()), HttpStatus.OK);
    }
}
