package com.driver.services;

import com.driver.EntryDto.ProductionHouseEntryDto;
import com.driver.model.ProductionHouse;
import com.driver.repository.ProductionHouseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductionHouseService {

    @Autowired
    ProductionHouseRepository productionHouseRepository;

    public Integer addProductionHouse(ProductionHouseEntryDto productionHouseEntryDto) throws Exception {
        // Check if production house with same name exists
        if (productionHouseRepository.findByName(productionHouseEntryDto.getName()) != null) {
            throw new Exception("Production house already exists");
        }
        
        ProductionHouse productionHouse = new ProductionHouse(productionHouseEntryDto.getName());
        ProductionHouse savedProductionHouse = productionHouseRepository.save(productionHouse);
        
        return savedProductionHouse.getId();
    }

    public Integer addProductionHouseToDb(ProductionHouseEntryDto productionHouseEntryDto) throws Exception {
        return addProductionHouse(productionHouseEntryDto);
    }
}
