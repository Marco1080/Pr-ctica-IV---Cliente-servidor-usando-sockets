import io.magicthegathering.javasdk.api.CardAPI;
import io.magicthegathering.javasdk.resource.Card;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        /*
        int multiverseId = 1;
        Card card = CardAPI.getCard(multiverseId);
        System.out.println(card);
        */

        List<Card> cards = CardAPI.getAllCards();
        for (Card currentCard: cards) {
            System.out.println(currentCard);
        }

    }
}
