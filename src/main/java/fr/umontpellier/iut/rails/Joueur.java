package fr.umontpellier.iut.rails;

import java.util.*;
import java.util.stream.Collectors;

public class Joueur {

    /**
     * Les couleurs possibles pour les joueurs (pour l'interface graphique)
     */
    public static enum Couleur {
        JAUNE, ROUGE, BLEU, VERT, ROSE;
    }

    /**
     * Jeu auquel le joueur est rattaché
     */
    private Jeu jeu;
    /**
     * Nom du joueur
     */
    private String nom;
    /**
     * CouleurWagon du joueur (pour représentation sur le plateau)
     */
    private Couleur couleur;
    /**
     * Nombre de gares que le joueur peut encore poser sur le plateau
     */
    private int nbGares;
    /**
     * Nombre de wagons que le joueur peut encore poser sur le plateau
     */
    private int nbWagons;
    /**
     * Liste des missions à réaliser pendant la partie
     */
    private List<Destination> destinations;
    /**
     * Liste des cartes que le joueur a en main
     */
    private List<CouleurWagon> cartesWagon;
    /**
     * Liste temporaire de cartes wagon que le joueur est en train de jouer pour
     * payer la capture d'une route ou la construction d'une gare
     */
    private List<CouleurWagon> cartesWagonPosees;
    /**
     * Score courant du joueur (somme des valeurs des routes capturées)
     */
    private int score;

    public Joueur(String nom, Jeu jeu, Joueur.Couleur couleur) {
        this.nom = nom;
        this.jeu = jeu;
        this.couleur = couleur;
        nbGares = 3;
        nbWagons = 45;
        cartesWagon = new ArrayList<>();
        cartesWagonPosees = new ArrayList<>();
        destinations = new ArrayList<>();
        score = 12; // chaque gare non utilisée vaut 4 points
    }

    public String getNom() {
        return nom;
    }

    public Couleur getCouleur() {
        return couleur;
    }

    public int getNbWagons() {
        return nbWagons;
    }

    public int getNbGares() {
        return nbGares;
    }

    public Jeu getJeu() {
        return jeu;
    }

    public List<CouleurWagon> getCartesWagonPosees() {
        return cartesWagonPosees;
    }

    public List<CouleurWagon> getCartesWagon() {
        return cartesWagon;
    }

    public List<Destination> getDestinations() {
        return destinations;
    }

