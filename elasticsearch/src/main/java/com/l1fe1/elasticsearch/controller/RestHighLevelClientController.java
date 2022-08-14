package com.l1fe1.elasticsearch.controller;

import com.l1fe1.elasticsearch.dto.ResultDto;
import com.l1fe1.elasticsearch.service.RestHighLevelClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/hig-level")
public class RestHighLevelClientController {
    @Autowired
    private RestHighLevelClientService restHighLevelClientService;

    @PostMapping("/index")
    public void createIndex() {
        restHighLevelClientService.createIndex();
    }

    @GetMapping("/index")
    public void getIndex() {
        restHighLevelClientService.getIndex();
    }

    @DeleteMapping("/index")
    public void deleteIndex() {
        restHighLevelClientService.deleteIndex();
    }

    @PostMapping
    public void insert() {
        restHighLevelClientService.insert();
    }

    @PostMapping("/bulk")
    public void bulk() {
        restHighLevelClientService.bulk();
    }

    @GetMapping("/{id}")
    public void getById(@PathVariable String id) {
        restHighLevelClientService.getById(id);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable String id) {
        restHighLevelClientService.deleteById(id);
    }

    @GetMapping("/multi")
    public void multiGetById() {
        restHighLevelClientService.multiGetById();
    }

    @PutMapping
    public void updateByQuery() {
        restHighLevelClientService.updateByQuery();
    }

    @GetMapping("/sniffer")
    public void sniffer() {
        restHighLevelClientService.sniffer();
    }

    @GetMapping("/auto-sniffer")
    public void autoSniffer() {
        restHighLevelClientService.autoSniffer();
    }

    @PostMapping("/bulk-init")
    public void bulkInit() {
        restHighLevelClientService.bulkInit();
    }

    @GetMapping("/carInfo")
    public ResultDto carInfo(@RequestParam(value = "keyword") String keyword,
                             @RequestParam(value = "from") Integer from,
                             @RequestParam(value = "size") Integer size) {
        return restHighLevelClientService.carInfo(keyword, from, size);
    }

    @GetMapping("/scroll")
    public ResultDto scroll(@RequestParam(value = "scrollId",required = false) String scrollId) {
        return restHighLevelClientService.scroll(scrollId);
    }

    @PostMapping("/car/bulk")
    public ResultDto carBulk() {
        return restHighLevelClientService.carBulk();
    }

    @GetMapping("/template")
    public ResultDto templateSearch() {
        return restHighLevelClientService.templateSearch();
    }

    @GetMapping("/fuzzy")
    public ResultDto fuzzy(@RequestParam String name) {
        return restHighLevelClientService.fuzzy(name);
    }

    @GetMapping("/car/multi")
    public ResultDto carMulti() {
        return restHighLevelClientService.multiSearch();
    }

    @GetMapping("/bool")
    public ResultDto bool() {
        return restHighLevelClientService.boolSearch();
    }
}
