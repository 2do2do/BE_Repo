package twodorian.todo.member.query.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import twodorian.todo.member.query.domain.model.MemberData;

@Repository
public interface QueryMemberRepository extends MongoRepository<MemberData, ObjectId> {
}
