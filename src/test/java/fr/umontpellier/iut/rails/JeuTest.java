package fr.umontpellier.iut.rails;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class JeuTest {
    private IOJeu jeu;
    private Joueur joueur1;
    private Joueur joueur2;
    private Joueur joueur3;
    private Joueur joueur4;

    /**
     * Renvoie la route du jeu dont le nom est passé en argument
     *
     * @param nom le nom de la route
     * @return la route du jeu dont le nom est passé en argument (ou null si aucune
     *         route ne correspond)
     */
    public Route getRouteParNom(String nom) {
        for (Route route : jeu.getRoutes()) {
            if (route.getNom().equals(nom)) {
                return route;
            }
        }
        return null;
    }

    /**
     * Renvoie la ville du jeu dont le nom est passé en argument
     *
     * @param nom le nom de la ville
     * @return la ville du jeu dont le nom est passé en argument (ou null si aucune
     *         ville ne correspond)
     */
    public Ville getVilleParNom(String nom) {
        for (Ville ville : jeu.getVilles()) {
            if (ville.getNom().equals(nom)) {
                return ville;
            }
        }
        return null;
    }

    @BeforeEach
    void init() {
        jeu = new IOJeu(new String[] { "Guybrush", "Largo", "LeChuck", "Elaine" });
        List<Joueur> joueurs = jeu.getJoueurs();
        joueur1 = joueurs.get(0);
        joueur2 = joueurs.get(1);
        joueur3 = joueurs.get(2);
        joueur4 = joueurs.get(3);
    }

    /**
     * Place 5 cartes ROUGE dans les cartes visibles, vide la pioche, la défausse et
     * les mains des joueurs
     */
    void clear() {
        List<CouleurWagon> cartesVisibles = jeu.getCartesWagonVisibles();
        cartesVisibles.clear();
        cartesVisibles.add(CouleurWagon.ROUGE);
        cartesVisibles.add(CouleurWagon.ROUGE);
        cartesVisibles.add(CouleurWagon.ROUGE);
        cartesVisibles.add(CouleurWagon.ROUGE);
        cartesVisibles.add(CouleurWagon.ROUGE);
        jeu.getPileCartesWagon().clear();
        jeu.getDefausseCartesWagon().clear();

        joueur1.getCartesWagon().clear();
        joueur2.getCartesWagon().clear();
        joueur3.getCartesWagon().clear();
        joueur4.getCartesWagon().clear();
    }

    @org.junit.jupiter.api.Test
    void testInitialisation() {
        for (Joueur joueur : jeu.getJoueurs()) {
            assertEquals(3, joueur.getNbGares());
            assertEquals(4, joueur.getCartesWagon().size());
            assertEquals(45, joueur.getNbWagons());
        }
    }

    @Test
    void testCapturerRouteCouleur() {
        clear();
        List<CouleurWagon> cartesWagon = joueur2.getCartesWagon();
        cartesWagon.add(CouleurWagon.BLEU);
        cartesWagon.add(CouleurWagon.BLEU);
        cartesWagon.add(CouleurWagon.ROUGE);
        cartesWagon.add(CouleurWagon.ROUGE);
        cartesWagon.add(CouleurWagon.LOCOMOTIVE);

        jeu.setInput(
                "Bruxelles - Frankfurt", // coûte 2 BLEU
                "LOCOMOTIVE", // ok
                "ROUGE", // ne convient pas pour une route de 2 BLEU
                "BLEU" // ok

        );

        joueur2.jouerTour();
        assertEquals(joueur2, getRouteParNom("Bruxelles - Frankfurt").getProprietaire());
        assertTrue(TestUtils.contientExactement(
                joueur2.getCartesWagon(),
                CouleurWagon.BLEU, CouleurWagon.ROUGE, CouleurWagon.ROUGE));
        assertTrue(TestUtils.contientExactement(
                jeu.getDefausseCartesWagon(),
                CouleurWagon.BLEU,
                CouleurWagon.LOCOMOTIVE));
    }

    @Test
    void testJouerTourConstruireRouteGrisAvecLoco() {
        clear();
        List<CouleurWagon> cartesWagon = joueur2.getCartesWagon();
        cartesWagon.add(CouleurWagon.BLEU);
        cartesWagon.add(CouleurWagon.ROUGE);
        cartesWagon.add(CouleurWagon.ROUGE);
        cartesWagon.add(CouleurWagon.LOCOMOTIVE);
        cartesWagon.add(CouleurWagon.LOCOMOTIVE);

        jeu.setInput(
                "Bruxelles - Frankfurt", // coûte 2 BLEU
                "LOCOMOTIVE",
                "LOCOMOTIVE"
        );

        joueur2.jouerTour();
        assertEquals(joueur2, getRouteParNom("Bruxelles - Frankfurt").getProprietaire());
        assertTrue(TestUtils.contientExactement(
                joueur2.getCartesWagon(),
                CouleurWagon.BLEU, CouleurWagon.ROUGE, CouleurWagon.ROUGE));
        assertTrue(TestUtils.contientExactement(
                jeu.getDefausseCartesWagon(),
                CouleurWagon.LOCOMOTIVE,
                CouleurWagon.LOCOMOTIVE));
    }

    @Test
    void testCapturerTunnelLocomotivepossible() {
        clear();
        List<CouleurWagon> cartesWagon = joueur2.getCartesWagon();
        cartesWagon.add(CouleurWagon.LOCOMOTIVE);
        cartesWagon.add(CouleurWagon.LOCOMOTIVE);
        cartesWagon.add(CouleurWagon.LOCOMOTIVE);
        cartesWagon.add(CouleurWagon.LOCOMOTIVE);

        // cartes qui seront piochées après avoir payé le prix initial du tunnel
        jeu.getPileCartesWagon().add(0, CouleurWagon.LOCOMOTIVE);
        jeu.getPileCartesWagon().add(0, CouleurWagon.ROSE);
        jeu.getPileCartesWagon().add(0, CouleurWagon.JAUNE);

        jeu.setInput(
                "Marseille - Zurich", // coûte 2 ROSE (tunnel)
                "LOCOMOTIVE", // ok
                "LOCOMOTIVE", // ok
                "LOCOMOTIVE"// ok
        );

        joueur2.jouerTour();
        assertEquals(joueur2, getRouteParNom("Marseille - Zurich").getProprietaire());
        assertTrue(TestUtils.contientExactement(
                jeu.getDefausseCartesWagon(),
                CouleurWagon.LOCOMOTIVE,
                CouleurWagon.LOCOMOTIVE,
                CouleurWagon.LOCOMOTIVE,
                CouleurWagon.LOCOMOTIVE,
                CouleurWagon.ROSE,
                CouleurWagon.JAUNE));
    }

    @Test
    void testCapturerTunnelrose_rajout2cartes() {
        clear();
        List<CouleurWagon> cartesWagon = joueur2.getCartesWagon();
        cartesWagon.add(CouleurWagon.ROSE);
        cartesWagon.add(CouleurWagon.ROSE);
        cartesWagon.add(CouleurWagon.ROSE);
        cartesWagon.add(CouleurWagon.ROSE);

        // cartes qui seront piochées après avoir payé le prix initial du tunnel
        jeu.getPileCartesWagon().add(0, CouleurWagon.LOCOMOTIVE);
        jeu.getPileCartesWagon().add(0, CouleurWagon.ROSE);
        jeu.getPileCartesWagon().add(0, CouleurWagon.JAUNE);

        jeu.setInput(
                "Marseille - Zurich", // coûte 2 ROSE (tunnel)
                "ROSE", // ok
                "ROSE", // ok
                "ROSE", // ok
                "ROSE"// ok
        );

        joueur2.jouerTour();
        assertEquals(joueur2, getRouteParNom("Marseille - Zurich").getProprietaire());
        assertTrue(TestUtils.contientExactement(
                jeu.getDefausseCartesWagon(),
                CouleurWagon.ROSE,
                CouleurWagon.ROSE,
                CouleurWagon.ROSE,
                CouleurWagon.ROSE,
                CouleurWagon.LOCOMOTIVE,
                CouleurWagon.ROSE,
                CouleurWagon.JAUNE));
    }

    @Test
    void testCapturerTunnel() {
        clear();
        List<CouleurWagon> cartesWagon = joueur2.getCartesWagon();
        cartesWagon.add(CouleurWagon.ROSE);
        cartesWagon.add(CouleurWagon.ROSE);
        cartesWagon.add(CouleurWagon.ROUGE);
        cartesWagon.add(CouleurWagon.ROUGE);
        cartesWagon.add(CouleurWagon.LOCOMOTIVE);

        // cartes qui seront piochées après avoir payé le prix initial du tunnel
        jeu.getPileCartesWagon().add(0, CouleurWagon.BLEU);
        jeu.getPileCartesWagon().add(0, CouleurWagon.ROSE);
        jeu.getPileCartesWagon().add(0, CouleurWagon.JAUNE);

        jeu.setInput(
                "Marseille - Zurich", // coûte 2 ROSE (tunnel)
                "ROSE", // ok
                "LOCOMOTIVE", // ok
                "ROUGE", //passe pas
                "ROSE"// coût supplémentaire du tunnel
        );

        joueur2.jouerTour();
        assertEquals(joueur2, getRouteParNom("Marseille - Zurich").getProprietaire());
        assertTrue(TestUtils.contientExactement(
                joueur2.getCartesWagon(),
                CouleurWagon.ROUGE, CouleurWagon.ROUGE));
        assertTrue(TestUtils.contientExactement(
                jeu.getDefausseCartesWagon(),
                CouleurWagon.ROSE,
                CouleurWagon.ROSE,
                CouleurWagon.LOCOMOTIVE,
                CouleurWagon.BLEU,
                CouleurWagon.ROSE,
                CouleurWagon.JAUNE));
    }
}
