package domain;

import domain.accesscontrol.Producer;
import domain.accesscontrol.SystemAdmin;
import domain.credit.Credit;
import domain.credit.CreditedPerson;
import domain.program.Episode;
import domain.program.Program;
import domain.program.TVSeries;
import domain.program.Transmission;

import java.util.List;
import java.util.UUID;

public interface IPersistenceHandler {
    //TVSeries
    List<TVSeries> getTVSeries();
    boolean storeTVSeries(TVSeries tvSeries);
    boolean updateTVSeries(UUID id, String name, String description);
    boolean deleteTVSeries(UUID id);

    //Episode
    List<Episode> getEpisodes();
    boolean storeEpisode(Program program, Episode episode);
    boolean updateEpisode(Program program, Episode episode);
    boolean deleteEpisode(UUID id);

    //Transmission
    List<Transmission> getTransmissions();
    boolean storeTransmission(Transmission transmission);
    boolean updateTransmission(Transmission transmission);
    boolean deleteTransmission(UUID id);

    //Producer
    List<Producer> getProducers();
    boolean storeProducer(Producer producer);
    boolean deleteProducer(UUID id);

    //SystemAdmin
    List<SystemAdmin> getSystemAdmins();
    boolean storeSystemAdmin(SystemAdmin systemAdmin);
    boolean deleteSystemAdmin(UUID id);

    //CreditedPerson
    List<CreditedPerson> getCreditedPeople();
    boolean storeCreditedPerson(CreditedPerson creditedPerson);
    boolean updateCreditedPerson(UUID id);
    boolean deleteCreditedPerson(UUID id);

    //Credit
    List<Credit> getCredits(UUID uuid);
    boolean storeCredit(Program program, Credit credit);
    boolean updateCredit(UUID programID, UUID personID, String oldRole, String newRole);
    boolean deleteCredit(Program program, Credit credit);
}
