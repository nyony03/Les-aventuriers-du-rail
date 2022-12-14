package fr.umontpellier.iut.rails;

import java.util.HashMap;
import java.util.Collections;
import java.util.ArrayList;


public class Route {
    /**
     * Première extrémité
     */
    private Ville ville1;
    /**
     * Deuxième extrémité
     */
    private Ville ville2;
    /**
     * Nombre de segments
     */
    private int longueur;
    /**
     * CouleurWagon pour capturer la route (éventuellement GRIS, mais pas LOCOMOTIVE)
     */
    private CouleurWagon couleur;
    /**
     * Joueur qui a capturé la route (`null` si la route est encore à prendre)
     */
    private Joueur proprietaire;
    /**
     * Nom unique de la route. Ce nom est nécessaire pour résoudre l'ambiguïté entre les routes doubles
     * (voir la classe Plateau pour plus de clarté)
     */
    private String nom;

    private String couleurChoisi;

    public String getCouleurChoisi() {
        return couleurChoisi;
    }

    public Route(Ville ville1, Ville ville2, int longueur, CouleurWagon couleur) {
        this.ville1 = ville1;
        this.ville2 = ville2;
        this.longueur = longueur;
        this.couleur = couleur;
        nom = ville1.getNom() + " - " + ville2.getNom();
        proprietaire = null;
    }

    public Ville getVille1() {
        return ville1;
    }

    public Ville getVille2() {
        return ville2;
    }

    public int getLongueur() {
        return longueur;
    }

    public CouleurWagon getCouleur() {
        return couleur;
    }

    public Joueur getProprietaire() {
        return proprietaire;
    }

    public void setProprietaire(Joueur proprietaire) {
        this.proprietaire = proprietaire;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String toLog() {
        return String.format("<span class=\"route\">%s - %s</span>", ville1.getNom(), ville2.getNom());
    }

    @Override
    public String toString() {
        return String.format("[%s - %s (%d, %s)]", ville1, ville2, longueur, couleur);
    }

    /**
     * @return un objet simple représentant les informations de la route
     */
    public Object asPOJO() {
        HashMap<String, Object> data = new HashMap<>();
        data.put("nom", getNom());
        if (proprietaire != null) {
            data.put("proprietaire", proprietaire.getCouleur());
        }
        return data;
    }


    public boolean nbCarteRequis(Joueur j) {
        int nbCouleur = 0;
        if (getCouleur().equals(CouleurWagon.GRIS) && j.nbMaxCarteSimilaire() >= getLongueur()) {
            return true;
        }
        nbCouleur = Collections.frequency(j.getCartesWagon(), getCouleur())+Collections.frequency(j.getCartesWagon(), CouleurWagon.LOCOMOTIVE);
        return (getLongueur() <= nbCouleur);
    }
//    j.getCartesWagon().contains(getCouleur()) || (j.getCartesWagon().contains(CouleurWagon.LOCOMOTIVE)) &&

    public int utilisationRoute(Joueur j) {
        ArrayList<CouleurWagon> choix = new ArrayList<>();
        int frequenceLoco = Collections.frequency(j.getCartesWagon(), CouleurWagon.LOCOMOTIVE);
        if (couleur.equals(CouleurWagon.GRIS)) {
            couleurChoisi = couleur.name();
            for (CouleurWagon couleurCarte : CouleurWagon.getCouleursSimples()) {
                int frequenceCouleur = Collections.frequency(j.getCartesWagon(), couleurCarte);
                if (frequenceCouleur + frequenceLoco >= (getLongueur())) {
                    for (int f = 0; f < frequenceCouleur; f++) {
                        choix.add(couleurCarte);
                    }
                }
            }
        } else {
            for(int i = 0; i<Collections.frequency(j.getCartesWagon(), couleur); i++){
                choix.add(couleur);
            }
        }
        for (int n = 0; n < frequenceLoco; n++) {
            choix.add(CouleurWagon.LOCOMOTIVE);
        }
        couleurChoisi = j.choisirCarteWagon(choix, longueur, true);
        return -1;
    }

    public boolean verificationRouteDouble(Joueur j){
        if(nom.contains("1") || nom.contains("2")){
            for(Route route : j.getJeu().getRoutes()){
                if(route.getVille1() == this.ville1 && route.getVille2() == this.ville2){
                    if(route.getProprietaire() != null){
                        return route.getProprietaire().equals(j);
                    }
                }
            }
        }
        return false;
    }

}

