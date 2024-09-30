package com.translator.webchat.controller;

import com.translator.webchat.dto.request.ChangePasswordRequestDTO;
import com.translator.webchat.dto.request.CreateSessionDTO;
import com.translator.webchat.dto.response.ResponseData;
import com.translator.webchat.dto.response.ResponseGetUser;
import com.translator.webchat.dto.response.UserSignInResponse;
import com.translator.webchat.service.impl.UserServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;

@RestController
@RequestMapping("/v1/api/user")
@Validated
@Slf4j
@Tag(name = "User", description = "The User API")
@RequiredArgsConstructor
public class UserController {

    private final UserServiceImpl userService;

    @Operation(summary = "Change password", description = "Change the user's password",  responses = {
            @ApiResponse(responseCode = "200", description = "Password changed in database",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(name = "password changed successfully", summary = "return true",
                                    value = "{\"status\": 200, \"message\": \"Password changed successfully\", \"data\": \"true\"}"
                            ))),
            @ApiResponse(responseCode = "400", description = "Old password and new password is invalid",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(name = "bad request", summary = "old password and new password is invalid",
                                    value = "{status: 400, message: \"Password must be at least 8 characters\", data: \"1\"}<br />" +
                                            "{status: 400, message: \"New password must be at least 8 characters\", data: \"2\" }<br />"
                            ))),
            @ApiResponse(responseCode = "400", description = "Old password is incorrect",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(name = "unauthorized", summary = "old password is incorrect",
                                    value = "{status: 401, message: \"Old password is incorrect\", data: \"3\"}<br />"

                            ))),
            @ApiResponse(responseCode = "409", description = "Old password and new password is same",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(name = "conflict", summary = "old password and new password is same",
                                    value = "{status: 409, message: \"Old password and new password can't be the same\", data: \"4\" }<br />"
                            )))
    })
    @PatchMapping("/changepassword")
    public ResponseEntity<ResponseData<String>> changePassword (
            @Valid @RequestBody ChangePasswordRequestDTO changePasswordRequest,
            String email) throws NoSuchAlgorithmException, InvalidKeySpecException {
        userService.changePassword(changePasswordRequest);
        return ResponseEntity.ok(new ResponseData<>(HttpStatus.OK.value(), "Password changed successfully", "true"));
    }


    @Operation(summary = "Get user", description = "Get the user's information", responses = {
            @ApiResponse(responseCode = "200", description = "User found",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(name = "user found", summary = "return user",
                                    value = "{\"status\": 200, \"message\": \"User found\", \"data\": {\"username\": \"username\", \"nickname\": \"nickname\"}}"
                            ))),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(name = "user not found", summary = "user not found",
                                    value = "{status: 404, message: \"User not found\", data: \"1\"}"
                            )))
    })
    @GetMapping("/getuser")
    public ResponseData<ResponseGetUser> getUser(@Valid @RequestParam String usernameOrNickname)
            throws NoSuchAlgorithmException, InvalidKeySpecException {

        ResponseGetUser responseGetUser = userService.getUser(usernameOrNickname);
        return new ResponseData<>(HttpStatus.OK.value(), "User found", responseGetUser);
    }


    @Operation(summary = "Create session", description = "Create a new session", responses = {
            @ApiResponse(responseCode = "200", description = "Session created",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(name = "session created", summary = "true",
                                    value = "{\"status\": 200, \"message\": \"Session created\", \"data\": \"true\"}"
                            ))),
            @ApiResponse(responseCode = "404", description = "User1 Not Found",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(name = "User1 Not Found", summary = "User1 Not Found",
                                    value = "{status: 404, message: \"User1 Not Found\", data: \"1\"}"
                            ))),
            @ApiResponse(responseCode = "404", description = "User2 Not Found",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(name = "User2 Not Found", summary = "User2 Not Found",
                                    value = "{status: 404, message: \"User2 Not Found\", data: \"1\"}"
                            ))),
            @ApiResponse(responseCode = "409", description = "Session already exist",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(name = "Session already exist", summary = "Session already exist",
                                    value = "{status: 409, message: \"Session already exist\", data: \"1\"}"
                            )))
    })
    @PostMapping("/createsession")
    public ResponseEntity<ResponseData<String>> createSession(@Valid @RequestBody CreateSessionDTO createSessionDTO)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        // Logic to create session
        String data = userService.createSession(createSessionDTO);
        ResponseData<String> responseData = new ResponseData<>(HttpStatus.OK.value(), "Session created", data);
        return ResponseEntity.ok(responseData);
    }

    @Operation(summary = "Get all users", description = "Get the user's information", responses = {
            @ApiResponse(responseCode = "200", description = "User found",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(name = "user found", summary = "return user",
                                    value = "{\"status\": 200, \"message\": \"User found\", \"data\": [{\"username\": \"username\", \"nickname\": \"nickname\"}}]"
                            )))
    })
    @GetMapping("/getall")
    public ResponseData<List<UserSignInResponse>> getAll() {

        List<UserSignInResponse> allUsers = userService.getAllUsers();
        return new ResponseData<>(HttpStatus.OK.value(), "Users found", allUsers);
    }
}
