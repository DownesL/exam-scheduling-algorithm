# Take home-opdracht over metaheuristieken
## Literatuur
Om een goed beeld te schetsen van de opdracht heb ik besloten enkele papers te lezen over Exam scheduling.
Een eerste paper was "A mew model for automated examination timetabling" geschreven door McCollum, B., McMullam P., Parkes, A.J., Burke, E.K. en Qu, R. Zij bespreken een geavenceerd model dat gebruikt kan worden om efficient examenroosters op te stellen. Het model representeert het probleem dat gesteld wrd tijdens de 2nd International Timetabling Competition (ITC2007). Het doel van de paper is om het verschil tussen de realiteit en het bestaande model van het probleem te verkleinen. Er wordt dus geen diepgaande uitleg gedaan over hoe het probleem opgelost kan worden, maar legt wel kort uit welke oplossingsmethodes de beste scores kregen in de competitie.

De twweede paper die ik gelezen heb is "Meta-heuristic approches for the University Course Timetabling Problem" door Sina, A., Razali, Y., Say Leng, G. en Salwani, A. Die paper ging niet over het probleem dat wij moesten oplossen, maar er werden wel allerlei oplossingsmogelijkheden besproken die op beide problemen toepasbaar zijn. Voorbeelden hiervan zijn verschillende "population-based approaches" zoals "Evolutionary Algorithms" en "Swarm Intelligence" of "Single solution-based approaches" zoals Tabu Search of Simulated annealing.

De derde paper, "Addressing Examiniation Timetabling Problem Using a Partial Exams Approach in Constructive and Improvement" door Mandal, A. K., Kahar, M. N. M. en Kendall, G., bood me een efficiente kostfunctie aan. De paper haalde meer aan dan gewoon een kostfunctie, maar gezien de deadline niet oneindig ver is, koos ik ervoor enkel hun kostfunctie mijn eigen oplossing te implementeren. De auteurs beschrijven hoe het gemiddelde van de kost per student geminimaliseerd moet worden. De kost kan berekend worden door som te nemen van elke cel van de conflict matrix vermenigvuldigd met een factor die afhankelijk is van de nabijheid van 2 examens.
De conflict matrix wordt opgesteld door in elke cel, met de coordinaten gelijk aan de id's van de 2 examens, de grootte van de doorsnede van de aanwezigen, bij de examens, te plaatsen.
Deze methode om de kost te berekenen is efficient omdat je niet telkens opnieuw meerdere keren moet itereren over alle examens en studenten om een juist antwoord te bekomen, maar de doorsnede kan opzoeken in de conflictmatrix adhv de ID's en dan kan vermenigvuldigen met de nabijheidsfactor.

## Framework

Ik heb ervoor gekozen zelf een klein framework uit te shrijven dat leek op dat van Meneer Demeester, maar toch uitbreiding mogelijk maakte. Mijn oplossing bestaan telkens uit het maken van een initiele oplossing en dan een verbeteringsfase.

### Initele oplossing
De initiele oplossing wordt gevonden door de timeslots op te vullen en na te gaan of er geen harde constraints worden gebroken.
Vervolgens wordt er een verbetering toegepast. Deze verbetering zorgt ervoor dat de minst opgevulde timeslots voor de meest opgevulde timeslots worden geplaats. Dit zorgt voor een betere initiele score om de verbeteringsfase op te starten.

### Verbeteringsfase

#### 2-type Local search
Mijn 1ste oplossing bekom ik door een simpele local search algoritme toe te passen op mijn initiele oplossing. Elke iteratie wordt een verandering toegepast, nl. een exame wordt in een andere tijdslot geplaatst (als deze beweging geen hardconstraint breekt). Hierdoor bekom ik een goede oplossing. Om te zorgen dat ik uit lokale minima geraak, gebruik ik een 2e soort veranderingsfunctie. Hierbij worden 2 timeslots van plek gewisseld om te zien of er een verbetering plaatsvindt. Om de 1000 iteraties van de 1ste beweging voer ik 100 iteraties uit van de 2e beweging.

#### Late Acceptance
Voor mijn 2e algoritme gebruik een versie van Late Acceptance. Om zeker te zijn dat ik toch nog een beetje betere scores krijg, doe ik weer om de 1000 iteraties 100 iteraties van de 2e beweging.

## Resultaten

Mijn resultaten zijn redelijk goed. Voor mijn 1ste algoritme kom ik op (met een gunstige seed) gemakkelijk op een score van 168 volgens de benchmark.
Het 2e algoritme scoort een beetje minder goed, en steeds ongeveer 5-10 punten hoger.
Opmerkelijk is dat de beste scores volgens mijn berekeningen niet het beste resultaat geven volgens de benchmark applicatie en dit doet mij vermoeden dat ik toch iets fout doe.
