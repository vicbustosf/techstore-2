package cl.techstore.techstore_api.service;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
 
@Service
public class AuditoriaSqsService {
 
    private final SqsClient sqsClient;
    private final ObjectMapper objectMapper;
 
    @Value("${aws.sqs.queue-url}")
    private String queueUrl;
 
    public AuditoriaSqsService(@Value("${aws.region}") String region) {
        this.sqsClient = SqsClient.builder()
                .region(Region.of(region))
                .build();
 
        this.objectMapper = new ObjectMapper();
    }
 
    public void enviarEvento(String accion, Long productoId, String nombre, String usuario) {
        try {
            Map<String, Object> mensaje = new LinkedHashMap<>();
            mensaje.put("accion", accion);
            mensaje.put("productoId", productoId);
            mensaje.put("nombre", nombre);
            mensaje.put("usuario", usuario);
            mensaje.put("fecha", LocalDateTime.now().toString());
 
            String mensajeJson = objectMapper.writeValueAsString(mensaje);
 
            SendMessageRequest request = SendMessageRequest.builder()
                    .queueUrl(queueUrl)
                    .messageBody(mensajeJson)
                    .build();
 
            sqsClient.sendMessage(request);
 
        } catch (Exception e) {
            System.out.println("No se pudo enviar auditoría a SQS: " + e.getMessage());
        }
    }
}