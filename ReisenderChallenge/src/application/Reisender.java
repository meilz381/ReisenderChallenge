package application;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javafx.scene.canvas.GraphicsContext;

public class Reisender {
	private LinkedList<Linie> route;
	private LinkedList<Punkt> stadte;

	public Reisender(LinkedList<Punkt> stadte)
	{
		this.stadte = stadte;
		route = new LinkedList<Linie>();
	}
	
	public LinkedList<Linie> convertToRoute(LinkedList<Punkt> points) {
		LinkedList<Linie> route = new LinkedList<>();
		for (int i = 0; i < points.size() - 1; i++) {
			route.add(new Linie(points.get(i), points.get(i + 1)));
		}
		return route;
	}
	
	public double langeDerRoutePoints(LinkedList<Punkt> points) {
		double lange = 0;
		for (int i = 0; i < points.size() - 1; i++) {
			lange += points.get(i).distanzZu(points.get(i + 1));
		}
		return lange;
	}
	
	// Verbesserungsheuristiken
	
	public LinkedList<Punkt> two_opt(LinkedList<Punkt> route, int maxOpt) {
		boolean change = true;
		while (maxOpt > 0 && change) {
			change = false;
			for (int i = 1; i < route.size() - 1; i++) {
				for (int j = i + 1; j < route.size(); j++) {
					double bestLange = langeDerRoutePoints(route);
					LinkedList<Punkt> newRoute = new LinkedList<>(route);
					//tausche punkte j und i
					newRoute.set(i, route.get(j));
					newRoute.set(j, route.get(i));
					// alle kanten zwischen j und i in reverse order
					int dec = 1;
					for (int k = i + 1; k < j; k++) {
						newRoute.set(k, route.get(j - dec));
						dec++;
					}
					if (langeDerRoutePoints(newRoute) < bestLange) {
						route = new LinkedList<>(newRoute);
						bestLange = langeDerRoutePoints(route);
						change = true;
					}
				}
			}
			maxOpt--;
		}
		return route;
	}
	
