package com.krasnopolskyi.fitcoach.http.rest;

import com.krasnopolskyi.fitcoach.entity.TrainingType;
import com.krasnopolskyi.fitcoach.service.TrainingTypeService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/fit-coach/training-types")
@RequiredArgsConstructor
public class TrainingTypeController {

    private final TrainingTypeService trainingTypeService;

    /**
     * @return List with all available training types
     */
    @Operation(summary = "Retrieve all training types",
            description = "Returns a list of all available training types.")
    @GetMapping
    public ResponseEntity<List<TrainingType>> findAll() {
        return ResponseEntity.status(HttpStatus.OK).body(trainingTypeService.findAll());
    }
}
