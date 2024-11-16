package edu.sabanciuniv.howudoin.users;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "users") //Links the UserModel class to "users" collection in the MongoDB
public class UserModel
{
    @Id //MongoDB will automatically generate a unique ID for each user document
    private String id;

    private String email;
    private String password;
    private String firstName;
    private String lastName;

    private boolean emailVerified;


    /* Setters and Getters */

    public String getId()
    {
        return id;
    }
    public void setId(String id)
    {
        this.id = id;
    }

    public String getEmail()
    {
        return email;
    }
    public void setEmail(String email)
    {
        this.email = email;
    }

    public String getPassword()
    {
        return password;
    }
    public void setPassword(String password)
    {
        this.password = password;
    }

    public String getFirstName()
    {
        return firstName;
    }
    public void setFirstName(String firstName)
    {
        this.firstName = firstName;
    }

    public String getLastName()
    {
        return lastName;
    }
    public void setLastName(String lastName)
    {
        this.lastName = lastName;
    }

    public boolean isEmailVerified()
    {
        return emailVerified;
    }
    public void setEmailVerified(boolean emailVerified)
    {
        this.emailVerified = emailVerified;
    }

}