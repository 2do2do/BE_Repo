package twodorian.todo.member.command.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import twodorian.todo.member.command.domain.model.Member;

import java.util.Optional;

@Repository
public interface CommandMemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByEmail(String email);
}
