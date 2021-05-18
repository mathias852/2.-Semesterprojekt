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
    boolean updateTVSeries(TVSeries tvSeries);
    boolean deleteTVSeries(TVSeries tvSeries);

    //Episode
    List<Episode> getEpisodes();
    boolean storeEpisode(Episode episode);
    boolean updateEpisode(Episode episode);
    boolean deleteEpisode(Episode episode);

    //Transmission
    List<Transmission> getTransmissions();
    boolean storeTransmission(Transmission transmission);
    boolean updateTransmission(Transmission transmission);
    boolean deleteTransmission(Transmission transmission);

    //Producer
    List<Producer> getProducers();
    boolean storeProducer(Producer producer);
    boolean deleteProducer(Producer producer);

    //SystemAdmin
    List<SystemAdmin> getSystemAdmins();
    boolean storeSystemAdmin(SystemAdmin systemAdmin);
    boolean deleteSystemAdmin(SystemAdmin systemAdmin);

    //CreditedPerson
    List<CreditedPerson> getCreditedPeople();
    boolean storeCreditedPerson(CreditedPerson creditedPerson);
    boolean updateCreditedPerson(CreditedPerson creditedPerson);
    boolean deleteCreditedPerson(CreditedPerson creditedPerson);

    //Credit
    List<Credit> getCredits(Program program);
    boolean storeCredit(Program program, Credit credit);
    boolean updateCredit(Program program, Credit credit, String oldRole);
    boolean deleteCredit(Program program, Credit credit);

    //Notification
    List<Notification> getNotifications();
    boolean storeNotification(Notification notification);
    boolean updateNotification(Notification notification);
}
