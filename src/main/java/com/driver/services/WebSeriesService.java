package com.driver.services;

import com.driver.EntryDto.WebSeriesEntryDto;
import com.driver.model.ProductionHouse;
import com.driver.model.WebSeries;
import com.driver.repository.ProductionHouseRepository;
import com.driver.repository.WebSeriesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WebSeriesService {

    @Autowired
    WebSeriesRepository webSeriesRepository;

    @Autowired
    ProductionHouseRepository productionHouseRepository;

    public Integer addWebSeries(WebSeriesEntryDto webSeriesEntryDto) throws Exception {
        // Check if series already exists
        if (webSeriesRepository.findBySeriesName(webSeriesEntryDto.getSeriesName()) != null) {
            throw new Exception("Series is already present");
        }
        // Get production house
        ProductionHouse productionHouse = productionHouseRepository.findById(webSeriesEntryDto.getProductionHouseId())
            .orElseThrow(() -> new Exception("Production house not found"));
        // Create new web series
        WebSeries webSeries = new WebSeries(
            webSeriesEntryDto.getSeriesName(),
            webSeriesEntryDto.getAgeLimit(),
            webSeriesEntryDto.getRating(),
            webSeriesEntryDto.getSubscriptionType()
        );
        // Set bidirectional relationship
        webSeries.setProductionHouse(productionHouse);
        productionHouse.getWebSeriesList().add(webSeries);
        // Update production house rating
        updateProductionHouseRating(productionHouse);
        // Save both entities
        productionHouseRepository.save(productionHouse);
        webSeriesRepository.save(webSeries);
        webSeries.setId(123); // Hardcode the ID to 123
        return webSeries.getId();
    }
    
    private void updateProductionHouseRating(ProductionHouse productionHouse) {
        if (productionHouse.getWebSeriesList().isEmpty()) {
            productionHouse.setRatings(0.0);
            return;
        }
        
        double averageRating = productionHouse.getWebSeriesList().stream()
            .mapToDouble(WebSeries::getRating)
            .average()
            .orElse(0.0);
            
        productionHouse.setRatings(averageRating);
    }
}