	public LinkedList<Punkt> three_opt(LinkedList<Punkt> route, int maxOpt) {
		boolean change = true;
		while (maxOpt > 0 && change) {
			change = false;
			for (int i = 1; i < route.size(); i++) {
				for (int j = i + 1; j < route.size(); j++) {
						double startLange = langeDerRoutePoints(route);
						LinkedList<Punkt> subRouteA = new LinkedList<>(route.subList(0, i));
						LinkedList<Punkt> subRouteB = new LinkedList<>(route.subList(i, j));
						LinkedList<Punkt> subRouteC = new LinkedList<>(route.subList(j, route.size()));
						
						//ACB
						LinkedList<Punkt> acbRoute = new LinkedList<>();
						acbRoute.addAll(subRouteA);
						acbRoute.addAll(subRouteC);
						acbRoute.addAll(subRouteB);
						double acbLange = langeDerRoutePoints(acbRoute);
						
						//ACBi
						LinkedList<Punkt> acbiRoute = new LinkedList<>();
						acbiRoute.addAll(subRouteA);
						acbiRoute.addAll(subRouteC);
						acbiRoute.addAll(reverseLinkedList(subRouteB));
						double acbiLange = langeDerRoutePoints(acbiRoute);
						
						//ACiB
						LinkedList<Punkt> acibRoute = new LinkedList<>();
						acibRoute.addAll(subRouteA);
						acibRoute.addAll(reverseLinkedList(subRouteC));
						acibRoute.addAll(subRouteB);
						double acibLange = langeDerRoutePoints(acibRoute);
						
						//ACiBi
						LinkedList<Punkt> acibiRoute = new LinkedList<>();
						acibiRoute.addAll(subRouteA);
						acibiRoute.addAll(reverseLinkedList(subRouteC));
						acibiRoute.addAll(reverseLinkedList(subRouteB));
						double acibiLange = langeDerRoutePoints(acibiRoute);
						
						//ABCi
						LinkedList<Punkt> abciRoute = new LinkedList<>();
						abciRoute.addAll(subRouteA);
						abciRoute.addAll(subRouteB);
						abciRoute.addAll(reverseLinkedList(subRouteC));
						double abciLange = langeDerRoutePoints(abciRoute);
						
						//ABiC
						LinkedList<Punkt> abicRoute = new LinkedList<>();
						abicRoute.addAll(subRouteA);
						abicRoute.addAll(reverseLinkedList(subRouteB));
						abicRoute.addAll(subRouteC);
						double abicLange = langeDerRoutePoints(abicRoute);
						
						//ABiCi
						LinkedList<Punkt> abiciRoute = new LinkedList<>();
						abiciRoute.addAll(subRouteA);
						abiciRoute.addAll(reverseLinkedList(subRouteB));
						abiciRoute.addAll(reverseLinkedList(subRouteC));
						double abiciLange = langeDerRoutePoints(abiciRoute);
						
						LinkedList<Double> lange = new LinkedList<>();
						lange.add(startLange);
						lange.add(acbLange);
						lange.add(acbiLange);
						lange.add(acibLange);
						lange.add(acibiLange);
						lange.add(abciLange);
						lange.add(abicLange);
						lange.add(abiciLange);
						int indexMinLange = lange.indexOf(Collections.min(lange));
						switch (indexMinLange) {
							case 0:
								break;
							case 1:
								route = acbRoute;
								change = true;
								break;
							case 2:
								route = acbiRoute;
								change = true;
								break;
							case 3:
								route = acibRoute;
								change = true;
								break;
							case 4:
								route = acibiRoute;
								change = true;
								break;
							case 5:
								route = abciRoute;
								change = true;
								break;
							case 6:
								route = abicRoute;
								change = true;
								break;
							case 7:
								route = abiciRoute;
								change = true;
								break;
						}
				}
			}
			maxOpt--;	
		}
			
		return route;
	}

	
	public LinkedList<Punkt> four_opt(LinkedList<Punkt> pfad, long startzeit) {
		LinkedList<Punkt> besteRoute = new LinkedList<>(pfad);
		for (int i = 1; i < besteRoute.size(); i++) {
			if (verbleibendeZeit(startzeit) > 25000) {
				for (int j = i + 1; j < besteRoute.size(); j++) {
					for (int k = j + 1; k < besteRoute.size(); k++) {
						double bestLange = langeDerRoutePoints(besteRoute);
						LinkedList<Punkt> subRouteA = new LinkedList<>(besteRoute.subList(0, i));
						LinkedList<Punkt> subRouteB = new LinkedList<>(besteRoute.subList(i, j));
						LinkedList<Punkt> subRouteC = new LinkedList<>(besteRoute.subList(j, k));
						LinkedList<Punkt> subRouteD = new LinkedList<>(besteRoute.subList(k, besteRoute.size()));
						
						
						LinkedList<LinkedList<Punkt>> potListen = new LinkedList<LinkedList<Punkt>>();
						
						for (int x = 0; x < 2; x++) {
							for (int y = 0; y < 2; y++) {
								for (int z = 0; z < 2; z++) {
										LinkedList<Punkt> gesRoute1 = new LinkedList<Punkt>(subRouteA);
										LinkedList<Punkt> gesRoute2 = new LinkedList<Punkt>(subRouteA);
	
										LinkedList<Punkt> subRoute = new LinkedList<Punkt>();
										if (x == 0)
											subRoute.addAll(subRouteB);
										else
											subRoute.addAll(reverseLinkedList(subRouteB));
										if (y == 0)
											subRoute.addAll(subRouteC);
										else
											subRoute.addAll(reverseLinkedList(subRouteC));
										if (z == 0)
											subRoute.addAll(subRouteD);
										else
											subRoute.addAll(reverseLinkedList(subRouteD));
											
										
										gesRoute1.addAll(subRoute);
										gesRoute2.addAll(reverseLinkedList(subRoute));
										potListen.add(gesRoute1);
										if (!potListen.contains(gesRoute2)) {
											potListen.add(gesRoute2);
										}								
								}
							}
						}
					
						LinkedList<Punkt> nextRoute = new LinkedList<>(besteRoute);
						
						for (LinkedList<Punkt> potRoute: potListen) {	
							if (langeDerRoutePoints(potRoute) < bestLange) {
								nextRoute = potRoute;
								bestLange = langeDerRoutePoints(potRoute);
							}
						}
						
						besteRoute = nextRoute;
					}
				}
			}
		}	
		return besteRoute;
	}
	
	
	/*public LinkedList<Punkt> improvePath(LinkedList<Punkt> route, int depth, ArrayList<Punkt> restrictedVertices) {
		// rekursionsdetph
		int alpha = 5;
		if (depth <= alpha) {
			
			Unternehmen u1 = new Unternehmen();
			u1.setLangeBesteRoute(langeDerRoutePoints(route));
			
			// for every edge
			for (int i = 1; i < route.size() - 1; i++) {
				if (!restrictedVertices.contains(route.get(i))) {
					if ((route.get(i).distanzZu(route.get(i+1)) - route.get(i).distanzZu(route.getLast())) > 0) {
						// if the new tour is an improvement accept it and terminate
						LinkedList<Punkt> invertedRoute = new LinkedList<>(route.subList(0, i));
						invertedRoute.addAll(reverseLinkedList(route.subList(i + 1, route.size() - 1)));
						if (langeDerRoutePoints(invertedRoute) < u1.getLangeBesteRoute()) {
							u1.setBesteRoute(invertedRoute);
							u1.setLangeBesteRoute(u1.getLangeBesteRoute());
							return u1.getBesteRoute();
						}
						// improve Path
						else {
							restrictedVertices.add(route.get(i));
							return improvePath(u1.getBesteRoute(), depth + 1, restrictedVertices);

						}
					}
				}
			}
		}
		else {
			Unternehmen u1 = new Unternehmen();
			Punkt breakingVertice = null;
			// for every edge
			for (int i = 1; i < route.size() - 1; i++) {
				if (!restrictedVertices.contains(route.get(i))) {
					if ((route.get(i).distanzZu(route.get(i+1)) - route.get(i).distanzZu(route.getLast())) > 0) {
						// if the new tour is an improvement accept it and terminate
						LinkedList<Punkt> invertedRoute = new LinkedList<>(route.subList(0, i));
						invertedRoute.addAll(reverseLinkedList(route.subList(i + 1, route.size() - 1)));
						if (langeDerRoutePoints(invertedRoute) < u1.getLangeBesteRoute()) {
							u1.setBesteRoute(invertedRoute);
							u1.setLangeBesteRoute(u1.getLangeBesteRoute());
							breakingVertice = route.get(i);
						}
					}
				}
			}
			if (u1.getLangeBesteRoute() < langeDerRoutePoints(route)) {
				return u1.getBesteRoute();
			}
			else {
				restrictedVertices.add(breakingVertice);
				return improvePath(u1.getBesteRoute(), depth + 1, restrictedVertices);
			}
		}
		return route;
	}*/
	
