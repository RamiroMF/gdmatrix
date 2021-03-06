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
package org.santfeliu.elections.service;

/**
 *
 * @author unknown
 */
public class DBPoliticalPartyCall
{
  private String provinceId;
  private String townId;
  private String typeId;
  private String date;
  private String partyId;
  private int councillors;
  private int position;
  
  public DBPoliticalPartyCall()
  {
  }

  public void setProvinceId(String provinceId)
  {
    this.provinceId = provinceId;
  }

  public String getProvinceId()
  {
    return provinceId;
  }

  public void setTownId(String townId)
  {
    this.townId = townId;
  }

  public String getTownId()
  {
    return townId;
  }

  public void setTypeId(String typeId)
  {
    this.typeId = typeId;
  }

  public String getTypeId()
  {
    return typeId;
  }

  public void setDate(String date)
  {
    this.date = date;
  }

  public String getDate()
  {
    return date;
  }

  public void setPartyId(String partyId)
  {
    this.partyId = partyId;
  }

  public String getPartyId()
  {
    return partyId;
  }

  public void setCouncillors(int councillors)
  {
    this.councillors = councillors;
  }

  public int getCouncillors()
  {
    return councillors;
  }

  public void setPosition(int position)
  {
    this.position = position;
  }

  public int getPosition()
  {
    return position;
  }
}
