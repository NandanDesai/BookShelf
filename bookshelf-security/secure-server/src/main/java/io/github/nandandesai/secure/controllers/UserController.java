package io.github.nandandesai.secure.controllers;

import io.github.nandandesai.secure.dto.LoginSuccessResult;
import io.github.nandandesai.secure.dto.Photo;
import io.github.nandandesai.secure.dto.UserDto;
import io.github.nandandesai.secure.dto.requests.*;
import io.github.nandandesai.secure.dto.responses.Response;
import io.github.nandandesai.secure.dto.responses.ResponseType;
import io.github.nandandesai.secure.exceptions.*;
import io.github.nandandesai.secure.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/secure")
public class UserController {

    private Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @GetMapping("/users")
    public Response<List<UserDto>> getAll() {
        List<UserDto> userList = userService.getAllUsers();
        Response<List<UserDto>> response = new Response<>();
        response.setPayload(userList);
        response.setType(ResponseType.SUCCESS);
        return response;
    }

    @GetMapping("/users/{id}")
    public Response<UserDto> getUser(@PathVariable("id") Integer id) throws ResourceNotFoundException {
        Response<UserDto> response = new Response<>();
        response.setPayload(userService.getUser(id));
        response.setType(ResponseType.SUCCESS);
        return response;
    }

    @PostMapping("/signup")
    @ResponseBody
    public ResponseEntity<Response> signUp(@Valid @RequestBody UserSignUpRequest userSignUpRequest, HttpServletResponse response) throws InternalServerException, DuplicateEntityException, RecaptchaVerificationException {
        LoginSuccessResult loginSuccessResult = userService.addUser(userSignUpRequest);
        String token = loginSuccessResult.getToken();
        Cookie cookie = new Cookie("token", token);
        cookie.setMaxAge(7 * 24 * 60 * 60); // expires in 7 days
        //cookie.setSecure(true);
        cookie.setHttpOnly(true);
        cookie.setPath("/secure"); // this setting will ask the browser to send this cookie with every url path
        response.addCookie(cookie);
        Response<UserDto> res = new Response<UserDto>().setPayload(loginSuccessResult.getUserDto())
                .setType(ResponseType.SUCCESS);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @PostMapping("/login")
    @ResponseBody
    public ResponseEntity<Response> login(@Valid @RequestBody UserLoginRequest userLoginRequest, HttpServletResponse response) throws LoginFailedException, RecaptchaVerificationException, InternalServerException {
        LoginSuccessResult loginSuccessResult = userService.login(userLoginRequest, false);
        String token = loginSuccessResult.getToken();
        Cookie cookie = new Cookie("token", token);
        cookie.setMaxAge(7 * 24 * 60 * 60); // expires in 7 days
        //cookie.setSecure(true);
        cookie.setHttpOnly(true);
        cookie.setPath("/secure"); // this setting will ask the browser to send this cookie with every url path
        response.addCookie(cookie);
        Response<UserDto> res = new Response<UserDto>().setPayload(loginSuccessResult.getUserDto())
                .setType(ResponseType.SUCCESS);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @PatchMapping("/users/pass")
    public Response<String> updatePassword(@Valid @RequestBody UpdateUserPasswordRequest userPasswordRequest) throws ResourceNotFoundException, ValidationException {
        userService.updatePassword(userPasswordRequest);
        return new Response<String>().setPayload("Password successfully updated.")
                .setType(ResponseType.SUCCESS);
    }

    @PatchMapping("/users/role")
    public Response<String> updateRole(@Valid @RequestBody UpdateUserRoleRequest userRoleRequest) throws ResourceNotFoundException, ValidationException {
        userService.updateRole(userRoleRequest);
        return new Response<String>().setPayload("Role successfully updated.")
                .setType(ResponseType.SUCCESS);
    }

    @PostMapping("/users/photo")
    public Response<String> photoUpload(@Valid @ModelAttribute AddUserPhotoRequest addUserPhotoRequest) throws InternalServerException, ResourceNotFoundException {
        userService.saveUserPhoto(addUserPhotoRequest);
        return new Response<String>().setPayload("Successfully uploaded")
                .setType(ResponseType.SUCCESS);
    }

    @GetMapping("/users/photo/{id}")
    public ResponseEntity<byte[]> getPhoto(@PathVariable("id") Integer id) throws ResourceNotFoundException, InternalServerException {
        Photo photo = userService.getUserPhoto(id);
        byte[] photoBytes = photo.getPhotoBytes();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(photo.getMimeType()));
        return new ResponseEntity<>(photoBytes, headers, HttpStatus.OK);
    }
}
