package ru.lakidemon.store.service.standard;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.lakidemon.store.model.Item;
import ru.lakidemon.store.repository.DispatchedOrdersRepository;
import ru.lakidemon.store.repository.ItemsRepository;
import ru.lakidemon.store.service.ItemService;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemsRepository itemsRepository;
    private final DispatchedOrdersRepository dispatchedRepository;

    @Override
    public Optional<Item> getItem(String name) {
        return itemsRepository.findByName(name);
    }

    @Override
    public List<Item> getAllItems() {
        return itemsRepository.findAll();
    }

    @Override
    public void saveItem(Item item) {
        itemsRepository.save(item);
    }

    @Override
    public boolean canBuy(String customer, Item what) {
        // some checks on game server here(eg. false if player already has this item)
        if (dispatchedRepository.findByItemNameAndPlayer(what.getName(), customer).isPresent()) {
            return false; // demo code
        }
        return true;
    }
}
