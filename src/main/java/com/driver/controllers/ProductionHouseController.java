package com.driver.controllers;

import com.driver.EntryDto.ProductionHouseEntryDto;
import com.driver.services.ProductionHouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/production")
public class ProductionHouseController {

    @Autowired
    ProductionHouseService productionHouseService;

    @PostMapping("/add")
    public Integer addProductionHouse(ProductionHouseEntryDto productionHouseEntryDto) {
        try {
            return productionHouseService.addProductionHouse(productionHouseEntryDto);
        } catch (Exception e) {
            return -1;
        }
    }
}
