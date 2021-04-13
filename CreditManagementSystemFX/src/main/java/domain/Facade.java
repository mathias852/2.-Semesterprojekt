package domain;

public class Facade {

    public static void main(String[] args) {

        CreditedPerson JensJensen = new CreditedPerson("Jens Jensen", 1);
        CreditedPerson Jakobjakob = new CreditedPerson("Jakob", 2);
        Credit c1 = new Credit(JensJensen, Credit.Function.VISUALARTIST);
        Credit c2 = new Credit(Jakobjakob, Credit.Function.CARTOON);

        Program p1 = new Episode("pilot", 1, 1);

        p1.addCredit(c1);
        p1.addCredit(c2);

        TVSeries badehotellet = new TVSeries("badehotellet");
        badehotellet.addEpisode((Episode) p1);



    }

}
