package application;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Linie implements Comparable<Linie>{
	private Punkt start;
	private Punkt ende;
	
	public Linie(Punkt s, Punkt e)
	{
		start = s;
		ende = e;
	}
	
	public Punkt getStart()
	{
		return start;
	}
	
	public Punkt getEnde()
	{
		return ende;
	}
	
	public void setStart(Punkt s)
	{
		start = s;
	}
	
	public void setEnde(Punkt e)
	{
		ende = e;
	}
	
	public Linie kopie()
	{
		Punkt s = start.kopie();
		Punkt e = ende.kopie();
		return new Linie(s, e);
	}
	
	public Linie kopieGleichePunkte()
	{
		return new Linie(start, ende);
	}
	
	public double lange()
	{
		return start.distanzZu(ende);
	}
	
	public boolean equals(Linie l)
	{
		return (this.start.equals(l.start) && this.ende.equals(l.ende));
	}
	
	public void zeichne(GraphicsContext gc)
	{
		// zeichnet Linie von start zu ende
		gc.setLineWidth(1);
		gc.setStroke(Color.RED);
		gc.strokeLine(start.getX(), start.getY(), ende.getX(), ende.getY());	
	}

	@Override
	public int compareTo(Linie o) {
		if (lange() < o.lange())
			return -1;
		else if (lange() == o.lange())
			return 0;
		else
			return 1;
	}
}
