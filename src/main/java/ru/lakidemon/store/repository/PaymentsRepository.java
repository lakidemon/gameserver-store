package ru.lakidemon.store.repository;

import org.springframework.data.repository.CrudRepository;
import ru.lakidemon.store.model.Payment;

public interface PaymentsRepository extends CrudRepository<Payment, Long> {
}
