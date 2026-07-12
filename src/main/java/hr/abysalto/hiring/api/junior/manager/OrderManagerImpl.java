package hr.abysalto.hiring.api.junior.manager;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import hr.abysalto.hiring.api.junior.dto.CreateOrderRequest;
import hr.abysalto.hiring.api.junior.dto.OrderItemRequest;
import hr.abysalto.hiring.api.junior.model.Order;
import hr.abysalto.hiring.api.junior.model.OrderItem;
import hr.abysalto.hiring.api.junior.model.OrderStatus;
import hr.abysalto.hiring.api.junior.model.PaymentOption;
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