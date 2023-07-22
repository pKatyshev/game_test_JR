package com.game.service;

import com.game.config.AppConfig;
import com.game.config.MyWebAppInit;
import com.game.config.WebConfig;
import com.game.entity.Player;
import com.game.repository.PlayerRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;


@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("dev")
@ContextConfiguration(classes = {AppConfig.class, MyWebAppInit.class, WebConfig.class})
@WebAppConfiguration
public class PlayerServiceTest {

    @Autowired
    private PlayerService playerService;


    @Test
    public void test() {
        Player player = playerService.findOne(10);
        System.out.println(player);
        Assert.assertEquals("Архилл", player.getName());
    }

    @Test
    public void getCountPlayers() {
        int trueCount = playerService.findAll().size();
        int countPlayers = playerService.getCountPlayers();

        System.out.println(trueCount);
        System.out.println(countPlayers);

        Assert.assertEquals(trueCount, countPlayers);
    }
}
