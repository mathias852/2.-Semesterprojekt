package domain.credit;

import java.util.UUID;

public class Credit {

    private CreditedPerson creditedPerson;
    private Function function;

    public Credit(CreditedPerson creditedPerson, Function function) {
        this.creditedPerson = creditedPerson;
        this.function = function;
    }

    //Enum that contains all of the "possible" functions/roles for a credit
    public enum Function {
        VISUALARTIST("Billedkunstnere"),
        PICTUREANDSOUND("Billed- og lydredigering"),
        CASTING("Casting"),
        CAST("Cast"),
        COLOURGRADING("Colourgrading"),
        CLOSEDCAPTIONS("Danske undertekster"),
        CHOIRDIRECTOR("Dirigenter"),
        DRONE("Dronefører"),
        PUPPETMASTER("Dukkefører"),
        PUBLISHER("Efter ide af"),
        NARRATOR("Fortæller"),
        PHOTOGRAPHER("Fotografer"),
        GRAPHICALDESIGNER("Grafiske designere"),
        VOICEACTOR("Indtalere"),
        ORCHESTRADIRECTOR("Kapelmester"),
        MOVIEEDITOR("Klipper"),
        CONCEPT("Koncept"),
        CONSULTANT("Konsulent"),
        CHOIR("Kor"),
        CHOREOGRAPHY("Koreografi"),
        SOUNDEDITOR("Lydredigering"),
        SOUNDENGINEER("Lyd eller tonemester"),
        LIGHT("Lys"),
        MUSICALARRANGEMENT("Musikalsk arrangement"),
        ORCHESTRA("Orkester"),
        TRANSLATOR("Oversætter"),
        EXTERNALPRODUCER("Producent"),
        PRODUCER("Producer"),
        PRODUCTIONCOORDINATOR("Produktionskoordinator"),
        PUPPETCREATOR("Produktion af dukker"),
        EDITOR("Redaktøren"),
        PROPS("Rekvisitør"),
        SETDESIGNER("Scenograf"),
        SCRIPTER("Scripter"),
        SFX("Speciel effects"),
        SPONSOR("Sponsorer"),
        CARTOON("Tegnefilm eller animation"),
        TEXTANDMUSIC("Tekst og musik"),
        ORGANIZER("Tilrettelæggelse"),
        EXTRAORDINARYEFFORT("Uhonoreret og ekstraordinær indsats");


        public final String role;
        Function(String role) {
            this.role = role;
        }
    }
    public CreditedPerson getCreditedPerson() {
        return creditedPerson;
    }
    public void setCreditedPerson(CreditedPerson creditedPerson) {
        this.creditedPerson = creditedPerson;
    }
    public Function getFunction() {
        return function;
    }
    public void setFunction(Function function) {
        this.function = function;
    }


    @Override
    public String toString() {
        return creditedPerson.getName() + " " + function.role;
    }
}


