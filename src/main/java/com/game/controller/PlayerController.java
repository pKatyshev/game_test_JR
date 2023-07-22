package com.game.controller;

import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;
import com.game.service.PlayerService;
import com.game.service.PlayerValidator;
import com.game.util.PlayerControllerException;
import com.game.util.PlayerNotFoundException;
import com.game.util.PlayerValidationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/rest/players")
public class PlayerController {

    private final PlayerService playerService;

    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    @ResponseBody
    @GetMapping
    public List<Player> getAllPlayers(
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "title", required = false) String title,
            @RequestParam(name = "race", required = false) Race race,
            @RequestParam(name = "profession", required = false) Profession profession,
            @RequestParam(name = "after", required = false) Long after,
            @RequestParam(name = "before", required = false) Long before,
            @RequestParam(name = "banned", required = false) Boolean banned,
            @RequestParam(name = "minExperience", required = false) Integer minExperience,
            @RequestParam(name = "maxExperience", required = false) Integer maxExperience,
            @RequestParam(name = "minLevel", required = false) Integer minLevel,
            @RequestParam(name = "maxLevel", required = false) Integer maxLevel,
            @RequestParam(name = "order", required = false) PlayerOrder order,
            @RequestParam(name = "pageNumber", defaultValue = "0") int pageNumber,
            @RequestParam(name = "pageSize", defaultValue = "3") int pageSize,
            HttpServletRequest request) {

        if (order == null) order = PlayerOrder.ID;
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(order.getFieldName()));

        return playerService.findAll(
                Specification.where(playerService.checkName(name))
                        .and(playerService.checkTitle(title))
                        .and(playerService.checkBirthDay(after, before))
                        .and(playerService.checkExperience(minExperience, maxExperience))
                        .and(playerService.checkLevel(minLevel, maxLevel))
                        .and(playerService.checkRace(race))
                        .and(playerService.checkProfession(profession))
                        .and(playerService.checkBanned(banned)),
                pageable);
    }

    private void printRequest(HttpServletRequest request) {
        Map<String, String[]> parameterMap = request.getParameterMap();
        for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
            System.out.println(entry.getKey() + " : " + entry.getValue()[0]);
        }
    }

    @GetMapping("/count")
    public int getPlayersCount(
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "title", required = false) String title,
            @RequestParam(name = "race", required = false) Race race,
            @RequestParam(name = "profession", required = false) Profession profession,
            @RequestParam(name = "after", required = false) Long after,
            @RequestParam(name = "before", required = false) Long before,
            @RequestParam(name = "banned", required = false) Boolean banned,
            @RequestParam(name = "minExperience", required = false) Integer minExperience,
            @RequestParam(name = "maxExperience", required = false) Integer maxExperience,
            @RequestParam(name = "minLevel", required = false) Integer minLevel,
            @RequestParam(name = "maxLevel", required = false) Integer maxLevel) {

        return playerService.findAll(Specification.where(playerService.checkName(name))
                .and(playerService.checkTitle(title))
                .and(playerService.checkBirthDay(after, before))
                .and(playerService.checkExperience(minExperience, maxExperience))
                .and(playerService.checkLevel(minLevel, maxLevel))
                .and(playerService.checkRace(race))
                .and(playerService.checkProfession(profession))
                .and(playerService.checkBanned(banned))).size();

    }

    @PostMapping
    @ResponseBody
    public ResponseEntity<Player> createPlayer(@RequestBody Player player) {
        Player savedPlayer = playerService.create(player);
        return ResponseEntity.ok(savedPlayer);
    }

    @PostMapping("/{id}")
    public ResponseEntity<Player> update(@PathVariable("id") int id,
                                         @RequestBody Player player) {

        if (id == 0) throw new PlayerControllerException("id cannot be 0");
        Player updatedPlayer = playerService.update(id, player);
        return ResponseEntity.ok(updatedPlayer);
    }

    @GetMapping("/{id}")
    public Player getPlayerById(@PathVariable("id") Long id) {
        if (id == 0) throw new PlayerControllerException("invalid player id: " + id);
        return playerService.findOne(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> delete(@PathVariable("id") Long id) {
        if (id == 0) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        playerService.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ExceptionHandler
    private ResponseEntity<String> handle(PlayerNotFoundException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    private ResponseEntity<String> handle(PlayerControllerException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    private ResponseEntity<String> handle(PlayerValidationException e) {
        System.out.println(e.getMessage());
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }
}
