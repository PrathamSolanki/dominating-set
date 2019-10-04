package algorithms;

import java.awt.Point;
import java.util.ArrayList;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class DefaultTeam {
  private ArrayList<Point> getNeighbors(Point p, ArrayList<Point> points, int edgeThreshold){
    ArrayList<Point> neighbors = new ArrayList<Point>();

    for (Point r: points) if (!p.equals(r) && p.distance(r) <= edgeThreshold) neighbors.add(r);
    return neighbors;
  }


  private int getDegree(Point p, ArrayList<Point> points, int edgeThreshold) {
    int degree = 0;

    for (Point r: points) if (!p.equals(r) && p.distance(r) <= edgeThreshold) degree++;
    return degree;
  }


  private boolean isDominating(ArrayList<Point> D, ArrayList<Point> P, int edgeThreshold) {
    ArrayList<Point> dominatedPoints = new ArrayList<Point>();

    for (Point d: D) {
      dominatedPoints.addAll(getNeighbors(d, P, edgeThreshold));
      dominatedPoints.add(d);
    }
    
    if (dominatedPoints.size() == P.size()) return true;
    return false;
  }


  private ArrayList<Point> kNaiveLocalSearch(ArrayList<Point> D, ArrayList<Point> P, int k, int edgeThreshold) {
    ArrayList<Point> clone = new ArrayList<Point>();
    for (Point d: D) {
      clone = (ArrayList<Point>)D.clone();
      clone.remove(d);
      if (isDominating(clone, P, edgeThreshold)) break;
    }
    return clone;
  }


  public ArrayList<Point> calculDominatingSet(ArrayList<Point> points, int edgeThreshold) {
    ArrayList<Point> graph = (ArrayList<Point>)points.clone();
    ArrayList<Point> result = new ArrayList<Point>();

    // for (Point p1: points) {
    //   boolean flag = false;
    //   for (Point p2: points) {
    //     if (p1 == p2) continue;
    //     else if (p1.distance(p2) <= edgeThreshold) {
    //       flag = true;
    //       break;
    //     }
    //   }
    //   if (flag != true) graph.remove(p1);
    //   // else if (getDegree(p1, points, edgeThreshold) < 3) graph.remove(p1);
    // }

    ArrayList<Point> rest = (ArrayList<Point>)graph.clone();

    while (!rest.isEmpty()) {
      Point v = rest.get(0);
      for (Point r: rest) if (getDegree(r, rest, edgeThreshold) > getDegree(v, rest, edgeThreshold)) v = r;
      result.add(v);      
      rest.removeAll(getNeighbors(v, rest, edgeThreshold));
      rest.remove(v);
    }

    return kNaiveLocalSearch(result, graph, 1, edgeThreshold);
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
