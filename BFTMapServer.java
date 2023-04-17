/**
 * BFT Map implementation (server side).
 *
 */
package intol.bftmap;

import bftsmart.tom.MessageContext;
import bftsmart.tom.ServiceReplica;
import bftsmart.tom.server.defaultservices.DefaultSingleRecoverable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;

public class BFTMapServer<K, V> extends DefaultSingleRecoverable {
    private final Logger logger = LoggerFactory.getLogger("bftsmart");
    private final ServiceReplica replica;
    private TreeMap<K, V> replicaMap;

    //The constructor passes the id of the server to the super class
    public BFTMapServer(int id) {
        replicaMap = new TreeMap<>();
        replica = new ServiceReplica(id, this, this);
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Use: java BFTMapServer <server id>");
            System.exit(-1);
        }
        new BFTMapServer<Integer, String>(Integer.parseInt(args[0]));
    }

    @Override
    public byte[] appExecuteOrdered(byte[] command, MessageContext msgCtx) {
        try {
            BFTMapMessage<K,V> response = new BFTMapMessage<>();
            BFTMapMessage<K,V> request = BFTMapMessage.fromBytes(command);
            BFTMapRequestType cmd = request.getType();
            
            Random random = new Random();

            logger.info("Ordered execution of a {} request from {}", cmd, msgCtx.getSender());

            switch (cmd) {
                //write operations on the map
                case PUT:
                    V oldValue = replicaMap.put(request.getKey(), request.getValue());

                    if (oldValue != null) {
                        response.setValue(oldValue);
                    }
                    return BFTMapMessage.toBytes(response);
                    
                case MINT:
                	String[] k = request.getValue().toString().split("\\|");
                	
                	//only clients with id 
                	if(k[0].equals("4")) {
                		V v = replicaMap.put(request.getKey(), request.getValue());
                        if(v != null) {
                            response.setValue(v);
                        }else {
                        	response.setValue(request.getKey());
                        }
                        return BFTMapMessage.toBytes(response);
                	}
                	
                case SPEND:
                	String[] s = request.getValue().toString().split("\\|");
                	String [] coins = s[3].toString().split(",");

                	int sum_coins = 0;

                	for (String coin : coins) {
                		
                		try {
                			String [] info = replicaMap.get(Integer.parseInt(coin)).toString().split("\\|");          			

                			// Check if the coin belongs to the client
                			if (Integer.parseInt(s[0]) != Integer.parseInt(info[0])) {
                            	
                				response.setValue(0); 
                				return BFTMapMessage.toBytes(response);
                            	
                			}else {
                				sum_coins += Integer.parseInt(info[1]);
                			}
                		} catch(NullPointerException e) {
                			response.setValue(1); 
            				return BFTMapMessage.toBytes(response);
              
                		}
                	}
                	
                	if(sum_coins >= Integer.parseInt(s[2])) {

                		//create new coin
                		V r_coin = (V) (s[1] + "|" + s[2] + "|" + "coin");
                		replicaMap.put(request.getKey(), r_coin);

                        //remove all coins used in the transaction
                		for (String transaction_coins : coins) {
                			V removed = replicaMap.remove(Integer.parseInt(transaction_coins));
                        }

                        //create new coin for the sender
                		
                		int sender_value = sum_coins - Integer.parseInt(s[2]);
                		
                		K key = (K) Integer.valueOf(Integer.valueOf(request.getKey().toString())+1000);                		

                		if (sender_value > 0) {
                			V sender_coin = (V) (s[0] + "|" + sender_value + "|" + "coin"); 
                    		
                            int random_key = random.nextInt(10000);
                            
                    		V newCoin = replicaMap.put(key, sender_coin);

                    		if(newCoin != null) {
                                response.setValue(newCoin);
                            }else {
                            	response.setValue(key);
                            }
                		}
                		response.setValue(key);
                        return BFTMapMessage.toBytes(response);
                		
                	}else {
                        response.setValue(2);
                        return BFTMapMessage.toBytes(response);
                	}
            }
     

            return null;
        }catch (IOException | ClassNotFoundException ex) {
            logger.error("Failed to process ordered request", ex);
            return new byte[0];
        }
    }

    @Override
    public byte[] appExecuteUnordered(byte[] command, MessageContext msgCtx) {
        try {
            BFTMapMessage<K,V> response = new BFTMapMessage<>();
            BFTMapMessage<K,V> request = BFTMapMessage.fromBytes(command);
            BFTMapRequestType cmd = request.getType();

            logger.info("Unordered execution of a {} request from {}", cmd, msgCtx.getSender());

            switch (cmd) {
                //read operations on the map
                case GET:
                    V ret = replicaMap.get(request.getKey());

                    if (ret != null) {
                        response.setValue(ret);
                    }
                    return BFTMapMessage.toBytes(response);
                
                case KEYSET:
                    Set<K> keySet = replicaMap.keySet();
                    response.setKeySet(keySet);

                    return BFTMapMessage.toBytes(response);
            }
        } catch (IOException | ClassNotFoundException ex) {
            logger.error("Failed to process unordered request", ex);
            return new byte[0];
        }
        return null;
    }

    @Override
    public byte[] getSnapshot() {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutput out = new ObjectOutputStream(bos)) {
            out.writeObject(replicaMap);
            out.flush();
            bos.flush();
            return bos.toByteArray();
        } catch (IOException ex) {
            ex.printStackTrace(); //debug instruction
            return new byte[0];
        }
    }

    @Override
    public void installSnapshot(byte[] state) {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(state);
             ObjectInput in = new ObjectInputStream(bis)) {
            replicaMap = (TreeMap<K, V>) in.readObject();
        } catch (ClassNotFoundException | IOException ex) {
            ex.printStackTrace(); //debug instruction
        }
    }

}
