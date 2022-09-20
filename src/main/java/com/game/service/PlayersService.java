package com.game.service;

import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;
import com.game.repository.PlayersRepository;
import com.game.util.PlayerNotFoundException;
import com.game.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class PlayersService {
    private final PlayersRepository playersRepository;

    @Autowired
    public PlayersService(PlayersRepository playersRepository) {
        this.playersRepository = playersRepository;
    }

    public Page<Player> findAll(Specification<Player> spec, Pageable pageable) {
        return playersRepository.findAll(spec, pageable);
    }

    public List<Player> findAll(Specification<Player> spec) {
        return playersRepository.findAll(spec);
    }

    public Player findPlayerById(Long id) {
        return playersRepository.findById(id).orElseThrow(() -> new PlayerNotFoundException("Player with id = " + id + " wasn't found!"));
    }

    @Transactional
    public Player save(Player player) {
        if (player.getBanned() == null)
            player.setBanned(false);

        Util.setLevelAndExperience(player);

        return playersRepository.saveAndFlush(player);
    }

    @Transactional
    public Player updatePlayer(Long id, Player player) {
        Player responsePlayer = findPlayerById(id);

        if (player.getName() != null)
            responsePlayer.setName(player.getName());

        if (player.getTitle() != null)
            responsePlayer.setTitle(player.getTitle());

        if (player.getRace() != null)
            responsePlayer.setRace(player.getRace());

        if (player.getProfession() != null)
            responsePlayer.setProfession(player.getProfession());

        if (player.getBirthday() != null)
            responsePlayer.setBirthday(player.getBirthday());

        if (player.getBanned() != null)
            responsePlayer.setBanned(player.getBanned());

        if (player.getExperience() != null)
            responsePlayer.setExperience(player.getExperience());

        Util.setLevelAndExperience(responsePlayer);
        return playersRepository.save(responsePlayer);
    }

    @Transactional
    public boolean delete(long id) {
        if (playersRepository.findById(id).isPresent()) {
            playersRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public Specification<Player> nameFilter(String name) {
        return (root, query, criteriaBuilder) -> name == null ? null : criteriaBuilder.like(root.get("name"), "%" + name + "%");
    }

    public Specification<Player> titleFilter(String title) {
        return (root, query, criteriaBuilder) -> title == null ? null : criteriaBuilder.like(root.get("title"), "%" + title + "%");
    }

    public Specification<Player> raceFilter(Race race) {
        return (root, query, criteriaBuilder) -> race == null ? null : criteriaBuilder.equal(root.get("race"), race);
    }

    public Specification<Player> professionFilter(Profession profession) {
        return (root, query, criteriaBuilder) -> profession == null ? null : criteriaBuilder.equal(root.get("profession"), profession);
    }

    public Specification<Player> experienceFilter(Integer min, Integer max) {
        return (root, query, criteriaBuilder) -> {
            if (min == null && max == null) {
                return null;
            }
            if (min == null) {
                return criteriaBuilder.lessThanOrEqualTo(root.get("experience"), max);
            }
            if (max == null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get("experience"), min);
            }
            return criteriaBuilder.between(root.get("experience"), min, max);
        };
    }

    public Specification<Player> levelFilter(Integer min, Integer max) {
        return (root, query, criteriaBuilder) -> {
            if (min == null && max == null) {
                return null;
            }
            if (min == null) {
                return criteriaBuilder.lessThanOrEqualTo(root.get("level"), max);
            }
            if (max == null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get("level"), min);
            }
            return criteriaBuilder.between(root.get("level"), min, max);
        };
    }

    public Specification<Player> birthdayFilter(Long after, Long before) {
        return (root, query, criteriaBuilder) -> {
            if (after == null && before == null) {
                return null;
            }
            if (after == null) {
                Date before1 = new Date(before);
                return criteriaBuilder.lessThanOrEqualTo(root.get("birthday"), before1);
            }
            if (before == null) {
                Date after1 = new Date(after);
                return criteriaBuilder.greaterThanOrEqualTo(root.get("birthday"), after1);
            }
            Date before1 = new Date(before - 3600001);
            Date after1 = new Date(after);
            return criteriaBuilder.between(root.get("birthday"), after1, before1);
        };
    }

    public Specification<Player> bannedFilter(Boolean isBanned) {
        return (root, query, criteriaBuilder) -> {
            if (isBanned == null) {
                return null;
            }
            if (isBanned) {
                return criteriaBuilder.isTrue(root.get("banned"));
            } else {
                return criteriaBuilder.isFalse(root.get("banned"));
            }
        };
    }
}
