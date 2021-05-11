package persistence;

import domain.Facade;
import domain.IPersistenceHandler;
import domain.accesscontrol.Producer;
import domain.accesscontrol.SystemAdmin;
import domain.credit.Credit;
import domain.credit.Credit.Function;
import domain.credit.CreditedPerson;
import domain.program.Program;
import domain.program.Episode;
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
                        sqlReturnValues.getString(3),
                        sqlReturnValues.getString(4),
                        UUID.fromString(sqlReturnValues.getString(2))));
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
                    "INSERT INTO tv_series (id, createdById, name, description) " +
                            "VALUES (?,?,?,?)");
            insertStatement.setString(1, String.valueOf(tvSeries.getUuid()));
            insertStatement.setString(2, String.valueOf(tvSeries.getCreatedBy()));
            insertStatement.setString(3, tvSeries.getName());
            insertStatement.setString(4, tvSeries.getDescription());
            insertStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public boolean updateTVSeries(UUID id, String name, String description) {
        try {
            PreparedStatement insertStatement = connection.prepareStatement(
                    "UPDATE tv_series SET name = ?, description = ? WHERE id = ?");
            insertStatement.setString(1, name);
            insertStatement.setString(2, description);
            insertStatement.setString(3, String.valueOf(id));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public boolean deleteTVSeries(UUID id) {
        try {
            PreparedStatement stmt = connection.prepareStatement("DELETE FROM tv_series WHERE id = ?");
            stmt.setString(1, String.valueOf(id));
            return stmt.execute();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
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
                for(TVSeries tvSeries : getTVSeries()){
                    if(tvSeries.getUuid().equals(UUID.fromString(tvSeriesId))){
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
                                sqlReturnValues.getString(10)));
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
    public boolean storeEpisode(Program program, Episode episode) {
        try{
            PreparedStatement stmt = connection.prepareStatement(
                    "INSERT INTO programs (id, name, description, createdById, duration, approved, production) VALUES (?,?,?,?,?,?,?)");
            stmt.setString(1, String.valueOf(program.getUuid()));
            stmt.setString(2, program.getName());
            stmt.setString(3, program.getDescription());
            stmt.setString(4, String.valueOf(program.getCreatedBy()));
            stmt.setInt(5, program.getDuration());
            stmt.setBoolean(6, program.isApproved());
            stmt.setString(7, program.getProduction());

            PreparedStatement stmt1 = connection.prepareStatement(
                    "INSERT INTO episodes (programsId, tvSeriesId, episodeNo, seasonNo) VALUES (?,?,?,?)");
            stmt1.setString(1, String.valueOf(program.getUuid()));
            stmt1.setString(2, String.valueOf(episode.getTvSeries().getUuid()));
            stmt1.setInt(3, episode.getEpisodeNo());
            stmt1.setInt(4, episode.getSeasonNo());

            return stmt.execute() && stmt1.execute();
        } catch (SQLException e){
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean updateEpisode(Program program, Episode episode) {
        try{
            PreparedStatement stmt = connection.prepareStatement(
                    "UPDATE programs SET name = ?, description = ?, duration = ?, approved = ?, production = ? WHERE id = ?");
            stmt.setString(1, program.getName());
            stmt.setString(2, program.getDescription());
            stmt.setInt(3, program.getDuration());
            stmt.setBoolean(4, program.isApproved());
            stmt.setString(5, program.getProduction());
            stmt.setString(6, String.valueOf(program.getUuid()));

            PreparedStatement stmt1 = connection.prepareStatement(
                    "UPDATE episodes SET episodeNo = ?, seasonNo = ? WHERE programsId = ?");
            stmt1.setInt(1, episode.getEpisodeNo());
            stmt1.setInt(2, episode.getSeasonNo());
            stmt1.setString(3, String.valueOf(program.getUuid()));

            return stmt.execute() && stmt1.execute();
        } catch (SQLException e){
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deleteEpisode(UUID id) {
        try {
            PreparedStatement stmt = connection.prepareStatement("DELETE FROM episodes WHERE programsId = ?");
            stmt.setString(1, String.valueOf(id));

            PreparedStatement stmt1 = connection.prepareStatement("DELETE FROM programs WHERE id = ?");
            stmt1.setString(1, String.valueOf(id));

            return stmt.execute() && stmt.execute();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
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
                returnValue.add(new Transmission(
                        UUID.fromString(sqlReturnValues.getString(1)),
                        sqlReturnValues.getString(2),
                        sqlReturnValues.getString(3),
                        UUID.fromString(sqlReturnValues.getString(4)),
                        sqlReturnValues.getInt(5),
                        sqlReturnValues.getBoolean(6),
                        sqlReturnValues.getString(7))); //This line for production
            }
            return returnValue;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean storeTransmission(Transmission transmission) {
        try{
            PreparedStatement stmt = connection.prepareStatement(
                    "INSERT INTO transmissions (programsid) VALUES (?) ");
            stmt.setString(1, String.valueOf(transmission.getUuid()));
            return stmt.execute();

        } catch (SQLException e){
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean updateTransmission(Transmission transmission) {
        try{

            PreparedStatement stmt = connection.prepareStatement(
                    "UPDATE programs SET name = ?, description = ?, duration = ?, approved = ?, production = ? WHERE id = ?");
            stmt.setString(1, transmission.getName());
            stmt.setString(2, transmission.getDescription());
            stmt.setInt(3, transmission.getDuration());
            stmt.setBoolean(4, transmission.isApproved());
            stmt.setString(5, transmission.getProduction());
            stmt.setString(6, String.valueOf(transmission.getUuid()));

            return stmt.execute();
        } catch (SQLException e){
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deleteTransmission(UUID id) {
        try {
            PreparedStatement stmt = connection.prepareStatement("DELETE FROM transmissions WHERE programsid = ?");
            stmt.setString(1, String.valueOf(id));

            PreparedStatement stmt1 = connection.prepareStatement("DELETE FROM programs WHERE id = ?");
            stmt1.setString(1, String.valueOf(id));

            return stmt.execute();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
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
                        sqlReturnValues.getString(3),
                        sqlReturnValues.getInt(4)));
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
            stmt.setString(1, String.valueOf(producer.getUuid()));
            stmt.setString(2, producer.getUsername());
            stmt.setString(3, String.valueOf(producer.getHashedPassword()));
            stmt.setString(4, "Producer"); //GOT NO IDEA IF THIS WORKS TBH ______________________________
            return stmt.execute();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deleteProducer(UUID id) {
        try {
            PreparedStatement stmt = connection.prepareStatement("DELETE FROM users WHERE id = ?");
            stmt.setString(1, String.valueOf(id));
            return stmt.execute();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
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
                        Integer.parseInt(sqlReturnValues.getString(3))));
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
            stmt.setString(1, String.valueOf(systemAdmin.getUuid()));
            stmt.setString(2, systemAdmin.getUsername());
            stmt.setString(3, String.valueOf(systemAdmin.getHashedPassword()));
            stmt.setString(4, "Admin");
            return stmt.execute();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deleteSystemAdmin(UUID id) {
        try {
            PreparedStatement stmt = connection.prepareStatement("DELETE FROM users WHERE id = ?");
            stmt.setString(1, String.valueOf(id));
            return stmt.execute();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
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
            PreparedStatement stmt = connection.prepareStatement("INSERT INTO users (id, username) VALUES (?,?) ");
            stmt.setString(1, String.valueOf(creditedPerson.getUuid()));
            stmt.setString(2, creditedPerson.getName());
            return stmt.execute();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean updateCreditedPerson(UUID id) {
        return false;
    }

    @Override
    public boolean deleteCreditedPerson(UUID id) {
        try {
            PreparedStatement stmt = connection.prepareStatement("DELETE FROM creditedPeople WHERE id = ?");
            stmt.setString(1, String.valueOf(id));
            return stmt.execute();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return false;
        }
    }

    @Override
    public List<Credit> getCredits(UUID uuid) {
        try {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM credits WHERE programId = ?");
            stmt.setString(1, String.valueOf(uuid));
            ResultSet sqlReturnValues = stmt.executeQuery();
            List<Credit> returnValue = new ArrayList<>();
            while (sqlReturnValues.next()) {
                String creditedPersonID = sqlReturnValues.getString(1);
                for(CreditedPerson cp : getCreditedPeople()){ //Using getCreditedPeople()-function from this class
                    if(cp.getUuid().equals(UUID.fromString(creditedPersonID))){
                        returnValue.add(new Credit(cp, Function.valueOf(sqlReturnValues.getString(2))));
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
            stmt.setString(1, String.valueOf(credit.getCreditedPerson().getUuid()));
            stmt.setString(2, credit.getFunction().role);
            stmt.setString(3, String.valueOf(program.getUuid()));
            return stmt.execute();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean updateCredit(UUID programID, UUID personID, String oldRole, String newRole) {
        return false;
    }

    @Override
    public boolean deleteCredit(Program program, Credit credit) {
        try {
            PreparedStatement stmt = connection.prepareStatement("DELETE FROM credits WHERE creditedpersonid = ? AND role = ? AND programid = ?");
            stmt.setString(1, String.valueOf(credit.getCreditedPerson().getUuid()));
            stmt.setString(2, credit.getFunction().role);
            stmt.setString(3, String.valueOf(program.getUuid()));
            return stmt.execute();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return false;
        }
    }
}
