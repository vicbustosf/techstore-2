package cl.techstore.techstore_api.security;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.util.Date;

// Indica que esta clase será administrada por Spring.
// Así Spring puede inyectarla en otras clases con @Autowired.
@Component
public class JwtUtil {

    // Toma el valor de la propiedad jwt.secret desde application.properties.
    // Este valor será la clave secreta usada para firmar el token.
    @Value("${jwt.secret}")
    private String secret;

    // Toma el valor de jwt.expiration desde application.properties.
    // Indica cuánto tiempo durará válido el token.
    @Value("${jwt.expiration}")
    private Long expiration;

    // Método privado que genera la clave de firma del JWT.
    // Usa el texto secreto y lo transforma en una SecretKey.
    private SecretKey getSigningKey() {

        // Convierte el texto secret a bytes y crea una clave segura para HMAC.
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    // Método que genera un token JWT.
    // Recibe el nombre de usuario y su rol.
    public String generateToken(String username, String rol) {

        // Comienza la construcción del token JWT.
        return Jwts.builder()

                // Define el "subject" del token.
                // Normalmente aquí va el username o email del usuario.
                .setSubject(username)

                // Agrega un dato extra al token.
                // En este caso guarda el rol del usuario.
                .claim("rol", rol)

                // Guarda la fecha y hora en que se creó el token.
                .setIssuedAt(new Date())

                // Define la fecha de expiración del token.
                // Suma el tiempo actual + la duración definida en application.properties.
                .setExpiration(new Date(System.currentTimeMillis() + expiration))

                // Firma el token usando la clave secreta y el algoritmo HS256.
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)

                // Convierte todo lo anterior en un String final.
                .compact();
    }

    // Método que extrae el username desde un token JWT.
    public String extractUsername(String token) {

        // Crea un parser para leer y validar el token.
        return Jwts.parserBuilder()

                // Indica la clave secreta con la que se validará la firma.
                .setSigningKey(getSigningKey())

                // Construye el parser.
                .build()

                // Procesa el token recibido.
                // Si el token fue alterado o está mal firmado, aquí fallará.
                .parseClaimsJws(token)

                // Obtiene el cuerpo del token.
                // El cuerpo contiene los datos guardados, como username, rol, fechas, etc.
                .getBody()

                // Extrae el subject, que en este caso es el username.
                .getSubject();
    }

    // Método que valida si un token es correcto.
    public boolean validateToken(String token) {

        // Intenta leer y validar el token.
        try {

            // Crea el parser, le pasa la clave secreta y procesa el token.
            // Si el token es válido, no ocurre ningún error.
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);

            // Si llegó hasta aquí, significa que el token es válido.
            return true;

        // Captura errores relacionados con JWT inválido, expirado, alterado o mal formado.
        } catch (JwtException | IllegalArgumentException e) {

            // Si ocurre algún error, el token no es válido.
            return false;
        }
    }
}
