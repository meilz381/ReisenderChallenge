package application;

import org.apache.commons.math3.ml.clustering.Clusterable;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Punkt implements Clusterable {
	private int x;
	private int y;
	private boolean flag;
	
	// Konstruktor
	public Punkt(int x, int y)
	{
		int maximum = 700;
		if (x<0)
			return;
		if (y<0)
			return;
		if (x>maximum)
			return;
		if (y>maximum)
			return;
		this.x = x;
		this.y = y;
		flag = false;
	}
	
	public int getX()
	{
		return x;
	}
	
	public int getY()
	{
		return y;
	}
	
	public boolean getFlag()
	{
		return flag;
	}
	
	public void setFlag(boolean f)
	{
		flag = f;
	}
	
	public Punkt kopie()
	{
		Punkt punkt = new Punkt(x, y);
		punkt.setFlag(flag);
		return punkt;
	}
	
	public boolean equals(Punkt p)
	{
		return (this.distanzZu(p) == 0);
	}
	
	public double distanzZu(Punkt p)
	{
		return Math.sqrt((x-p.x)*(x-p.x)+(y-p.y)*(y-p.y));
	}
	
	public void zeichne(GraphicsContext gc)
	{
		// Punkt gibt Zentrum des Punktes an	
		double durchmesser = 6;
		gc.setFill(Color.GRAY);
		gc.fillOval(x-durchmesser/2, y-durchmesser/2, durchmesser, durchmesser);		
	}

	@Override
	public double[] getPoint() {
		double []point = {x, y};
		return point;
	}
}
