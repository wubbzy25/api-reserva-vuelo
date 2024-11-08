package com.api.reservavuelos.Security;

import com.api.reservavuelos.DataProvider;
import com.api.reservavuelos.Exceptions.UserNotFoundException;
import com.api.reservavuelos.Models.Roles;
import com.api.reservavuelos.Models.Usuarios;
import com.api.reservavuelos.Repositories.UsuarioRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.servlet.HandlerExceptionResolver;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    Roles roleAdmin;
    @Mock
    Roles roleUser;
    @Mock
    UsuarioRepository usuarioRepository;
    @Mock
    HttpServletRequest request;
    @Mock
    HttpServletResponse response;
    @Mock
    HandlerExceptionResolver handlerExceptionResolver;
    @InjectMocks
    CustomUserDetailsService customUserDetailsService;

    @Test
    void mapToAuthorities() {

        // Definir el comportamiento de los mocks
        when(roleAdmin.getNombre()).thenReturn("ROLE_ADMIN");
        when(roleUser.getNombre()).thenReturn("ROLE_USER");

        // Crear la lista de roles
        List<Roles> roles = Arrays.asList(roleAdmin, roleUser);


        // Llamar al método mapToAuthorities
        Collection<GrantedAuthority> authorities = customUserDetailsService.mapToAuthorities(roles);

        // Crear la colección esperada de GrantedAuthority
        Collection<GrantedAuthority> expectedAuthorities = Arrays.asList(
                new SimpleGrantedAuthority("ROLE_ADMIN"),
                new SimpleGrantedAuthority("ROLE_USER")
        );

        // Verificar el resultado
        assertEquals(expectedAuthorities, authorities);

        // Verificar que los métodos getNombre fueron llamados en cada mock
        verify(roleAdmin, times(1)).getNombre();
        verify(roleUser, times(1)).getNombre();
    }


    @Test
    void lanzarExcepcionCuandoELUsuarioNoExistaBasadoEnElEmail(){
        when(usuarioRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> customUserDetailsService.loadUserByUsername("email"));

        verify(usuarioRepository, times(1)).findByEmail(anyString());
    }
    @Test
    void LanzarExcepcionCuandoElUsuarioNoExistaParaLaContraseña() throws ParseException {
        when(usuarioRepository.findByEmail(anyString())).thenReturn(Optional.of(DataProvider.obtenerUsuarioPorDefectoRolUsuario()));
        when(usuarioRepository.findPasswordByEmail(anyString())).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> customUserDetailsService.loadUserByUsername("email"));

        verify(usuarioRepository, times(1)).findByEmail(anyString());
    }
    @Test
    void loadUserByUsername() throws ParseException {
        String email = "test@example.com";
        String password = "password123";

        Usuarios mockUsuario = mock(Usuarios.class);
        when(mockUsuario.getEmail()).thenReturn(email);
        Roles rol = new Roles();
        rol.setNombre("ROLE_USER");
        when(mockUsuario.getRoles()).thenReturn(List.of(rol));

        when(usuarioRepository.findByEmail(email)).thenReturn(Optional.of(mockUsuario));
        when(usuarioRepository.findPasswordByEmail(email)).thenReturn(Optional.of(password));

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);

        assertEquals(email, userDetails.getUsername());
        assertEquals(password, userDetails.getPassword());
        assertEquals(1, userDetails.getAuthorities().size());
        assertEquals("ROLE_USER", userDetails.getAuthorities().iterator().next().getAuthority());

        verify(usuarioRepository, times(1)).findByEmail(email);
        verify(usuarioRepository, times(1)).findPasswordByEmail(email);
    }
}