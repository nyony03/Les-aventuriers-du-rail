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
    public String utilisationRoute(Joueur j) {
        super.utilisationRoute(j);
        ArrayList<CouleurWagon> cartesPiochees = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            cartesPiochees.add(j.getJeu().piocherCarteWagon());
        }
        boolean aCarte = false;
        String choix = ".";
        ArrayList<CouleurWagon> cartePossible = new ArrayList<>();
        ArrayList<CouleurWagon> carteEnMain = new ArrayList<>();
        carteEnMain.addAll(j.getCartesWagon());

        for (CouleurWagon carte : cartesPiochees) {
            if (carteEnMain.contains(carte) || cartesPiochees.contains(CouleurWagon.LOCOMOTIVE)) {
                if (j.nbCartesCouleur(carte)>0){
                    if(carte.equals(CouleurWagon.LOCOMOTIVE)) {

                    } else {
                        cartePossible.add(carte);
                        carteEnMain.remove(carte);
                    }
                }
            }
        }
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
        return null;
    }
}
