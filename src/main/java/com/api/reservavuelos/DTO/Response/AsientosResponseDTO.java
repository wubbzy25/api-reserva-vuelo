package com.api.reservavuelos.DTO.Response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AsientosResponseDTO {
 int numeroAsiento;
 String claseAsiento;
 String estadoAsiento;
}