    /**
     * Attend une entrée de la part du joueur (au clavier ou sur la websocket) et
     * renvoie le choix du joueur.
     * <p>
     * Cette méthode lit les entrées du jeu ({@code Jeu.lireligne()}) jusqu'à ce
     * qu'un choix valide (un élément de {@code choix} ou de {@code boutons} ou
     * éventuellement la chaîne vide si l'utilisateur est autorisé à passer) soit
     * reçu.
     * Lorsqu'un choix valide est obtenu, il est renvoyé par la fonction.
     * <p>
     * Si l'ensemble des choix valides ({@code choix} + {@code boutons}) ne comporte
     * qu'un seul élément et que {@code canPass} est faux, l'unique choix valide est
     * automatiquement renvoyé sans lire l'entrée de l'utilisateur.
     * <p>
     * Si l'ensemble des choix est vide, la chaîne vide ("") est automatiquement
     * renvoyée par la méthode (indépendamment de la valeur de {@code canPass}).
     * <p>
     * Exemple d'utilisation pour demander à un joueur de répondre à une question
     * par "oui" ou "non" :
     * <p>
     * {@code
     * List<String> choix = Arrays.asList("Oui", "Non");
     * String input = choisir("Voulez vous faire ceci ?", choix, new ArrayList<>(), false);
     * }
     * <p>
     * <p>
     * Si par contre on voulait proposer les réponses à l'aide de boutons, on
     * pourrait utiliser :
     * <p>
     * {@code
     * List<String> boutons = Arrays.asList("1", "2", "3");
     * String input = choisir("Choisissez un nombre.", new ArrayList<>(), boutons, false);
     * }
     *
     * @param instruction message à afficher à l'écran pour indiquer au joueur la
     *                    nature du choix qui est attendu
     * @param choix       une collection de chaînes de caractères correspondant aux
     *                    choix valides attendus du joueur
     * @param boutons     une collection de chaînes de caractères correspondant aux
     *                    choix valides attendus du joueur qui doivent être
     *                    représentés par des boutons sur l'interface graphique.
     * @param peutPasser  booléen indiquant si le joueur a le droit de passer sans
     *                    faire de choix. S'il est autorisé à passer, c'est la
     *                    chaîne de caractères vide ("") qui signifie qu'il désire
     *                    passer.
     * @return le choix de l'utilisateur (un élément de {@code choix}, ou de
     * {@code boutons} ou la chaîne vide)
     */
    public String choisir(String instruction, Collection<String> choix, Collection<String> boutons,
                          boolean peutPasser) {
        // on retire les doublons de la liste des choix
        HashSet<String> choixDistincts = new HashSet<>();
        choixDistincts.addAll(choix);
        choixDistincts.addAll(boutons);

        // Aucun choix disponible
        if (choixDistincts.isEmpty()) {
            return "";
        } else {
            // Un seul choix possible (renvoyer cet unique élément)
            if (choixDistincts.size() == 1 && !peutPasser)
                return choixDistincts.iterator().next();
            else {
                String entree;
                // Lit l'entrée de l'utilisateur jusqu'à obtenir un choix valide
                while (true) {
                    jeu.prompt(instruction, boutons, peutPasser);
                    entree = jeu.lireLigne();
                    // si une réponse valide est obtenue, elle est renvoyée
                    if (choixDistincts.contains(entree) || (peutPasser && entree.equals("")))
                        return entree;
                }
            }
        }
    }

    /**
     * Affiche un message dans le log du jeu (visible sur l'interface graphique)
     *
     * @param message le message à afficher (peut contenir des balises html pour la
     *                mise en forme)
     */
    public void log(String message) {
        jeu.log(message);
    }

    @Override
    public String toString() {
        StringJoiner joiner = new StringJoiner("\n");
        joiner.add(String.format("=== %s (%d pts) ===", nom, score));
        joiner.add(String.format("  Gares: %d, Wagons: %d", nbGares, nbWagons));
        joiner.add("  Destinations: "
                + destinations.stream().map(Destination::toString).collect(Collectors.joining(", ")));
        joiner.add("  Cartes wagon: " + CouleurWagon.listToString(cartesWagon));
        return joiner.toString();
    }

    /**
     * @return une chaîne de caractères contenant le nom du joueur, avec des balises
     * HTML pour être mis en forme dans le log
     */
    public String toLog() {
        return String.format("<span class=\"joueur\">%s</span>", nom);
    }

    /**
     * Renvoie une représentation du joueur sous la forme d'un objet Java simple
     * (POJO)
     */
    public Object asPOJO() {
        HashMap<String, Object> data = new HashMap<>();
        data.put("nom", nom);
        data.put("couleur", couleur);
        data.put("score", score);
        data.put("nbGares", nbGares);
        data.put("nbWagons", nbWagons);
        data.put("estJoueurCourant", this == jeu.getJoueurCourant());
        data.put("destinations", destinations.stream().map(Destination::asPOJO).collect(Collectors.toList()));
        data.put("cartesWagon", cartesWagon.stream().sorted().map(CouleurWagon::name).collect(Collectors.toList()));
        data.put("cartesWagonPosees",
                cartesWagonPosees.stream().sorted().map(CouleurWagon::name).collect(Collectors.toList()));
        return data;
    }

