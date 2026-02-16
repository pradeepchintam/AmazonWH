package com.amazonwh.wms.controller;

import com.amazonwh.wms.dto.PickTaskDto;
import com.amazonwh.wms.service.PickingService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/pick-tasks")
@RequiredArgsConstructor
public class PickingController {

    private final PickingService service;

    @GetMapping
    public Page<PickTaskDto> getAll(
            @RequestParam(required = false) Long dcId,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "100") int size) {
        return service.getAll(dcId, PageRequest.of(page, size, Sort.by("pickSequence")));
    }

    @GetMapping("/my-tasks")
    public List<PickTaskDto> getMyTasks(@RequestParam Long userId) {
        return service.getMyTasks(userId);
    }

    @PostMapping("/{id}/assign")
    public PickTaskDto assign(@PathVariable Long id, @RequestBody Map<String, Long> body) {
        return service.assign(id, body.get("userId"));
    }

    @PostMapping("/{id}/start")
    public PickTaskDto start(@PathVariable Long id) { return service.start(id); }

    @PostMapping("/{id}/complete")
    public PickTaskDto complete(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        return service.complete(id, body.get("qtyPicked"));
    }

    @PostMapping("/{id}/short")
    public PickTaskDto shortPick(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        return service.shortPick(id, body.get("qtyPicked"));
    }
}