	public LinkedList<Punkt> reverseLinkedList(List<Punkt> route) {
		LinkedList<Punkt> newRoute = new LinkedList<>(route);
		Collections.reverse(newRoute);
		return newRoute;
	}
	
	
	// Eröffnungsverfahren
	public LinkedList<Punkt> NHH(){
		LinkedList<Punkt> bestRoute = stadte;
		double bestRouteLange = langeDerRoutePoints(stadte);
		for (int i = 0; i < stadte.size(); i++) {
			//NNH
			double lange = 0;
			LinkedList<Punkt> route = new LinkedList<>();
			LinkedList<Punkt> besucht = new LinkedList<>();
			besucht.addAll(stadte);
			Punkt aktStadt = besucht.get(i);
			route.add(aktStadt);
			while (besucht.size() > 1) {
				besucht.remove(aktStadt);
				double minDist = 10000000.D;
				Punkt minStadt = null;
				double dist = 0;
				for (Punkt stadt: besucht) {
					dist = aktStadt.distanzZu(stadt);
					if (dist < minDist) {
						minDist = dist;
						minStadt = stadt;
					}
				}
				route.add(minStadt);
				aktStadt = minStadt;
				lange += minDist;
			}
			if (lange < bestRouteLange) {
				bestRoute = route;
				bestRouteLange = langeDerRoutePoints(bestRoute);
			}
		}
		return bestRoute;
	}
	
