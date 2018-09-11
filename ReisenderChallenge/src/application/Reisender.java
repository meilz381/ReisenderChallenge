package application;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.DoubleStream;

import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.ml.clustering.Clusterable;
import org.apache.commons.math3.ml.clustering.FuzzyKMeansClusterer;
import org.apache.commons.math3.ml.distance.EuclideanDistance;

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
	
	public LinkedList<Punkt> linKernighan(LinkedList<Punkt> punktRoute) {
		//Weg T
		LinkedList<Linie> ausgangsroute = convertToRoute(punktRoute);
		
		// backtrackingtiefe
		int p1 = 4;
		// unzulässigkeitstiefe
		int p2 = 6;
		
		for (int i = 0; i < 0; i++) {
			// wähle einen Knoten v1
			Punkt v1 = stadte.get(i);
			
			// wähle eine Kante x1 = [v1, v2]
			Linie x1;
			for (Linie l : ausgangsroute) {
				if (l.getStart() == v1) {
					x1 = l;
					break;
				}
			}
			
			// wähle eine Kante y1 = [v2, v3] die nicht in T enthalten ist, so das D1 > 0
			
			
		}
		
		
		return null;
	}
	
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
						
						ArrayList<Double> lange = new ArrayList<>();
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
	
	/*public LinkedList<Punkt> three_opt(LinkedList<Punkt> route, int maxOpt) {
		LinkedList<Punkt> besteRoute = new LinkedList<>(route);
		boolean change = true;
		while (maxOpt > 0 && change) {
			change = false;
			for (int i = 1; i < besteRoute.size(); i++) {
				for (int j = i + 1; j < besteRoute.size(); j++) {
					double bestLange = langeDerRoutePoints(besteRoute);
					LinkedList<Punkt> subRouteA = new LinkedList<>(besteRoute.subList(0, i));
					LinkedList<Punkt> subRouteB = new LinkedList<>(besteRoute.subList(i, j));
					LinkedList<Punkt> subRouteC = new LinkedList<>(besteRoute.subList(j, besteRoute.size()));
					
					
					ArrayList<LinkedList<Punkt>> subRouteList = new ArrayList<>();
					for (int x = 0; x < 2; x++) {
						for (int y = 0; y < 2; y++) {
								LinkedList<Punkt> subRoute = new LinkedList<>(subRouteA);
								if (x == 0)
									subRoute.addAll(subRouteB);
								else
									subRoute.addAll(reverseLinkedList(subRouteB));
								if (y == 0)
									subRoute.addAll(subRouteC);
								else
									subRoute.addAll(reverseLinkedList(subRouteB));
						}
					}	
					
					LinkedList<Punkt> nextRoute = new LinkedList<>(besteRoute);
					
					for (LinkedList<Punkt> potSubroute: subRouteList) {	
						if (langeDerRoutePoints(potSubroute) < bestLange) {
							nextRoute = potSubroute;
							bestLange = langeDerRoutePoints(potSubroute);
						}
					}
					
					besteRoute = nextRoute;
				}
			}
			maxOpt--;	
		}
		return besteRoute;
	}
	*/
	
	public LinkedList<Punkt> four_opt(LinkedList<Punkt> route, int maxOpt) {
		LinkedList<Punkt> besteRoute = new LinkedList<>(route);
		boolean change = true;
		while (maxOpt > 0 && change) {
			change = false;
			for (int i = 1; i < besteRoute.size(); i++) {
				for (int j = i + 1; j < besteRoute.size(); j++) {
					for (int k = j + 1; k < besteRoute.size(); k++) {
						double bestLange = langeDerRoutePoints(besteRoute);
						LinkedList<Punkt> subRouteA = new LinkedList<>(besteRoute.subList(0, i));
						LinkedList<Punkt> subRouteB = new LinkedList<>(besteRoute.subList(i, j));
						LinkedList<Punkt> subRouteC = new LinkedList<>(besteRoute.subList(j, k));
						LinkedList<Punkt> subRouteD = new LinkedList<>(besteRoute.subList(k, besteRoute.size()));
						
						
						ArrayList<LinkedList<Punkt>> subRouteList = new ArrayList<>();
						for (int x = 0; x < 2; x++) {
							for (int y = 0; y < 2; y++) {
								for (int z = 0; z < 2; z++) {
									LinkedList<Punkt> subRoute = new LinkedList<>(subRouteA);
									if (x == 0)
										subRoute.addAll(subRouteB);
									else
										subRoute.addAll(reverseLinkedList(subRouteB));
									if (y == 0)
										subRoute.addAll(subRouteC);
									else
										subRoute.addAll(reverseLinkedList(subRouteB));
									if (z == 0)
										subRoute.addAll(subRouteD);
									else
										subRoute.addAll(reverseLinkedList(subRouteD));
								}
							}
						}	
						
						LinkedList<Punkt> nextRoute = new LinkedList<>(besteRoute);
						
						for (LinkedList<Punkt> potSubroute: subRouteList) {	
							if (langeDerRoutePoints(potSubroute) < bestLange) {
								nextRoute = potSubroute;
								bestLange = langeDerRoutePoints(potSubroute);
							}
						}
						
						besteRoute = nextRoute;
					}
				}
			}
			maxOpt--;
		}
		return besteRoute;
	}	
	
	public LinkedList<Punkt> reverseLinkedList(LinkedList<Punkt> route) {
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
			ArrayList<Punkt> besucht = new ArrayList<>();
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
			ArrayList<Punkt> unbesucht = new ArrayList<>();
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
	
	public LinkedList<Punkt> fcmGSPO(int iterations) {
		int clusters = 5;
		LinkedList<Punkt> route = stadte;;
		if (stadte.size() > 200) {
			// city clustering
			ArrayList<LinkedList<Punkt>> cityCluster = cityClustering(clusters);
			
			// generate tour path for each subcluster
			for (LinkedList<Punkt> cluster : cityCluster) {
				cluster = generateInitialTourPath(cluster);
				cluster = two_opt(cluster, 5);
			}
			
			// merge of subcluster tour paths
			route = new LinkedList<>(cityCluster.get(0));
			for (int i = 1; i < clusters; i++) {
				ArrayList<Linie> verbindungen = new ArrayList<>();
				for (Punkt p1 : route) {
					for (Punkt p2 : cityCluster.get(i)) {
						verbindungen.add(new Linie(p1, p2));
					}
				}
				Collections.sort(verbindungen);
				Linie verbindung1 = verbindungen.get(0);
				int insertionPoint1 = route.indexOf(verbindung1.getStart());
				int insertionPoint2 = cityCluster.get(i).indexOf(verbindung1.getEnde());
				for (int j = 0; j < cityCluster.get(i).size(); j++) {
					route.add(insertionPoint1 +1, cityCluster.get(i).get((insertionPoint2 + j) % cityCluster.get(i).size()));
				}
			}
		}
		route = GPSO(iterations, route);
		
		return route;
	}
	
	private ArrayList<LinkedList<Punkt>> cityClustering(int clusters){
		EuclideanDistance distanceMeasure = new EuclideanDistance();
		FuzzyKMeansClusterer<Clusterable> fuzzyKMeansClusterer;
		fuzzyKMeansClusterer = new FuzzyKMeansClusterer<>(clusters, 1.2, 5000, distanceMeasure);
		Collection<Clusterable> test = new ArrayList<>(stadte);
		fuzzyKMeansClusterer.cluster(test);
		
		RealMatrix membershipMatrix = fuzzyKMeansClusterer.getMembershipMatrix();
		// crisp clustering
		ArrayList<LinkedList<Punkt>> cityClusters = new ArrayList<>();
		for (int i = 0; i < clusters; i++) {
			cityClusters.add(new LinkedList<>());
		}
		for (int i = 0; i < stadte.size(); i++) {
			RealVector membershipVektor = membershipMatrix.getRowVector(i);
			cityClusters.get(membershipVektor.getMaxIndex()).add(stadte.get(i));
		}
		
		return cityClusters;
	}
	
	
	private LinkedList<Punkt> convertToPunktRoute(double[] positionVektor) {
		ArrayIndexComparator comparator = new ArrayIndexComparator(positionVektor);
		Integer[] indexes = comparator.createIndexArray();
		Arrays.sort(indexes, comparator);
		LinkedList<Punkt> routePartikel = new LinkedList<>();
		for (int k = 0; k < stadte.size(); k++) {
			routePartikel.add(stadte.get(indexes[k]));
		}
		return routePartikel;
	}
	
	private LinkedList<Punkt> generateInitialTourPath(LinkedList<Punkt> cityCluster) {
		// calculate the geographical center
		double centerX = 0;
		double centerY = 0;
		for (int i = 0; i < cityCluster.size(); i++) {
			centerX += cityCluster.get(i).getX();
			centerY += cityCluster.get(i).getY();
		}
		centerX /= cityCluster.size();
		centerY /= cityCluster.size();
		
		// calculate orientation angle of city to the geographical center
		Map<Double, Punkt> punktOrientationAngleMap = new TreeMap<Double, Punkt>();
		
		for (int i = 0; i < stadte.size(); i++) {
			punktOrientationAngleMap.put(180/Math.PI * Math.atan((stadte.get(i).getY() - centerY) / (stadte.get(i).getX() - centerX)), stadte.get(i));
		}
		
		// generate a tour path according to the orientation angle value
		LinkedList<Punkt> route = new LinkedList<>();
		for(Punkt p : punktOrientationAngleMap.values()) {
			route.add(p);
		}
		
		return stadte;	
	}

	public LinkedList<Punkt> GPSO(int iterations, LinkedList<Punkt> startRoute) {
		
		int countCrossover = 50;
		int countMutation = 30;
		
		int countPartikel = 100;
		
		double[] bestFoundVector = new double[stadte.size()];
		/*for (int i = 0; i < stadte.size(); i++) {
			bestFoundVector[i] = Math.random();
		}
		*/
		for (int i = 0; i < startRoute.size(); i++) {
			bestFoundVector[stadte.indexOf(startRoute.get(i))] = 1 / Math.sqrt(i + 1);
		}
		
		
		LinkedList<Punkt> bestFoundRoute = convertToPunktRoute(bestFoundVector);
		double bestFountVectorFitness = 1 / langeDerRoutePoints(bestFoundRoute);
		
		Random r = new Random();
		
		ArrayList<Partikel> partikel = new ArrayList<>();
		for (int i = 0 ; i < countPartikel; i++) {
			partikel.add(new Partikel(stadte.size()));
		}
		
		
		for (int i = 0; i < iterations; i++) {
			
			ArrayList<Partikel> partikelNewGeneration = new ArrayList<>(partikel);
			
			//// evaluate individuals based on fitness function
			for (int j = 0; j < countPartikel; j++) {
				Partikel partikelchen = partikel.get(j);
				double[] positionVectorPartikel = partikelchen.getPositionVector();
				//generate Route
				LinkedList<Punkt> routePartikel = convertToPunktRoute(positionVectorPartikel);
				partikelchen.setRoute(routePartikel);
				partikelchen.setFitness(1 / langeDerRoutePoints(routePartikel));
			}
			
			
			//// Selection Scheme --- linear ranking
			// sort by fitness
			double[] fitnessPartikel = new double[countPartikel];
			for (int j = 0; j < countPartikel; j++) {
				fitnessPartikel[j] = partikel.get(j).getFitness();
			}
			ArrayIndexComparator comparator = new ArrayIndexComparator(fitnessPartikel);
			Integer[] indexes = comparator.createIndexArray();
			Arrays.sort(indexes, comparator);
			//Arrays.sort(indexes, Collections.reverseOrder());
			
			double minFitness = partikel.get(indexes[countPartikel - 1]).getFitness();
			double maxFitness = partikel.get(indexes[0]).getFitness();
			double[] selectionProbability = new double[countPartikel];
			for (int j = 0; j < countPartikel; j++) {
				selectionProbability[j] = 1/countPartikel * (maxFitness - (j) * (maxFitness - minFitness) / (countPartikel));
			}
			
			
			double totalFitness = DoubleStream.of(fitnessPartikel).sum();
			double[] relativeFitness = new double[countPartikel];
			double sum = 0;
			for (int j = 0; j < countPartikel; j++) {
				sum += selectionProbability[j];
				relativeFitness[j] = sum;
			}
			
			//// Recombination
			
			TreeMap<Double, Partikel> selectionPropabilityPartikelMap = new TreeMap<>();
			for (int j = 0; j < countPartikel; j++) {
				selectionPropabilityPartikelMap.put(relativeFitness[j], partikel.get(indexes[j]));
			}
			
			double[] randomNumbers = ThreadLocalRandom.current().doubles(0, totalFitness).distinct().limit(countCrossover * 2).toArray();
			
			// wähle die Chromosomen
			for (int j = 0; j < countCrossover; j++) {
				Partikel partikel1, partikel2;
				// Individuen
				Map.Entry<Double, Partikel> p1Low = selectionPropabilityPartikelMap.floorEntry(randomNumbers[j]);
				Map.Entry<Double, Partikel> p1High = selectionPropabilityPartikelMap.ceilingEntry(randomNumbers[j]);
				Map.Entry<Double, Partikel> p2Low = selectionPropabilityPartikelMap.floorEntry(randomNumbers[j + 1]);
				Map.Entry<Double, Partikel> p2High = selectionPropabilityPartikelMap.ceilingEntry(randomNumbers[j + 1]);
				partikel1 = (p1Low == null) ? p1High.getValue() : p1Low.getValue();
				partikel2 = (p2Low == null) ? p2High.getValue() : p2Low.getValue();
				
				double[] partikel1PositionVector = partikel1.getPositionVector().clone();
				double[] partikel2PositionVector = partikel2.getPositionVector().clone();
				
				int crossOverPoint = r.nextInt(stadte.size());
				double[] copyPartikel1PositionVector = partikel1.getPositionVector().clone();
				for (int k = crossOverPoint; k < stadte.size(); k++) {
					partikel1PositionVector[k] = partikel2PositionVector[k];
					partikel2PositionVector[k] = copyPartikel1PositionVector[k];
				}
				
				partikelNewGeneration.add(new Partikel(partikel1PositionVector, stadte.size()));
				partikelNewGeneration.add(new Partikel(partikel2PositionVector, stadte.size()));
			}
			
			// Mutation
			for (int j = 0; j < countMutation; j++) {
				int randomNumberChoosePartikel = r.nextInt(partikelNewGeneration.size());
				double[] partikelPositionVectorMutation = new double[stadte.size()];
				partikelPositionVectorMutation = partikelNewGeneration.get(randomNumberChoosePartikel).getPositionVector();
				
				int randomNumberMutatePartikel = r.nextInt(stadte.size());
				partikelPositionVectorMutation[randomNumberMutatePartikel] = Math.random();
				partikelNewGeneration.add(new Partikel(partikelPositionVectorMutation, stadte.size()));
				
			}
			
			// evalutate fitness again
			for (int j = 0; j < partikelNewGeneration.size(); j++) {
				Partikel partikelchen = partikelNewGeneration.get(j);
				double[] positionVectorPartikel = partikelchen.getPositionVector();
				//generate Route
				LinkedList<Punkt> routePartikel = convertToPunktRoute(positionVectorPartikel);
				partikelchen.setRoute(routePartikel);
				partikelchen.setFitness(1 / langeDerRoutePoints(routePartikel));
			}
			
			// sort by fitness
			double[] fitnessPartikel2 = new double[partikelNewGeneration.size()];
			for (int j = 0; j < partikelNewGeneration.size(); j++) {
				fitnessPartikel2[j] = partikelNewGeneration.get(j).getFitness();
			}
			ArrayIndexComparator comparator2 = new ArrayIndexComparator(fitnessPartikel2);
			Integer[] indexes2 = comparator2.createIndexArray();
			Arrays.sort(indexes2, comparator2);
			//Arrays.sort(indexes2, Collections.reverseOrder());
			
			maxFitness = partikelNewGeneration.get(indexes2[0]).getFitness();
			
			if (maxFitness < bestFountVectorFitness) {
				bestFountVectorFitness = maxFitness;
				bestFoundRoute = partikel.get(indexes[countPartikel - 1]).getRoute();
				bestFoundVector = partikel.get(indexes[countPartikel - 1]).getPositionVector();
			}
			bestFoundVector = partikel.get(indexes[0]).getPositionVector();
			
			// replace parent population, delete n last
			int nLast = countCrossover + countMutation;
			ArrayList<Partikel> removeList = new ArrayList<>();
			for (int j = 0; j < nLast; j++) {
				removeList.add(partikelNewGeneration.get(indexes2[nLast - 1 - j]));
			}
			partikelNewGeneration.removeAll(removeList);
			
			//// update Partikel
			//update position vektor
			for (int j = 0; j < partikelNewGeneration.size(); j++) {
				partikelNewGeneration.get(j).updateVectors(bestFoundVector);
			}
		}
		return bestFoundRoute;
	}
	
	
	
	

	public LinkedList<Linie> berechneRoute(long startzeit)
	{
		/*// genetische algorithmen
		int iterations = 0;
		
		if (stadte.size() == 16) {
			iterations = 100;
		}
		else if (stadte.size() == 40) {
			iterations = 100;
		}
		else {
			iterations = 50;
		}
		LinkedList<Punkt> gpso = fcmGSPO(iterations);
		gpso = two_opt(gpso, 5);
		gpso = three_opt(gpso, 5);
		gpso = four_opt(gpso, 1);
		LinkedList<Linie> gspoRoute = convertToRoute(gpso);*/
		
		
		
		////Klassische Algorithmen
		
		LinkedList<Punkt> dnnh = DNHH();
		if (stadte.size() < 200) {
			dnnh = two_opt(dnnh, 5);
			dnnh = three_opt(dnnh, 5);
//			dnnh = four_opt(dnnh, 1);
		}
		else {
			dnnh = two_opt(dnnh, 5);
			dnnh = three_opt(dnnh, 5);
//			dnnh = four_opt(dnnh, 1);
		}
		LinkedList<Linie> dnnhRoute = convertToRoute(dnnh);
		
		//NNH
		LinkedList<Punkt> nhh = NHH();
		if (stadte.size() < 200) {
			nhh = two_opt(nhh, 5);
			nhh = three_opt(nhh, 5);
//			nhh = four_opt(nhh, 1);
		}
		else {
			nhh = two_opt(nhh, 5);
			nhh = three_opt(nhh, 5);
//			nhh = four_opt(nhh, 1);
		}
		LinkedList<Linie> nhhRoute = convertToRoute(nhh);
		
			
		////multi fragment
		LinkedList<Punkt> multiFrag = null;
		LinkedList<Linie> multiFragRoute = null;
		if (stadte.size() < 200) {
			multiFrag = multiFragment();
			multiFrag = two_opt(multiFrag, 5);
			multiFrag = three_opt(multiFrag, 5);
//			multiFrag = four_opt(multiFrag, 1);
			multiFragRoute = convertToRoute(multiFrag);
		}
		
		//s90
		//38.5599
		//28.5888 + 9.971
		//s0
		//39.5077
		//29,5027 + 10.005
		ArrayList<Double> lange = new ArrayList<>();
		lange.add(langeDerRoutePoints(nhh));
		lange.add(langeDerRoutePoints(dnnh));
//		lange.add(langeDerRoutePoints(gpso));
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