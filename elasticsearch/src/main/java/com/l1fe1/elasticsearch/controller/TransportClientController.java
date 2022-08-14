package com.l1fe1.elasticsearch.controller;

import com.l1fe1.elasticsearch.service.TransportClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/transport")
public class TransportClientController {
    @Autowired
    private TransportClientService transportClientService;

    @PostMapping
    public void create() {
        transportClientService.create();
    }


}
