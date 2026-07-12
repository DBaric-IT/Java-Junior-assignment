package hr.abysalto.hiring.api.junior.dto;

import java.math.BigDecimal;
import lombok.Data;

/**
 * DTO = Data Transfer Object: oblik podataka koji klijent SALJE,
 * odvojen od modela iz baze. Jedna stavka koju korisnik dodaje u narudzbu.
 */

@Data
public class OrderItemRequest {
    private String name;         // naziv artikla
    private Short quantity;      // kolicina
    private BigDecimal price;    // cijena po komadu
}
