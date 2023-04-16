/**
 * BFT Map implementation (interactive client).
 *
 */
package intol.bftmap;

import java.io.Console;
import java.io.IOException;
import java.util.*;

public class BFTMapInteractiveClient {

    public static void main(String[] args) throws IOException {
        int clientId = (args.length > 0) ? Integer.parseInt(args[0]) : 1001;
        BFTMap<Integer, String> bftMap = new BFTMap<>(clientId);
        
        Random random = new Random();
        int random_key = random.nextInt(10000);

        Console console = System.console();

        System.out.println("\nCommands:\n");
        System.out.println("\tMY_COINS: List of all client coins");
        System.out.println("\tMINT: Create a new coin");
        System.out.println("\tSPEND: Transfer coins to another client");
        System.out.println("\tMY_NFTS: List of all client NFTS");
        System.out.println("\tMINT_NFT: Create a new NFT");
        System.out.println("\tREQUEST_NFT_TRANSFER: Create a request transfer for nft with an offered value and confirmation validity");
        System.out.println("\tCANCEL_NFT_REQUEST: Cancel a nft transfer request");
        System.out.println("\tMY_NFT_REQUESTS: List of all NFT requests");
        System.out.println("\tPROCESS_NFT_TRANSFER: Accept or reject a NFT transfer");
        System.out.println("\tEXIT: Terminate this client\n");

        while (true) {
            String cmd = console.readLine("\n  > ");

            if (cmd.equalsIgnoreCase("PUT")) {

                int key;
                try {
                    key = Integer.parseInt(console.readLine("Enter a numeric key: "));
                } catch (NumberFormatException e) {
                    System.out.println("\tThe key is supposed to be an integer!\n");
                    continue;
                }
                String value = console.readLine("Enter an alpha-numeric value: ");

                //invokes the op on the servers
                bftMap.put(key, value);

                System.out.println("\nkey-value pair added to the map\n");
            } else if (cmd.equalsIgnoreCase("GET")) {

                int key;
                try {
                    key = Integer.parseInt(console.readLine("Enter a numeric key: "));
                } catch (NumberFormatException e) {
                    System.out.println("\tThe key is supposed to be an integer!\n");
                    continue;
                }

                //invokes the op on the servers
                String value = bftMap.get(key);

                System.out.println("\nValue associated with " + key + ": " + value + "\n");

            } else if (cmd.equalsIgnoreCase("MY_COINS")) {
                
                Set<Integer> keys = bftMap.keySet();
                System.out.println("Your coins: ");
                for (Integer key : keys) {
                	String coin = (String) bftMap.get(key);
                    coin = bftMap.get(key);
                    String[] values = coin.split("\\|");
                    if (values[0].equals(Integer.toString(clientId)) && values[2].equals("coin")) {
                        System.out.println("Coin: " + key + " | Value: " + values[1]);
                    }
                }

            } else if (cmd.equalsIgnoreCase("MINT")) {
            	
            	int key;
                try {
                	key = Integer.parseInt(console.readLine("Enter a numeric key: "));
          
                } catch (NumberFormatException e) {
                	System.out.println("\\tThe key is supposed to be an integer!\\n"); 
                	continue;
                }
                
                String s = clientId + "|" + key + "|" + "coin";

                
                bftMap.put(random_key, s);

                System.out.println("\nCoin " + random_key +  " created successfully!");
                random_key=random.nextInt(10000);
                
            } else if (cmd.equalsIgnoreCase("SIZE")) {

                System.out.println("\tYou are supposed to implement this command :)\n");

            } else if (cmd.equalsIgnoreCase("EXIT")) {

                System.out.println("\tEXIT: Bye bye!\n");
                System.exit(0);

            } else {
                System.out.println("\tInvalid command :P\n");
            }
        }
    }

}
