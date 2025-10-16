package com.smarttodo.smarttodo.Service;

import com.smarttodo.smarttodo.DTO.UserDTO;
import com.smarttodo.smarttodo.Entity.User;
import com.smarttodo.smarttodo.Exception.UserNotFoundException;
import com.smarttodo.smarttodo.Mapper.MapStructUserMapper;
import com.smarttodo.smarttodo.Repository.UserRepository;
import com.smarttodo.smarttodo.Request.AuthenticationRequest;
import com.smarttodo.smarttodo.Response.AuthResponse;
import com.smarttodo.smarttodo.Utility.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;

    private final UserRepository userRepository;

    private final JwtUtil jwtUtils;

    private final MapStructUserMapper userMapper = Mappers.getMapper(MapStructUserMapper.class);


    /*
     * En metod för inloggning av användare.
     * @param authenticationRequest innehåller användarnamn och lösenord.
     * @return AuthResponse som innehåller JWT-token och användarens data som en DTO.
     */
    public AuthResponse userLogin(AuthenticationRequest authenticationRequest) {
        // Autentisera användaren med hjälp av AuthenticationManager
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authenticationRequest.getEmail(),
                        authenticationRequest.getPassword()
                )
        );
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        String token = jwtUtils.generateToken(userDetails.getUsername(), user.getUserId());
        UserDTO userDTO = userMapper.userToUserDTO(user);

        return new AuthResponse(token, userDTO);
    }
}
