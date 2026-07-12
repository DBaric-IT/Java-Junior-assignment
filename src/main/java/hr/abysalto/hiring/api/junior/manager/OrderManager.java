package hr.abysalto.hiring.api.junior.manager;

import java.util.List;

import hr.abysalto.hiring.api.junior.dto.CreateOrderRequest;
import hr.abysalto.hiring.api.junior.dto.OrderResponse;
import hr.abysalto.hiring.api.junior.dto.OrderTotalResponse;
import hr.abysalto.hiring.api.junior.model.Order;

public interface OrderManager {

    // Zadatak 2
    Order createOrder(CreateOrderRequest request);

    // Zadatak 3 + 6: pregled svih narudzbi; sort = "asc"/"desc" po iznosu (ili null = bez sortiranja)
    List<OrderResponse> getAllOrders(String sort);

    // Zadatak 3: pregled jedne narudzbe po broju; null ako ne postoji
    OrderResponse getOrderByNr(Long orderNr);

    // Zadatak 4: promjena statusa; null ako ne postoji, baca gresku ako je status neispravan
    OrderResponse updateStatus(Long orderNr, String status);

    // Zadatak 5: izracun ukupnog iznosa racuna (iz stavki); null ako narudzba ne postoji
    OrderTotalResponse getOrderTotal(Long orderNr);

}