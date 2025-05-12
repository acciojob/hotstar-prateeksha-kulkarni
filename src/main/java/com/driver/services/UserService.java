package com.driver.services;

import com.driver.model.Subscription;
import com.driver.model.SubscriptionType;
import com.driver.model.User;
import com.driver.model.WebSeries;
import com.driver.repository.UserRepository;
import com.driver.repository.WebSeriesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    WebSeriesRepository webSeriesRepository;

    public Integer addUser(User user) {
        User savedUser = userRepository.save(user);
        return savedUser.getId();
    }

    public Integer getAvailableCountOfWebSeriesViewable(Integer userId) {
        User user = userRepository.findById(userId).get();
        Subscription subscription = user.getSubscription();
        
        if (subscription == null) {
            return 0;
        }
        
        List<WebSeries> allWebSeries = webSeriesRepository.findAll();
        SubscriptionType userSubscriptionType = subscription.getSubscriptionType();
        
        return (int) allWebSeries.stream()
            .filter(webSeries -> {
                // Check age limit
                if (user.getAge() < webSeries.getAgeLimit()) {
                    return false;
                }
                
                // Check subscription type
                SubscriptionType seriesSubscriptionType = webSeries.getSubscriptionType();
                if (userSubscriptionType == SubscriptionType.ELITE) {
                    return true; // ELITE can watch everything
                } else if (userSubscriptionType == SubscriptionType.PRO) {
                    return seriesSubscriptionType == SubscriptionType.BASIC || 
                           seriesSubscriptionType == SubscriptionType.PRO;
                } else { // BASIC
                    return seriesSubscriptionType == SubscriptionType.BASIC;
                }
            })
            .count();
    }
}
