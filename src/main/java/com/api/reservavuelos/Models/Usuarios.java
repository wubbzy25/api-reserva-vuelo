package com.api.reservavuelos.Models;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@ToString
@Entity
@Table(name = "usuarios")
public class Usuarios  {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    @Column(length = 10)
    private String primer_nombre;
    @Column(length = 10)
    private String segundo_nombre;
    @Column(length = 10)
    private String primer_apellido;
    @Column(length = 10)
    private String segundo_apellido;
    @Column(length = 30)
    private String email;
    @Column (length = 10)
    private String telefono;
    @Column()
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date fecha_nacimiento;
    @Column()
    private String genero;
    private String contraseña;
    @ToString.Exclude
     @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
     @JoinTable(
             name = "usuarios_roles",
             joinColumns = @JoinColumn(name = "id_usuario"),
             inverseJoinColumns = @JoinColumn(name = "id_rol")
     )
     private List<Roles> roles;
    @ToString.Exclude
    @OneToOne(cascade = CascadeType.ALL, optional = false)
    @JoinColumn(name = "id_profile_image", referencedColumnName = "id_profile_image", nullable = false)
    private Profile_image profile_image;
    @ToString.Exclude
    @OneToOne(mappedBy = "usuarios", cascade = CascadeType.ALL)
    private TwoFactorAuth twoFactorAuth;
    @ToString.Exclude
    @OneToOne(mappedBy = "usuarios", cascade = CascadeType.ALL)
    private Reservas reservas;
    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Usuarios usuarios = (Usuarios) o;
        return getId() != null && Objects.equals(getId(), usuarios.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }


}
