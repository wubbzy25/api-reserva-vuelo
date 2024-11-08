package com.api.reservavuelos.Services;
//importamos las librerias necesarias
import com.api.reservavuelos.DTO.Cache.ProfileCacheDTO;
import com.api.reservavuelos.DTO.Request.ProfileRequestDTO;
import com.api.reservavuelos.DTO.Response.ResponseDTO;
import com.api.reservavuelos.Exceptions.UserNotFoundException;
import com.api.reservavuelos.Mappers.ProfileMapper;
import com.api.reservavuelos.Models.Profile_image;
import com.api.reservavuelos.Models.Usuarios;
import com.api.reservavuelos.Repositories.UsuarioRepository;
import com.api.reservavuelos.Repositories.ProfileImageRepository;
import com.api.reservavuelos.Security.JwtTokenProvider;
import com.api.reservavuelos.Security.getTokenForRequest;
import com.api.reservavuelos.Utils.DateFormatter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.security.MessageDigest;
import java.util.Map;

//definimos la clase ProfileService y le colocamos @Service para que Spring lo reconozca como un servicio
@Service
public class ProfileService {
    //declaramos los servicios, repositorios o interfaces necesarios para utilizarlos
    private final DateFormatter dateFormatter;
    private final CloudinaryService cloudinaryService;
    private final UsuarioRepository usuarioRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final getTokenForRequest getTokenForRequest;
    private final ProfileImageRepository profileImageRepository;
    private final CacheManager cacheManager;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final ProfileMapper profileMapper;
    //definimos la variables de la key de virustotal
    @Value("${virustotal.api.key}")
    private String apiKey;

    //definimos las constantes de tipos de imagen
    private static final String JPEG = "image/jpeg";
    private static final String PNG = "image/png";

    //aplicamos inyeccion de dependencias por medio del contructor
    @Autowired
    public ProfileService(CloudinaryService cloudinaryService,
                          UsuarioRepository usuarioRepository,
                          JwtTokenProvider jwtTokenProvider,
                          getTokenForRequest getTokenForRequest,
                          ProfileImageRepository profileImageRepository,
                          DateFormatter dateFormatter,
                          CacheManager cacheManager,
                          RestTemplate restTemplate,
                          ObjectMapper objectMapper,
                          ProfileMapper profileMapper) {
        this.cloudinaryService = cloudinaryService;
        this.usuarioRepository = usuarioRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.getTokenForRequest = getTokenForRequest;
        this.profileImageRepository = profileImageRepository;
        this.dateFormatter = dateFormatter;
        this.cacheManager = cacheManager;
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.profileMapper = profileMapper;
    }

    //metodo para obtener el perfil del usuario, y guardarlo en cache
    @Cacheable(value = "ProfileCache", key = "#id")
    public ProfileCacheDTO getProfile(Long id) {
        Usuarios usuario = usuarioRepository.findById(id)
                .orElseThrow(UserNotFoundException::new);
        Profile_image profileImage = profileImageRepository.getProfileImage(usuario.getEmail());
        String nombreCompleto = String.format("%s %s %s %s",
                usuario.getPrimer_nombre(),
                usuario.getSegundo_nombre(),
                usuario.getPrimer_apellido(),
                usuario.getSegundo_apellido());
        return createProfileCacheDTO(profileImage.getImage_url(), usuario, nombreCompleto);
    }

    //metodo para actualizar el perfil del usuario, y actualizar la cache
    @CachePut(value = "ProfileCache", key = "#id")
    public ProfileCacheDTO updateProfile(Long id, ProfileRequestDTO profileRequestDTO) {
        Usuarios usuario = usuarioRepository.findById(id)
                .orElseThrow(UserNotFoundException::new);
       profileMapper.updateProfileFromDto(profileRequestDTO, usuario);
        Usuarios usuarioGuardado = usuarioRepository.save(usuario);

        String nombreCompleto = String.format("%s %s %s %s",
                usuarioGuardado.getPrimer_nombre(),
                usuarioGuardado.getSegundo_nombre(),
                usuarioGuardado.getPrimer_apellido(),
                usuarioGuardado.getSegundo_apellido());
        String profileImage = profileImageRepository.getProfileImage(usuarioGuardado.getEmail()).getImage_url();

        return createProfileCacheDTO(profileImage, usuarioGuardado, nombreCompleto);
    }


