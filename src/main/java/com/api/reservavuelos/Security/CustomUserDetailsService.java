package com.api.reservavuelos.Security;
//importamos las librerias necesarias
import com.api.reservavuelos.Exceptions.UserNotFoundException;
import com.api.reservavuelos.Models.Roles;
import com.api.reservavuelos.Models.Usuarios;
import com.api.reservavuelos.Repositories.UsuarioRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

//definimos la clase CustomUserDetailsService que implementa la interfaz UserDetailsService y le colocamos la anotacion @Service para que spring lo reconozca como un servicio
@Service
public class CustomUserDetailsService  implements UserDetailsService {
    //declaramos los servicios, repositorios o interfaces necesarios para utilizarlos
    //repositorio para realizar las operaciones de la base de datos
    private final UsuarioRepository usuarioRepository;
    //interfaz para resolver los errores y los pueda manejar el GlobalExceptionHandler
    private final HandlerExceptionResolver resolver;
    //interfaz para obtener la peticion
    private final HttpServletRequest request;
    //interfaz para obtener la respuesta
    private final HttpServletResponse response;

    //aplicamos inyeccion de dependencias por el medio del contructor
    @Autowired
    public CustomUserDetailsService(UsuarioRepository usuarioRepository,
                                    @Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver,
                                    HttpServletRequest request,
                                    HttpServletResponse response) {
        this.usuarioRepository = usuarioRepository;
        this.resolver = resolver;
        this.request = request;
        this.response = response;
    }

    //metodo para convertir los roles de una lista a una coleccion de autoridades
    public Collection<GrantedAuthority> mapToAuthorities(List<Roles> roles){
    return roles.stream().map(role ->new SimpleGrantedAuthority(role.getNombre())).collect(Collectors.toList());
    }
    //metodo que implementa la interfaz UserDetailsService para cargar los datos del usuario y retornarlo como un objeto de tipo UserDetails
    @Override
    public UserDetails loadUserByUsername(String Email) throws UserNotFoundException {
        try {
            Usuarios usuarios = usuarioRepository.findByEmail(Email).orElseThrow(UserNotFoundException::new);
            String password = usuarioRepository.findPasswordByEmail(Email).orElseThrow(UserNotFoundException::new);
            return new User(usuarios.getEmail(), password, mapToAuthorities(usuarios.getRoles()));
        } catch (UserNotFoundException e){
            resolver.resolveException(request, response, null, e);
            throw e;
        }
    }
}
