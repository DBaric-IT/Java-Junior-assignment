package hr.abysalto.hiring.api.junior.dto;

import java.math.BigDecimal;

import lombok.Data;

/**
 * Odgovor s ukupnim iznosom racuna. Npr: { "orderNr": 1, "totalPrice": 19.50, "currency": "EUR" }
 */
@Data
public class OrderTotalResponse {
    private Long orderNr;
    private BigDecimal totalPrice;
    private String currency;
}