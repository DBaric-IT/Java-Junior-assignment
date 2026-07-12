package hr.abysalto.hiring.api.junior.repository;

import hr.abysalto.hiring.api.junior.model.Order;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Repozitorij za narudzbe. CrudRepository nam besplatno daje
 * save(), findById(), findAll(), deleteById()... i sam generira SQL.
 * (isti pristup kao BuyerRepository)
 */
@Repository
public interface OrderRepository extends CrudRepository<Order, Long> {
}
