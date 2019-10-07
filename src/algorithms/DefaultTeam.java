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
import java.util.TreeMap;
import java.util.HashMap;
import java.util.List;
import java.util.HashSet;
import java.util.Set;

public class DefaultTeam {
  public ArrayList<Point> calculDominatingSet(ArrayList<Point> points, int edgeThreshold) {
    //REMOVE >>>>>
    ArrayList<Point> rest = (ArrayList<Point>)points.clone();
    ArrayList<Point> solution = new ArrayList<>();
    
    //for (int i=0;i<points.size()/3;i++) result.remove(0);
    // if (false) result = readFromFile("output0.points");
    // else saveToFile("output",result);
    //<<<<< REMOVE
    
    while (!rest.isEmpty()) {
    	Point v = rest.get(0);
    	for (Point r : rest) {
    		if (degree(r, rest, edgeThreshold) > degree(v, rest, edgeThreshold)) {
    			v = r;
    		}
    	}
    	solution.add(v);
    	rest.removeAll(neighbours(v, rest, edgeThreshold));
    }
    
    //for (int k=1; k <= 5; k++) {
    int k = 4;	
	int old_score = Integer.MAX_VALUE;
    int current_score = solution.size();
	while (current_score < old_score) {
    	solution = improve(solution, points, edgeThreshold, k);
    	old_score = current_score;
    	current_score = solution.size();
    }
    //}
    
    return solution;
  }
  
  
  private int degree(Point point, ArrayList<Point> points, int edgeThreshold) {
	  int degree = 0;
	  for (Point p : points) {
		  if (p.distance(point) <= edgeThreshold) {
			  degree++;
		  }
	  }
	  return degree;
  }
  
  private ArrayList<Point> neighbours(Point point, ArrayList<Point> points, int edgeThreshold) {
	  ArrayList<Point> result = new ArrayList<>(Arrays.asList(point));
	  
	  for (Point p : points) {
		  if (p.distance(point) <= edgeThreshold) {
			  result.add(p);
		  }
	  }
	  return result;
  }
  
  private ArrayList<Point> improve(ArrayList<Point> solution, ArrayList<Point> points, int edgeThreshold, int k) {
	  
	  ArrayList<Point> newSolution = (ArrayList<Point>)solution.clone();
	  //HashMap<Point, TreeMap<Double, Point>> distances = initDistances(solution);
	 
	  for (Point s1 : solution) {
		  TreeMap<Double, Point> distances = initDistances(solution, s1);
		  ArrayList<Point> closestPoints = findClosestPoints(distances, k);
		  if (newSolution.containsAll(closestPoints)) {
			  newSolution.removeAll(closestPoints);
		  }
		  else {
			  continue;
		  }
		  
		  Set<Point> ps = getClosePoints(points, closestPoints, edgeThreshold);
		  for (Point[] combination : getCombinations(new ArrayList<>(ps), k-1)) {
			  newSolution.addAll(Arrays.asList(combination));
			  if (isDominatingSet(newSolution, points, edgeThreshold)) {
				  return newSolution;
			  }
			  newSolution.removeAll(Arrays.asList(combination));
		  }
		  newSolution.addAll(closestPoints);
	  }
	  
//	  for (Point s1 : solution) {
//		  for (Point s2 : solution) {
//			  
//			  if (newSolution.contains(s1) && newSolution.contains(s2)) {
//				  newSolution.remove(s1);
//				  newSolution.remove(s2);
//			  }
//			  else if (!newSolution.contains(s1)) {
//				  break;
//			  }
//			  else {
//				  continue;
//			  }
//			  
//			  boolean found = false;
//			  for (Point p : points) {
//				  newSolution.add(p);
//				  if (!isDominatingSet(newSolution, points, edgeThreshold)) {
//					  newSolution.remove(p);
//				  }
//				  else {
//					  found = true;
//					  break;
//				  }
//			  }
//			  if (!found) {
//				  newSolution.add(s1);
//				  newSolution.add(s2);
//			  }
//			  
//		  }
//	  }
	  
	  return solution;
  }
  
  private List<Point[]> getCombinations (List<Point> points, int k) {
	  List<Point[]> combinations = new ArrayList<>();
	  getCombinations_r(combinations, new Point[k], points, 0, points.size()-1, 0);
	  return combinations;
  }
  
  private void getCombinations_r (List<Point[]> combinations, Point[] data, List<Point> points, int start, int end, int index) {
	  if (index == data.length) {
		  Point[] combination = data.clone();
		  combinations.add(combination);
	  }
	  else if (start <= end) {
		  data[index] = points.get(start);
		  getCombinations_r(combinations, data, points, start+1, end, index+1);
		  getCombinations_r(combinations, data, points, start+1, end, index);
	  }
  }
  
  private TreeMap<Double, Point> initDistances (ArrayList<Point> points, Point p) {
	  //HashMap<Point, TreeMap<Double, Point>> distances = new HashMap<>();
	  
	  TreeMap<Double, Point> distances = new TreeMap<>();
	  
	  for (Point p2: points) {
		  distances.put(p2.distance(p), p2); // I get the point itself as well, so it is included in the closest points
	  }
//	  distances.put(p, distancesOfPoint);
//	  
//	  for (Point p : points) {
//		  TreeMap<Double, Point> distancesOfPoint = new TreeMap<>(); 
//		  for (Point p2: points) {
//			  distancesOfPoint.put(p2.distance(p), p2); // I get the point itself as well, so it is included in the closest points
//		  }
//		  distances.put(p, distancesOfPoint);
//	  }
	  
	  return distances;
  }
  
  private ArrayList<Point> findClosestPoints(TreeMap<Double, Point> distances, int k) {
	  ArrayList<Point> closestPoints = new ArrayList<>();
	 
	  for (int i=0; i < k; i++) {
		  closestPoints.add(distances.get(distances.firstKey()));
		  distances.remove(distances.firstKey());  // Each point of the solution will be processed only one for each improve operation
	  }
	  
	  return closestPoints;
  }
  
  private boolean isDominatingSet(ArrayList<Point> solution, ArrayList<Point> points, int edgeThreshold) {
	  for (Point p : points) {
		  boolean found = false;
		  for (Point s : solution) {
			  if (p.distance(s) <= edgeThreshold) {
				  found = true;
				  break;
			  }
		  }
		  if (found == false) return false;
	  }
	  return true;
  }
  
  private Set<Point> getClosePoints(ArrayList<Point> points, ArrayList<Point> referencePoints, int edgeThreshold) {
	  Set<Point> result = new HashSet<>();
	  
	  for (Point p : points) {
		  for (Point r : referencePoints) {
			  if (p.distance(r) <= edgeThreshold) {
				  result.add(p);
				  break;
			  }
		  }
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
