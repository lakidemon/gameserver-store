package ru.lakidemon.store.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.lakidemon.store.model.Payment;

import java.util.Optional;

@Repository
public interface PaymentsRepository extends CrudRepository<Payment, Long> {

    Optional<Payment> findByOrderId(long orderId);

}
