package entities;
// Generated Feb 20, 2017 2:55:43 AM by Hibernate Tools 4.3.1


import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * Tickets generated by hbm2java
 */
@Entity
@Table(name="tickets"
    ,catalog="pia"
)
public class Tickets  implements java.io.Serializable {


     private Integer idtic;
     private Festivals festivals;
     private Users users;
     private int isBought;
     private int price;
     private Date date;
     private Date reserved;
     private String festivalName;

    public Tickets() {
    }

	
    public Tickets(Festivals festivals, Users users, int isBought, int price, Date reserved, String festivalName) {
        this.festivals = festivals;
        this.users = users;
        this.isBought = isBought;
        this.price = price;
        this.reserved = reserved;
        this.festivalName = festivalName;
    }
    public Tickets(Festivals festivals, Users users, int isBought, int price, Date date, Date reserved, String festivalName) {
       this.festivals = festivals;
       this.users = users;
       this.isBought = isBought;
       this.price = price;
       this.date = date;
       this.reserved = reserved;
       this.festivalName = festivalName;
    }
   
     @Id @GeneratedValue(strategy=IDENTITY)

    
    @Column(name="IDTic", unique=true, nullable=false)
    public Integer getIdtic() {
        return this.idtic;
    }
    
    public void setIdtic(Integer idtic) {
        this.idtic = idtic;
    }

@ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="IDFest", nullable=false)
    public Festivals getFestivals() {
        return this.festivals;
    }
    
    public void setFestivals(Festivals festivals) {
        this.festivals = festivals;
    }

@ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="IDUser", nullable=false)
    public Users getUsers() {
        return this.users;
    }
    
    public void setUsers(Users users) {
        this.users = users;
    }

    
    @Column(name="isBought", nullable=false)
    public int getIsBought() {
        return this.isBought;
    }
    
    public void setIsBought(int isBought) {
        this.isBought = isBought;
    }

    
    @Column(name="price", nullable=false)
    public int getPrice() {
        return this.price;
    }
    
    public void setPrice(int price) {
        this.price = price;
    }

    @Temporal(TemporalType.DATE)
    @Column(name="date", length=10)
    public Date getDate() {
        return this.date;
    }
    
    public void setDate(Date date) {
        this.date = date;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="reserved", nullable=false, length=19)
    public Date getReserved() {
        return this.reserved;
    }
    
    public void setReserved(Date reserved) {
        this.reserved = reserved;
    }

    
    @Column(name="festivalName", nullable=false, length=45)
    public String getFestivalName() {
        return this.festivalName;
    }
    
    public void setFestivalName(String festivalName) {
        this.festivalName = festivalName;
    }




}


