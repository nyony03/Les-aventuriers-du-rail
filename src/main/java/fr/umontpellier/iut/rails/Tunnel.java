package fr.umontpellier.iut.rails;

import java.util.ArrayList;
import java.util.Collections;

public class Tunnel extends Route {
    public Tunnel(Ville ville1, Ville ville2, int longueur, CouleurWagon couleur) {
        super(ville1, ville2, longueur, couleur);
    }

    @Override
    public String toString() {
        return "[" + super.toString() + "]";
    }

    @Override
    public int utilisationRoute(Joueur j) {
        // création d'un booléen peut passer pour gérer le changement de proprietaire dans
        super.utilisationRoute(j);
        int nbCarteRajout = 0;
        int nbCarteSupplementaire = 0;
        for (int i = 0; i < 3; i++) {
            CouleurWagon cartePiochee = j.getJeu().piocherCarteWagon();
            j.log(cartePiochee.name());
            // On pioche 3 cartes et à chaque pioche on vérifie si on doit payer une carte une carte de plus ou pas
            if(cartePiochee.equals(CouleurWagon.StringToObject(getCouleurChoisi())) || cartePiochee.equals(CouleurWagon.LOCOMOTIVE)){
                nbCarteSupplementaire ++;
            }
        }
        // if pour dire il a assez de carte ou pas / s'il a assez de carte, s'il peut payer il passe les cartes, il peut faire un choix
        // et sinon on lui rend toutes les cartes dans sa carteWagon
        int nbCouleurEtLoco = Collections.frequency(j.getCartesWagon(), CouleurWagon.LOCOMOTIVE);
        //si getCouleurChoisi est différent de loco
        if(!getCouleurChoisi().equals("LOCOMOTIVE")){
            nbCouleurEtLoco += Collections.frequency(j.getCartesWagon(), CouleurWagon.StringToObject(getCouleurChoisi()));
        }
        if(nbCouleurEtLoco >= nbCarteSupplementaire){
            nbCarteRajout=nbCarteSupplementaire;
        }else{
          //il n'a pas assez de carte pour mettre les wagons dans le tunnel
            j.getCartesWagon().addAll(j.getCartesWagonPosees());
        }
        return nbCarteRajout;
    }
}
