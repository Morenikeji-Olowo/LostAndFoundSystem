package com.example.lostfoundMS.services;

import com.example.lostfoundMS.entities.Item;
import com.example.lostfoundMS.entities.ItemType;
import com.example.lostfoundMS.entities.User;
import com.example.lostfoundMS.repo.ItemRepository;

import com.example.lostfoundMS.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class ItemService {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;


    public void reportLostItem(Item item, String email){
        User user = userRepository.findByEmail(email)
                .orElseThrow(()-> new RuntimeException("User not found"));
        item.setUser(user);
        item.setType(ItemType.LOST);
        itemRepository.save(item);
    }

    public void reportFoundItem(Item item, String email){
        User user = userRepository.findByEmail(email)
                .orElseThrow(()-> new RuntimeException("User not found"));
        item.setUser(user);
        item.setType(ItemType.FOUND);
        itemRepository.save(item);
    }

    public List<Item> getAllFoundItems(){
        return itemRepository.findByType(ItemType.FOUND);
    }
    public List<Item> getAllLostItems(){
        return itemRepository.findByType(ItemType.LOST);
    }

    public Item getItemById(Long id){
        return itemRepository.findById(id)
                .orElseThrow(()->new RuntimeException("Item not found"));
    }

    public List<Item> getItemsByUser(String email){
        User user = userRepository.findByEmail(email)
                .orElseThrow(()->new RuntimeException("User not found"));
        return itemRepository.findByUserId(user.getId());
    }

    public List<Item> searchFoundItems(String Keyword){
        if(Keyword == null || Keyword.trim().isEmpty()){
            return getAllFoundItems();
        }
        return itemRepository.searchByKeyword(ItemType.FOUND, Keyword.trim());
    }
    public List<Item> getFoundItemsByCategory(String category){
        return itemRepository.findByType(ItemType.FOUND)
                .stream()
                .filter(item -> item.getCategory().equalsIgnoreCase(category))
                .toList();
    }
    public Item save(Item item) {
        return itemRepository.save(item);
    }
    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }
    public List<Item> getTop5Items(){
        return itemRepository.getTop4Items();
    }
}
