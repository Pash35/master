package searchengine.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import searchengine.dto.indexing.IndexingResponse;
import searchengine.dto.search.SearchResponse;
import searchengine.dto.statistics.StatisticsResponse;
import searchengine.services.IndexingService;
import searchengine.services.SearchService;
import searchengine.services.StatisticsService;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ApiController {

    private final StatisticsService statisticsService;
    private final IndexingService indexingService;
    private final SearchService searchService;


    @GetMapping("/statistics")
    public ResponseEntity<StatisticsResponse> statistics() {
        return ResponseEntity.ok(statisticsService.getStatistics());
    }

    @GetMapping("/startIndexing")
    public ResponseEntity<IndexingResponse> startIndexing() {
        return ResponseEntity.ok(indexingService.startIndexing());
    }

    @GetMapping("/stopIndexing")
    public ResponseEntity<IndexingResponse> stopIndexing() {
        return ResponseEntity.ok(indexingService.stopIndexing());
    }

    @PostMapping("/indexPage")
    public ResponseEntity<IndexingResponse> indexPage(@RequestBody String ref) {

        String result = "";
        try {    result = java.net.URLDecoder.decode(ref, StandardCharsets.UTF_8.name());

        } catch (UnsupportedEncodingException e) {
            e.getMessage();
        }
        return ResponseEntity.ok(indexingService.indexPage(result));
    }

    @GetMapping(path = "/search")
    public  ResponseEntity<SearchResponse> search (@RequestParam("query") String query, @RequestParam("offset") int offset,
                                                   @RequestParam("limit") int limit, @RequestParam(required = false) String site) throws IOException {


        return ResponseEntity.ok(searchService.search(query, offset, limit, site));
    }
}
