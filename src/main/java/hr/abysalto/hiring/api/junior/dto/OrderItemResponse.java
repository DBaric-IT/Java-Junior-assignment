package hr.abysalto.hiring.api.junior.dto;

import java.math.BigDecimal;

import lombok.Data;

/**
 * Kako jedna stavka izgleda u odgovoru (pregled narudzbe).
 */
@Data
public class OrderItemResponse {
    private Short itemNr;
    private String name;
    private Short quantity;
    private BigDecimal price;
}