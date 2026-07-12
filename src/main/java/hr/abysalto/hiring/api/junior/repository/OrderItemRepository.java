package hr.abysalto.hiring.api.junior.repository;

import java.util.List;
import hr.abysalto.hiring.api.junior.model.OrderItem;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Repozitorij za stavke. Uz gotove CRUD metode, treba nam i dohvat
 * svih stavki jedne narudzbe - napisan vlastitim SQL-om preko @Query.
 */
@Repository

public interface OrderItemRepository extends CrudRepository<OrderItem, Long> {
    @Query("SELECT * FROM order_item WHERE order_nr = :orderNr")
    List<OrderItem> findByOrderNr(@Param("orderNr") Long orderNr);
}