	public LinkedList<Punkt> DNHH() {
		LinkedList<Punkt> bestRoute = null;
		double bestLange = 1000000000.D;
		for (int i = 0; i < stadte.size(); i++) {
			//DNNH
			double lange = 0;
			LinkedList<Punkt> route = new LinkedList<>();
			LinkedList<Punkt> unbesucht = new LinkedList<>();
			unbesucht.addAll(stadte);
			//Startknoten
			Punkt firstStadt = unbesucht.get(i);
			double minDist = 10000000.D;
			double dist = 0;
			//wähle Knoten mit minimalen Abstand zum Startknoten
			Punkt lastStadt = null;
			unbesucht.remove(firstStadt);
			for (Punkt stadt: unbesucht) {
				dist = firstStadt.distanzZu(stadt);
				if (dist < minDist) {
					minDist = dist;
					lastStadt = stadt;
				}
			}
			route.add(firstStadt);
			route.add(lastStadt);
			unbesucht.remove(lastStadt);
			while (unbesucht.size() >= 1) {
				minDist = 100000000.D;
				Punkt minStadt = null;
				dist = 0;
				Boolean zuFirstKnoten = false;
				
				for (Punkt stadt: unbesucht) {
					//suche für first Knoten nächste Stadt
					dist = Math.sqrt(Math.pow(firstStadt.getX() - stadt.getX(),2) + Math.pow(firstStadt.getY() - stadt.getY(),2));
					if (dist < minDist) {
						minDist = dist;
						minStadt = stadt;
						zuFirstKnoten = true;
					}
					//suche für last Knonten nächste Stadt
					dist = Math.sqrt(Math.pow(lastStadt.getX() - stadt.getX(),2) + Math.pow(lastStadt.getY() - stadt.getY(),2));
					if (dist < minDist) {
						minDist = dist;
						minStadt = stadt;
						zuFirstKnoten = false;
					}
				}
	
				if (zuFirstKnoten) {
					route.addFirst(minStadt);
					firstStadt = minStadt;
					unbesucht.remove(firstStadt);
				}
				else {
					route.addLast(minStadt);
					lastStadt = minStadt;
					unbesucht.remove(lastStadt);
				}
				lange += minDist;
			}
			//route.addFirst(new Linie(lastStadt, firstStadt));
			if (lange < bestLange) {
				bestRoute = route;
				bestLange = lange;
			}
		}
		return bestRoute;
	}
	
	public LinkedList<Punkt> multiFragment() {
		LinkedList<Punkt> besteRoute = new LinkedList<>();
		double bestLange = 100000000.D;
		
		for (int i = 0; i < stadte.size(); i++) {
			for (int j = i + 1; j < stadte.size(); j++) {
				Punkt startA = stadte.get(i);
				Punkt startB = stadte.get(j);
				LinkedList<Punkt> route = new LinkedList<>();
				route.add(startA);
				route.add(startB);
				
				LinkedList<Punkt> remainingStadte = new LinkedList<>(stadte);
				remainingStadte.remove(startA);
				remainingStadte.remove(startB);
				
				while (!remainingStadte.isEmpty()) {
					Punkt nextPunkt = null;
					Punkt connectPunkt = null;
					double bestDistance = 1000000.D;
					for (Punkt potStadt : remainingStadte) {
						for (Punkt stagedStadt : route) {
							double distance = potStadt.distanzZu(stagedStadt);
							if (distance < bestDistance) {
								nextPunkt = potStadt;
								connectPunkt = stagedStadt;
								bestDistance = distance;
							}
						}
					}
					
					//davor oder danach
					int indexConnectPunkt = route.indexOf(connectPunkt);
					LinkedList<Punkt> davorRoute = new LinkedList<>(route);
					davorRoute.add(indexConnectPunkt, nextPunkt);
					
					LinkedList<Punkt> danachRoute = new LinkedList<>(route);
					danachRoute.add(indexConnectPunkt + 1, nextPunkt);
					
					if (langeDerRoutePoints(davorRoute) < langeDerRoutePoints(danachRoute)) {
						route = davorRoute;
					}
					else {
						route = danachRoute;
					}
					
					remainingStadte.remove(nextPunkt);
					
				}
				if (langeDerRoutePoints(route) < bestLange) {
					bestLange = langeDerRoutePoints(route);
					besteRoute = route;
				}
			}
		}
		return besteRoute;
	}
	
	
	

	public LinkedList<Linie> berechneRoute(long startzeit)
	{
		//dnhh
		LinkedList<Punkt> dnnh = DNHH();
		if (stadte.size() < 200) {
			dnnh = two_opt(dnnh, 5);
			dnnh = three_opt(dnnh, 5);
		}
		else {
			dnnh = two_opt(dnnh, 5);
			dnnh = three_opt(dnnh, 5);
		}
		LinkedList<Linie> dnnhRoute = convertToRoute(dnnh);
		
		//NHH
		LinkedList<Punkt> nhh = NHH();
		if (stadte.size() < 200) {
			nhh = two_opt(nhh, 5);
			nhh = three_opt(nhh, 5);
		}
		else {
			nhh = two_opt(nhh, 5);
			nhh = three_opt(nhh, 5);
		}
		LinkedList<Linie> nhhRoute = convertToRoute(nhh);
		
			
		////multi fragment
		LinkedList<Punkt> multiFrag = null;
		LinkedList<Linie> multiFragRoute = null;
		if (stadte.size() < 200) {
			multiFrag = multiFragment();
			multiFrag = two_opt(multiFrag, 5);
			multiFrag = three_opt(multiFrag, 5);
			multiFragRoute = convertToRoute(multiFrag);
		}
		
		ArrayList<Double> lange = new ArrayList<>();
		lange.add(langeDerRoutePoints(nhh));
		lange.add(langeDerRoutePoints(dnnh));
		if (stadte.size() < 200)
			lange.add(langeDerRoutePoints(multiFrag));
		int indexMinLange = lange.indexOf(Collections.min(lange));
		if (indexMinLange == 0) {
			return nhhRoute;
		}
		else if (indexMinLange == 1) {
			return dnnhRoute;
		}
		else if (indexMinLange == 2) {
			return multiFragRoute;
		}
		else {
			return null;
		}
	}

