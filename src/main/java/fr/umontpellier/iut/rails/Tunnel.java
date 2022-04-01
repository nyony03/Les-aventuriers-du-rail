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
    public boolean utilisationRoute(Joueur j) {
        // création d'un booléen peut passer pour gérer le changement de proprietaire dans
        boolean peutPasser = false;
        super.utilisationRoute(j);
        int nbCarteSupplementaire = 0;
        ArrayList<CouleurWagon> cartesPiochees = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            CouleurWagon cartePiochee = j.getJeu().piocherCarteWagon();
            cartesPiochees.add(cartePiochee);
            // On pioche 3 cartes et à chaque pioche on vérifie si on doit payer une carte une carte de plus ou pas
            if(cartePiochee == CouleurWagon.StringToObject(getCouleurChoisi()) || cartePiochee == CouleurWagon.LOCOMOTIVE){
                nbCarteSupplementaire ++;
            }
        }
        boolean aCarte = false;
        String choix = ".";
        ArrayList<CouleurWagon> cartePossible = new ArrayList<>();
        ArrayList<CouleurWagon> carteEnMain = new ArrayList<>();
        carteEnMain.addAll(j.getCartesWagon());
        // if pour dire il a assez de carte ou pas / s'il a assez de carte, s'il peut payer il passe les cartes, il peut faire un choix
        // et sinon on lui rend toutes les cartes dans sa carteWagon
        int nbCouleurEtLoco = Collections.frequency(j.getCartesWagon(), CouleurWagon.LOCOMOTIVE);
        //si getCouleurChoisi est différent de loco
        if(!getCouleurChoisi().equals("LOCOMOTIVE")){
            nbCouleurEtLoco += Collections.frequency(j.getCartesWagon(), CouleurWagon.StringToObject(getCouleurChoisi()));
        }
        if(nbCouleurEtLoco >= nbCarteSupplementaire){
            if(peutPasser)
                // A REMPLIR
                // On lui laisse le choix de choisir de passer ou de mettre les cartes nbCouleur+loco dans carteWagon posé
            peutPasser = true;

        }else{
          //il n'a pas assez de carte pour mettre les wagons dans le tunnel
            j.getCartesWagon().addAll(j.getCartesWagonPosees());
            peutPasser = false;
        }




//        for (CouleurWagon carte : cartesPiochees) {
//            if (carteEnMain.contains(carte) || cartesPiochees.contains(CouleurWagon.LOCOMOTIVE)) {
//                if (j.nbCartesCouleur(carte)>0){
//                    if(carte.equals(CouleurWagon.LOCOMOTIVE)) {
//                        if(j.getJeu().piocherCarteWagon() == CouleurWagon.StringToObject(getCouleurChoisi()) || ){
//
//                        }
//                    } else {
//                        cartePossible.add(carte);
//                        carteEnMain.remove(carte);
//                    }
//                }
//            }
//        }
//        if (aCarte) {
//            ArrayList<String> bouton = new ArrayList<>();
//            bouton.add("oui");
//            choix = j.choisir("Souhaitez-vous toujours prendre le tunnel ?", new ArrayList<String>(), bouton, true);
//            if (choix.equals("oui")) {
//                j.choisirCarteWagon(cartePossible, nbMemeCouleur);
//            } else {
//                j.getCartesWagon().addAll(j.getCartesWagonPosees());
//                j.getCartesWagonPosees().clear();
//            }
//
//        }
        return peutPasser;
    }
}
