package domain;

import java.util.ArrayList;

public class Credit {

    private CreditedPerson creditedPerson;
    private Function function;

    public Credit(CreditedPerson creditedPerson, Function function) {
        this.creditedPerson = creditedPerson;
        this.function = function;
    }

    enum Function {
        VISUALARTIST("Billedkunstnere"),
        PICTUREANDSOUND("Billed- og lydredigering"),
        CASTING("Casting"),
        COLOURGRADING("Colourgrading"),
        CHOIRDIRECTOR("Dirigenter"),
        DRONE("Dronefører"),
        PUPPETMASTER("Dukkefører"),
        PUPPETCREATOR("Produktion af dukker"),
        NARRATOR("Fortæller"),
        PHOTOGRAPHER("Fotografer"),
        PUBLISHER("Efter ide af"),
        GRAPHICALDESIGNER("Grafiske designere"),
        VOICEACTOR("Indtalere"),
        ORCHESTRADIRECTOR("Kapelmester"),
        MOVIEEDITOR("Klipper"),
        CONCEPT("Koncept"),
        CONSULTANT("Konsulent"),
        CHOIR("Kor"),
        CHOREOGRAPHY("Koreografi"),
        SOUNDENGINEER("Lyd eller tonemester"),
        SOUNDEDITOR("Lydredigering"),
        LIGHT("Lys"),
        CAST("Cast"),
        MUSICALARRANGEMENT("Musikalsk arrangement"),
        ORCHESTRA("Orkester"),
        TRANSLATOR("Oversætter"),
        EXTERNALPRODUCER("Producent"),
        PRODUCER("Producer"),
        PRODUCTIONCOORDINATOR("Produktionskoordinator"),
        ORGANIZER("Tilrettelæggelse"),
        EDITOR("Redaktøren"),
        PROPS("Rekvisitør"),
        SETDESIGNER("Scenograf"),
        SCRIPTER("Scripter"),
        SFX("Speciel effects"),
        SPONSOR("Sponsorer"),
        CARTOON("Tegnefilm eller animation"),
        CLOSEDCAPTIONS("Danske undertekster"),
        TEXTANDMUSIC("Tekst og musik"),
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


