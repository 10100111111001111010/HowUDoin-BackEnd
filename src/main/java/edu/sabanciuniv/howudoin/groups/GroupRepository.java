package edu.sabanciuniv.howudoin.groups;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface GroupRepository extends MongoRepository<GroupModel, String>
{
    List<GroupModel> findByMemberIdsContaining(String userId);

    @Query("{ 'member_ids': ?0 }")
    List<GroupModel> findGroupsByMemberId(String memberId);
}