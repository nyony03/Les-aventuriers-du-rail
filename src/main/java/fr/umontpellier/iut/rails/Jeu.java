package fr.umontpellier.iut.rails;

import com.google.gson.Gson;
import fr.umontpellier.iut.gui.GameServer;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

public class Jeu implements Runnable {
    /**
     * Liste des joueurs
     */
    private List<Joueur> joueurs;

    /**
     * Le joueur dont c'est le tour
     */
    private Joueur joueurCourant;
    /**
     * Liste des villes représentées sur le plateau de jeu
     */
    private List<Ville> villes;
    /**
     * Liste des routes du plateau de jeu
     */
    private List<Route> routes;
    /**
     * Pile de pioche (face cachée)
     */
    private List<CouleurWagon> pileCartesWagon;
    /**
     * Cartes de la pioche face visible (normalement il y a 5 cartes face visible)
     */
    private List<CouleurWagon> cartesWagonVisibles;
    /**
     * Pile de cartes qui ont été défaussée au cours de la partie
     */
    private List<CouleurWagon> defausseCartesWagon;
    /**
     * Pile des cartes "Destination" (uniquement les destinations "courtes", les
     * destinations "longues" sont distribuées au début de la partie et ne peuvent
     * plus être piochées après)
     */
    private List<Destination> pileDestinations;
    /**
     * File d'attente des instructions recues par le serveur
     */
    private BlockingQueue<String> inputQueue;
    /**
     * Messages d'information du jeu
     */
    private List<String> log;

    public Jeu(String[] nomJoueurs) {
        /*
         * ATTENTION : Cette méthode est à réécrire.
         *
         * Le code indiqué ici est un squelette minimum pour que le jeu se lance et que
         * l'interface graphique fonctionne.
         * Vous devez modifier ce code pour que les différents éléments du jeu soient
         * correctement initialisés.
         */

        // initialisation des entrées/sorties
        inputQueue = new LinkedBlockingQueue<>();
        log = new ArrayList<>();

        // création des cartes
        pileCartesWagon = new ArrayList<>();
        cartesWagonVisibles = new ArrayList<>();
        defausseCartesWagon = new ArrayList<>();
        pileDestinations = new ArrayList<>();

        // création des joueurs
        ArrayList<Joueur.Couleur> couleurs = new ArrayList<>(Arrays.asList(Joueur.Couleur.values()));
        Collections.shuffle(couleurs);
        joueurs = new ArrayList<>();
        for (String nom : nomJoueurs) {
            Joueur joueur = new Joueur(nom, this, couleurs.remove(0));
            joueurs.add(joueur);
        }
        joueurCourant = joueurs.get(0);

        // distribution et placement des cartes wagon
        for (int i = 0; i < 14; i++) {
            pileCartesWagon.add(CouleurWagon.LOCOMOTIVE);
        }

        for (int i = 0; i < 12; i++) {
            pileCartesWagon.add(CouleurWagon.BLANC);
            pileCartesWagon.add(CouleurWagon.BLEU);
            pileCartesWagon.add(CouleurWagon.ROSE);
            pileCartesWagon.add(CouleurWagon.ROUGE);
            pileCartesWagon.add(CouleurWagon.ORANGE);
            pileCartesWagon.add(CouleurWagon.NOIR);
            pileCartesWagon.add(CouleurWagon.VERT);
            pileCartesWagon.add(CouleurWagon.JAUNE);
        }
        Collections.shuffle(pileCartesWagon);
        for (Joueur joueur : joueurs) {
            for (int i = 0; i < 4; i++) {
                joueur.getCartesWagon().add(pileCartesWagon.remove(0));
            }
        }

        for (int i = 0, loco = 0; i < 5; i++) {
            if (pileCartesWagon.get(0) == CouleurWagon.LOCOMOTIVE) {
                loco++;
            }
            cartesWagonVisibles.add(pileCartesWagon.remove(0));
            if (loco == 3) {
                defausseCartesWagon.addAll(cartesWagonVisibles);
                cartesWagonVisibles.clear();
                i = 0;
            }
        }

        // creation de la pile de destinations
        pileDestinations = Destination.makeDestinationsEurope();
        Collections.shuffle(pileDestinations);

        // création des villes et des routes
        Plateau plateau = Plateau.makePlateauEurope();
        villes = plateau.getVilles();
        routes = plateau.getRoutes();
    }

