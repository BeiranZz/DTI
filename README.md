# Project 2 – BFT-SMaRt Decentralized Token Infrastructure (DTI)

Trabalho realizado por:

Pedro Santos nº 53677  
Diogo Toupa nº 60382 
Rui Silva nº 57643 

# Instructions:

Inside the the project folder, run:
In Linux: ./gradlew installDist
In Windows: gradlew installDist

Now inside build/install/library.

1) Open five terminals (or tabs in a terminal)
2)  On the first four, run the following commands (one on each):
./smartrun.sh intol.bftmap.BFTMapServer 0
./smartrun.sh intol.bftmap.BFTMapServer 1
./smartrun.sh intol.bftmap.BFTMapServer 2
./smartrun.sh intol.bftmap.BFTMapServer 3
3) Wait until first four terminals print “Ready to process operations”
4) On the last terminal, run the client (ONLY CLIENT "4" CAN MINT COINS)
./smartrun.sh intol.bftmap.BFTMapInteractiveClient <ClientID>
  5) Close the application using CTR-C on five terminals.


