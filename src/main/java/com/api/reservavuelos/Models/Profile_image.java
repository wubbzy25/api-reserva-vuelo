package com.api.reservavuelos.Models;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
@Table(name = "profile_image")
public class Profile_image  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_profile_image;
    @Column()
    private String public_id;
    @Column()
    private String image_url;

    @ToString.Exclude
    @OneToOne(mappedBy = "profile_image", cascade = CascadeType.ALL)
    private Usuarios usuarios;

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Profile_image that = (Profile_image) o;
        return getId_profile_image() != null && Objects.equals(getId_profile_image(), that.getId_profile_image());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}

