

import java.util.*; 
  
 class Comparer implements Comparator { 
 public int compare(Object obj1, Object obj2) 
 { 
   int i1 = ((Integer)obj1).intValue(); 
   int i2 = ((Integer)obj2).intValue(); 
  
   return Math.abs(i1) - Math.abs(i2); 
 } 
  
 } 
  
 public class collect { 
  
 public static void main(String args[]) 
 { 
   Vector vec = new Vector(); 
  
   vec.addElement(new Integer(-200)); 
   vec.addElement(new Integer(100)); 
   vec.addElement(new Integer(400)); 
   vec.addElement(new Integer(-300)); 
  
  /** Calling Collections.sort on this vector sorts the vector in a 
    * natural way, that is, from lowest to highest according to the values 
    * contained in the wrappers. 
    */ 
  
   Collections.sort(vec); 
   for (int i = 0; i < vec.size(); i++) { 
    int e=((Integer)vec.elementAt(i)).intValue(); 
    System.out.println(e); 
   } 
   System.out.println(); 
  /** This ordering is done by defining a class "Comparer" that 
    * implements the java.util.Comparator interface, returning < 0, 0, or > 0 
    * according to whether the first element is less than, equal to, or greater 
    * than the second. 
    */ 
  
   Collections.sort(vec, new Comparer()); 
   for (int i = 0; i < vec.size(); i++) { 
     int e=((Integer)vec.elementAt(i)).intValue(); 
     System.out.println(e); 
   } 
 } 
  
 }