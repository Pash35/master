package searchengine.controllers;

import lombok.RequiredArgsConstructor;
<<<<<<< HEAD
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
=======
import org.hibernate.mapping.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import searchengine.config.Site;
>>>>>>> c50fbabb287430063d38ef8b6a33f7a3358b3beb
import searchengine.dto.indexing.IndexingResponse;
import searchengine.dto.search.SearchResponse;
import searchengine.dto.statistics.StatisticsResponse;
import searchengine.services.IndexingService;
<<<<<<< HEAD
=======
import searchengine.services.IndexingServiceImpl;
>>>>>>> c50fbabb287430063d38ef8b6a33f7a3358b3beb
import searchengine.services.SearchService;
import searchengine.services.StatisticsService;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
<<<<<<< HEAD
=======
import java.util.Optional;
>>>>>>> c50fbabb287430063d38ef8b6a33f7a3358b3beb

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
