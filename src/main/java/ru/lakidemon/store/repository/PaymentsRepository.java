package ru.lakidemon.store.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.lakidemon.store.model.Payment;

import java.util.Optional;

@Repository
public interface PaymentsRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByOrderId(long orderId);

}
