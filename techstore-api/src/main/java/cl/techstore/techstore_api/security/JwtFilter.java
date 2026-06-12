package cl.techstore.techstore_api.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

@Component // Le dice a Spring que esta clase será administrada como un componente del sistema
public class JwtFilter extends OncePerRequestFilter { 
    // Esta clase será un filtro de seguridad.
    // OncePerRequestFilter asegura que el filtro se ejecute una sola vez por cada petición HTTP.

    @Autowired // Spring inyecta automáticamente una instancia de JwtUtil
    private JwtUtil jwtUtil;
    // JwtUtil es la clase que normalmente se encarga de crear, leer y validar tokens JWT.

    @Autowired // Spring inyecta automáticamente el servicio que carga usuarios
    @Lazy // Retrasa la carga de este servicio para evitar posibles problemas de dependencia circular
    private UserDetailsService userDetailsService;
    // UserDetailsService sirve para buscar los datos del usuario según su username.

    @Override // Indica que estamos sobrescribiendo un método de la clase padre
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        // Este método se ejecuta cada vez que llega una petición HTTP al sistema.
        // request: contiene la información de la solicitud.
        // response: contiene la respuesta que se enviará al cliente.
        // filterChain: permite continuar con el siguiente filtro o con el controlador.

        String authHeader = request.getHeader("Authorization");
        // Obtiene el valor del header Authorization.
        // Ejemplo esperado:
        // Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...

        String token = null;
        // Variable donde guardaremos el token JWT limpio, sin la palabra "Bearer".

        String username = null;
        // Variable donde guardaremos el nombre de usuario extraído desde el token.

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            // Verifica que el header Authorization exista
            // y que comience con la palabra "Bearer ".

            token = authHeader.substring(7);
            // Extrae solo el token.
            // Se usa substring(7) porque "Bearer " tiene 7 caracteres.
            // Ejemplo:
            // "Bearer abc123" -> "abc123"

            username = jwtUtil.extractUsername(token);
            // Extrae el username guardado dentro del token JWT.
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // Verifica dos cosas:
            // 1. Que sí se haya obtenido un username desde el token.
            // 2. Que el usuario todavía no esté autenticado en el contexto de seguridad.

            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            // Busca los datos del usuario en el sistema.
            // Devuelve un objeto UserDetails con username, password y roles/permisos.

            if (jwtUtil.validateToken(token)) {
                // Valida que el token sea correcto.
                // Normalmente revisa que no esté vencido y que la firma sea válida.

                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                // Crea un objeto de autenticación para Spring Security.
                // userDetails: datos del usuario autenticado.
                // null: no se pasa contraseña porque ya se validó con el token.
                // userDetails.getAuthorities(): roles o permisos del usuario.

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                // Agrega detalles extra de la petición, como IP o información del navegador.

                SecurityContextHolder.getContext().setAuthentication(authToken);
                // Guarda la autenticación en el contexto de seguridad de Spring.
                // Desde este momento, Spring considera que el usuario está autenticado.
            }
        }

        filterChain.doFilter(request, response);
        // Permite que la petición continúe su camino.
        // Si todo está bien, llegará al controller correspondiente.
        // Si falta autenticación en un endpoint protegido, Spring Security bloqueará la petición.
    }
}