    /**
     * Propose une liste de cartes destinations, parmi lesquelles le joueur doit en
     * garder un nombre minimum n.
     * <p>
     * Tant que le nombre de destinations proposées est strictement supérieur à n,
     * le joueur peut choisir une des destinations qu'il retire de la liste des
     * choix, ou passer (en renvoyant la chaîne de caractères vide).
     * <p>
     * Les destinations qui ne sont pas écartées sont ajoutées à la liste des
     * destinations du joueur. Les destinations écartées sont renvoyées par la
     * fonction.
     *
     * @param destinationsPossibles liste de destinations proposées parmi lesquelles
     *                              le joueur peut choisir d'en écarter certaines
     * @param n                     nombre minimum de destinations que le joueur
     *                              doit garder
     * @return liste des destinations qui n'ont pas été gardées par le joueur
     */
    public List<Destination> choisirDestinations(List<Destination> destinationsPossibles, int n) {
        ArrayList<String> bouton = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            bouton.add(destinationsPossibles.get(i).toString());
        }
        boolean peutPasser = true;

        int i = 0;
        String choix = ".";
        List<Destination> nonChoisi = new ArrayList<>();
        while (!choix.isEmpty() && i < n) {
            choix = choisir("Défausser jusqu'à 2 cartes : ", new ArrayList<>(), bouton, peutPasser);
            i++;
            for (Destination destination : destinationsPossibles) {
                if (destination.getNom().equals(choix)) {
                    nonChoisi.add(destination);
                }
            }
        }
        destinationsPossibles.removeAll(nonChoisi);
        destinations.addAll(destinationsPossibles);
        return nonChoisi;
    }

    /**
     * Exécute un tour de jeu du joueur.
     * <p>
     * Cette méthode attend que le joueur choisisse une des options suivantes :
     * - le nom d'une carte wagon face visible à prendre ;
     * - le nom "GRIS" pour piocher une carte wagon face cachée s'il reste des
     * cartes à piocher dans la pile de pioche ou dans la pile de défausse ;
     * - la chaîne "destinations" pour piocher des cartes destination ;
     * - le nom d'une ville sur laquelle il peut construire une gare (ville non
     * prise par un autre joueur, le joueur a encore des gares en réserve et assez
     * de cartes wagon pour construire la gare) ;
     * - le nom d'une route que le joueur peut capturer (pas déjà capturée, assez de
     * wagons et assez de cartes wagon) ;
     * - la chaîne de caractères vide pour passer son tour
     * <p>
     * Lorsqu'un choix valide est reçu, l'action est exécutée (il est possible que
     * l'action nécessite d'autres choix de la part de l'utilisateur, comme "choisir les cartes wagon à défausser pour capturer une route" ou
     * "construire une gare", "choisir les destinations à défausser", etc.)
     */
    public void jouerTour() {
        ArrayList<String> choixInteractif = new ArrayList<>();
        ArrayList<Ville> garePossible = new ArrayList<>();
        ArrayList<Route> routePossible = new ArrayList<>();
        for (CouleurWagon couleurWagon : jeu.getCartesWagonVisibles()) {
            choixInteractif.add("" + couleurWagon.name());
        }
        choixInteractif.add("GRIS");
        choixInteractif.add("destinations");
        if (nbGares == 3 && nbMaxCarteSimilaire() >= 1 || nbGares == 2 && nbMaxCarteSimilaire() >= 2 || nbGares == 1 && nbMaxCarteSimilaire() >= 3) {
            for (Ville gare : jeu.getVilles()) {
                if (gare.getProprietaire() == null) {
                    choixInteractif.add(gare.getNom());
                    garePossible.add(gare);
                }
            }
        }
        for (Route route : jeu.getRoutes()) {
            if (route.getProprietaire() == null && route.nbCarteRequis(this)) {
                choixInteractif.add(route.getNom());
                routePossible.add(route);
            }
        }

        ArrayList<String> choixBouton = new ArrayList<>(choixInteractif);
        String choix = ".";
        boolean peutPasser = true;
        choix = choisir(getNom() + ", choisis parmi les propositions suivante :", choixInteractif, choixBouton, peutPasser);

        //Le joueur va choisir entre piocher carteWagon / choisir une route /choisir une destination / construire une gare

        //choix = piocher
        //choix = piocher carteWagon pas loco dans carteWagon visible
        if (!choix.equals("GRIS")) {
            for (CouleurWagon carte : CouleurWagon.getCouleursSimples()) {
                if (choix.equals(carte.name())) {
                    cartesWagon.add(carte);
                    jeu.retirerCarteWagonVisible(carte);
                }
                for (CouleurWagon carteVisible : jeu.getCartesWagonVisibles()) {
                    if (!carte.equals(CouleurWagon.LOCOMOTIVE)) {
                        choixInteractif.add(carteVisible.name());
                    }
                }
                choixInteractif.add("GRIS");
                choixBouton.addAll(choixInteractif);
                choix = choisir(getNom() + ", choisis parmi les propositions suivante :", choixInteractif, choixBouton, peutPasser);
                if (choix.equals("GRIS")) {
                    cartesWagon.add(jeu.piocherCarteWagon());
                } else {
                    for (CouleurWagon couleurCarte : CouleurWagon.getCouleursSimples()) {
                        if (choix.equals(couleurCarte.name())) {
                            cartesWagon.add(couleurCarte);
                            jeu.retirerCarteWagonVisible(couleurCarte);
                        }
                    }
                }
            }
        }
        //Si le choix est de pioché une locomotive dans la carte Wagon visible
        if (choix.equals("LOCOMOTIVE")) {
            jeu.retirerCarteWagonVisible(CouleurWagon.LOCOMOTIVE);
            cartesWagon.add(CouleurWagon.LOCOMOTIVE);

        }

        //si le choix est de piocher des cartes dans la pileCarteWagon
        if (choix.equals("GRIS")) { // A REFAIRE
            cartesWagon.add(jeu.piocherCarteWagon());
            choixInteractif.clear();
            choixBouton.clear();

            //il a le choix de piocher encore de la pile GRIS ou il choisit de piocher dans carteVagonVisible
            String choix2 = "";
            choix2 = choisir(getNom() + ", choisis entre piocher dans la pile ou prendre une carte visible", choixInteractif, choixBouton, peutPasser);
            if (choix2.equals("GRIS")) {
                cartesWagon.add(jeu.piocherCarteWagon());
            } else {
                for (CouleurWagon carte : jeu.getCartesWagonVisibles()) {
                    if (!carte.equals(CouleurWagon.LOCOMOTIVE)) {
                        choixInteractif.add(carte.name());
                        cartesWagon.add(carte);
                        jeu.retirerCarteWagonVisible(carte);
                    }
                }
            }

//            choixInteractif.add("GRIS");
//            choixBouton.addAll(choixInteractif);
//            choix = choisir(getNom() + ", choisis parmi les propositions suivante :", choixInteractif, choixBouton, peutPasser);
//            if( choix.equals("LOCOMOTIVE")){
//                jeu.retirerCarteWagonVisible(CouleurWagon.LOCOMOTIVE);
//                cartesWagon.add(CouleurWagon.LOCOMOTIVE);
//            }else{
//                for(CouleurWagon carte : CouleurWagon.getCouleursSimples()){
//                    if(choix.equals(carte.name())){
//                        cartesWagon.add(carte);
//                        jeu.retirerCarteWagonVisible(carte);
//                    }
//                }
//            }
        }

        //si le choix est pioché une carte dans la pile destinations
        if (choix.equals("destinations")) {
            ArrayList<Destination> destinationsPiochees = new ArrayList<>();
            while (destinationsPiochees.size() <= 3) {
                destinationsPiochees.add(jeu.piocherDestination());
            }
            jeu.getPileDestinations().addAll(jeu.getPileDestinations().size() - 1, choisirDestinations(destinationsPiochees, 1));
        }

        //si choix = choisir une route
        for (Route route : routePossible) {
            if (choix.equals(route.getNom())) {
                boolean routeUtilise = route.utilisationRoute(this);
                //si la route peut être passer
                if (routeUtilise) {
                    route.utilisationRoute(this);
                    route.setProprietaire(this);
                    scoreJoueur(route.getLongueur());
                }
            }
        }

        //si choix = construire une gare
        for (Ville ville : garePossible) {
            if (choix.equals(ville.getNom())) {
                choisirCarteWagon(cartesWagon, 4 - nbGares);
                ville.setProprietaire(this);
                nbGares -= 1;
                score -= 4;
            }
        }

        for (CouleurWagon carte : cartesWagonPosees) {
            jeu.defausserCarteWagon(carte);
            cartesWagon.remove(carte);
        }
        cartesWagonPosees.clear();
    }


    public int nbCartesCouleur(CouleurWagon couleur) {
        int nbCartesCouleur = 0;
        for (CouleurWagon carteWagon : cartesWagon) {
            if (carteWagon.equals(couleur)) {
                nbCartesCouleur++;
            }
        }
        return nbCartesCouleur;
    }


    public int nbMaxCarteSimilaire() {
        int nbMaxCarteSimilaire = 0;
        int nbCarteSimilaire = 0;
        for (CouleurWagon couleur : CouleurWagon.getCouleursSimples()) {
            for (CouleurWagon carte : cartesWagon) {
                if (carte.equals(couleur)) {
                    nbCarteSimilaire++;
                }
            }
            if (nbCarteSimilaire > nbMaxCarteSimilaire) {
                nbMaxCarteSimilaire++;
            }
        }
        return nbMaxCarteSimilaire;
    }


    public String choisirCarteWagon(List<CouleurWagon> cartesWagonPossibles, int n) {
        String couleurChoisi = "";
        ArrayList<String> choixBouton = new ArrayList<>();
        for (CouleurWagon carteWagon : cartesWagonPossibles) {                      //création de la liste de choix avec les cartes wagons mis en parametre
            choixBouton.add(carteWagon.toString());
        }
        int i = 0;
        String choix = ".";
        ArrayList<String> choixInteractif = new ArrayList<>(choixBouton);
        while (i < n) {                                                                   //tant que i est inférieur au nombre de cartes à choisir (n en parametre)
            choix = choisir("Choisir " + n + " carte à utiliser  : ", choixInteractif, choixBouton, false);
            i++;
            for (int h = 0; h < cartesWagonPossibles.size() - 1; h++) {                  //pour toutes les cartes possibles
                String nomCarteWagon = cartesWagonPossibles.get(h).name();                 //on les ajoute dans l'attribut carte wagon posée
                if (nomCarteWagon.equals(choix)) {
                    cartesWagonPosees.add(cartesWagonPossibles.get(h));
                    h = cartesWagonPossibles.size();
                }
            }
            int occurence = Collections.frequency(choixBouton, choix);
            if (!choix.equals("LOCOMOTIVE")) {                                       //on determine quelles cartes il est possible de proposer au joueur
                couleurChoisi = choix;
                ArrayList<String> cartesGardees = new ArrayList<>();                //pour le choix de ses prochaines cartes compte tenu de son premier choix
                for (int j = 0; j < occurence - 1; j++) {
                    cartesGardees.add(choix);
                }
                occurence = Collections.frequency(choixBouton, "LOCOMOTIVE");
                for (int j = 0; j < occurence; j++) {
                    cartesGardees.add("LOCOMOTIVE");
                }
                choixBouton.retainAll(cartesGardees);
                choixInteractif.retainAll(cartesGardees);
            } else {
                choixBouton.remove("LOCOMOTIVE");
                choixInteractif.remove("LOCOMOTIVE");
            }
        }
        if (couleurChoisi.equals("")) {
            couleurChoisi = "LOCOMOTIVE";
        }
        return couleurChoisi;
    }

    public void scoreJoueur(int nbWagon) {
        if (nbWagon == 1) {
            score += 1;
        }
        if (nbWagon == 2) {
            score += 2;
        }
        if (nbWagon == 3) {
            score += 4;
        }
        if (nbWagon == 4) {
            score += 7;
        }
        if (nbWagon == 6) {
            score += 15;
        }
        if (nbWagon == 8) {
            score += 21;
        }

    }
}
