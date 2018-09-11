package application;

import java.util.LinkedList;

public class Partikel {
	
	//Vektoren
	private double[] velocityVector;
	private double[] positionVector;
	private double[] bestFoundVector;
	
	private double bestFoundVectorFitness = 0;

	LinkedList<Punkt> route;
	private double fitness;
	
	//Konstanten
	private static double intertiaWeight = 0.85;
	private static double c1 = 0.5;
	private static double c2 = 2;
	
	private int anzahlStadte;
	
	public Partikel(int anzahlStadte) {		
		this.anzahlStadte = anzahlStadte;
		velocityVector = new double[anzahlStadte];
		positionVector = new double[anzahlStadte];
		
		for (int i = 0; i < anzahlStadte; i++) {
			velocityVector[i] = Math.random();
			positionVector[i] = Math.random();
		}
		bestFoundVector = positionVector;
	}
	
	
	public Partikel(double[] positionVector, int anzahlStadte) {
		this.positionVector = positionVector;
		this.anzahlStadte = anzahlStadte;
		velocityVector = new double[anzahlStadte];
		for (int i = 0; i < anzahlStadte; i++) {
			velocityVector[i] = Math.random();
		}
		bestFoundVector = positionVector;
	}
	
	public void updateVectors(double[] bestFoundVectorGlobal) {
		for (int i = 0; i < anzahlStadte; i++) {
			velocityVector[i] = velocityVector[i] * intertiaWeight 
					+ (c1 * Math.random() * (bestFoundVector[i] - positionVector[i])) 
					+ (c2 * Math.random() * (bestFoundVectorGlobal[i] - positionVector[i]));
			positionVector[i] += velocityVector[i];
		}
		
		if (fitness > bestFoundVectorFitness) {
			bestFoundVectorFitness = fitness;
			bestFoundVector = positionVector;
		}
	}


	public double[] getPositionVector() {
		return positionVector;
	}


	public void setPositionVector(double[] positionVector) {
		this.positionVector = positionVector;
	}


	public double[] getBestFoundVector() {
		return bestFoundVector;
	}


	public void setBestFoundVector(double[] bestFoundVector) {
		this.bestFoundVector = bestFoundVector;
	}


	public LinkedList<Punkt> getRoute() {
		return route;
	}


	public void setRoute(LinkedList<Punkt> route) {
		this.route = route;
	}


	public double getFitness() {
		return fitness;
	}


	public void setFitness(double fitness) {
		this.fitness = fitness;
	}
	
}
