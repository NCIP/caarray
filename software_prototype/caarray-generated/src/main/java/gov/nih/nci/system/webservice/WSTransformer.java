package gov.nih.nci.system.webservice;

import gov.nih.nci.common.util.Constant;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;

import org.apache.log4j.Logger;

/*
 * Created on May 16, 2006
 * Shaziya Muhsin
 * 
 */
public class WSTransformer {
    private static Logger log = Logger.getLogger(WSTransformer.class);  
    private Properties beanProperties = new Properties();    
    private boolean processOntology = true;
    private boolean implFlag;
    private boolean wsPackage = true;
    
    public WSTransformer(String beanFileName) throws Exception{       
        try{
            if(beanFileName == null){
                beanFileName = "cacoreBeans.properties";
            }
            loadClassNames(beanFileName);            
        }catch(Exception ex){
            log.error(ex.getMessage());
            throw new Exception(ex.getMessage());
        }
        
    }
    
    public void setProcessOntology (boolean ontology){
        processOntology = ontology;
    }
    
   
    
    private  void loadClassNames(String beanFileName) throws Exception{       
        List fileList = new ArrayList();
        if(beanFileName != null){
            if(beanFileName.indexOf(Constant.COMMA)>0){
                StringTokenizer st = new StringTokenizer(beanFileName,",");
                while(st.hasMoreTokens()){
                    fileList.add(st.nextToken());
                }
            }
            else{
                fileList.add(beanFileName);
            }
            for(int f=0; f<fileList.size(); f++){
                try{
                    String fileName = (String)fileList.get(f);
                    if(fileName !=null && fileName.length()>0){
                        beanProperties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName));
                    }
                }catch(Exception ex){
                    log.error(ex.getMessage());
                    throw new Exception("Error: "+ex.getMessage());
                }
                
            }
        }
        else{
            log.error("Error: Unable to locate property files");
            throw new Exception("Error: Unable to locate property files");
        }
    }
    
    //====================
    
