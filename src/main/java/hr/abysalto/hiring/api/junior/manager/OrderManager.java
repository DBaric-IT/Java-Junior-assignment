package hr.abysalto.hiring.api.junior.manager;

import hr.abysalto.hiring.api.junior.dto.CreateOrderRequest;
import hr.abysalto.hiring.api.junior.model.Order;

/**
 * Ugovor (interface) za rad s narudzbama. Kontroler ovisi o OVOM sucelju,
 * a ne o konkretnoj klasi - labaviji spoj, laksa zamjena/testiranje.
 */

public interface OrderManager {
    // Zadatak 2: dodavanje nove narudzbe. Vraca spremljenu narudzbu (s dodijeljenim brojem).
    Order createOrder(CreateOrderRequest request);
}
