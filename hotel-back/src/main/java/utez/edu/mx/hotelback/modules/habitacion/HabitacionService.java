package utez.edu.mx.hotelback.modules.habitacion;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import utez.edu.mx.hotelback.modules.asignacion.AsignacionHabitacionRepository;
import utez.edu.mx.hotelback.modules.habitacion.dto.HabitacionCreateDTO;
import utez.edu.mx.hotelback.modules.habitacion.dto.HabitacionDTO;
import utez.edu.mx.hotelback.modules.habitacion.dto.HabitacionUpdateEstadoDTO;
import utez.edu.mx.hotelback.utils.APIResponse;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class HabitacionService {

    private final HabitacionRepository habitacionRepository;
    private final AsignacionHabitacionRepository asignacionRepository;

    // ➤ Nuevo directorio para guardar los QR
    private static final String QR_UPLOAD_DIR = "uploads/qrs/";

    public HabitacionService(HabitacionRepository habitacionRepository,
                             AsignacionHabitacionRepository asignacionRepository) {
        this.habitacionRepository = habitacionRepository;
        this.asignacionRepository = asignacionRepository;

        // Crear directorio de QR si no existe
        try {
            Files.createDirectories(Paths.get(QR_UPLOAD_DIR));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private HabitacionDTO convertEntityToDTO(Habitacion h) {
        return new HabitacionDTO(
                h.getId(),
                h.getNumero(),
                h.getEstado(),
                h.getQr(),
                h.getAsignaciones() != null && !h.getAsignaciones().isEmpty()
        );
    }

    private List<HabitacionDTO> convertEntitiesToDTO(List<Habitacion> habitaciones) {
        List<HabitacionDTO> list = new ArrayList<>();
        for (Habitacion h : habitaciones) {
            list.add(convertEntityToDTO(h));
        }
        return list;
    }

    @Transactional(readOnly = true)
    public ResponseEntity<APIResponse> findAll() {
        APIResponse body = new APIResponse(
                "Operación exitosa",
                convertEntitiesToDTO(habitacionRepository.findAll()),
                HttpStatus.OK
        );
        return new ResponseEntity<>(body, body.getStatus());
    }

    @Transactional(readOnly = true)
    public ResponseEntity<APIResponse> findById(UUID id) {
        APIResponse body;
        Habitacion found = habitacionRepository.findById(id).orElse(null);
        if (found != null) {
            body = new APIResponse("Operación exitosa", convertEntityToDTO(found), HttpStatus.OK);
        } else {
            body = new APIResponse("La habitación no existe", true, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(body, body.getStatus());
    }

    @Transactional(readOnly = true)
    public ResponseEntity<APIResponse> findByEstado(EstadoHabitacion estado) {
        APIResponse body = new APIResponse(
                "Operación exitosa",
                convertEntitiesToDTO(habitacionRepository.findByEstado(estado)),
                HttpStatus.OK
        );
        return new ResponseEntity<>(body, body.getStatus());
    }

    @Transactional(rollbackFor = {SQLException.class, Exception.class})
    public ResponseEntity<APIResponse> saveHabitacion(HabitacionCreateDTO dto) {
        APIResponse body;
        try {
            if (habitacionRepository.findByNumero(dto.getNumero()).isPresent()) {
                body = new APIResponse("Ya existe una habitación con ese número", true, HttpStatus.BAD_REQUEST);
                return new ResponseEntity<>(body, body.getStatus());
            }

            Habitacion h = new Habitacion();
            h.setNumero(dto.getNumero());
            h.setEstado(dto.getEstado() != null ? dto.getEstado() : EstadoHabitacion.LIMPIA);

            h = habitacionRepository.saveAndFlush(h);

            // ➤ Generar QR
            byte[] qrImage = generarQrCode(h.getId().toString(), 300, 300);
            h.setQr(qrImage);

            // ➤ Guardar el QR como archivo físico
            String fileName = h.getId().toString() + ".png";
            Path filePath = Paths.get(QR_UPLOAD_DIR + fileName);
            Files.write(filePath, qrImage);

            habitacionRepository.save(h);

            body = new APIResponse("Operación exitosa", convertEntityToDTO(h), HttpStatus.CREATED);
        } catch (Exception ex) {
            ex.printStackTrace();
            body = new APIResponse("No se pudo registrar la habitación", true, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(body, body.getStatus());
    }

    private byte[] generarQrCode(String texto, int ancho, int alto) throws Exception {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(texto, BarcodeFormat.QR_CODE, ancho, alto);

        ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);

        return pngOutputStream.toByteArray();
    }

    @Transactional(rollbackFor = {SQLException.class, Exception.class})
    public ResponseEntity<APIResponse> updateEstado(HabitacionUpdateEstadoDTO dto) {
        APIResponse body;
        Habitacion found = habitacionRepository.findById(dto.getId()).orElse(null);
        if (found != null) {
            try {
                found.setEstado(dto.getEstado());
                habitacionRepository.saveAndFlush(found);
                body = new APIResponse("Operación exitosa", convertEntityToDTO(found), HttpStatus.OK);
            } catch (Exception ex) {
                ex.printStackTrace();
                body = new APIResponse("No se pudo actualizar la habitación", true, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            body = new APIResponse("La habitación no existe", true, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(body, body.getStatus());
    }

    @Transactional(rollbackFor = {SQLException.class, Exception.class})
    public ResponseEntity<APIResponse> marcarLimpia(UUID id) {
        HabitacionUpdateEstadoDTO dto = new HabitacionUpdateEstadoDTO(id, EstadoHabitacion.LIMPIA);
        return updateEstado(dto);
    }

    @Transactional(rollbackFor = {SQLException.class, Exception.class})
    public ResponseEntity<APIResponse> deleteHabitacion(UUID id) {
        APIResponse body;
        Habitacion found = habitacionRepository.findById(id).orElse(null);
        if (found != null) {
            try {
                // PRIMERO: Eliminar todas las asignaciones de esta habitación
                asignacionRepository.findByHabitacionIdAndActivaTrue(id).forEach(asignacion -> {
                    asignacionRepository.delete(asignacion);
                });

                // También eliminar las inactivas si las hay
                asignacionRepository.deleteAll(
                        asignacionRepository.findAll().stream()
                                .filter(a -> a.getHabitacion().getId().equals(id))
                                .toList()
                );

                // SEGUNDO: Eliminar el archivo de QR si existe
                try {
                    Path qrPath = Paths.get(QR_UPLOAD_DIR + id.toString() + ".png");
                    Files.deleteIfExists(qrPath);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // TERCERO: Eliminar la habitación
                habitacionRepository.deleteById(id);

                body = new APIResponse("Operación exitosa", HttpStatus.OK);
            } catch (Exception ex) {
                ex.printStackTrace();
                body = new APIResponse("No se pudo eliminar la habitación: " + ex.getMessage(), true, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            body = new APIResponse("La habitación no existe", true, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(body, body.getStatus());
    }
}