//  ===========================================================================
    public boolean getProcessOntology(){
        return processOntology;
    }
    //===========================================================================
    

    
      public Object buildSearchCriteria(Object obj) throws Exception{
        
          Class objKlass;
          Object newObject;
          try {
                objKlass = obj.getClass();              
                String objFullName = objKlass.getName();
                String newObjectName = objFullName.replaceAll(".ws.", ".");
                if (newObjectName.endsWith("Impl"))
                {
                    newObjectName = newObjectName.substring(0, newObjectName.length()-4);
                }
                Class newObjClass = Class.forName(newObjectName); 
                newObject= newObjClass.newInstance();               
                newObject = buildCriteria(obj, newObject);              
            
        }catch (Exception e) {
            log.error("WS Error"+ e.getMessage());              
            throw new Exception (e.getMessage());
        }
        return newObject;
    }
      
      //==========================================================================
      private Object buildCriteria(Object criteria, Object newObject){          
            try{
                
                Field[] fields = criteria.getClass().getDeclaredFields();
                Field[] newFields = newObject.getClass().getDeclaredFields();
                
                for(int i=0; i<fields.length; i++){
                    fields[i].setAccessible(true);
                    Field field = fields[i];
                    String fieldName = field.getName();
                    String fieldType = field.getType().getName();
                    
                    if(fieldName.equalsIgnoreCase("serialVersionUID")){
                        continue;
                    }
                    if(field.get(criteria)!=null){
                        Object value = field.get(criteria);
                        Field newField = findFieldByName(newFields, fieldName);                 
                        newField.setAccessible(true);
                        if(fieldName.endsWith("Collection")){
                            if(((Collection)value).size()>0){                               
                                String bean = fieldName.substring(0,fieldName.indexOf("Collection"));
                                bean = bean.substring(0,1).toUpperCase() + bean.substring(1);
                                String beanClassName =  getClassName(bean); 
                              if(beanClassName == null){
                                  beanClassName = getOntologyClassName(bean);
                              }
                             
                                if(beanClassName != null){
                                    Object assoObject = Class.forName(beanClassName).newInstance();                                 
                                    List newList = new ArrayList();
                                    Set setList = new HashSet();                        
                                    Vector vector = new Vector();
                                
                                    for(Iterator it = ((Collection)value).iterator(); it.hasNext();){
                                        assoObject = buildCriteria(it.next(),assoObject);
                                        if(assoObject != null){
                                            if(fieldType.endsWith("Collection")){
                                                newList.add(assoObject);
                                            }
                                            else if(fieldType.endsWith("Vector")){
                                                vector.add(assoObject);
                                            }
                                            else if(fieldType.endsWith("Set")){
                                                setList.add(assoObject);
                                            }                                               
                                        }                           
                                    }                                   
                                    if(newList.size()>0){                                       
                                        newField.set(newObject, newList);
                                    }
                                    else if(setList.size()>0){                                      
                                        newField.set(newObject, setList);
                                    }
                                    else if(vector.size()>0){                                       
                                        newField.set(newObject, vector);
                                    }
                                }
                            }                       
                        } 
                        else if(fieldType.startsWith("java") || field.getType().isPrimitive()){                 
                            if(value != null){
                                newField.set(newObject, value);
                            }
                        }
                        else{
                            String bean = fieldType.substring(fieldType.lastIndexOf(Constant.DOT)+1);                        
                            String beanClass = getClassName(bean);  
                          
                            if(beanClass != null){                          
                                Object assoObject = Class.forName(newField.getType().getName()).newInstance();                          
                                if(value != null){                              
                                    assoObject = buildCriteria(value, assoObject);                              
                                    try{
                                        if(assoObject != null){                                     
                                            newField.set(newObject, assoObject);                                        
                                        }                                   
                                    }
                                    catch(Exception ex){                                    
                                        log.error("Exception: ", ex);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            catch(Exception ex){
            	log.error("Exception: ", ex);
            }
            return newObject;
           }

      
      //==========================================================================
      
      private String getClassName(String className){
          boolean found = false;
          String cName = null;
            if(beanProperties != null){
                for(Iterator i= beanProperties.keySet().iterator(); i.hasNext();){
                    String key = (String)i.next();                 
                     if(className.lastIndexOf(Constant.DOT)>1){
                        if(key.equals(className)){
                            found=true;
                            cName = key;
                            break;
                        }
                    }
                    else{
                        if(key.substring(key.lastIndexOf(Constant.DOT)+1).equals(className)){
                            found=true;
                            cName = key;
                            break;
                        }
                    }            
                }
            }
            return cName;
        }
      //==========================================================================
      private Field findFieldByName(Field[] fields, String fieldName){          
            Field field = null;
            for(int i=0; i<fields.length; i++){             
                fields[i].setAccessible(true);              
                if(fields[i].getName().equals(fieldName)){                  
                    field = fields[i];
                    break;
                }
            }
            return field;
        }
      //==========================================================================
      public List generateWSResults(List results) throws Exception{     
          
          List alteredResults = new ArrayList();
          if(results.size()>0){
              for(int i=0; i<results.size(); i++){                
                  Object result = results.get(i);
                 
                  String resultClassName = result.getClass().getName();
                  Object newResult = null;
                  
                  if(implFlag){
                      resultClassName = resultClassName+"Impl";                       
                  }
                  if(wsPackage){                      
                      int index = resultClassName.lastIndexOf(Constant.DOT);
                      String className = resultClassName.substring(0, index)+".ws."+ resultClassName.substring(index + 1);
                      newResult = Class.forName(className).newInstance(); 
                  }
                  else{
                      newResult = Class.forName(resultClassName).newInstance();
                  }                  
                  
                  generateWSResults(result, newResult, result.getClass(), newResult.getClass());
                  Class superClass = result.getClass().getSuperclass();
                  
                  
                  if(superClass != null && !superClass.equals(Object.class) && !superClass.isInterface()){
                      Class newSuperClass = newResult.getClass().getSuperclass();
                      generateWSResults(result, newResult, superClass, newSuperClass );
                      superClass = superClass.getSuperclass();
                      
                  }
                  alteredResults.add(newResult);
              }
          }
          else{
              return null;
          } 
         
          return alteredResults;
      }
      //==========================================================================
      private void generateWSResults(Object result, Object newResult, Class resultClass, Class newResultClass) throws Exception{
          
          try{
              Field[] fields = resultClass.getDeclaredFields();
              Field[] newFields = newResultClass.getDeclaredFields();
              if(newFields.length == 0 && !newResultClass.getSuperclass().equals(Object.class)){
                  newFields = newResultClass.getSuperclass().getDeclaredFields();
              }
              
              if(newFields.length > 0){
                  for(int i=0; i< fields.length; i++){
                      fields[i].setAccessible(true);
                      String fieldName = fields[i].getName();
                      String fieldType = fields[i].getType().getName();
                      
                      if(fieldName.equalsIgnoreCase("serialVersionUID")){
                            continue;
                        }
                      Field newField = this.findFieldByName(newFields, fieldName);
                      
                      if(newField != null){
                          if(fieldName.endsWith("Collection") && fieldType.startsWith("java")){
                              String bean = fieldName.substring(0,1).toUpperCase() + fieldName.substring(1, fieldName.indexOf("Collection"));
                              String beanClassName = getClassName(bean);
                              if(beanClassName != null){
                                  Collection value = new ArrayList();
                                  newField.set(newResult, value);
                              }
                          }
                          else if(!((fields[i].getType().isPrimitive() ||fieldType.startsWith("java") && !fieldType.endsWith("Collection")))){
                              newField.set(newResult, null);
                          }
                          else{
                              Object value = fields[i].get(result);
                              if(value != null){
                                  newField.set(newResult, value);
                              }
                          }
                      }                     
                  }
              }
              
          }catch(Exception ex){
              throw new Exception(ex.getMessage());
          }
      }
     
      //  ==========================================================================
      
      public String getSearchClassName(String targetClassName)throws Exception{          
          String searchClassName = null;
          String className = null;
          
          if(targetClassName.indexOf(".ws.")>0){
              className = targetClassName.replaceAll(".ws.",".");      
              wsPackage = true;                          
          }
          else{
              className = targetClassName;              
              wsPackage = false;
          } 
          
          searchClassName = getClassName(className);
          
          if(searchClassName == null && className.endsWith("Impl")){
              implFlag = true;
              String cName = className.substring(0,className.lastIndexOf("Impl"));
              searchClassName = getClassName(cName);              
          }   
          else{
              implFlag = false;
          }
         
          if(searchClassName == null){
              throw new Exception("Invalid class name: "+ targetClassName);
          }
          return searchClassName;
      }
    
      //  ==========================================================================
      public Object getSearchCriteria(Object criteria)throws Exception{
          Object searchCriteria = null;
          try{
              if(criteria.getClass().getName().indexOf(".ws.")>0){
                  searchCriteria = buildSearchCriteria(criteria);            
              }
              else{              
                  searchCriteria = criteria;                  
              }
          }catch(Exception ex){
              throw new Exception(ex.getMessage());
          }
         return searchCriteria; 
      }
      //==========================================================================
    private String getOntologyClassName(String beanName){
        String ontologyBeanName = null;
        String ontologyClassName = null;
        if(beanName.indexOf("Ontology")>0 && processOntology){
            if(beanName.startsWith("Child") && beanName.length()>5){
                ontologyBeanName = beanName.substring(5);
            }
            else if(beanName.startsWith("Parent")){
                ontologyBeanName = beanName.substring(6);
            }
            
            if(ontologyBeanName != null){
                ontologyClassName = getClassName(ontologyBeanName);
            }
        }
        
        return ontologyClassName;
    } 
      //==========================================================================

   
}
