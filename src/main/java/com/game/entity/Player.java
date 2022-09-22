package com.game.entity;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "player")
public class Player {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "title")
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(name = "race")
    private Race race;

    @Enumerated(EnumType.STRING)
    @Column(name = "profession")
    private Profession profession;

    @Column(name = "experience")
    private Integer experience;

    @Column(name = "level")
    private Integer level;

    @Column(name = "untilNextLevel")
    private Integer untilNextLevel;

    @Column(name = "birthday")
    private Date birthday;

    @Column(name = "banned")
    private Boolean banned;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Race getRace() {
        return race;
    }

    public void setRace(Race race) {
        this.race = race;
    }

    public Profession getProfession() {
        return profession;
    }

    public void setProfession(Profession profession) {
        this.profession = profession;
    }

    public Integer getExperience() {
        return experience;
    }

    public void setExperience(Integer experience) {
        this.experience = experience;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Integer getUntilNextLevel() {
        return untilNextLevel;
    }

    public void setUntilNextLevel(Integer untilNextLevel) {
        this.untilNextLevel = untilNextLevel;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public Boolean getBanned() {
        return banned;
    }

    public void setBanned(Boolean banned) {
        this.banned = banned;
    }

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

    public static boolean invalidParameters(Player player, Long id) {
        return (id <= 0 || player.getExperience() != null && (player.getExperience() < 0 || player.getExperience() > 10000000))
                || (player.getBirthday() != null && player.getBirthday().getTime() < 0);

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
