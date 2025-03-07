package twodorian.todo._core.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import twodorian.todo._core.error.ApplicationException;
import twodorian.todo._core.error.ErrorCode;
import twodorian.todo.member.command.domain.model.Member;
import twodorian.todo.member.command.domain.repository.CommandMemberRepository;

@Service
@RequiredArgsConstructor
public class CustomUserDetail implements UserDetailsService {

    private final CommandMemberRepository commandMemberRepository;

    @Override
    public UserDetails loadUserByUsername(String email) {
        return commandMemberRepository.findByEmail(email)
                .map(this::createUserDetails)
                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND));
    }

    public UserDetails createUserDetails(Member member) {
        GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(member.getAuthority().toString());

        return User.builder()
                .username(member.getId().toString())
                .password(member.getPassword())
                .authorities(grantedAuthority)
                .build();
    }
}