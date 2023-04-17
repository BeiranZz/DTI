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
        System.out.println("\tCANCEL_REQUEST_NFT_TRANSFER: Cancel a nft transfer request");
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

                
                String result = bftMap.mint(random_key, m).toString();
                random_key=random.nextInt(10000);
                
                if(result.equals("0")) {
                    System.out.println("You don't have permissions to mint a coin!");
                }else {
                    System.out.println("\nCoin " + result +  " created successfully!");
                }
   
                
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
                
                String a = bftMap.spend(random_key, s).toString();
                
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
            	

            } else if (cmd.equalsIgnoreCase("MY_NFTS")) {
                Set<Integer> keys = bftMap.keySet();
                System.out.println("Your NFTS:");

                for (int key : keys) {
            		String nft = (String) bftMap.get(key);
                	String[] values = nft.split("\\|");
                	if(values[0].equals(Integer.toString(clientId)) && isValidIndex(values,3) && values[3].equals("nft")) {
                		String name = values[1];
                		String uri = values[2];
                		System.out.println("ID " + key + " | NAME: " + name + " | URI: " + uri );
                	}
                    
                }
                
            }else if (cmd.equalsIgnoreCase("MINT_NFT")){
                
                String name = console.readLine("Enter a name for the nft: ");

                String uri = console.readLine("Enter a URI for the nft: ");
                String n = clientId + "|" + name +"|" + uri + "|" + "nft"; 

                String result = bftMap.mint_nft(random_key, n).toString();

                if (result.equals("0")){
                	System.out.println("There is already a NFT with that name!");
                }else {
                	System.out.println("\nNFT " + result +  " created successfully!");
                }
                random_key=random.nextInt(10000);

            } else if (cmd.equalsIgnoreCase("REQUEST_NFT_TRANSFER")){
            	
            	System.out.println("All available NFTS:");
                Set<Integer> keys = bftMap.keySet();
                for (int key : keys) {
                    String nft = (String) bftMap.get(key);
                    String[] values = nft.split("\\|");
                    if (values[0].equals(Integer.toString(clientId))) {
                        continue;
                    }
                    
                    if(!isValidIndex(values,3)){
                    	continue;
                    }
                    
                    if(isValidIndex(values,3) && !values[3].equals("nft")){
                    	continue;
                    }
                    
                    System.out.println("NFT ID: " + key + "| NAME: " + values[1] +"| URI: " + values[2]);
                }
                
                String nft = console.readLine("Enter the id of the nft you want to offer: ");
                
                String value = console.readLine("Enter the value you want to pay for the NFT: ");
                              
                String coins = console.readLine("Enter the coin ids that you are going to use (Put a comma in between every id): ");
                
                String validity = console.readLine("Enter the confirmation validity: ");

                String request = clientId + "|" + nft + "|" + coins + "|" + value + "|" + validity + "|" + "nft_request"; 
                
                String s = bftMap.request_nft_transfer(random_key, request).toString();
                random_key = random.nextInt(10000);

                if (s.equals("0")){
                	System.out.println("Can't request on your own NFT!");
                }else if (s.equals("1")){
                	System.out.println("You put coins that not belong to you!");
                }else if (s.equals("2")){
                	System.out.println("You don't have enought value!");
                }else if (s.equals("3")){
                	System.out.println("You already have a request!");
                }else if (s.equals("4")){
                	System.out.println("The NFT ID doesn't exist!");
                }else {
                	System.out.println("NFT Request " + s + " confirmed!");
                }

            } else if (cmd.equalsIgnoreCase("MY_NFT_REQUESTS")) {
                 Set<Integer> keys = bftMap.keySet();
                 System.out.println("Your NFT requests: ");
            	 String request;
            	 String nfts = "";
            	 boolean hasNFTS = false;
            	 
            	 for (int m : keys) {
                	 request = (String) bftMap.get(m);
                     String[] requests = request.split("\\|");
                     if(isValidIndex(requests,3) && requests[3].equals("nft")) {
                    	 if(requests[0].equals(Integer.toString(clientId))){
                    		 hasNFTS = true;
                    		 nfts += requests[1] + ",";
                    	 }
                     }
            	 }
                 String [] nfts_list = nfts.split(",");
                 for (int key : keys) {
                	 request = (String) bftMap.get(key);
                     String[] requests = request.split("\\|");

                     if (hasNFTS && isValidIndex(requests,5) && requests[5].equals("nft_request")) {
                    	 String nftId = requests[1];
                         String[] info = ((String) bftMap.get(Integer.parseInt(requests[1]))).split("\\|");
                         String name = info[1];
                         String uri = info[2];   
                         String value = requests[3];
                         
                         for(String nft : nfts_list) {
                        	 if(nft.equals(name)) {
                        		 System.out.println("ISSUER: " + requests[0] + " | " +"NFT ID: " + nftId + " | " + "NFT NAME: " + name + " | " + "NFT URI: " + uri + " | " + "VALUE: " + value + " | " + "VALIDITY: " + requests[4]);
                        	 }
                         }
                     }
                 }
                
            } else if (cmd.equalsIgnoreCase("CANCEL_REQUEST_NFT_TRANSFER")){
                String nftId = console.readLine("Enter the nft id you want to cancel the request: ");
                String cancelRequest = clientId + "|" + nftId + "|" + "cancel_request_nft_transfer"; 
                
                String c = bftMap.remove(cancelRequest).toString();
                
                if (c.equals("0")){
                	System.out.println("Can't cancel that request!");
                } else {
                	System.out.println("Request cancelled!");
                }

            } else if (cmd.equalsIgnoreCase("PROCESS_NFT_TRANSFER")) {
                int nftId = 0;
                
                try {
                	nftId = Integer.parseInt(console.readLine("Enter the nft id: "));
                } catch (NumberFormatException e) {
                	System.out.println("Invalid input: The ID is supposed to be an integer!"); 
                }
                
                int buyerId = 0;
                try {
                	buyerId = Integer.parseInt(console.readLine("Enter the buyer id: "));
                } catch (NumberFormatException e) {
                	System.out.println("Invalid input: The value is supposed to be an integer!"); 
                }
                
                String accept = console.readLine("Do you accept the transfer? (True/False)");
            
                String transferRequest = clientId + "|" + nftId + "|" + buyerId + "|" + accept + "|" + "process_nft_transfer" ;

                String p = bftMap.process_nft_transfer(random_key, transferRequest).toString();
                random_key = random.nextInt(10000);

                if (p.equals("0")) {
                    System.out.println("You are not the owner of this NFT!");
                } else if (p.equals("1")){
                	System.out.println("There is no request on your NFT!");
                } else if (p.equals("2")){
                	System.out.println("ERROR! Coins don't belong to buyer!");
                }else if (p.equals("3")){
                	System.out.println("Error!");
                }else if (p.equals("4")){
                	System.out.println("Offer declined!");
                }else {
                    System.out.println("Transfer successful");
                }
                
            } else if (cmd.equalsIgnoreCase("EXIT")) {

                System.out.println("\tEXIT: Bye bye!\n");
                System.exit(0);

            } else {
                System.out.println("\tInvalid command :P\n");
            }
        }
    }
    
    public static boolean isValidIndex(String[] arr, int index) {
        try {
        	String i = arr[index];
        } catch (IndexOutOfBoundsException e) {
            return false;
        }
        return true;
    }

}