    //metodo para subir la imagen del perfil del usuario, y actualizar la cache
    public ResponseDTO uploadImage(MultipartFile multipartFile, HttpServletRequest request, HttpServletResponse response) throws Exception {
        //validamos el tipo de archivo
        validateFileType(multipartFile.getContentType());
        //validamos que el archivo no sea un archivo dañino
        String fileHash = getFileHash(multipartFile);
        if(!isFileSafe(fileHash, multipartFile)){
            throw new IllegalArgumentException("Este archivo puede contener malware");
        }
        String token = getTokenForRequest.getToken(request);
        String email = jwtTokenProvider.getEmailFromToken(token);
        Usuarios usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(UserNotFoundException::new);
        Profile_image profileImage = profileImageRepository.getProfileImage(email);

        if (profileImage == null) {
            throw new IllegalArgumentException("Profile image not found");
        }
        if (profileImage.getPublic_id() != null) {
            cloudinaryService.delete(profileImage.getPublic_id());
        }

        //subimos la imagen a cloudinary y guardamos el url en la base de datos
        Map uploadResult = cloudinaryService.upload(multipartFile);
        String url = (String) uploadResult.get("url");
        String publicId = (String) uploadResult.get("public_id");
        profileImage.setImage_url(url);
        profileImage.setPublic_id(publicId);
        profileImageRepository.save(profileImage);

        String nombreCompleto = String.format("%s %s %s %s",
                usuario.getPrimer_nombre(),
                usuario.getSegundo_nombre(),
                usuario.getPrimer_apellido(),
                usuario.getSegundo_apellido());
        ProfileCacheDTO profileCacheDTO = createProfileCacheDTO(url, usuario, nombreCompleto);
        updateProfileCache(usuario.getId(), profileCacheDTO);

        return new ResponseDTO(dateFormatter.formatearFecha(), "P-200", "La imagen fue subida correctamente", request.getRequestURI());
    }

    //metodo para crear el dto de la cache del perfil del usuario
    private ProfileCacheDTO createProfileCacheDTO(String imageUrl, Usuarios usuario, String nombreCompleto) {
        ProfileCacheDTO profile = new ProfileCacheDTO();
        profile.setId_usuario(usuario.getId());
        profile.setUrl_imagen(imageUrl);
        profile.setPrimer_nombre(usuario.getPrimer_nombre());
        profile.setSegundo_nombre(usuario.getSegundo_nombre());
        profile.setPrimer_apellido(usuario.getPrimer_apellido());
        profile.setSegundo_apellido(usuario.getSegundo_apellido());
        profile.setNombre_completo(nombreCompleto);
        profile.setEmail(usuario.getEmail());
        profile.setTelefono(usuario.getTelefono());
        profile.setFecha_nacimiento(usuario.getFecha_nacimiento());
        profile.setGenero(usuario.getGenero());
        return profile;
    }

    //metod opara actualizar la cache del perfil del usuario
    private void updateProfileCache(Long id, ProfileCacheDTO profileCacheDTO) {
        Cache cache = cacheManager.getCache("ProfileCache");
        if (cache != null) {
            cache.put(id, profileCacheDTO);
        }
    }

    //metodo para validar el tipo de archivo
    private void validateFileType(String contentType) {
        if ((!JPEG.equals(contentType) && !PNG.equals(contentType))) {
            throw new IllegalArgumentException("El tipo de archivo no es válido. Sólo se permiten imágenes en formato JPEG o PNG.");
        }
    }

    //metodo para validar que el archivo no sea dañino
     boolean isFileSafe(String fileHash, MultipartFile file) {
        try {
            String url = "https://www.virustotal.com/api/v3/files/" + fileHash;
            HttpHeaders headers = new HttpHeaders();
            headers.set("x-apikey", apiKey);
            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            String responseBody = response.getBody();
            int positives = extractPositivesFromResponse(responseBody);
            return positives == 0;
        } catch (HttpClientErrorException.NotFound e) {
            return uploadFileToVirusTotal(file);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    private int extractPositivesFromResponse(String responseBody) {
        try {
            JsonNode jsonNode = objectMapper.readTree(responseBody);
            JsonNode positivesNode = jsonNode.path("data").path("attributes").path("last_analysis_stats").path("malicious");
            return positivesNode.asInt();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    private String getFileHash(MultipartFile file) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(file.getBytes());

        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            hexString.append(String.format("%02x", b));
        }
        return hexString.toString();
    }

    private boolean uploadFileToVirusTotal(MultipartFile file) {
        try {
            String url = "https://www.virustotal.com/api/v3/files";
            HttpHeaders headers = new HttpHeaders();
            headers.set("x-apikey", apiKey);
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", new ByteArrayResource(file.getBytes()) {
                @Override
                public String getFilename() {
                    return file.getOriginalFilename();
                }
            });

            HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(body, headers);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

            String responseBody = response.getBody();
            System.out.println("Archivo subido con éxito. Respuesta: " + responseBody);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
