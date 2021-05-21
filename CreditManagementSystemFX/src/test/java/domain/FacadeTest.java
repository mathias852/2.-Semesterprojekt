package domain;

import domain.credit.Credit;
import domain.credit.CreditedPerson;
import domain.program.Episode;
import domain.program.Program;
import domain.program.TVSeries;
import org.junit.*;

import java.util.UUID;

import static org.junit.Assert.*;


public class FacadeTest {
    Facade facade;
    LoginHandler loginHandler;

    @Before
    public void setUp() {
        facade = Facade.getInstance();
        facade.importFromDatabase();
        loginHandler = LoginHandler.getInstance();
        loginHandler.importLogins();
        loginHandler.setCurrentUser(loginHandler.getUsers().get(0));
    }

    @Test
    public void createEpisode() {
        UUID episodeUuid = UUID.randomUUID();
        UUID tvUuid = UUID.randomUUID();
        UUID personUuid = loginHandler.getUsers().get(0).getUuid();
        Episode episode = new Episode(episodeUuid, new TVSeries(tvUuid, "TestTv", "TestTvDescription", personUuid),
                "TestEpisode", "TestEpisodeDescription", personUuid, 1, 1, 120, false, "TV2");

        facade.createTvSeries(tvUuid, "TestTv", "TestTvDescription", personUuid);
        facade.createEpisode(episodeUuid, facade.getTvSeriesFromUuid(tvUuid),
                "TestEpisode", "TestEpisodeDescription", personUuid, 1, 1, 120, false, "TV2");
        assertEquals(episode.getName(), facade.getTvSeriesFromUuid(tvUuid).getSeasonMap().get(1).get(0).getName());
        assertEquals(episode.getDescription(), facade.getTvSeriesFromUuid(tvUuid).getSeasonMap().get(1).get(0).getDescription());
        assertEquals(episode.getCreatedBy(), facade.getTvSeriesFromUuid(tvUuid).getSeasonMap().get(1).get(0).getCreatedBy());
        assertEquals(episode.getEpisodeNo(), facade.getTvSeriesFromUuid(tvUuid).getSeasonMap().get(1).get(0).getEpisodeNo());
        assertEquals(episode.getSeasonNo(), facade.getTvSeriesFromUuid(tvUuid).getSeasonMap().get(1).get(0).getSeasonNo());
    }

    @Test
    public void approveProgram() {
        facade.approveProgram(facade.getPrograms().get(0));
        assertTrue(facade.getPrograms().get(0).isApproved());
    }

    @Test
    public void updateCredit() {
        UUID creditedPersonUuid = UUID.randomUUID();
        Program episode = facade.getProgramFromCreatedBy("TestEpisode", loginHandler.getUsers().get(0).getUuid());
        facade.createPerson(creditedPersonUuid, "TestPerson");
        facade.createCredit(facade.getPersonFromUuid(creditedPersonUuid), Credit.Function.VISUALARTIST, episode);
        Credit credit = episode.getCredits().get(0);
        facade.updateCredit(credit, Credit.Function.CAST);
        assertTrue(credit.getFunction() == Credit.Function.CAST);
    }
}