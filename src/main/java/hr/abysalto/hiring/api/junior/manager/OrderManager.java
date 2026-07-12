package hr.abysalto.hiring.api.junior.manager;

import java.util.List;

import hr.abysalto.hiring.api.junior.dto.CreateOrderRequest;
import hr.abysalto.hiring.api.junior.dto.OrderResponse;
import hr.abysalto.hiring.api.junior.model.Order;

public interface OrderManager {

    // Zadatak 2
    Order createOrder(CreateOrderRequest request);

    // Zadatak 3: pregled svih narudzbi (lijepi oblik s imenom kupca, adresom, stavkama)
    List<OrderResponse> getAllOrders();

    // Zadatak 3: pregled jedne narudzbe po broju; null ako ne postoji
    OrderResponse getOrderByNr(Long orderNr);
}