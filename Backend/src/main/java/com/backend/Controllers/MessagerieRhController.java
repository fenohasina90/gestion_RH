package com.backend.Controllers;

import com.backend.services.MessagerieRhService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/rh/messages")
@CrossOrigin(origins = "*")
public class MessagerieRhController {

    private final MessagerieRhService messagerieRhService;

    public MessagerieRhController(MessagerieRhService messagerieRhService) {
        this.messagerieRhService = messagerieRhService;
    }

    @GetMapping
    public ResponseEntity<?> getAllMessages() {
        List<Map<String, Object>> messages = messagerieRhService.getAllMessages();
        return ResponseEntity.ok(messages);
    }

    @PostMapping("/{id}/reponse")
    public ResponseEntity<?> repondre(@PathVariable("id") Integer idMessage,
                                      @RequestBody Map<String, Object> body) {
        String reponse = body.get("reponse") != null ? body.get("reponse").toString() : null;
        Map<String, Object> result = messagerieRhService.repondreMessage(idMessage, reponse);
        return ResponseEntity.ok(result);
    }
}
