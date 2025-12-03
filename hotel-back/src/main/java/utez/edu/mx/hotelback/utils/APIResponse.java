package utez.edu.mx.hotelback.utils;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class APIResponse {
    private String message;
    private Object data;
    private boolean error;
    private HttpStatus status;

    // Mandar un mensaje
    public APIResponse(String message, HttpStatus status) {
        this.message = message;
        this.status = status;
    }

    // Mandar un mensaje y un cuerpo de respuesta (data)
    public APIResponse(String message, Object data, HttpStatus status) {
        this.message = message;
        this.data = data;
        this.status = status;
    }

    // Mandar un mensaje de error
    public APIResponse(String message, boolean error, HttpStatus status) {
        this.message = message;
        this.error = true;
        this.status = status;
    }
}
