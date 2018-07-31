package application;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;

public class Unternehmen {
	
	double langeBesteRoute;
	
	LinkedList<Punkt> besteRoute;
	
	

	public Unternehmen() {
		langeBesteRoute = 100000000.D;
	}

	public double getLangeBesteRoute() {
		return langeBesteRoute;
	}

	public void setLangeBesteRoute(double langeBesteRoute) {
		this.langeBesteRoute = langeBesteRoute;
	}

	public LinkedList<Punkt> getBesteRoute() {
		return besteRoute;
	}

	public void setBesteRoute(LinkedList<Punkt> besteRoute) {
		this.besteRoute = besteRoute;
	}
	
	public double langeDerRoutePoints(LinkedList<Punkt> points) {
		double lange = 0;
		for (int i = 0; i < points.size() - 1; i++) {
			lange += points.get(i).distanzZu(points.get(i + 1));
		}
		return lange;
	}
	
	public void two_opt(int maxOpt) {
		boolean change = true;
		while (maxOpt > 0 && change) {
			change = false;
			for (int i = 1; i < besteRoute.size() - 1; i++) {
				for (int j = i + 1; j < besteRoute.size(); j++) {
					double bestLange = langeDerRoutePoints(besteRoute);
					LinkedList<Punkt> newRoute = new LinkedList<>(besteRoute);
					
					//tausche punkte j und i
					newRoute.set(i, besteRoute.get(j));
					newRoute.set(j, besteRoute.get(i));
					
					// alle kanten zwischen j und i in reverse order
					int dec = 1;
					for (int k = i + 1; k < j; k++) {
						newRoute.set(k, besteRoute.get(j - dec));
						dec++;
					}
					
					if (langeDerRoutePoints(newRoute) < bestLange) {
						besteRoute = new LinkedList<>(newRoute);
						bestLange = langeDerRoutePoints(besteRoute);
						change = true;
					}
				}
			}
			maxOpt--;
		}
	}
	
	public LinkedList<Punkt> reverseLinkedList(LinkedList<Punkt> route) {
		LinkedList<Punkt> newRoute = new LinkedList<>(route);
		Collections.reverse(newRoute);
		return newRoute;
	}
	
	public void three_opt(int maxOpt) {
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
	}
	
	
	public void four_opt(int maxOpt) {
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
	}
	
	

}
