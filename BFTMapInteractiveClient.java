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
        BFTMap<Integer, Object> bftMap = new BFTMap<>(clientId);
        
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
                String value = (String) bftMap.get(key);

                System.out.println("\nValue associated with " + key + ": " + value + "\n");

            } else if (cmd.equalsIgnoreCase("MY_COINS")) {
                
                Set<Integer> keys = bftMap.keySet();
                System.out.println("Your coins: ");
                for (Integer key : keys) {
                	String coin = (String) bftMap.get(key);
                    coin = (String) bftMap.get(key);
                    String[] values = coin.split("\\|");
                    if (values[0].equals(Integer.toString(clientId)) && values[2].equals("coin")) {
                        System.out.println("Coin: " + key + " | Value: " + values[1]);
                    }
                }

            } else if (cmd.equalsIgnoreCase("MINT")) {
            	
            	int value;
                try {
                	value = Integer.parseInt(console.readLine("Enter the value for the coin: "));
          
                } catch (NumberFormatException e) {
                	System.out.println("\\tThe key is supposed to be an integer!\\n"); 
                	continue;
                }
                
                String m = clientId + "|" + value + "|" + "coin";

                
                bftMap.put(random_key, m);

                System.out.println("\nCoin " + random_key +  " created successfully!");
                random_key=random.nextInt(10000);
                
            } else if (cmd.equalsIgnoreCase("SPEND")) {
            	int receiver;
                try {
                	receiver = Integer.parseInt(console.readLine("Enter the receiver ID: "));
                } catch (NumberFormatException e) {
                	System.out.println("The ID is supposed to be an integer!"); 
                	continue;
                }
                
                int value;
                try {
                	value = Integer.parseInt(console.readLine("Enter the value to transfer: "));
                } catch (NumberFormatException e) {
                	System.out.println("The value is supposed to be an integer!"); 
                	continue;
                }
                
                String coins;
                try {
                	coins = console.readLine("Enter the coin ids (Put a comma in between every id): ");
                } catch (NumberFormatException e) {
                	System.out.println("Please enter the values separated by a comma!"); 
                	continue;
                }
                String[] all_coins = coins.split(",");
                for (String coin : all_coins) {
            		try {
            			//Check if every id is an integer
                        Integer.parseInt(coin);
                    } catch (NumberFormatException e) {
                    	System.out.println("Coin IDs are supposed to be integers!");
                    	continue;
                    }             	
            	}
                String s = clientId + "|" + receiver + "|" + value + "|" + coins + "|" + "spend"; 
                
                String a = bftMap.put(random_key, s).toString();
                
            	random_key = random.nextInt(10000);
                
                if(a.equals("0")) {
                    System.out.println("You're not the onwer of this coin!");
                }else if(a.equals("1")) {
                    System.out.println("This coin doesn't exist!");
                }else if(a.equals("2")) {
                    System.out.println("You don't have enought funds!");
                }else {
                	System.out.println("Transfer " + a + " successful!");
                }
            	

            } else if (cmd.equalsIgnoreCase("EXIT")) {

                System.out.println("\tEXIT: Bye bye!\n");
                System.exit(0);

            } else {
                System.out.println("\tInvalid command :P\n");
            }
        }
    }

}
