package edu.sabanciuniv.howudoin.users;

import lombok.Data;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "users") // Links the UserModel class to "users" collection in MongoDB
@Data // Generates getters, setters and toString
public class UserModel {
    @Id // MongoDB will automatically generate a unique ID for each user document
    private String id;

    private String email;
    private String emailVerificationToken;
    private boolean emailVerified;

    @ToString.Exclude // Prevents password from being included in toString
    private String password;

    private String firstName;
    private String lastName;

    @Field("friend_ids")  // Single friend list field with consistent naming
    private List<String> friendIds = new ArrayList<>();
}