package co.teamsphere.teamsphere.controller;

import co.teamsphere.teamsphere.DTO.UserDTO;
import co.teamsphere.teamsphere.DTOmapper.UserDTOMapper;
import co.teamsphere.teamsphere.exception.UserException;
import co.teamsphere.teamsphere.models.User;
import co.teamsphere.teamsphere.request.UpdateUserRequest;
import co.teamsphere.teamsphere.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/user")
@Slf4j
public class UserController {
    private final UserService userService;

    private final UserDTOMapper userDTOMapper;
    public UserController(UserService userService, UserDTOMapper  userDTOMapper) {
        this.userService = userService;
        this.userDTOMapper = userDTOMapper;
    }

    @PutMapping("/update/{userId}")
    public ResponseEntity<UserDTO> updateUserHandler(@RequestBody UpdateUserRequest req, @PathVariable UUID userId) throws UserException {
        try {
            log.info("Processing update user request for user with ID: {}", userId);

            User updatedUser = userService.updateUser(userId, req);
            UserDTO userDTO = userDTOMapper.toUserDTO(updatedUser);

            log.info("User with ID {} updated successfully", userId);

            return new ResponseEntity<>(userDTO, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error during update user process for user with ID: {}", userId, e);
            throw new UserException("Error during update user process" + e);
        }
    }

    @GetMapping("/profile")
    public ResponseEntity<UserDTO> getUserProfileHandler(@RequestHeader("Authorization") String jwt) {
        try {
            log.info("Processing get user profile request");

            User user = userService.findUserProfile(jwt);

            UserDTO userDTO = userDTOMapper.toUserDTO(user);

            log.info("User profile retrieved successfully");

            return new ResponseEntity<>(userDTO, HttpStatus.ACCEPTED);
        } catch (Exception e) {
            log.error("Error during get user profile process", e);
            // We might want to handle this exception differently.
            // Here, I'm letting it propagate to the client as a 500 Internal Server Error.
            throw new RuntimeException("Error during get user profile process", e);
        }
    }

    @GetMapping("/search")
    public ResponseEntity<HashSet<UserDTO>> searchUsersByName(@RequestParam("name") String name) {
        try {
            log.info("Processing search users by name={}", name);

            List<User> users = userService.searchUser(name);

            HashSet<User> set = new HashSet<>(users);

            HashSet<UserDTO> userDtos = userDTOMapper.toUserDtos(set);

            log.info("Users search completed successfully for name={}", name);

            return new ResponseEntity<>(userDtos, HttpStatus.ACCEPTED);
        } catch (Exception e) {
            log.error("Error during search users process", e);
            // We might want to handle this exception differently.
            // Here, I'm letting it propagate to the client as a 500 Internal Server Error.
            throw new RuntimeException("Error during search users process", e);
        }
    }
}
