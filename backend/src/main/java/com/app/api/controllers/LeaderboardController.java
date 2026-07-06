package com.app.api.controllers;
import com.app.api.dtos.LeaderboardResponse;
import com.app.api.services.LeaderboardService;
import com.app.api.services.FirebaseAuthService;
import com.google.firebase.auth.FirebaseAuthException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
@RestController
@RequestMapping("/api")
public class LeaderboardController {
    private final LeaderboardService leaderboardService;
    private final FirebaseAuthService firebaseAuthService;

    public LeaderboardController(LeaderboardService leaderboardService, FirebaseAuthService firebaseAuthService){
        this.leaderboardService = leaderboardService;
        this.firebaseAuthService = firebaseAuthService;
    }

    /**
     * GET /api/leaderboard?limit=10
     *
     * rankBy is accepted as a parameter to match the API contract but is currently
     * fixed to averageRating (helper_analytics_table.average_rating).
     * When user-level XP is added to the schema, the service can branch on rankBy.
     */

    @GetMapping("/leaderboard")
    public ResponseEntity<?> getLeaderboard(
        @RequestHeader("Authorization") String authHeader,
        @RequestParam(defaultValue = "averageRating") String rankBy,
        @RequestParam(defaultValue = "10")            int    limit
    ){
        if(!rankBy.equals("averageRating") && !rankBy.equals("xp")){
            return ResponseEntity.badRequest().body("{\\\"error\\\": \\\"rankBy must be one of: averageRating, xp\\}");
        }

        try {
            String token = authHeader.replace("Bearer ", "");
            int userId = firebaseAuthService.getUserIdFromToken(token);
            LeaderboardResponse response = leaderboardService.getLeaderboard(userId, limit);
            return ResponseEntity.ok(response);
        } catch (FirebaseAuthException e) {
            return ResponseEntity.status(401).body("Invalid or expired Firebase token");
        }
    }
}
