package entities;
// Generated Feb 20, 2017 2:55:43 AM by Hibernate Tools 4.3.1


import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * Users generated by hbm2java
 */
@Entity
@Table(name="users"
    ,catalog="pia"
    , uniqueConstraints = @UniqueConstraint(columnNames="username") 
)
public class Users  implements java.io.Serializable {


     private Integer iduser;
     private String name;
     private String surname;
     private String username;
     private String password;
     private String phone;
     private String email;
     private int isAdmin;
     private int isActive;
     private int reserved;
     private Set<Festivals> festivalses = new HashSet<Festivals>(0);
     private Set<Messages> messageses = new HashSet<Messages>(0);
     private Set<Files> fileses = new HashSet<Files>(0);
     private Set<Comments> commentses = new HashSet<Comments>(0);
     private Set<Tickets> ticketses = new HashSet<Tickets>(0);

    public Users() {
    }

	
    public Users(String name, String surname, String username, String password, String phone, String email, int isAdmin, int isActive, int reserved) {
        this.name = name;
        this.surname = surname;
        this.username = username;
        this.password = password;
        this.phone = phone;
        this.email = email;
        this.isAdmin = isAdmin;
        this.isActive = isActive;
        this.reserved = reserved;
    }
    public Users(String name, String surname, String username, String password, String phone, String email, int isAdmin, int isActive, int reserved, Set<Festivals> festivalses, Set<Messages> messageses, Set<Files> fileses, Set<Comments> commentses, Set<Tickets> ticketses) {
       this.name = name;
       this.surname = surname;
       this.username = username;
       this.password = password;
       this.phone = phone;
       this.email = email;
       this.isAdmin = isAdmin;
       this.isActive = isActive;
       this.reserved = reserved;
       this.festivalses = festivalses;
       this.messageses = messageses;
       this.fileses = fileses;
       this.commentses = commentses;
       this.ticketses = ticketses;
    }
   
     @Id @GeneratedValue(strategy=IDENTITY)

    
    @Column(name="IDUser", unique=true, nullable=false)
    public Integer getIduser() {
        return this.iduser;
    }
    
    public void setIduser(Integer iduser) {
        this.iduser = iduser;
    }

    
    @Column(name="name", nullable=false, length=45)
    public String getName() {
        return this.name;
    }
    
    public void setName(String name) {
        this.name = name;
    }

    
    @Column(name="surname", nullable=false, length=45)
    public String getSurname() {
        return this.surname;
    }
    
    public void setSurname(String surname) {
        this.surname = surname;
    }

    
    @Column(name="username", unique=true, nullable=false, length=45)
    public String getUsername() {
        return this.username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }

    
    @Column(name="password", nullable=false, length=45)
    public String getPassword() {
        return this.password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }

    
    @Column(name="phone", nullable=false, length=45)
    public String getPhone() {
        return this.phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }

    
    @Column(name="email", nullable=false, length=45)
    public String getEmail() {
        return this.email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }

    
    @Column(name="isAdmin", nullable=false)
    public int getIsAdmin() {
        return this.isAdmin;
    }
    
    public void setIsAdmin(int isAdmin) {
        this.isAdmin = isAdmin;
    }

    
    @Column(name="isActive", nullable=false)
    public int getIsActive() {
        return this.isActive;
    }
    
    public void setIsActive(int isActive) {
        this.isActive = isActive;
    }

    
    @Column(name="reserved", nullable=false)
    public int getReserved() {
        return this.reserved;
    }
    
    public void setReserved(int reserved) {
        this.reserved = reserved;
    }

@ManyToMany(fetch=FetchType.EAGER)
    @JoinTable(name="blocked", catalog="pia", joinColumns = { 
        @JoinColumn(name="IDUser", nullable=false, updatable=false) }, inverseJoinColumns = { 
        @JoinColumn(name="IDFest", nullable=false, updatable=false) })
    public Set<Festivals> getFestivalses() {
        return this.festivalses;
    }
    
    public void setFestivalses(Set<Festivals> festivalses) {
        this.festivalses = festivalses;
    }

@OneToMany(fetch=FetchType.EAGER, mappedBy="users")
    public Set<Messages> getMessageses() {
        return this.messageses;
    }
    
    public void setMessageses(Set<Messages> messageses) {
        this.messageses = messageses;
    }

@OneToMany(fetch=FetchType.EAGER, mappedBy="users")
    public Set<Files> getFileses() {
        return this.fileses;
    }
    
    public void setFileses(Set<Files> fileses) {
        this.fileses = fileses;
    }

@OneToMany(fetch=FetchType.EAGER, mappedBy="users")
    public Set<Comments> getCommentses() {
        return this.commentses;
    }
    
    public void setCommentses(Set<Comments> commentses) {
        this.commentses = commentses;
    }

@OneToMany(fetch=FetchType.EAGER, mappedBy="users")
    public Set<Tickets> getTicketses() {
        return this.ticketses;
    }
    
    public void setTicketses(Set<Tickets> ticketses) {
        this.ticketses = ticketses;
    }




}

