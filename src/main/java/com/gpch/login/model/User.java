package com.gpch.login.model;

import org.hibernate.validator.constraints.Length;
import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import java.util.Set;

@Entity
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "user_id")
    private int id;
    @Column(name = "email")
    @Email(message = "*Please provide a valid Email")
    @NotEmpty(message = "*Please provide an email")
    private String email;
    @Column(name = "password")
    @Length(min = 5, message = "*Your password must have at least 5 characters")
    @NotEmpty(message = "*Please provide your password")
    private String password;
    @Column(name = "name")
    @NotEmpty(message = "*Please provide your name")
    private String name;
    @Column(name = "last_name")
    @NotEmpty(message = "*Please provide your last name")
    private String lastName;
    @Column(name = "active")
    private int active;
    @Column(name = "isUsing2FA")
    private int isUsing2FA;
    @Column(name = "secret")
    private String secret;
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles;


    @java.lang.SuppressWarnings("all")
    public static class UserBuilder {
        @java.lang.SuppressWarnings("all")
        private int id;
        @java.lang.SuppressWarnings("all")
        private String email;
        @java.lang.SuppressWarnings("all")
        private String password;
        @java.lang.SuppressWarnings("all")
        private String name;
        @java.lang.SuppressWarnings("all")
        private String lastName;
        @java.lang.SuppressWarnings("all")
        private int active;
        @java.lang.SuppressWarnings("all")
        private int isUsing2FA;
        @java.lang.SuppressWarnings("all")
        private String secret;
        @java.lang.SuppressWarnings("all")
        private Set<Role> roles;

        @java.lang.SuppressWarnings("all")
        UserBuilder() {
        }

        @java.lang.SuppressWarnings("all")
        public UserBuilder id(final int id) {
            this.id = id;
            return this;
        }

        @java.lang.SuppressWarnings("all")
        public UserBuilder email(final String email) {
            this.email = email;
            return this;
        }

        @java.lang.SuppressWarnings("all")
        public UserBuilder password(final String password) {
            this.password = password;
            return this;
        }

        @java.lang.SuppressWarnings("all")
        public UserBuilder name(final String name) {
            this.name = name;
            return this;
        }

        @java.lang.SuppressWarnings("all")
        public UserBuilder lastName(final String lastName) {
            this.lastName = lastName;
            return this;
        }

        @java.lang.SuppressWarnings("all")
        public UserBuilder active(final int active) {
            this.active = active;
            return this;
        }

        @java.lang.SuppressWarnings("all")
        public UserBuilder isUsing2FA(final int isUsing2FA) {
            this.isUsing2FA = isUsing2FA;
            return this;
        }

        @java.lang.SuppressWarnings("all")
        public UserBuilder secret(final String secret) {
            this.secret = secret;
            return this;
        }

        @java.lang.SuppressWarnings("all")
        public UserBuilder roles(final Set<Role> roles) {
            this.roles = roles;
            return this;
        }

        @java.lang.SuppressWarnings("all")
        public User build() {
            return new User(id, email, password, name, lastName, active, isUsing2FA, secret, roles);
        }

        @java.lang.Override
        @java.lang.SuppressWarnings("all")
        public java.lang.String toString() {
            return "User.UserBuilder(id=" + this.id + ", email=" + this.email + ", password=" + this.password + ", name=" + this.name + ", lastName=" + this.lastName + ", active=" + this.active + ", isUsing2FA=" + this.isUsing2FA + ", secret=" + this.secret + ", roles=" + this.roles + ")";
        }
    }

    @java.lang.SuppressWarnings("all")
    public static UserBuilder builder() {
        return new UserBuilder();
    }

    @java.lang.SuppressWarnings("all")
    public int getId() {
        return this.id;
    }

    @java.lang.SuppressWarnings("all")
    public String getEmail() {
        return this.email;
    }

    @java.lang.SuppressWarnings("all")
    public String getPassword() {
        return this.password;
    }

    @java.lang.SuppressWarnings("all")
    public String getName() {
        return this.name;
    }

    @java.lang.SuppressWarnings("all")
    public String getLastName() {
        return this.lastName;
    }

    @java.lang.SuppressWarnings("all")
    public int getActive() {
        return this.active;
    }

    @java.lang.SuppressWarnings("all")
    public int getIsUsing2FA() {
        return this.isUsing2FA;
    }

    @java.lang.SuppressWarnings("all")
    public String getSecret() {
        return this.secret;
    }

    @java.lang.SuppressWarnings("all")
    public Set<Role> getRoles() {
        return this.roles;
    }

    @java.lang.SuppressWarnings("all")
    public void setId(final int id) {
        this.id = id;
    }

    @java.lang.SuppressWarnings("all")
    public void setEmail(final String email) {
        this.email = email;
    }

    @java.lang.SuppressWarnings("all")
    public void setPassword(final String password) {
        this.password = password;
    }

    @java.lang.SuppressWarnings("all")
    public void setName(final String name) {
        this.name = name;
    }

    @java.lang.SuppressWarnings("all")
    public void setLastName(final String lastName) {
        this.lastName = lastName;
    }

    @java.lang.SuppressWarnings("all")
    public void setActive(final int active) {
        this.active = active;
    }

    @java.lang.SuppressWarnings("all")
    public void setIsUsing2FA(final int isUsing2FA) {
        this.isUsing2FA = isUsing2FA;
    }

    @java.lang.SuppressWarnings("all")
    public void setSecret(final String secret) {
        this.secret = secret;
    }

    @java.lang.SuppressWarnings("all")
    public void setRoles(final Set<Role> roles) {
        this.roles = roles;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public boolean equals(final java.lang.Object o) {
        if (o == this) return true;
        if (!(o instanceof User)) return false;
        final User other = (User) o;
        if (!other.canEqual((java.lang.Object) this)) return false;
        if (this.getId() != other.getId()) return false;
        final java.lang.Object this$email = this.getEmail();
        final java.lang.Object other$email = other.getEmail();
        if (this$email == null ? other$email != null : !this$email.equals(other$email)) return false;
        final java.lang.Object this$password = this.getPassword();
        final java.lang.Object other$password = other.getPassword();
        if (this$password == null ? other$password != null : !this$password.equals(other$password)) return false;
        final java.lang.Object this$name = this.getName();
        final java.lang.Object other$name = other.getName();
        if (this$name == null ? other$name != null : !this$name.equals(other$name)) return false;
        final java.lang.Object this$lastName = this.getLastName();
        final java.lang.Object other$lastName = other.getLastName();
        if (this$lastName == null ? other$lastName != null : !this$lastName.equals(other$lastName)) return false;
        if (this.getActive() != other.getActive()) return false;
        if (this.getIsUsing2FA() != other.getIsUsing2FA()) return false;
        final java.lang.Object this$secret = this.getSecret();
        final java.lang.Object other$secret = other.getSecret();
        if (this$secret == null ? other$secret != null : !this$secret.equals(other$secret)) return false;
        final java.lang.Object this$roles = this.getRoles();
        final java.lang.Object other$roles = other.getRoles();
        if (this$roles == null ? other$roles != null : !this$roles.equals(other$roles)) return false;
        return true;
    }

    @java.lang.SuppressWarnings("all")
    protected boolean canEqual(final java.lang.Object other) {
        return other instanceof User;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        result = result * PRIME + this.getId();
        final java.lang.Object $email = this.getEmail();
        result = result * PRIME + ($email == null ? 43 : $email.hashCode());
        final java.lang.Object $password = this.getPassword();
        result = result * PRIME + ($password == null ? 43 : $password.hashCode());
        final java.lang.Object $name = this.getName();
        result = result * PRIME + ($name == null ? 43 : $name.hashCode());
        final java.lang.Object $lastName = this.getLastName();
        result = result * PRIME + ($lastName == null ? 43 : $lastName.hashCode());
        result = result * PRIME + this.getActive();
        result = result * PRIME + this.getIsUsing2FA();
        final java.lang.Object $secret = this.getSecret();
        result = result * PRIME + ($secret == null ? 43 : $secret.hashCode());
        final java.lang.Object $roles = this.getRoles();
        result = result * PRIME + ($roles == null ? 43 : $roles.hashCode());
        return result;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public java.lang.String toString() {
        return "User(id=" + this.getId() + ", email=" + this.getEmail() + ", password=" + this.getPassword() + ", name=" + this.getName() + ", lastName=" + this.getLastName() + ", active=" + this.getActive() + ", isUsing2FA=" + this.getIsUsing2FA() + ", secret=" + this.getSecret() + ", roles=" + this.getRoles() + ")";
    }

    @java.lang.SuppressWarnings("all")
    public User(final int id, final String email, final String password, final String name, final String lastName, final int active, final int isUsing2FA, final String secret, final Set<Role> roles) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.name = name;
        this.lastName = lastName;
        this.active = active;
        this.isUsing2FA = isUsing2FA;
        this.secret = secret;
        this.roles = roles;
    }

    @java.lang.SuppressWarnings("all")
    public User() {
    }
}
