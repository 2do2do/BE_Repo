package twodorian.todo.member.command.domain.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import twodorian.todo.member.command.domain.model.property.Authority;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "user_tb")
public class Member {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String nickname;
    @Column
    private String email;
    @Column
    private String password;

    @Enumerated(value = EnumType.STRING)
    @ColumnDefault("'USER'")
    private Authority authority;

    @Builder
    public Member(String nickname, String email, String password, Authority authority) {
        this.nickname = nickname;
        this.email = email;
        this.password = password;
        this.authority = authority;
    }
}
