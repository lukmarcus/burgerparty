

public class Messages_fr extends com.agateau.burgerparty.utils.Messages {
    public Messages_fr() {
        plainEntries.put("Burger Master", "Maître Burger");
        plainEntries.put("Burger God", "Dieu des Burgers");
        plainEntries.put("Practice Area", "Zone d'essai");
        plainEntries.put("Star Collector", "Collectionneur d'étoiles");
        plainEntries.put("Close Call", "C'était juste");
        plainEntries.put("Morning Gamer", "Joueur du matin");
        plainEntries.put("Evening Gamer", "Joueur du soir");
        pluralEntries.put(
            new PluralId("1 remaining.", "%n remaining."),
            new String[] {
                "1 restant.",
                "%n restants.",
            }
        );
        pluralEntries.put(
            new PluralId("ignore-n-burgers", "Serve %n burgers."),
            new String[] {
                "-",
                "Servir %n burgers.",
            }
        );
        pluralEntries.put(
            new PluralId("ignore-practice", "Collect %n stars to unlock the practice area."),
            new String[] {
                "-",
                "Récupérer %n étoiles pour débloquer la zone d'essai.",
            }
        );
        pluralEntries.put(
            new PluralId("ignore-collect", "Collect %n stars."),
            new String[] {
                "-",
                "Récupérer %n étoiles.",
            }
        );
        pluralEntries.put(
            new PluralId("ignore-close-call", "Finish a level with %n seconds left."),
            new String[] {
                "-",
                "Finir un niveau avec %n secondes.",
            }
        );
        pluralEntries.put(
            new PluralId("ignore-morning", "Start a game between 7AM and 10AM for %n days."),
            new String[] {
                "-",
                "Jouer entre 7 heures et 10 heures du matin pendant %n jours.",
            }
        );
        pluralEntries.put(
            new PluralId("ignore-evening", "Start a game between 7PM and 11PM for %n days."),
            new String[] {
                "-",
                "Jouer entre 19 heures et 23 heures du matin pendant %n jours.",
            }
        );
    }

    @Override
    public int plural(int n) {
        return (n > 1) ? 1 : 0;
    }
}