package com.game.service;

import com.game.controller.PlayerController;
import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;
import com.game.repository.PlayerRepository;
import com.game.util.PlayerControllerException;
import com.game.util.PlayerNotFoundException;
import com.game.util.PlayerValidationException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class PlayerService {

    private final PlayerRepository playerRepository;
    private final PlayerValidator playerValidator;

    public PlayerService(PlayerRepository playerRepository, PlayerValidator playerValidator) {
        this.playerRepository = playerRepository;
        this.playerValidator = playerValidator;
    }

    public Player findOne(long id) {
        Optional<Player> optional = playerRepository.findById(id);
        if (optional.isPresent()){
            return optional.get();
        } else throw new PlayerNotFoundException("player id = " + id + " not found");

    }

    public List<Player> findAll(Specification<Player> spec) {
        return playerRepository.findAll(spec);
    }

    public List<Player> findAll(Specification<Player> spec, Pageable pageRequest) {
        return playerRepository.findAll(spec, pageRequest).getContent();
    }

    public List<Player> findAll() {
        return playerRepository.findAll();
    }

    public int getCountPlayers() {
        return (int) playerRepository.count();
    }

    public void delete(long id) {
        playerRepository.delete(findOne(id));
    }

    public Player create(Player player) {
        playerValidator.validate(player);
        calculateLevel(player);
        Player savedPlayer = playerRepository.saveAndFlush(player);

        System.out.println("SAVED PLAYER ID " + savedPlayer.getId());
        return savedPlayer;
    }

    public Player update(int id, Player player) {
        Player newPlayer = findOne(id);

        if(player.getName() != null) {
            playerValidator.checkName(player.getName());
            newPlayer.setName(player.getName());
        }

        if(player.getTitle() != null) {
            playerValidator.checkTitle(player.getTitle());
            newPlayer.setTitle(player.getTitle());
        }

        if(player.getRace() != null) {
            newPlayer.setRace(player.getRace());
        }

        if (player.getProfession() != null) {
            newPlayer.setProfession(player.getProfession());
        }

        if (player.getBirthday() != null) {
            playerValidator.checkBirthday(player.getBirthday());
            newPlayer.setBirthday(player.getBirthday());
        }

        if (player.getBanned() != null) {
            newPlayer.setBanned(player.getBanned());
        }

        if (player.getExperience() != null) {
            playerValidator.checkExperience(player.getExperience());
            newPlayer.setExperience(player.getExperience());
        }

        calculateLevel(newPlayer);

        return playerRepository.saveAndFlush(newPlayer);
    }

    public Specification<Player> checkName(String name) {
        return (root, query, criteriaBuilder) -> {
            if (name == null) return null;
            return criteriaBuilder.like(root.get("name"), "%"+name+"%");
        };

    }

    public Specification<Player> checkTitle(String title) {
        return (root, query, criteriaBuilder) -> {
            if (title == null) return null;
            return criteriaBuilder.like(root.get("title"), "%" + title + "%");
        };
    }

    public Specification<Player> checkBirthDay(Long after, Long before) {
        return (root, query, builder) -> {
            if (after == null && before == null) return null;

            if (after == null) return builder.lessThanOrEqualTo(root.get("birthday"), new Date(before));
            if (before == null) return builder.greaterThanOrEqualTo(root.get("birthday"), new Date(after));

            return builder.between(root.get("birthday"), new Date(after), new Date(before));
        };
    }

    public Specification<Player> checkExperience(Integer min, Integer max) {
        if (min == null && max == null) return null;
        if (min == null) min = 0;
        if (max == null) max = Integer.MAX_VALUE;

        Integer finalMin = min;
        Integer finalMax = max;

        return (root, query, criteriaBuilder) -> criteriaBuilder.between(root.get("experience"), finalMin, finalMax);
    }

    public Specification<Player> checkLevel(Integer minLevel, Integer maxLevel) {
        if (minLevel == null && maxLevel == null) return null;
        if (minLevel == null) minLevel = 0;
        if (maxLevel == null) maxLevel = Integer.MAX_VALUE;

        Integer finalMinLevel = minLevel;
        Integer finalMaxLevel = maxLevel;

        return (root, query, criteriaBuilder) -> criteriaBuilder.between(root.get("level"), finalMinLevel, finalMaxLevel);
    }


    public Specification<Player> checkRace(Race race) {
        return (root, query, criteriaBuilder) -> {
            if (race == null) return null;
            return criteriaBuilder.equal(root.get("race"), race);
        };
    }

    public Specification<Player> checkProfession(Profession profession) {
        return (root, query, criteriaBuilder) -> {
            if (profession == null) return null;
            return criteriaBuilder.equal(root.get("profession"), profession);
        };
    }

    public Specification<Player> checkBanned(Boolean banned) {
        return (root, query, criteriaBuilder) -> {
          if (banned == null) return null;
          if (banned) {
              return criteriaBuilder.isTrue(root.get("banned"));
          } else return criteriaBuilder.isFalse(root.get("banned"));
        };
    }

    private void calculateLevel(Player player) {
        int level = (int) (Math.sqrt(2500 + (200 * player.getExperience())) - 50 ) / 100;
        player.setLevel(level);
        calculateUntilNextLevel(player);
    }

    private void calculateUntilNextLevel(Player player) {
        int until = 50 * (player.getLevel() + 1) * (player.getLevel() + 2) - player.getExperience();
        player.setUntilNextLevel(until);
    }

}
