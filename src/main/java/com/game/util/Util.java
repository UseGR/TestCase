package com.game.util;

import com.game.entity.Player;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


public class Util {
    public static ResponseEntity<HttpStatus> checkId(String id) {
        try {
            if (id == null || Long.parseLong(id) <= 0 || id.equals(""))
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            else
                return new ResponseEntity<>(HttpStatus.OK);

        } catch (NumberFormatException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    public static void setLevelAndExperience(Player player) {
        player.setLevel(computeLevel(player));
        player.setUntilNextLevel(computeExperience(player));
    }

    private static int computeLevel(Player player) {
        return (int) ((Math.sqrt(2500 + 200 * player.getExperience()) - 50) / 100);
    }

    private static int computeExperience(Player player) {
        return 50 * (computeLevel(player) + 1) * (computeLevel(player) + 2) - player.getExperience();
    }
}
