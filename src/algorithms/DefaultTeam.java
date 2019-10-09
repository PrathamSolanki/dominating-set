package algorithms;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class DefaultTeam {
	private int getDegree(Point point, ArrayList<Point> points, int edgeThreshold) {
		int degree = 0;
		for (Point p : points) {
			if (p.distance(point) <= edgeThreshold) {
				degree++;
			}
		}
		return degree;
	}
	
	private ArrayList<Point> getNeighbors(Point point, ArrayList<Point> points, int edgeThreshold) {
		ArrayList<Point> result = new ArrayList<>(Arrays.asList(point));
		
		for (Point p : points) {
			if (p.distance(point) <= edgeThreshold) {
				result.add(p);
			}
		}
		return result;
	}


	private boolean isDominating(ArrayList<Point> D, ArrayList<Point> P, int edgeThreshold) {
		ArrayList<Point> clone = (ArrayList<Point>) P.clone();
	
		for (Point d: D) {
		  clone.removeAll(getNeighbors(d, P, edgeThreshold));
		}
		
		if (clone.isEmpty()) return true;
		return false;
	  }


  private ArrayList<Point> kNaive(ArrayList<Point> solution, ArrayList<Point> points, int edgeThreshold) {
	for(int i = 0; i < solution.size(); i++) {
		Point a = solution.get(i);
		for(int j = i+1; j < solution.size() ;j++) {
			Point b = solution.get(j);
			if(a.distance(b) >= 3 * edgeThreshold) {
				continue;
			}
			Point median = new Point((a.x+b.x)/2, (a.y+b.y)/2);
			ArrayList<Point> rest = (ArrayList<Point>) points.clone();
			for(int k = 0; k < rest.size(); k++) {
				Point x = rest.get(k);
				if(x.distance(median) <= edgeThreshold) {
					ArrayList<Point> attempt = (ArrayList<Point>) solution.clone();
					
					attempt.remove(a);
					attempt.remove(b);
					
					attempt.add(x);
					
					if(isDominating(attempt, points, edgeThreshold)) {
						return attempt;
					}				 
				}
			}  
		}	 
	}
	return solution;
}


  public ArrayList<Point> calculDominatingSet(ArrayList<Point> points, int edgeThreshold) {
    ArrayList<Point> result = new ArrayList<Point>();

    ArrayList<Point> rest = (ArrayList<Point>)points.clone();

    while (!rest.isEmpty()) {
      Point v = rest.get(0);
      for (Point r: rest) if (getDegree(r, rest, edgeThreshold) > getDegree(v, rest, edgeThreshold)) v = r;
      result.add(v);      
      rest.removeAll(getNeighbors(v, rest, edgeThreshold));
      rest.remove(v);
	}
	
	while (true) {
		int oldNumDomPoints = result.size();
		result = kNaive(result, points, edgeThreshold);
		if (result.size() >= oldNumDomPoints) break;
	}

    return result;
  }
  
  
  //FILE PRINTER
  private void saveToFile(String filename,ArrayList<Point> result){
    int index=0;
    try {
      while(true){
        BufferedReader input = new BufferedReader(new InputStreamReader(new FileInputStream(filename+Integer.toString(index)+".points")));
        try {
          input.close();
        } catch (IOException e) {
          System.err.println("I/O exception: unable to close "+filename+Integer.toString(index)+".points");
        }
        index++;
      }
    } catch (FileNotFoundException e) {
      printToFile(filename+Integer.toString(index)+".points",result);
    }
  }
  private void printToFile(String filename,ArrayList<Point> points){
    try {
      PrintStream output = new PrintStream(new FileOutputStream(filename));
      int x,y;
      for (Point p:points) output.println(Integer.toString((int)p.getX())+" "+Integer.toString((int)p.getY()));
      output.close();
    } catch (FileNotFoundException e) {
      System.err.println("I/O exception: unable to create "+filename);
    }
  }

  //FILE LOADER
  private ArrayList<Point> readFromFile(String filename) {
    String line;
    String[] coordinates;
    ArrayList<Point> points=new ArrayList<Point>();
    try {
      BufferedReader input = new BufferedReader(
          new InputStreamReader(new FileInputStream(filename))
          );
      try {
        while ((line=input.readLine())!=null) {
          coordinates=line.split("\\s+");
          points.add(new Point(Integer.parseInt(coordinates[0]),
                Integer.parseInt(coordinates[1])));
        }
      } catch (IOException e) {
        System.err.println("Exception: interrupted I/O.");
      } finally {
        try {
          input.close();
        } catch (IOException e) {
          System.err.println("I/O exception: unable to close "+filename);
        }
      }
    } catch (FileNotFoundException e) {
      System.err.println("Input file not found.");
    }
    return points;
  }
}
