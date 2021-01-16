package io.github.nandandesai.insecure.security;

import io.github.nandandesai.insecure.models.User;
import io.github.nandandesai.insecure.repositories.UserRepository;
import io.github.nandandesai.insecure.security.models.UserSecurityDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserSecurityDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userOptional = userRepository.findByEmail(username);
        if(!userOptional.isPresent()){
            throw new UsernameNotFoundException("'"+username+"' not found");
        }
        return new UserSecurityDetails(userOptional.get());
    }
}
