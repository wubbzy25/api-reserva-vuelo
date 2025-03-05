package com.api.reservavuelos.Models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "two_factor_auth")
@Getter
@Setter
public class TwoFactorAuth {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idTwoFactorAuth;
    @Column
    private String secretKey;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id_usuario")
    private Usuarios usuarios;


}
