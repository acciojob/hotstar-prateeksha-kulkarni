package com.driver.services;


import com.driver.EntryDto.SubscriptionEntryDto;
import com.driver.model.Subscription;
import com.driver.model.SubscriptionType;
import com.driver.model.User;
import com.driver.repository.SubscriptionRepository;
import com.driver.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class SubscriptionService {

    @Autowired
    SubscriptionRepository subscriptionRepository;

    @Autowired
    UserRepository userRepository;

    public Integer buySubscription(SubscriptionEntryDto subscriptionEntryDto) {
        // Get the user
        User user = userRepository.findById(subscriptionEntryDto.getUserId()).get();
        
        // Calculate total amount based on subscription type and number of screens
        int totalAmount = calculateSubscriptionAmount(
            subscriptionEntryDto.getSubscriptionType(),
            subscriptionEntryDto.getNoOfScreensRequired()
        );
        
        // Create new subscription
        Subscription subscription = new Subscription(
            subscriptionEntryDto.getSubscriptionType(),
            subscriptionEntryDto.getNoOfScreensRequired(),
            new Date(),
            totalAmount
        );
        
        // Set bidirectional relationship
        subscription.setUser(user);
        user.setSubscription(subscription);
        
        // Save subscription
        subscriptionRepository.save(subscription);
        
        return totalAmount;
    }

    public Integer upgradeSubscription(Integer userId) throws Exception {
        User user = userRepository.findById(userId).get();
        Subscription currentSubscription = user.getSubscription();
        
        if (currentSubscription == null) {
            throw new Exception("No subscription found for user");
        }
        
        if (currentSubscription.getSubscriptionType() == SubscriptionType.ELITE) {
            throw new Exception("Already the best Subscription");
        }
        
        // Calculate new subscription type
        SubscriptionType newType = currentSubscription.getSubscriptionType() == SubscriptionType.BASIC ? 
            SubscriptionType.PRO : SubscriptionType.ELITE;
            
        // Calculate price difference
        int currentAmount = currentSubscription.getTotalAmountPaid();
        int newAmount = calculateSubscriptionAmount(newType, currentSubscription.getNoOfScreensSubscribed());
        int priceDifference = newAmount - currentAmount;
        
        // Update subscription
        currentSubscription.setSubscriptionType(newType);
        currentSubscription.setTotalAmountPaid(newAmount);
        subscriptionRepository.save(currentSubscription);
        
        return priceDifference;
    }

    public Integer calculateTotalRevenueOfHotstar() {
        List<Subscription> allSubscriptions = subscriptionRepository.findAll();
        return allSubscriptions.stream()
            .mapToInt(Subscription::getTotalAmountPaid)
            .sum();
    }
    
    private int calculateSubscriptionAmount(SubscriptionType type, int noOfScreens) {
        int baseAmount;
        int perScreenAmount;
        
        switch (type) {
            case BASIC:
                baseAmount = 500;
                perScreenAmount = 200;
                break;
            case PRO:
                baseAmount = 800;
                perScreenAmount = 250;
                break;
            case ELITE:
                baseAmount = 1000;
                perScreenAmount = 350;
                break;
            default:
                throw new IllegalArgumentException("Invalid subscription type");
        }
        
        return baseAmount + (perScreenAmount * noOfScreens);
    }
}
