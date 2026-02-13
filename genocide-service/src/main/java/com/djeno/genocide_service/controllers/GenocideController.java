package com.djeno.genocide_service.controllers;

import com.djeno.genocide_service.persistence.dto.ResponseMessage;
import com.djeno.genocide_service.services.GenocideService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/genocide")
@RestController
public class GenocideController {

    private final GenocideService genocideService;

    @PostMapping("/kill/{id}")
    public ResponseEntity<ResponseMessage> killPopulationOfCity(@PathVariable int id) {
        genocideService.killPopulationOfCity(id);
        return ResponseEntity.ok().body(new ResponseMessage("Население города уничтожено"));
    }

    @PostMapping("/move-to-poorest/{id}")
    public ResponseEntity<ResponseMessage> movePopulationToPoorestCity(@PathVariable int id) {
        int targetCityId = genocideService.movePopulationToPoorestCity(id);
        return ResponseEntity.ok().body(new ResponseMessage("Население города с id " + id + " переселено в город с id: " + targetCityId));
    }
}
