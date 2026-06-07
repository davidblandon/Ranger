package com.example.rhservice.infrastructure.web.controller;

import com.example.rhservice.application.port.in.ShiftService;
import com.example.rhservice.domain.model.Shift;
import com.example.rhservice.domain.model.TimeBlock;
import com.example.rhservice.infrastructure.web.dto.ShiftRequest;
import com.example.rhservice.infrastructure.web.dto.ShiftResponse;
import com.example.rhservice.infrastructure.web.dto.TimeBlockRequest;
import com.example.rhservice.infrastructure.web.dto.TimeBlockResponse;
import com.example.rhservice.infrastructure.web.security.CurrentUserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/shifts")
@Tag(name = "Shifts")
public class ShiftController {

    private final ShiftService shiftService;
    private final CurrentUserService currentUserService;

    public ShiftController(ShiftService shiftService, CurrentUserService currentUserService) {
        this.shiftService = shiftService;
        this.currentUserService = currentUserService;
    }

    @GetMapping
    public List<ShiftResponse> findAll(Authentication authentication) {
        return currentUserService.filterShifts(authentication, shiftService.findAll()).stream().map(this::toResponse).toList();
    }

    @GetMapping("/{id}")
    public ShiftResponse findById(@PathVariable Long id, Authentication authentication) {
        Shift shift = loadShift(id);
        currentUserService.requireShiftAccess(authentication, shift);
        return toResponse(shift);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ShiftResponse create(@Valid @RequestBody ShiftRequest request) {
        Shift shift = new Shift();
        applyRequest(shift, request);
        return toResponse(shiftService.save(shift));
    }

    @PutMapping("/{id}")
    public ShiftResponse update(@PathVariable Long id, @Valid @RequestBody ShiftRequest request) {
        Shift shift = loadShift(id);
        applyRequest(shift, request);
        return toResponse(shiftService.save(shift));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        shiftService.delete(id);
    }

    private Shift loadShift(Long id) {
        return shiftService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Shift not found"));
    }

    private void applyRequest(Shift shift, ShiftRequest request) {
        shift.setMonday(toTimeBlocks(request.monday()));
        shift.setTuesday(toTimeBlocks(request.tuesday()));
        shift.setWednesday(toTimeBlocks(request.wednesday()));
        shift.setThursday(toTimeBlocks(request.thursday()));
        shift.setFriday(toTimeBlocks(request.friday()));
        shift.setSaturday(toTimeBlocks(request.saturday()));
        shift.setSunday(toTimeBlocks(request.sunday()));
    }

    private List<TimeBlock> toTimeBlocks(List<TimeBlockRequest> requests) {
        if (requests == null) {
            return new ArrayList<>();
        }
        return requests.stream()
                .map(request -> new TimeBlock(request.start(), request.end()))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private ShiftResponse toResponse(Shift shift) {
        return new ShiftResponse(
                shift.getId(),
                toTimeBlockResponses(shift.getMonday()),
                toTimeBlockResponses(shift.getTuesday()),
                toTimeBlockResponses(shift.getWednesday()),
                toTimeBlockResponses(shift.getThursday()),
                toTimeBlockResponses(shift.getFriday()),
                toTimeBlockResponses(shift.getSaturday()),
                toTimeBlockResponses(shift.getSunday())
        );
    }

    private List<TimeBlockResponse> toTimeBlockResponses(List<TimeBlock> timeBlocks) {
        if (timeBlocks == null) {
            return List.of();
        }
        return timeBlocks.stream()
                .map(block -> new TimeBlockResponse(block.getStart(), block.getEnd()))
                .toList();
    }
}