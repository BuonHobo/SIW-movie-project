package it.uniroma3.siw.model;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.Set;

@Entity
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"first_name", "last_name", "birthday"})})
public class Artist {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank
    @Column(nullable = false,name = "first_name")
    private String firstName;
    @NotBlank
    @Column(nullable = false,name = "last_name")
    private String lastName;

    @PastOrPresent
    @Column(nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @NotNull
    private Date birthday;

    @PastOrPresent
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date deathDate;

    @ManyToMany(mappedBy = "actors")
    private Set<Movie> actedMovies;

    @OneToMany(mappedBy = "director")
    private Set<Movie> directedMovies;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn
    private Image picture;

    public String getFullName(){
        return getFirstName() +' '+ getLastName();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Artist artist = (Artist) o;

        if (!firstName.equals(artist.firstName)) return false;
        if (!lastName.equals(artist.lastName)) return false;
        return birthday.equals(artist.birthday);
    }

    @Override
    public int hashCode() {
        int result = firstName.hashCode();
        result = 31 * result + lastName.hashCode();
        result = 31 * result + birthday.hashCode();
        return result;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public Date getDeathDate() {
        return deathDate;
    }

    public void setDeathDate(Date deathDate) {
        this.deathDate = deathDate;
    }

    public Set<Movie> getActedMovies() {
        return actedMovies;
    }

    public void setActedMovies(Set<Movie> actedMovies) {
        this.actedMovies = actedMovies;
    }

    public Set<Movie> getDirectedMovies() {
        return directedMovies;
    }

    public void setDirectedMovies(Set<Movie> directedMovies) {
        this.directedMovies = directedMovies;
    }

    public Image getPicture() {
        return picture;
    }

    public void setPicture(Image picture) {
        this.picture = picture;
    }
}