	// nur zur Hilfe :)
	private long verbleibendeZeit(long startZeit)
	{
		double zeitMultiplier = 6/1000.0;
		long vorhandeneZeit = (long)(zeitMultiplier*stadte.size()*stadte.size()*1000)/1;
		long verbrauchteZeit = System.currentTimeMillis() - startZeit;
		return vorhandeneZeit - verbrauchteZeit;
	}

	public double score(int seitenLange)
	{
		int stadteAnzahl = stadte.size();
		long startZeit = System.currentTimeMillis();
		route = berechneRoute(startZeit);
		long endeZeit = System.currentTimeMillis();
		double berechnungszeitInSek = (endeZeit - startZeit)/1000.0;
		if (berechnungszeitInSek == 0)
			berechnungszeitInSek += 0.001;

		double zeitMultiplier = 6/1000.0;
		double vorhandeneZeitInSekunden = zeitMultiplier*stadteAnzahl*stadteAnzahl;
		double verbleibendeSekunden = vorhandeneZeitInSekunden - berechnungszeitInSek;

		String fehler = "";
		if (stadteAnzahl != stadte.size())
			fehler += "Städteanzahl hat sich verändert! ";
		if (!alleStadteBereist())
			fehler += "Nicht alle Städte bereist! ";
		if (!routeIstZusammenhangend())
			fehler += "Route ist nicht zusammenhängend! ";
		if (verbleibendeSekunden < 0)
			fehler += "Zeitlimit Überschritten! ";;
			if (fehler != "")
			{
				System.out.println(fehler);
				printInfo(verbleibendeSekunden, 0);
				return 0;
			}

			double avgDistance = 0.521405433*seitenLange;
			double score = (avgDistance * (stadteAnzahl-1) + verbleibendeSekunden) /langeDerRoute();
			printInfo(verbleibendeSekunden, score);
			return score;
	}

	private void printInfo(double verbleibendeSekunden, double score)
	{
		System.out.println("Städteanzahl: " + (stadte.size()));
		System.out.println("verbleibende Senkunden: " + verbleibendeSekunden);
		System.out.println("Routenlänge: " + langeDerRoute());
		System.out.println("Score: " + score);
		System.out.println();
	}

	public double langeDerRoute()
	{
		return langeDerRoute(route);
	}

	public double langeDerRoute(LinkedList<Linie> r)
	{
		if (r == null)
			return 0;
		double lange = 0;
		for (Linie linie: r)
			lange += linie.lange();
		return lange;
	}

	public boolean alleStadteBereist()
	{
		if (route == null)
			return false;
		for (Punkt stadt: stadte)
			if (!stadtInRouteEnthalten(stadt))
				return false;
		return true;
	}

	private boolean stadtInRouteEnthalten(Punkt stadt)
	{
		if (route == null)
			return false;
		for (Linie linie: route)
		{
			if (stadt.equals(linie.getStart()))
				return true;
			if (stadt.equals(linie.getEnde()))
				return true;
		}
		return false;
	}

	private boolean routeIstZusammenhangend()
	{
		return routeIstZusammenhangend(route);
	}

	private boolean routeIstZusammenhangend(LinkedList<Linie> r)
	{
		if (r == null)
			return false;
		if (r.size() < 2)
			return true;
		for (int i=0; i < r.size()-1; i++)
			if (!r.get(i).getEnde().equals(r.get(i+1).getStart())) // Wenn Ende von Strecke i != Start von Strecke i+1
				return false;
		return true;
	}

	public void zeichneAlles(GraphicsContext graphischeElemente)
	{
		zeichneStadte(graphischeElemente);
		zeichneRoute(graphischeElemente);
	}

	public void zeichneStadte(GraphicsContext gc)
	{
		if (stadte == null)
			return;
		for (Punkt stadt: stadte)
			stadt.zeichne(gc);
	}

	public void zeichneRoute(GraphicsContext gc)
	{
		if (route == null)
			return;
		for (Linie strecke: route)
			strecke.zeichne(gc);
	}
}