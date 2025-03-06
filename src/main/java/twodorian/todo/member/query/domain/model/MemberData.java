package twodorian.todo.member.query.domain.model;

import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@NoArgsConstructor
@Document(collection = "members")
public class MemberData {

    @Id
    private ObjectId id;

    private Long memberId;
    private String nickname;
    @Indexed(unique = true)
    private String email;
    private String authority;

    @Builder
    public MemberData(Long memberId, String nickname, String email, String authority) {
        this.memberId = memberId;
        this.nickname = nickname;
        this.email = email;
        this.authority = authority;
    }
}
