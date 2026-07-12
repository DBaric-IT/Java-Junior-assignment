package hr.abysalto.hiring.api.junior.manager;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import hr.abysalto.hiring.api.junior.dto.CreateOrderRequest;
import hr.abysalto.hiring.api.junior.dto.OrderItemRequest;
import hr.abysalto.hiring.api.junior.dto.OrderItemResponse;
import hr.abysalto.hiring.api.junior.dto.OrderResponse;
import hr.abysalto.hiring.api.junior.model.*;
import hr.abysalto.hiring.api.junior.repository.BuyerAddressRepository;
import hr.abysalto.hiring.api.junior.repository.BuyerRepository;
import hr.abysalto.hiring.api.junior.repository.OrderItemRepository;
import hr.abysalto.hiring.api.junior.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OrderManagerImpl implements OrderManager {

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderItemRepository orderItemRepository;
    // Trebaju nam da kod pregleda dohvatimo ime kupca i adresu dostave.
    @Autowired
    private BuyerRepository buyerRepository;
    @Autowired
    private BuyerAddressRepository buyerAddressRepository;

    @Override
    public Order createOrder(CreateOrderRequest request) {
        // 1) Nacin placanja stigne kao tekst -> pretvorimo ga u enum.
        //    Ako je nepoznat (npr. "BITCOIN"), bacimo gresku da ne spremimo smece.
        PaymentOption paymentOption = PaymentOption.fromString(request.getPaymentOption());
        if (paymentOption == null) {
            throw new IllegalArgumentException("Nepoznat nacin placanja: " + request.getPaymentOption());
        }

        // 2) Sastavimo narudzbu. Neke stvari postavlja server, ne klijent:
        Order order = new Order();
        order.setBuyerId(request.getBuyerId());
        order.setDeliveryAddressId(request.getDeliveryAddressId());
        order.setPaymentOption(paymentOption);
        order.setContactNumber(request.getContactNumber());
        order.setNote(request.getNote());
        order.setCurrency(request.getCurrency());
        order.setOrderStatus(OrderStatus.WAITING_FOR_CONFIRMATION); // nova narudzba uvijek "na cekanju"
        order.setOrderTime(LocalDateTime.now());                    // vrijeme = sada
        order.setTotalPrice(calculateTotal(request.getItems()));    // iznos racunamo mi

        // 3) Spremimo narudzbu. save() vrati narudzbu s dodijeljenim brojem (order_nr).
        Order savedOrder = this.orderRepository.save(order);

        // 4) Sad kad znamo broj narudzbe, spremimo svaku stavku i povezemo je s njom.
        short itemNr = 1;
        for (OrderItemRequest itemRequest : request.getItems()) {
            OrderItem item = new OrderItem();
            item.setOrderNr(savedOrder.getOrderNr());
            item.setItemNr(itemNr);
            item.setName(itemRequest.getName());
            item.setQuantity(itemRequest.getQuantity());
            item.setPrice(itemRequest.getPrice());
            this.orderItemRepository.save(item);
            itemNr++;
        }

        return savedOrder;
    }

    @Override
    public List<OrderResponse> getAllOrders() {
        List<OrderResponse> result = new ArrayList<>();
        // findAll() vrati sve narudzbe; svaku pretvorimo u lijepi oblik.
        for (Order order : this.orderRepository.findAll()) {
            result.add(mapToResponse(order));
        }
        return result;
    }

    @Override
    public OrderResponse getOrderByNr(Long orderNr) {
        // findById vrati Optional; ako narudzba ne postoji -> null (kontroler -> 404)
        Order order = this.orderRepository.findById(orderNr).orElse(null);
        if (order == null) {
            return null;
        }
        return mapToResponse(order);
    }

    @Override
    public OrderResponse updateStatus(Long orderNr, String status) {
        // 1) nadji narudzbu; ako ne postoji -> null (kontroler -> 404)
        Order order = this.orderRepository.findById(orderNr).orElse(null);
        if (order == null) {
            return null;
        }
        // 2) tekst -> enum; ako je nepoznat -> greska (kontroler -> 400)
        OrderStatus newStatus = OrderStatus.fromString(status);
        if (newStatus == null) {
            throw new IllegalArgumentException("Nepoznat status: " + status
                    + " (dozvoljeno: WAITING_FOR_CONFIRMATION, PREPARING, DONE)");
        }
        // 3) postavi novi status i spremi (save() nad postojecim orderNr radi UPDATE)
        order.setOrderStatus(newStatus);
        this.orderRepository.save(order);
        return mapToResponse(order);
    }

    /**
     * Pretvara Order (iz baze) u OrderResponse (lijepi oblik).
     * Spaja podatke iz vise tablica: ime kupca (buyer), adresu (buyer_address)
     * i stavke (order_item).
     */
    private OrderResponse mapToResponse(Order order) {
        OrderResponse response = new OrderResponse();
        response.setOrderNr(order.getOrderNr());

        // ime kupca iz tablice buyer
        Buyer buyer = this.buyerRepository.findById(order.getBuyerId()).orElse(null);
        response.setBuyerName(buyer != null ? buyer.getFirstName() + " " + buyer.getLastName() : "(nepoznat kupac)");

        // adresa iz tablice buyer_address
        BuyerAddress address = this.buyerAddressRepository.findById(order.getDeliveryAddressId()).orElse(null);
        response.setDeliveryAddress(address != null
                ? address.getCity() + ", " + address.getStreet() + " " + address.getHomeNumber()
                : "(nepoznata adresa)");

        response.setStatus(order.getOrderStatus() != null ? order.getOrderStatus().toString() : null);
        response.setPaymentOption(order.getPaymentOption() != null ? order.getPaymentOption().toString() : null);
        response.setOrderTime(order.getOrderTime());
        response.setContactNumber(order.getContactNumber());
        response.setNote(order.getNote());
        response.setCurrency(order.getCurrency());
        response.setTotalPrice(order.getTotalPrice());

        // stavke te narudzbe
        List<OrderItemResponse> itemResponses = new ArrayList<>();
        for (OrderItem item : this.orderItemRepository.findByOrderNr(order.getOrderNr())) {
            OrderItemResponse itemResponse = new OrderItemResponse();
            itemResponse.setItemNr(item.getItemNr());
            itemResponse.setName(item.getName());
            itemResponse.setQuantity(item.getQuantity());
            itemResponse.setPrice(item.getPrice());
            itemResponses.add(itemResponse);
        }
        response.setItems(itemResponses);

        return response;
    }

    /**
     * Ukupni iznos = zbroj (cijena * kolicina) po svim stavkama.
     * BigDecimal (a ne double) jer je za novac tocan i ne gubi na zaokruzivanju.
     */
    private BigDecimal calculateTotal(List<OrderItemRequest> items) {
        BigDecimal total = BigDecimal.ZERO;
        if (items == null) {
            return total;
        }
        for (OrderItemRequest item : items) {
            BigDecimal linePrice = item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            total = total.add(linePrice);
        }
        return total;
    }
}