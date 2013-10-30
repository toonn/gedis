Gedistribueerde systemen
========================

Java RMI: Een CarRentalAgency
-----------------------------

Om de code uit te voeren:
(Wij hebben alle relatieve paden geschreven met het idee dat de uitvoer
 gestart wordt in bin/. Een voorbeeld: trips is bij ons
  "../assignment-RMI-23_src/trips")

rmiregistry
java rentalagency/SessionCreationService
java rental/RentalServer "host"
java client/Client "host"

Het host argument is optioneel en default naar localhost.

Wanneer we dit zelf doen op deze manier slagen alle testen
(alles geeft een 'correct' totaal).

We hebben geprobeerd om het programma gedistribueerd te draaien maar bij het
opstarten van rental/RentalServer krijgen we
"java.rmi.ConnectException: Connection refused to host: 127.0.1.1;".
We vermoeden dat dit is omdat de /etc/hosts file op de computers in de labo's
op cw niet juist zijn. De hostname's worden daar gemapt op 127.0.1.1 .
We hebben dus de die de java.rmi.serverhostname property invult verwijderd en
vermoeden dat onze implementatie dus niet zonder verandering of extra jvm
argumenten (-Djava.rmi....) gedistribueerd gedraait kan worden.
