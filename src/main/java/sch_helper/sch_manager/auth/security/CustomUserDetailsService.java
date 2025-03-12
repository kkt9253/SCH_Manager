package sch_helper.sch_manager.auth.security;

import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import sch_helper.sch_manager.domain.user.entity.User;
import sch_helper.sch_manager.domain.user.repository.UserRepository;

import java.util.Optional;

@Service
@AllArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        // 예외처리 추가
        Optional<User> userData = userRepository.findByUsername(username);

        if (userData.isEmpty()) {
            return new CustomUserDetails(userData.get());
        }
        return null;
    }
}
