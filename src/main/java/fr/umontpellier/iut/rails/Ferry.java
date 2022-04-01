package fr.umontpellier.iut.rails;

import java.util.ArrayList;
import java.util.Collections;

public class Ferry extends Route {
    /**
     * Nombre de locomotives qu'un joueur doit payer pour capturer le ferry
     */
    private int nbLocomotives;

    public Ferry(Ville ville1, Ville ville2, int longueur, CouleurWagon couleur, int nbLocomotives) {
        super(ville1, ville2, longueur, couleur);
        this.nbLocomotives = nbLocomotives;
    }

    @Override
    public boolean nbCarteRequis(Joueur j) {
        return super.nbCarteRequis(j) && j.nbCartesCouleur(CouleurWagon.LOCOMOTIVE) >= nbLocomotives;
    }

    @Override
    public String toString() {
        return String.format("[%s - %s (%d, %s, %d)]", getVille1(), getVille2(), getLongueur(), getCouleur(),
                nbLocomotives);
    }

    @Override
    public int utilisationRoute(Joueur j) {
        ArrayList<CouleurWagon> choix = new ArrayList<>();
        for(int i = 0; i < nbLocomotives; i++){
            j.getCartesWagonPosees().add(CouleurWagon.LOCOMOTIVE);
            j.getCartesWagon().remove(CouleurWagon.LOCOMOTIVE);
        }
        for(CouleurWagon couleurCarte : j.getCartesWagon()){
            int frequence = Collections.frequency(j.getCartesWagon(), couleurCarte);
            if( frequence >= (getLongueur()-nbLocomotives)){
                for(int f = 0; f < frequence; f++){
                    choix.add(couleurCarte);
                }
            }
        }
        j.choisirCarteWagon(choix, getLongueur()-nbLocomotives, true);
        return 0;
    }

}