    public List<CouleurWagon> getPileCartesWagon() {
        return pileCartesWagon;
    }

    public List<CouleurWagon> getCartesWagonVisibles() {
        return cartesWagonVisibles;
    }

    public List<Ville> getVilles() {
        return villes;
    }

    public List<Route> getRoutes() {
        return routes;
    }

    public Joueur getJoueurCourant() {
        return joueurCourant;
    }

    public List<CouleurWagon> getDefausseCartesWagon() {
        return defausseCartesWagon;
    }

    public List<Destination> getPileDestinations() {
        return pileDestinations;
    }

    /**
     * Exécute la partie
     */
    public void run() {
        /*
         * ATTENTION : Cette méthode est à réécrire.
         *
         * Cette méthode doit :
         * - faire choisir à chaque joueur les destinations initiales qu'il souhaite
         * garder : on pioche 3 destinations "courtes" et 1 destination "longue", puis
         * le
         * joueur peut choisir des destinations à défausser ou passer s'il ne veut plus
         * en défausser. Il doit en garder au moins 2.
         * - exécuter la boucle principale du jeu qui fait jouer le tour de chaque
         * joueur à tour de rôle jusqu'à ce qu'un des joueurs n'ait plus que 2 wagons ou
         * moins
         * - exécuter encore un dernier tour de jeu pour chaque joueur après
         */

        /**
         * Le code proposé ici n'est qu'un exemple d'utilisation des méthodes pour
         * interagir avec l'utilisateur, il n'a rien à voir avec le code de la partie et
         * doit donc être entièrement réécrit.
         */
        // distribution des cartes destination
        ArrayList<Destination> destinationsLongues = Destination.makeDestinationsLonguesEurope();
        Collections.shuffle(destinationsLongues);
        for (Joueur joueur : joueurs) {
            joueurCourant = joueur;
            ArrayList<Destination> choixDestinations = new ArrayList<>();
            choixDestinations.add(destinationsLongues.remove(0));
            for (int i = 0; i < 3; i++) {
                choixDestinations.add(pileDestinations.remove(0));
            }
            joueur.choisirDestinations(choixDestinations, 2);
        }

        // déroulement d'une partie
        boolean dernierTour = false;
        boolean enCours = true;
        while(enCours) {
            int dernierJoueur =0;
            while (!dernierTour) {
                for(int i = 0; i< joueurs.size(); i++){
                    joueurCourant = joueurs.get(i);
                    joueurCourant.jouerTour();
                    if (joueurCourant.getNbWagons() < 3 && dernierJoueur == 0) {
                        dernierTour = true;
                        dernierJoueur = i+1;
                    }
                }
            }

            for (int j = 0 ; j<dernierJoueur; j++) {
                joueurCourant = joueurs.get(j);
                joueurCourant.jouerTour();
            }
            enCours = false;
        }
        prompt("Fin de partie", new ArrayList<>(), false);
    }



    /**
     * Ajoute une carte dans la pile de défausse.
     * Dans le cas peu probable, où il y a moins de 5 cartes wagon face visibles
     * (parce que la pioche
     * et la défausse sont vides), alors il faut immédiatement rendre cette carte
     * face visible.
     *
     * @param c carte à défausser
     */

    public void defausserCarteWagon(CouleurWagon c) {
        if (pileCartesWagon.isEmpty() && defausseCartesWagon.isEmpty() && cartesWagonVisibles.size() < 5) {
            cartesWagonVisibles.add(c);
        } else {
            defausseCartesWagon.add(0 ,c);
        }
    }

    /**
     * Pioche une carte de la pile de pioche
     * Si la pile est vide, les cartes de la défausse sont replacées dans la pioche
     * puis mélangées avant de piocher une carte
     *
     * @return la carte qui a été piochée (ou null si aucune carte disponible)
     */
    public CouleurWagon piocherCarteWagon() {
        if (pileCartesWagon.isEmpty() && defausseCartesWagon.isEmpty()) {
            return null;
        }
        if (pileCartesWagon.isEmpty()) {
            pileCartesWagon.addAll(defausseCartesWagon);
            defausseCartesWagon.clear();
            Collections.shuffle(pileCartesWagon);
        }
        return pileCartesWagon.remove(0);
    }

