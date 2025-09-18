package com.battleship.game.controller;

import com.battleship.game.model.DTO.CreateGameRequest;
import com.battleship.game.model.DTO.GameResponse;
import com.battleship.game.model.DTO.JoinGameRequest;
import com.battleship.game.model.DTO.LeaveGameRequest;
import com.battleship.game.model.entity.Game;
import com.battleship.game.service.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/games")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class GameController {
    private final GameService gameService;

    /**
     * Create a new game
     * POST /api/games
     * Body: { "playerName": "John" }
     */
    @PostMapping
    public ResponseEntity<GameResponse> createGame(
            @RequestBody CreateGameRequest request) {
        try {
            Game game = gameService.createGame(request.getPlayerName());
            return ResponseEntity.ok(GameResponse.from(game));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Join an existing game by game ID
     * POST /api/games/{gameCode}/join
     * Body: { "playerName": "Jane" }
     */
    @PostMapping("/{gameCode}/join")
    public ResponseEntity<GameResponse> joinGame(
            @PathVariable String gameCode,
            @RequestBody JoinGameRequest request) {
        try {
            Game game = gameService.joinGame(gameCode, request.getPlayerName());
            return ResponseEntity.ok(GameResponse.from(game));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            GameResponse errorResponse = new GameResponse(
                    null, null, null, null, null, e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * Get game details
     * GET /api/games/{gameCode}
     */
    @GetMapping("/{gameCode}")
    public ResponseEntity<GameResponse> getGame(@PathVariable String gameCode) {
        try {
            Game game = gameService.getGame(gameCode);
            return ResponseEntity.ok(GameResponse.from(game));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Get all available games (waiting for players)
     * GET /api/games/available
     */
    @GetMapping("/available")
    public ResponseEntity<List<GameResponse>> getAvailableGames() {
        List<Game> games = gameService.getAvailableGames();
        List<GameResponse> response = games.stream()
                .map(GameResponse::from)
                .toList();
        return ResponseEntity.ok(response);
    }

    /**
     * Leave a game
     * POST /api/games/{gameId}/leave
     * Body: { "playerId": "xxx" }
     */
    @PostMapping("/{gameId}/leave")
    public ResponseEntity<GameResponse> leaveGame(
            @PathVariable String gameId,
            @RequestBody LeaveGameRequest request) {
        try {
            Game game = gameService.leaveGame(gameId, request.getPlayerId());
            return ResponseEntity.ok(GameResponse.from(game));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * get all games (for testing purposes)
     * GET /api/games
     */
    @GetMapping()
    public ResponseEntity<List<GameResponse>> getAllGames() {
        List<Game> games = gameService.getAllGames();
        List<GameResponse> response = new ArrayList<>();
        for (Game game : games) {
            response.add(GameResponse.from(game));
        }
        return ResponseEntity.ok(response);
    }
}
