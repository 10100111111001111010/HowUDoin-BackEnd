package edu.sabanciuniv.howudoin.groups;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupMessagesRepository extends MongoRepository<GroupMessagesModel, String>
{
    Page<GroupMessagesModel> findByGroupIdOrderByCreatedAtDesc(String groupId, Pageable pageable);
    long countByGroupId(String groupId);
    void deleteByGroupId(String groupId);
}