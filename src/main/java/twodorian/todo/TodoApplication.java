package twodorian.todo;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.crypto.password.PasswordEncoder;
import twodorian.todo.member.command.domain.model.Member;
import twodorian.todo.member.command.domain.repository.CommandMemberRepository;

import java.util.Arrays;

@SpringBootApplication
@EnableFeignClients
@EnableJpaAuditing
public class TodoApplication {

    public static void main(String[] args) {
        SpringApplication.run(TodoApplication.class, args);
    }

    @Profile("local")
    @Bean
    CommandLineRunner localServerStart(CommandMemberRepository commandMemberRepository,
                                       PasswordEncoder passwordEncoder) {
        return args -> {
            commandMemberRepository.saveAll(Arrays.asList(
                    newMember("test01", "test01@test.com", "test1234", passwordEncoder)
            ));
        };
    }

    private Member newMember(String nickname, String email, String password, PasswordEncoder passwordEncoder) {

        String encodedPassword = passwordEncoder.encode(password);

        return Member.builder()
                .nickname(nickname)
                .email(email)
                .password(encodedPassword)
                .build();
    }
}
