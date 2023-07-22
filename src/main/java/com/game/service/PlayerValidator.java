package com.game.service;

import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;
import com.game.util.PlayerValidationException;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;

@Component
public class PlayerValidator {

    public void validate(Player player) {
        checkName(player.getName());
        checkTitle(player.getTitle());
        checkRace(player.getRace());
        checkProfession(player.getProfession());
        checkBirthday(player.getBirthday());
        checkExperience(player.getExperience());
    }

    public void checkName(String name) {
        if(name == null || name.length() > 12 || name.equals("")) {
            throw new PlayerValidationException("Name is not valid");
        }
    }

    public void checkTitle(String title) {
        if(title == null || title.length() > 30) {
            throw new PlayerValidationException("Title is not valid");
        }
    }

    public void checkRace(Race race) {
        if (race == null) {
            throw new PlayerValidationException("race cannot be empty");
        }
    }

    public void checkProfession(Profession profession) {
        if (profession == null) {
            throw new PlayerValidationException("profession cannot be empty");
        }
    }

    public void checkExperience(int experience) {
        if (experience < 0 || experience > 10000000) {
            throw new PlayerValidationException("Experience is not valid");
        }
    }

    public void checkBirthday(Date birthday) {
        if (birthday.before(new Date(1999-1900, Calendar.DECEMBER, 31))) {
            throw new PlayerValidationException("1Date is not valid");
        }

        if (birthday.after(new Date(3000-1900, Calendar.DECEMBER, 31))) {
            throw new PlayerValidationException("2Date is not valid");
        }
    }

}
