package persistence;

import domain.Facade;
import domain.IPersistenceHandler;
import domain.Notification;
import domain.accesscontrol.Producer;
import domain.accesscontrol.SystemAdmin;
import domain.credit.Credit;
import domain.credit.CreditedPerson;
import domain.program.Episode;
import domain.program.Program;
import domain.program.TVSeries;
import domain.program.Transmission;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PersistenceHandler implements IPersistenceHandler {
    private static PersistenceHandler instance;
    private Connection connection = null;

    private PersistenceHandler() {
        initializePostgresqlDatabase();
    }

    public static PersistenceHandler getInstance() {
        if (instance == null) {
            instance = new PersistenceHandler();
        }
        return instance;
    }

    private void initializePostgresqlDatabase() {
        try {
            DriverManager.registerDriver(new org.postgresql.Driver());
            String url = "localhost";
            int port = 5432;
            connection = DriverManager.getConnection("jdbc:postgresql://" + url + ":" + port + "/" +
                            DatabaseConfig.getDatabaseName(),
                    DatabaseConfig.getUsername(),
                    DatabaseConfig.getPassword());
        } catch (SQLException | IllegalArgumentException ex) {
            ex.printStackTrace(System.err);
        } finally {
            if (connection == null) System.exit(-1);
        }
    }


    @Override
    public List<TVSeries> getTVSeries() {
        try {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM tv_series");
            ResultSet sqlReturnValues = stmt.executeQuery();
            List<TVSeries> returnValue = new ArrayList<>();
            while (sqlReturnValues.next()) {
                returnValue.add(new TVSeries(
                        UUID.fromString(sqlReturnValues.getString(1)),
                        sqlReturnValues.getString(2),
                        sqlReturnValues.getString(3),
                        UUID.fromString(sqlReturnValues.getString(4))));
            }
            return returnValue;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean storeTVSeries(TVSeries tvSeries) {
        try {
            PreparedStatement insertStatement = connection.prepareStatement(
                    "INSERT INTO tv_series (id, name, description, createdById) " +
                            "VALUES (?,?,?,?)");
            insertStatement.setObject(1, tvSeries.getUuid());
            insertStatement.setString(2, tvSeries.getName());
            insertStatement.setString(3, tvSeries.getDescription());
            insertStatement.setObject(4, tvSeries.getCreatedBy());
            insertStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public boolean updateTVSeries(TVSeries tvSeries) {
        try {
            PreparedStatement insertStatement = connection.prepareStatement(
                    "UPDATE tv_series SET name = ?, description = ? WHERE id = ?");
            insertStatement.setString(1, tvSeries.getName());
            insertStatement.setString(2, tvSeries.getDescription());
            insertStatement.setObject(3, tvSeries.getUuid());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public boolean deleteTVSeries(TVSeries tvSeries) {
        try {
            //For-loop that deletes all episodes and its credits that connected to a given tvSeries
            for (Integer i : tvSeries.getSeasonMap().keySet()) {
                for (Episode episode : tvSeries.getSeasonMap().get(i)) {
                    deleteEpisode(episode);
                }
            }
            PreparedStatement stmt = connection.prepareStatement("DELETE FROM tv_series WHERE id = ?");
            stmt.setObject(1, tvSeries.getUuid());

            return stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<Episode> getEpisodes() {
        try {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM episodesView");
            ResultSet sqlReturnValues = stmt.executeQuery();
            List<Episode> returnValue = new ArrayList<>();
            while (sqlReturnValues.next()) {

                String tvSeriesId = sqlReturnValues.getString(2);

                //Using getTVSeries()-function from this class
                for (TVSeries tvSeries : getTVSeries()) {
                    if (tvSeries.getUuid().equals(UUID.fromString(tvSeriesId))) {
                        //To get the production name based on the production resultSet
                        PreparedStatement stmt2 = connection.prepareStatement("SELECT name FROM productions WHERE id = ?");
                        stmt2.setInt(1, sqlReturnValues.getInt(10));
                        ResultSet sqlReturnValues2 = stmt2.executeQuery();

                        while (sqlReturnValues2.next()) {

                            returnValue.add(new Episode(
                                    UUID.fromString(sqlReturnValues.getString(1)),
                                    tvSeries,
                                    sqlReturnValues.getString(3),
                                    sqlReturnValues.getString(4),
                                    UUID.fromString(sqlReturnValues.getString(5)),
                                    sqlReturnValues.getInt(6),
                                    sqlReturnValues.getInt(7),
                                    sqlReturnValues.getInt(8),
                                    sqlReturnValues.getBoolean(9),
                                    //To get the production name based on the production resultSet
                                    sqlReturnValues2.getString(1)));
                        }
                    }
                }
            }
            return returnValue;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean storeEpisode(Episode episode) {
        try {
            PreparedStatement stmt = connection.prepareStatement(
                    "INSERT INTO programs (id, name, description, createdById, duration, approved, production) VALUES (?,?,?,?,?,?,?)");
            stmt.setObject(1, episode.getUuid());
            stmt.setString(2, episode.getName());
            stmt.setString(3, episode.getDescription());
            stmt.setObject(4, episode.getCreatedBy());
            stmt.setInt(5, episode.getDuration());
            stmt.setBoolean(6, episode.isApproved());
            stmt.setInt(7, getProductionId(episode.getProduction()));

            PreparedStatement stmt1 = connection.prepareStatement(
                    "INSERT INTO episodes (programsId, tvSeriesId, episodeNo, seasonNo) VALUES (?,?,?,?)");
            stmt1.setObject(1, episode.getUuid());
            stmt1.setObject(2, episode.getTvSeries().getUuid());
            stmt1.setInt(3, episode.getEpisodeNo());
            stmt1.setInt(4, episode.getSeasonNo());

            stmt.execute();
            stmt1.execute();

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean updateEpisode(Episode episode) {
        try {
            PreparedStatement stmt = connection.prepareStatement(
                    "UPDATE programs SET name = ?, description = ?, duration = ?, approved = ?, production = ? WHERE id = ?");
            stmt.setString(1, episode.getName());
            stmt.setString(2, episode.getDescription());
            stmt.setInt(3, episode.getDuration());
            stmt.setBoolean(4, episode.isApproved());
            stmt.setInt(5, getProductionId(episode.getProduction()));
            stmt.setObject(6, episode.getUuid());

            PreparedStatement stmt1 = connection.prepareStatement(
                    "UPDATE episodes SET episodeNo = ?, seasonNo = ? WHERE programsId = ?");
            stmt1.setInt(1, episode.getEpisodeNo());
            stmt1.setInt(2, episode.getSeasonNo());
            stmt1.setObject(3, episode.getUuid());

            stmt.execute();
            stmt1.execute();

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deleteEpisode(Episode episode) {
        //Note: id, programId & programsId have the same ID
        try {
            PreparedStatement stmt1 = connection.prepareStatement("DELETE FROM credits WHERE programid = ?");
            stmt1.setObject(1, episode.getUuid());

            PreparedStatement stmt2 = connection.prepareStatement("DELETE FROM episodes WHERE programsId = ?");
            stmt2.setObject(1, episode.getUuid());

            PreparedStatement stmt3 = connection.prepareStatement("DELETE FROM programs WHERE id = ?");
            stmt3.setObject(1, episode.getUuid());

            stmt1.execute();
            stmt2.execute();
            stmt3.execute();

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<Transmission> getTransmissions() {
        try {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM transmissionsView");
            ResultSet sqlReturnValues = stmt.executeQuery();
            List<Transmission> returnValue = new ArrayList<>();
            while (sqlReturnValues.next()) {
                PreparedStatement stmt2 = connection.prepareStatement("SELECT name FROM productions WHERE id = ?");
                stmt2.setInt(1, sqlReturnValues.getInt(7));
                ResultSet sqlReturnValues2 = stmt2.executeQuery();

                while (sqlReturnValues2.next()) {

                    returnValue.add(new Transmission(
                            UUID.fromString(sqlReturnValues.getString(1)),
                            sqlReturnValues.getString(2),
                            sqlReturnValues.getString(3),
                            UUID.fromString(sqlReturnValues.getString(4)),
                            sqlReturnValues.getInt(5),
                            sqlReturnValues.getBoolean(6),
                            sqlReturnValues2.getString(1))); //This line for production
                }
            }
            return returnValue;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean storeTransmission(Transmission transmission) {
        try {
            PreparedStatement stmt2 = connection.prepareStatement(
                    "INSERT INTO programs (id, name, description, createdById, duration, approved, production) VALUES (?,?,?,?,?,?,?)");
            stmt2.setObject(1, transmission.getUuid());
            stmt2.setString(2, transmission.getName());
            stmt2.setString(3, transmission.getDescription());
            stmt2.setObject(4,transmission.getCreatedBy());
            stmt2.setInt(5, transmission.getDuration());
            stmt2.setBoolean(6, transmission.isApproved());
            stmt2.setInt(7, getProductionId(transmission.getProduction()));

            PreparedStatement stmt = connection.prepareStatement(
                    "INSERT INTO transmissions (programsid) VALUES (?) ");
            stmt.setObject(1, transmission.getUuid());
            stmt2.execute();
            stmt.execute();

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean updateTransmission(Transmission transmission) {
        try {
            PreparedStatement stmt = connection.prepareStatement(
                    "UPDATE programs SET name = ?, description = ?, duration = ?, approved = ?, production = ? WHERE id = ?");
            stmt.setString(1, transmission.getName());
            stmt.setString(2, transmission.getDescription());
            stmt.setInt(3, transmission.getDuration());
            stmt.setBoolean(4, transmission.isApproved());
            stmt.setInt(5, getProductionId(transmission.getProduction()));
            stmt.setObject(6, transmission.getUuid());
            stmt.execute();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deleteTransmission(Transmission transmission) {
        try {
            PreparedStatement stmt = connection.prepareStatement("DELETE FROM credits WHERE programid = ?");
            stmt.setObject(1, transmission.getUuid());

            PreparedStatement stmt2 = connection.prepareStatement("DELETE FROM transmissions WHERE programsid = ?");
            stmt2.setObject(1, transmission.getUuid());

            PreparedStatement stmt3 = connection.prepareStatement("DELETE FROM programs WHERE id = ?");
            stmt3.setObject(1, transmission.getUuid());

            stmt.execute();
            stmt2.execute();
            stmt3.execute();

            return true;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    @Override
    public List<Producer> getProducers() {
        try {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM users WHERE type='Producer'");
            ResultSet sqlReturnValues = stmt.executeQuery();
            List<Producer> returnValue = new ArrayList<>();
            while (sqlReturnValues.next()) {
                returnValue.add(new Producer(
                        UUID.fromString(sqlReturnValues.getString(1)),
                        sqlReturnValues.getString(2),
                        sqlReturnValues.getInt(3)
                ));
            }
            return returnValue;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean storeProducer(Producer producer) {
        try {
            PreparedStatement stmt = connection.prepareStatement("INSERT INTO users (id, username, password, type) VALUES (?,?,?,?) ");
            stmt.setObject(1, producer.getUuid());
            stmt.setString(2, producer.getUsername());
            stmt.setInt(3, producer.getHashedPassword());
            stmt.setString(4, "Producer");

            return stmt.execute();
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deleteProducer(Producer producer) {
        try {
            PreparedStatement stmt = connection.prepareStatement("DELETE FROM users WHERE id = ?");
            stmt.setObject(1, producer.getUuid());
            return stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<SystemAdmin> getSystemAdmins() {
        try {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM users WHERE type='Admin'");
            ResultSet sqlReturnValues = stmt.executeQuery();
            List<SystemAdmin> returnValue = new ArrayList<>();
            while (sqlReturnValues.next()) {
                returnValue.add(new SystemAdmin(
                        UUID.fromString(sqlReturnValues.getString(1)),
                        sqlReturnValues.getString(2),
                        sqlReturnValues.getInt(3)
                ));
            }
            return returnValue;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean storeSystemAdmin(SystemAdmin systemAdmin) {
        try {
            PreparedStatement stmt = connection.prepareStatement("INSERT INTO users (id, username, password, type) VALUES (?,?,?,?) ");
            stmt.setObject(1, systemAdmin.getUuid());
            stmt.setString(2, systemAdmin.getUsername());
            stmt.setInt(3, systemAdmin.getHashedPassword());
            stmt.setString(4, "Admin");

            return stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deleteSystemAdmin(SystemAdmin systemAdmin) {
        try {
            PreparedStatement stmt = connection.prepareStatement("DELETE FROM users WHERE id = ?");
            stmt.setObject(1, systemAdmin.getUuid());

            return stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<CreditedPerson> getCreditedPeople() {
        try {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM creditedPeople");
            ResultSet sqlReturnValues = stmt.executeQuery();
            List<CreditedPerson> returnValue = new ArrayList<>();
            while (sqlReturnValues.next()) {
                returnValue.add(new CreditedPerson(
                        UUID.fromString(sqlReturnValues.getString(1)),
                        sqlReturnValues.getString(2)));
            }

            return returnValue;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean storeCreditedPerson(CreditedPerson creditedPerson) {

        try {
            PreparedStatement stmt = connection.prepareStatement("INSERT INTO creditedPeople (id, name) VALUES (?,?) ");
            stmt.setObject(1, creditedPerson.getUuid());
            stmt.setString(2, creditedPerson.getName());

            return stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean updateCreditedPerson(CreditedPerson creditedPerson) {
        try {
            PreparedStatement stmt = connection.prepareStatement(
                    "UPDATE creditedPeople SET name = ? WHERE id = ?");
            stmt.setString(1, creditedPerson.getName());
            stmt.setObject(2, creditedPerson.getUuid());

            return stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deleteCreditedPerson(CreditedPerson creditedPerson) {
        try {
            PreparedStatement stmt = connection.prepareStatement("DELETE FROM credits WHERE creditedPersonId = ?");
            stmt.setObject(1, creditedPerson.getUuid());

            PreparedStatement stmt2 = connection.prepareStatement("DELETE FROM creditedPeople WHERE id = ?");
            stmt2.setObject(1, creditedPerson.getUuid());
            stmt.execute();
            stmt2.execute();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<Credit> getCredits(Program program) {
        try {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM credits WHERE programId = ?");
            //stmt.setString(1, program.getUuid().toString());
            stmt.setObject(1, program.getUuid());
            ResultSet sqlReturnValues = stmt.executeQuery();
            List<Credit> returnValue = new ArrayList<>();
            while (sqlReturnValues.next()) {
                //Select a creditedPersonsId from the credits-table
                //String creditedPersonID = sqlReturnValues.getString(1);
                UUID creditedPersonID =  sqlReturnValues.getObject(1, java.util.UUID.class);
                for (CreditedPerson cp : getCreditedPeople()) { //Using getCreditedPeople()-function from this class
                    if (cp.getUuid().equals(creditedPersonID)) {
                        returnValue.add(new Credit(cp, Facade.getInstance().getFunction(sqlReturnValues.getString(2))));
                    }
                }
            }
            return returnValue;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean storeCredit(Program program, Credit credit) {
        try {
            PreparedStatement stmt = connection.prepareStatement("INSERT INTO credits (creditedPersonId, role, programId) VALUES (?,?,?)");
            stmt.setObject(1, credit.getCreditedPerson().getUuid());
            stmt.setString(2, credit.getFunction().role);
            stmt.setObject(3, program.getUuid());

            return stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean updateCredit(Program program, Credit credit, String oldRole) {
        try {
            PreparedStatement stmt = connection.prepareStatement(
                    "UPDATE credits SET role = ? WHERE creditedPersonId = ? AND programid = ? AND role = ?");
            stmt.setString(1, credit.getFunction().role);
            stmt.setObject(2, credit.getCreditedPerson().getUuid());
            stmt.setObject(3, program.getUuid());
            stmt.setString(4, oldRole);

            PreparedStatement stmt1 = connection.prepareStatement(
                    "UPDATE programs SET approved = false WHERE id = ?");
            stmt1.setObject(1, program.getUuid());

            stmt.execute();
            stmt1.execute();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deleteCredit(Program program, Credit credit) {
        try {
            PreparedStatement stmt = connection.prepareStatement("DELETE FROM credits WHERE creditedpersonid = ? AND role = ? AND programid = ?");
            stmt.setObject(1, credit.getCreditedPerson().getUuid());
            stmt.setString(2, credit.getFunction().role);
            stmt.setObject(3, program.getUuid());

            return stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<Notification> getNotifications() {
        try {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM notifications");
            ResultSet sqlReturnValues = stmt.executeQuery();
            List<Notification> returnValue = new ArrayList<>();
            while (sqlReturnValues.next()) {

                Notification notification = new Notification(
                        sqlReturnValues.getString(2));
                notification.setSeen(sqlReturnValues.getBoolean(3));
                returnValue.add(notification);
            }
            return returnValue;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean storeNotification(Notification notification) {
        try {
            PreparedStatement stmt = connection.prepareStatement("INSERT INTO notifications (title, seen) VALUES (?, ?)");
            stmt.setString(1, notification.getTitle());
            stmt.setBoolean(2, notification.getSeen());
            return stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean updateNotification(Notification notification) {
        try {
            PreparedStatement stmt = connection.prepareStatement(
                    "UPDATE notifications SET seen = ? WHERE title = ?");
            stmt.setBoolean(1, notification.getSeen());
            stmt.setString(2, notification.getTitle());
            return stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private int getProductionId(String name) {
        int productionId = 0;
        try {
            PreparedStatement productionStmt = connection.prepareStatement("SELECT id FROM productions WHERE name = ?");
            productionStmt.setString(1, name);
            ResultSet productionResultSet = productionStmt.executeQuery();
            while (productionResultSet.next()) {
                productionId = productionResultSet.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return productionId;
    }
}