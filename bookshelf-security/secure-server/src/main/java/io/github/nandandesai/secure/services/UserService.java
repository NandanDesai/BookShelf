package io.github.nandandesai.secure.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.nandandesai.secure.configs.UserDataPaths;
import io.github.nandandesai.secure.dto.LoginSuccessResult;
import io.github.nandandesai.secure.dto.Photo;
import io.github.nandandesai.secure.dto.UserDto;
import io.github.nandandesai.secure.dto.requests.*;
import io.github.nandandesai.secure.exceptions.*;
import io.github.nandandesai.secure.models.Role;
import io.github.nandandesai.secure.models.User;
import io.github.nandandesai.secure.repositories.RoleRepository;
import io.github.nandandesai.secure.repositories.UserRepository;
import io.github.nandandesai.secure.security.JwtService;
import io.github.nandandesai.secure.security.UserSecurityDetailsService;
import io.github.nandandesai.secure.security.models.UserAuthority;
import io.github.nandandesai.secure.security.models.UserSecurityDetails;
import io.github.nandandesai.secure.utils.FileOperation;
import okhttp3.*;
import org.apache.commons.imaging.ImageInfo;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.Imaging;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserDataPaths userDataPaths;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserSecurityDetailsService userSecurityDetailsService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${recaptcha.secret}")
    private String recaptchaSecret;

    @PreAuthorize("hasAuthority('Admin')")
    public List<UserDto> getAllUsers() {
        return UserDto.getUserDtoListFromUsers(userRepository.findAll());
    }

    @PreAuthorize("#id == authentication.principal.grantedAuthorities[0].userId or hasAuthority('Admin')")
    public UserDto getUser(Integer id) throws ResourceNotFoundException {
        Optional<User> userOptional = userRepository.findById(id);
        if(!userOptional.isPresent()){
            throw new ResourceNotFoundException("User with ID '"+id+"' not found");
        }
        return UserDto.getUserDtoFromUser(userOptional.get());
    }

    public boolean verifyRecaptchaChallenge(String userChallengeToken) throws InternalServerException {
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("secret", recaptchaSecret)
                .addFormDataPart("response", userChallengeToken)
                .build();
        Request request = new Request.Builder()
                .url("https://www.google.com/recaptcha/api/siteverify")
                .post(requestBody)
                .build();
        try {
            Response response = client.newCall(request).execute();
            String responseJsonString = response.body().string();
            ObjectMapper mapper = new ObjectMapper();
            boolean success = mapper.readTree(responseJsonString).findValue("success").asBoolean();
            logger.info("reCAPTCHA success: "+success);
            return success;
        } catch (IOException e) {
            e.printStackTrace();
        }
        throw new InternalServerException("There was a problem verifying the reCAPTCHA challenge.");
    }

    //returns jwt if success
    public LoginSuccessResult addUser(UserSignUpRequest userSignUpRequest) throws InternalServerException, DuplicateEntityException, RecaptchaVerificationException {
        logger.info("CHALLENGE TOKEN: " + userSignUpRequest.getChallengeToken());
        logger.info("RECAPTCHA SECRET: "+ recaptchaSecret);
        boolean success = verifyRecaptchaChallenge(userSignUpRequest.getChallengeToken());
        if(!success){
            throw new RecaptchaVerificationException("reCAPTCHA challenge failed");
        }
        Optional<User> optionalUser = userRepository.findByEmail(userSignUpRequest.getEmail());
        if(optionalUser.isPresent()){
            throw new DuplicateEntityException("email \""+userSignUpRequest.getEmail()+"\" already exists");
        }

        User user = new User();
        user.setEmail(userSignUpRequest.getEmail())
                .setPassword(passwordEncoder.encode(userSignUpRequest.getPassword()))
                .setFullName(userSignUpRequest.getFullName())
                .setLastLogin(LocalDateTime.now())
                .setProfilePicName("default-avatar.png");
        Optional<Role> optionalRole = roleRepository.findById("Free");
        if(!optionalRole.isPresent()){
            throw new InternalServerException("role \"Free\" not found");
        }
        user.setRole(optionalRole.get());
        userRepository.save(user);
        logger.info("User added to the db!");
        UserLoginRequest userLoginRequest = new UserLoginRequest();
        userLoginRequest.setEmail(userSignUpRequest.getEmail());
        userLoginRequest.setPassword(userSignUpRequest.getPassword());
        userLoginRequest.setChallengeToken(userSignUpRequest.getChallengeToken());
        try {
            return login(userLoginRequest, true);
        } catch (LoginFailedException e) {
            logger.info("Unexpected behavior occurred");
            e.printStackTrace();
            throw new InternalServerException(e.getMessage());
        }
    }

    //returns a JWT if success
    //this method is also used in last step of signup process. That's why we have a switch.
    //if it's signup process, then don't verify captcha because it'll already be done in the signup process
    public LoginSuccessResult login(UserLoginRequest userLoginRequest, boolean isSignupProcess) throws LoginFailedException, InternalServerException, RecaptchaVerificationException {
        if(!isSignupProcess) {
            logger.info("CHALLENGE TOKEN: " + userLoginRequest.getChallengeToken());
            logger.info("RECAPTCHA SECRET: " + recaptchaSecret);
            boolean success = verifyRecaptchaChallenge(userLoginRequest.getChallengeToken());
            if (!success) {
                throw new RecaptchaVerificationException("reCAPTCHA challenge failed");
            }
        }
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(userLoginRequest.getEmail(), userLoginRequest.getPassword()));
        }catch (BadCredentialsException e){
            e.printStackTrace();
            throw new LoginFailedException("Incorrect username or password");
        }
        UserSecurityDetails userSecurityDetails = (UserSecurityDetails) userSecurityDetailsService.loadUserByUsername(userLoginRequest.getEmail());
        List<GrantedAuthority> grantedAuthorities = userSecurityDetails.getAuthorities();
        UserAuthority userAuthority = (UserAuthority) grantedAuthorities.get(0);
        String email = userSecurityDetails.getUsername();
        Integer userId = userAuthority.getUserId();
        String role = userAuthority.getAuthority();

        //update last seen
        User user = userRepository.findByEmail(userLoginRequest.getEmail()).get();
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);

        //create the token
        String token = jwtService.createToken(userId, email, role);

        return new LoginSuccessResult().setToken(token).setUserDto(UserDto.getUserDtoFromUser(user));
    }

    @PreAuthorize("#userPasswordRequest.id == authentication.principal.grantedAuthorities[0].userId")
    public void updatePassword(UpdateUserPasswordRequest userPasswordRequest) throws ResourceNotFoundException, ValidationException {
        Optional<User> userOptional = userRepository.findById(userPasswordRequest.getId());
        if(!userOptional.isPresent()){
            throw new ResourceNotFoundException("user with ID: "+userPasswordRequest.getId()+" not found");
        }
        User user = userOptional.get();
        if(!passwordEncoder.matches(userPasswordRequest.getCurPassword(), user.getPassword())){
            throw new ValidationException("Current password didn't match");
        }
        String hashedNewPassword = passwordEncoder.encode(userPasswordRequest.getNewPassword());
        user.setPassword(hashedNewPassword);
        userRepository.save(user);
    }

    @PreAuthorize("hasAuthority('Admin') and #userRoleRequest.id != authentication.principal.grantedAuthorities[0].userId")
    public void updateRole(UpdateUserRoleRequest userRoleRequest) throws ResourceNotFoundException {
        Optional<User> userOptional = userRepository.findById(userRoleRequest.getId());
        if(!userOptional.isPresent()){
            throw new ResourceNotFoundException("user with ID: "+userRoleRequest.getId()+" not found");
        }
        User user = userOptional.get();
        Optional<Role> roleOptional = roleRepository.findById(userRoleRequest.getRole());
        if(!roleOptional.isPresent()){
            throw new ResourceNotFoundException("user with role: "+userRoleRequest.getRole()+" not found");
        }
        Role role = roleOptional.get();
        user.setRole(role);
        userRepository.save(user);
    }

    @PreAuthorize("#addUserPhotoRequest.id == authentication.principal.grantedAuthorities[0].userId")
    public void saveUserPhoto(AddUserPhotoRequest addUserPhotoRequest) throws InternalServerException, ResourceNotFoundException {
        Optional<User> userOptional = userRepository.findById(addUserPhotoRequest.getId());
        if(!userOptional.isPresent()){
            throw new ResourceNotFoundException("user with ID: "+addUserPhotoRequest.getId()+" not found");
        }
        User user = userOptional.get();
        try {
            //delete the current profile pic from the filesystem first
            //delete it only if it's not default-avatar.png
            //Remember that from security POV, other users of the same role shouldn't be able to access
            //or delete the photo apart from the photo's owner.
            String currentPhotoName = user.getProfilePicName();
            if(!currentPhotoName.equalsIgnoreCase("default-avatar.png")) {
                boolean result = FileOperation.deleteFile(userDataPaths.getUserPhotoDirPath(), user.getProfilePicName());
                if (result) {
                    logger.info("current profile pic for user: " + user.getEmail() + " with filename: " + user.getProfilePicName() + " deleted.");
                }else {
                    throw new InternalServerException("failed to delete the current profile pic from file system for the user: "+user.getEmail());
                }
            }
            MultipartFile photo = addUserPhotoRequest.getPhoto();
            String photoName = addUserPhotoRequest.getId()+photo.getOriginalFilename();
            //write the new photo to the filesystem
            boolean result = FileOperation.writeFile(userDataPaths.getUserPhotoDirPath(), photoName, photo.getBytes());
            if (!result) {
                //don't think we'll encounter this case but still...
                //over-validation -_-
                throw new InternalServerException("file with the name '" + photoName + "' already exists");
            }
            //update that file name in the database
            user.setProfilePicName(photoName);
            userRepository.save(user);
        } catch (IOException e) {
            e.printStackTrace();
            throw new InternalServerException(e.getMessage());
        }
    }

    @PreAuthorize("#id == authentication.principal.grantedAuthorities[0].userId or hasAuthority('Admin')")
    public Photo getUserPhoto(Integer id) throws ResourceNotFoundException, InternalServerException {
        Optional<User> userOptional = userRepository.findById(id);
        if(!userOptional.isPresent()){
            throw new ResourceNotFoundException("user with ID: "+id+" not found");
        }
        String photoName = userOptional.get().getProfilePicName();
        try {
            byte[] bytes = FileOperation.readFile(userDataPaths.getUserPhotoDirPath(), photoName);
            ImageInfo imageInfo = Imaging.getImageInfo(bytes);
            return new Photo().setPhotoBytes(bytes)
                    .setMimeType(imageInfo.getMimeType());
        } catch (NoSuchFileException e) {
            String message = "No photo with the name '" + photoName + "' found";
            logger.error(message);
            throw new ResourceNotFoundException(message);
        } catch (IOException | ImageReadException e) {
            e.printStackTrace();
            throw new InternalServerException(e.getMessage());
        }
    }

}
