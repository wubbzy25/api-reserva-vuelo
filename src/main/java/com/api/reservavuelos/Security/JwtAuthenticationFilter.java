    package com.api.reservavuelos.Security;
   //importamos las librerias necesarias
    import com.api.reservavuelos.Utils.Url_WhiteList;
    import jakarta.servlet.FilterChain;
    import jakarta.servlet.ServletException;
    import jakarta.servlet.http.HttpServletRequest;
    import jakarta.servlet.http.HttpServletResponse;
    import lombok.NonNull;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.beans.factory.annotation.Qualifier;
    import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
    import org.springframework.security.core.GrantedAuthority;
    import org.springframework.security.core.context.SecurityContextHolder;
    import org.springframework.security.core.userdetails.UserDetails;
    import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
    import org.springframework.stereotype.Component;
    import org.springframework.web.filter.OncePerRequestFilter;
    import org.springframework.web.servlet.HandlerExceptionResolver;

    import java.io.IOException;
    import java.util.List;

    //definimos la clase JwtAuthenticationFilter que hereda de OncePerRequestFilter y le colocamos la anotacion @Component para que spring lo reconozca
    @Component
    public class JwtAuthenticationFilter extends OncePerRequestFilter {
         //declaramos los servicios, repositorios o interfaces necesarios para utilizarlos
         //servicio para manejar los detalles del usuario en el contexto de seguridad de spring
         private final CustomUserDetailsService customUserDetailsService;
         //componente para realizar acciones con el token(generar, validar y extraer el token)
         private final JwtTokenProvider jwtTokenProvider;
         //componente para obtener el token de la peticion
         private final getTokenForRequest GetTokenForRequest;
         //interfaz para resolver los errores de las peticiones y que el globalExceptionHandler maneje los errores
         @Qualifier("handlerExceptionResolver")
         private final HandlerExceptionResolver resolver;
         //componente para definir las rutas que no necesitan autenticacion
         private final Url_WhiteList urlWhiteList;

         //aplicamos inyeccion de dependencias por medio del contructor
         @Autowired
         public JwtAuthenticationFilter(CustomUserDetailsService customUserDetailsService,
                                        JwtTokenProvider jwtTokenProvider,
                                        getTokenForRequest getTokenForRequest,
                                        @Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver,
                                        Url_WhiteList urlWhiteList) {
            this.customUserDetailsService = customUserDetailsService;
            this.jwtTokenProvider = jwtTokenProvider;
            this.GetTokenForRequest = getTokenForRequest;
            this.resolver = resolver;
            this.urlWhiteList = urlWhiteList;
         }

         //definimos el filtro para validar el token y decidir si el usuario se autentica o no
         @Override
        protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
            //verificamos si la url desde se realizo la peticion es una url que necesita autenticacion, si no, entonces pasa al siguiente filtro
             String requestURI = request.getRequestURI();
            if(urlWhiteList.Url_whiteList().contains(requestURI)){
                filterChain.doFilter(request, response);
                return;
            }

            //obtenemos el token de la peticion
            String  token = GetTokenForRequest.getToken(request, response);
            //obtenemos el email del token
            String email = jwtTokenProvider.getEmailFromToken(token);
            //cargamos el usuario con el email obtenid
           UserDetails userDetails =  customUserDetailsService.loadUserByUsername(email);
           //obtenemos los roles del usuario y lo convertimos a una lista
           List<String> userRoles = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
           //verificamos si el usuario tiene el rol de usuario o administrador para poder autenticarse
           if(userRoles.contains("usuario") ||userRoles.contains("administrador") ){
               //autenticamos el usuario con los roles que tiene
               UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails,
                       null, userDetails.getAuthorities());
               //seteamos los detalles del usuario para que el contexto de seguridad sepa que usuario se esta autenticando
               authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
               //seteamos el usuario autenticado en el contexto de seguridad de spring
               SecurityContextHolder.getContext().setAuthentication(authenticationToken);
           }
          //pasamos al siguiente filtro
         filterChain.doFilter(request, response);

        }
    }
