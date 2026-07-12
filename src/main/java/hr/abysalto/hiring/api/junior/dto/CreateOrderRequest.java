package hr.abysalto.hiring.api.junior.dto;

import java.util.List;
import lombok.Data;

/**
 * Tijelo (body) POST zahtjeva za novu narudzbu.
 * Namjerno NE trazimo: order_nr (baza dodjeljuje), status (uvijek krece "na cekanju"),
 * vrijeme (uzimamo trenutno) i total_price (racunamo sami iz stavki).
 */
@Data
public class CreateOrderRequest {
    private Long buyerId;                    // koji kupac narucuje (mora postojati u tablici buyer)
    private Long deliveryAddressId;          // adresa dostave (mora postojati u buyer_address)
    private String paymentOption;            // "CASH", "CARD_UPFRONT", "CARD_ON_DELIVERY"
    private String contactNumber;
    private String note;                     // napomena (nije obavezna)
    private String currency;                 // npr. "EUR"
    private List<OrderItemRequest> items;    // popis artikala
}