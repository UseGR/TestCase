package com.game.controller;

import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;
import com.game.service.PlayersService;
import com.game.util.Util;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Calendar;
import java.util.List;

@RestController
@RequestMapping("/rest/players")
public class PlayerRestController {
    private final PlayersService playersService;

    public PlayerRestController(PlayersService playersService) {
        this.playersService = playersService;
    }

    @GetMapping
    public ResponseEntity<List<Player>> getPlayersList(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "race", required = false) Race race,
            @RequestParam(value = "profession", required = false) Profession profession,
            @RequestParam(value = "after", required = false) Long after,
            @RequestParam(value = "before", required = false) Long before,
            @RequestParam(value = "banned", required = false) Boolean banned,
            @RequestParam(value = "minExperience", required = false) Integer minExperience,
            @RequestParam(value = "maxExperience", required = false) Integer maxExperience,
            @RequestParam(value = "minLevel", required = false) Integer minLevel,
            @RequestParam(value = "maxLevel", required = false) Integer maxLevel,
            @RequestParam(value = "order", required = false, defaultValue = "ID") PlayerOrder order,
            @RequestParam(value = "pageNumber", required = false, defaultValue = "0") Integer pageNumber,
            @RequestParam(value = "pageSize", required = false, defaultValue = "3") Integer pageSize) {

        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(order.getFieldName()));

        return new ResponseEntity<>(playersService.findAll(
                        Specification.where(
                                        playersService.nameFilter(name)
                                                .and(playersService.titleFilter(title)))
                                .and(playersService.raceFilter(race))
                                .and(playersService.professionFilter(profession))
                                .and(playersService.birthdayFilter(after, before))
                                .and(playersService.bannedFilter(banned))
                                .and(playersService.experienceFilter(minExperience, maxExperience))
                                .and(playersService.levelFilter(minLevel, maxLevel)), pageable)
                .getContent(), HttpStatus.OK);
    }

    @GetMapping("/count")
    public ResponseEntity<Integer> getPlayersCount(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "race", required = false) Race race,
            @RequestParam(value = "profession", required = false) Profession profession,
            @RequestParam(value = "after", required = false) Long after,
            @RequestParam(value = "before", required = false) Long before,
            @RequestParam(value = "banned", required = false) Boolean banned,
            @RequestParam(value = "minExperience", required = false) Integer minExperience,
            @RequestParam(value = "maxExperience", required = false) Integer maxExperience,
            @RequestParam(value = "minLevel", required = false) Integer minLevel,
            @RequestParam(value = "maxLevel", required = false) Integer maxLevel) {

        return new ResponseEntity<>(playersService.findAll(
                Specification.where(
                                playersService.nameFilter(name)
                                        .and(playersService.titleFilter(title)))
                        .and(playersService.raceFilter(race))
                        .and(playersService.professionFilter(profession))
                        .and(playersService.birthdayFilter(after, before))
                        .and(playersService.bannedFilter(banned))
                        .and(playersService.experienceFilter(minExperience, maxExperience))
                        .and(playersService.levelFilter(minLevel, maxLevel))).size()
                , HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Player> createPlayer(@RequestBody Player body) {
        if (body == null || body.getName() == null || body.getTitle() == null || body.getRace() == null
                || body.getProfession() == null || body.getBirthday() == null || body.getExperience() == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        if (body.getName().length() < 1 || body.getName().length() > 12)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        if (body.getTitle().length() > 30)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        if (body.getExperience() < 0 || body.getExperience() > 10_000_000)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        if (body.getBirthday().getTime() < 0)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        Calendar date = Calendar.getInstance();
        date.setTime(body.getBirthday());

        if (date.get(Calendar.YEAR) < 2_000 || date.get(Calendar.YEAR) > 3_000)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);


        Player player = playersService.save(body);
        return new ResponseEntity<>(player, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Player> getPlayer(@PathVariable("id") String id) {
        if (Util.checkId(id).equals(new ResponseEntity<>(HttpStatus.BAD_REQUEST)))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        return new ResponseEntity<>(playersService.findPlayerById(Long.parseLong(id)), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Player> updatePlayer(@PathVariable("id") String id, @RequestBody Player player) {
        if (Util.checkId(id).equals(new ResponseEntity<>(HttpStatus.BAD_REQUEST)) ||
                player.getExperience() != null && player.getExperience() < 0 || player.getExperience() > 10000000
                || player.getBirthday() != null && player.getBirthday().getTime() < 0) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Player responsePlayer = playersService.updatePlayer(Long.parseLong(id), player);

        if (responsePlayer == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        else
            return new ResponseEntity<>(responsePlayer, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePlayer(@PathVariable("id") String id) {
        if (Util.checkId(id).equals(new ResponseEntity<>(HttpStatus.BAD_REQUEST)))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        if (playersService.delete(Long.parseLong(id)))
            return new ResponseEntity<>(HttpStatus.OK);
        else return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }


}