    /**
     * Retire une carte wagon de la pile des cartes wagon visibles.
     * Si une carte a été retirée, la pile de cartes wagons visibles est recomplétée
     * (remise à 5, éventuellement remélangée si 3 locomotives visibles)
     */
    public void retirerCarteWagonVisible(CouleurWagon c) {
        System.out.println("ici");
        cartesWagonVisibles.remove(c);
        int locomotive = 0;
        while (cartesWagonVisibles.size() < 5){
            if(pileCartesWagon.get(0) == CouleurWagon.LOCOMOTIVE){
            locomotive++;
        }
            cartesWagonVisibles.add(pileCartesWagon.remove(0));
            if(locomotive == 3){
                cartesWagonVisibles.removeAll(defausseCartesWagon);
            }
        }
    }

    /**
     * Pioche et renvoie la destination du dessus de la pile de destinations.
     *
     * @return la destination qui a été piochée (ou `null` si aucune destination
     * disponible)
     */
    public Destination piocherDestination() {
        if (pileDestinations.isEmpty()) {
            return null;
        } else {
            return pileDestinations.remove(0);
        }
    }

    public List<Joueur> getJoueurs() {
        return joueurs;
    }

    @Override
    public String toString() {
        StringJoiner joiner = new StringJoiner("\n");
        for (Joueur j : joueurs) {
            joiner.add(j.toString());
        }
        return joiner.toString();
    }

    /**
     * Ajoute un message au log du jeu
     */
    public void log(String message) {
        log.add(message);
    }

    /**
     * Ajoute un message à la file d'entrées
     */
    public void addInput(String message) {
        inputQueue.add(message);
    }

    /**
     * Lit une ligne de l'entrée standard
     * C'est cette méthode qui doit être appelée à chaque fois qu'on veut lire
     * l'entrée clavier de l'utilisateur (par exemple dans {@code Player.choisir})
     *
     * @return une chaîne de caractères correspondant à l'entrée suivante dans la
     * file
     */
    public String lireLigne() {
        try {
            return inputQueue.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Envoie l'état de la partie pour affichage aux joueurs avant de faire un choix
     *
     * @param instruction l'instruction qui est donnée au joueur
     * @param boutons     labels des choix proposés s'il y en a
     * @param peutPasser  indique si le joueur peut passer sans faire de choix
     */
    public void prompt(String instruction, Collection<String> boutons, boolean peutPasser) {
        System.out.println();
        System.out.println(this);
        if (boutons.isEmpty()) {
            System.out.printf(">>> %s: %s <<<%n", joueurCourant.getNom(), instruction);
        } else {
            StringJoiner joiner = new StringJoiner(" / ");
            for (String bouton : boutons) {
                joiner.add(bouton);
            }
            System.out.printf(">>> %s: %s [%s] <<<%n", joueurCourant.getNom(), instruction, joiner);
        }

        Map<String, Object> data = Map.ofEntries(
                new AbstractMap.SimpleEntry<String, Object>("prompt", Map.ofEntries(
                        new AbstractMap.SimpleEntry<String, Object>("instruction", instruction),
                        new AbstractMap.SimpleEntry<String, Object>("boutons", boutons),
                        new AbstractMap.SimpleEntry<String, Object>("nomJoueurCourant", getJoueurCourant().getNom()),
                        new AbstractMap.SimpleEntry<String, Object>("peutPasser", peutPasser))),
                new AbstractMap.SimpleEntry<>("villes",
                        villes.stream().map(Ville::asPOJO).collect(Collectors.toList())),
                new AbstractMap.SimpleEntry<>("routes",
                        routes.stream().map(Route::asPOJO).collect(Collectors.toList())),
                new AbstractMap.SimpleEntry<String, Object>("joueurs",
                        joueurs.stream().map(Joueur::asPOJO).collect(Collectors.toList())),
                new AbstractMap.SimpleEntry<String, Object>("piles", Map.ofEntries(
                        new AbstractMap.SimpleEntry<String, Object>("pileCartesWagon", pileCartesWagon.size()),
                        new AbstractMap.SimpleEntry<String, Object>("pileDestinations", pileDestinations.size()),
                        new AbstractMap.SimpleEntry<String, Object>("defausseCartesWagon", defausseCartesWagon),
                        new AbstractMap.SimpleEntry<String, Object>("cartesWagonVisibles", cartesWagonVisibles))),
                new AbstractMap.SimpleEntry<String, Object>("log", log));
        GameServer.setEtatJeu(new Gson().toJson(data));
    }
}
