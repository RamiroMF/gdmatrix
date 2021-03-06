/*
 * GDMatrix
 *  
 * Copyright (C) 2020, Ajuntament de Sant Feliu de Llobregat
 *  
 * This program is licensed and may be used, modified and redistributed under 
 * the terms of the European Public License (EUPL), either version 1.1 or (at 
 * your option) any later version as soon as they are approved by the European 
 * Commission.
 *  
 * Alternatively, you may redistribute and/or modify this program under the 
 * terms of the GNU Lesser General Public License as published by the Free 
 * Software Foundation; either  version 3 of the License, or (at your option) 
 * any later version. 
 *   
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT 
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 *    
 * See the licenses for the specific language governing permissions, limitations 
 * and more details.
 *    
 * You should have received a copy of the EUPL1.1 and the LGPLv3 licenses along 
 * with this program; if not, you may find them at: 
 *    
 * https://joinup.ec.europa.eu/software/page/eupl/licence-eupl
 * http://www.gnu.org/licenses/ 
 * and 
 * https://www.gnu.org/licenses/lgpl.txt
 */
package org.santfeliu.dic.util;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.matrix.dic.Property;

/**
 *
 * @author unknown
 */
public class DictionaryUtils
{
  public static Property getPropertyByName(List<Property> propertyList,
    String name)
  {
    if (propertyList != null && name != null)
    {
      for (int i = 0; i < propertyList.size(); i++)
      {
        Property property = propertyList.get(i);
        if (name.equals(property.getName()))
        {
          return property;
        }
      }
    }
    return null;
  }
  
  public static String getPropertyValue(List<Property> propertyList, 
    String name)
  {    
    Property property = getPropertyByName(propertyList, name);
    if (property == null) 
      return null;
    else 
      return (property.getValue().isEmpty() ? null : 
        property.getValue().get(0));
  }

  /*
   * Returns any object's property, named propertyName, as a Property object.
   * This property may be a system property or a dynamic property.
   */
  public static Property getProperty(Object object, String propertyName)
  {
    if (propertyName == null)
      return null;

    try
    {
      Class docClass = object.getClass();
      Method[] docClassMethods = docClass.getMethods();
      for (Method docClassMethod : docClassMethods)
      {
        String methodName = docClassMethod.getName();
        if(methodName.equalsIgnoreCase("get" + propertyName)
          || methodName.equalsIgnoreCase("is" + propertyName))
        {
          Property p = new Property();
          p.setName(propertyName);
          Object value = docClassMethod.invoke(object, new Object[]{});
          if (value instanceof Collection)
          {
            for (Object v : (Collection)value)
            {
              p.getValue().add(v != null ? String.valueOf(v) : null);
            }
          }
          else
            p.getValue().add(value != null ? String.valueOf(value) : null);

          return p;
        }

      }

      //If getter not found tries dynamic properties
      try
      {
        Method getPropertyMethod = docClass.getMethod("getProperty");
        Object result = getPropertyMethod.invoke(object);
        List<Property> properties = (List<Property>) result;

        for (Property p : properties)
        {
          if (p.getName().equals(propertyName))
            return p;
        }
      }
      catch (Exception ex)
      {
      }

      return null;
    }
    catch (Exception e)
    {
      return null;
    }
  }

