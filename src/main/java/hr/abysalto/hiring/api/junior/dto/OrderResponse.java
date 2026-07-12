package hr.abysalto.hiring.api.junior.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

/**
 * Lijepi oblik narudzbe za prikaz. Za razliku od Order modela, ovdje saljemo
 * pravo IME KUPCA i cijelu ADRESU (dohvaceni iz drugih tablica), i nema
 * tehnickih duplikata (stringOrderStatus i sl.).
 */
@Data
public class OrderResponse {
    private Long orderNr;
    private String buyerName;              // ime kupca
    private String status;                 // status kao tekst
    private LocalDateTime orderTime;
    private String paymentOption;
    private String deliveryAddress;        // adresa dostave (grad, ulica i broj)
    private String contactNumber;
    private String note;
    private String currency;
    private BigDecimal totalPrice;
    private List<OrderItemResponse> items; // popis artikala
}