package utez.edu.mx.hotelback.modules.user;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import utez.edu.mx.hotelback.modules.user.dto.UserCreateDTO;
import utez.edu.mx.hotelback.modules.user.dto.UserUpdateDTO;
import utez.edu.mx.hotelback.utils.APIResponse;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@AllArgsConstructor
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService userService;

    @GetMapping("")
    public ResponseEntity<APIResponse> findAll() {
        return userService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<APIResponse> findById(@PathVariable("id") UUID id) {
        return userService.findById(id);
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<APIResponse> findByUsername(@PathVariable("username") String username) {
        return userService.findByUsername(username);
    }

    @PostMapping("")
    public ResponseEntity<APIResponse> saveUser(@RequestBody UserCreateDTO dto) {
        return userService.saveUser(dto);
    }

    @PutMapping("")
    public ResponseEntity<APIResponse> updateUser(@RequestBody UserUpdateDTO dto) {
        return userService.updateUser(dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<APIResponse> deleteUser(@PathVariable("id") UUID id) {
        return userService.deleteUser(id);
    }
}