   /*
   * Sets the value to the property named propertyName.
   * This property may be a system property or a dynamic property.
   * The method tries to set as a system property at first time, if no success then
   * sets as a dynamic property.
   * If property is multivalued and incremental flag is set to true, then the
   * value is added ti current values, else value replace the current.
   */
  public static void setProperty(Object object,
    String propertyName, Object value, boolean incremental)
  {
    //System properties
    Class docClass = object.getClass();
    Method[] docClassMethods = docClass.getDeclaredMethods();
    boolean propertyFound = false;
    int i = 0;
    while (i < docClassMethods.length && !propertyFound)
    {
      Method docClassMethod = docClassMethods[i];
      String methodName = docClassMethod.getName();
      if (methodName.equalsIgnoreCase("set" + propertyName))
      {
        Class[] parameterTypes = docClassMethod.getParameterTypes();
        Class parameterType = parameterTypes[0];
        try
        {
          if (!(Collection.class.isAssignableFrom(parameterType))
            && value != null)
          {
            if (parameterType.equals(value.getClass()))
              docClassMethod.invoke(object, new Object[]{value});
            else if (List.class.isAssignableFrom(value.getClass()))
              docClassMethod.invoke(object, new Object[]{((List)value).get(0)});
          }
          propertyFound = true;
        }
        catch (Exception e)
        {
          propertyFound = false;
        }
      }
      else if (methodName.equalsIgnoreCase("get" + propertyName)
        && !"property".equalsIgnoreCase(propertyName))
      {
        Class returnType = docClassMethod.getReturnType();
        if (List.class.isAssignableFrom(returnType))
        {
          try
          {
            List values =
              (List)docClassMethod.invoke(object, new Object[]{});
            if (!incremental) values.clear();
            if (value != null && List.class.isAssignableFrom(value.getClass()))
            {
              for (int j = 0; j < ((List)value).size(); j++)
              {
                values.add(((List)value).get(j));
              }
            }
            else if (value != null)
              values.add(value);
            propertyFound = true;
          }
          catch (Exception e)
          {
            propertyFound = false;
          }
        }
      }
      i++;
    }

    //Dynamic Properties
    if (!propertyFound)
    {
      try
      {
        Method getPropertyMethod = docClass.getMethod("getProperty");
        Object result = getPropertyMethod.invoke(object);
        List<Property> properties = (List<Property>) result;
        List<Property> removed = new ArrayList();
        boolean propFound = false;
        for (Property property : properties)
        {
          if (propertyName.equalsIgnoreCase(property.getName()))
          {
            if (!incremental) property.getValue().clear();
            if (value != null && List.class.isAssignableFrom(value.getClass()))
              property.getValue().addAll((List)value);
            else if (value != null)
              property.getValue().add(String.valueOf(value));
            else if (value == null)
              //properties.remove(property);
              removed.add(property);
            propFound = true;
          }
        }
        for (Property p : removed)
        {
          properties.remove(p);
        }
        if (!propFound)
        {
          Property p = new Property();
          p.setName(propertyName);
          if (List.class.isAssignableFrom(value.getClass()))
            p.getValue().addAll((List)value);
          else
            p.getValue().add(String.valueOf(value));
          properties.add(p);
        }
      }
      catch (Exception ex)
      {
      }  
    }
  }

  public static void setProperty(Object object,
    String propertyName, Object value)
  {
    setProperty(object, propertyName, value, false);
  }

  public static void setProperties(Object object,
    List<Property> properties)
  {
    if (properties != null && !properties.isEmpty())
    {
      for (Property property : properties)
      {
        setProperty(object, property.getName(), property.getValue());
      }
    }
  }

  public static List<Property> getProperties(Object object)
  {
    List<Property> properties = new ArrayList();
    try
    {
      //Search in getters
      Class docClass = object.getClass();
      Method[] docClassMethods = docClass.getMethods();
      for (Method docClassMethod : docClassMethods)
      {
        String methodName = docClassMethod.getName();
        if(!methodName.equals("getProperty") && (methodName.startsWith("get")
          || methodName.startsWith("is")))
        {
          String propertyName = null;
          if (methodName.startsWith("get"))
            propertyName = StringUtils.uncapitalize(methodName.substring(3));
          else
            propertyName = StringUtils.uncapitalize(methodName.substring(2));
          Property p = new Property();
          p.setName(propertyName);
          Object value = docClassMethod.invoke(object, new Object[]{});
          if (value instanceof Collection)
          {
            for (Object v : (Collection)value)
            {
              p.getValue().add(String.valueOf(v));
            }
          }
          else
            p.getValue().add(String.valueOf(value));

          properties.add(p);
        }
        else if (methodName.equals("getProperty"))
        {
          Object result = docClassMethod.invoke(object);
          List<Property> props = (List<Property>) result;
          properties.addAll(props);
        }
      }
    }
    catch (Exception e)
    {
    }

    return properties;
  }
  
  public static List<Property> getPropertiesFromMap(Map map)
  {
    List<Property> result = null;
    if (map != null && !map.isEmpty())
    {
      result = new ArrayList();
      for (Object item : map.entrySet())
      {
        Map.Entry entry = (Map.Entry)item;
        String propertyName = String.valueOf(entry.getKey());
        Object propertyValue = entry.getValue();
        if (propertyValue instanceof List)
        {
          addProperty(result, propertyName, (List)propertyValue);          
        }
        else
        {
          addProperty(result, propertyName, String.valueOf(propertyValue));
        }
      }
    }
    return result;
  }

  public static boolean containsProperty(Object object,
    String propertyName)
  {
    return getProperty(object, propertyName) != null;
  }

  public static void addProperty(List<Property> properties, String name,
    String value)
  {
    if (properties != null)
    {
      Property property = new Property();
      property.setName(name);
      property.getValue().add(value);
      properties.add(property);
    }
  }

  public static void addProperty(List<Property> properties, String name,
    List values)
  {
    if (properties != null)
    {
      Property property = new Property();
      property.setName(name);
      for (Object value : values)
      {
        property.getValue().add(String.valueOf(value));
      }
      properties.add(property);
    }
  }
}
