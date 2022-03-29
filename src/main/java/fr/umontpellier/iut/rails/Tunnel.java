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
    public void utilisationRoute(Joueur j) {
        super.utilisationRoute(j);
        ArrayList<CouleurWagon> cartesPiochees = new ArrayList<>();
        for(int i = 0; i<3; i++){
            cartesPiochees.add(j.getJeu().piocherCarteWagon());
        }
        int nbMemeCouleur = 0;
        for(CouleurWagon carte: cartesPiochees){
            if(j.getCartesWagonPosees().contains(carte) || cartesPiochees.contains(CouleurWagon.LOCOMOTIVE)){
                nbMemeCouleur++;
            }
        }
        if(nbMemeCouleur>0){
            String choix = ".";
            boolean aCarte = false;
            ArrayList<CouleurWagon> cartePossible = new ArrayList<>();
            for(CouleurWagon carte : j.getCartesWagonPosees()){
                for (int h = 0; h<nbMemeCouleur && !aCarte; h++){
                    if(j.getCartesWagon().contains(carte) || j.getCartesWagon().contains(CouleurWagon.LOCOMOTIVE)){
                        aCarte = true;
                        cartePossible.add(carte);
                    }
                }
            }
            if (aCarte){
                ArrayList<String> bouton = new ArrayList<>();
                bouton.add("oui");
                choix = j.choisir("Souhaitez-vous toujours prendre le tunnel ?", new ArrayList<String>(), bouton, true);
                if (choix.equals("oui")){
                    j.choisirCarteWagon(cartePossible, nbMemeCouleur);
                }else{
                    j.getCartesWagon().addAll(j.getCartesWagonPosees());
                    j.getCartesWagonPosees().clear();
                }

            }
        }
    }
}
