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
package org.santfeliu.ws;

import com.sun.istack.NotNull;
import com.sun.xml.ws.api.ResourceLoader;
import com.sun.xml.ws.api.server.BoundEndpoint;
import com.sun.xml.ws.api.server.Container;
import com.sun.xml.ws.transport.http.servlet.ServletModule;

import javax.servlet.ServletContext;
import javax.xml.ws.WebServiceException;
import java.util.ArrayList;
import java.util.List;
import java.net.URL;
import java.net.MalformedURLException;

/**
 * Provides access to {@link ServletContext} via {@link Container}. Pipes
 * can get ServletContext from Container and use it to load some resources.
 */
class ServletContainer extends Container
{

  private final ServletContext servletContext;
  private final ServletModule module = new ServletModule()
  {

    private final List<BoundEndpoint> endpoints = new ArrayList<BoundEndpoint>();

    public
    @NotNull
    List<BoundEndpoint> getBoundEndpoints()
    {
      return endpoints;
    }

    public
    @NotNull
    String getContextPath()
    {
      // Cannot compute this since we don't know about hostname and port etc
      throw new WebServiceException("Container " +
        ServletContainer.class.getName() + " doesn't support getContextPath()");
    }
  };
  private final ResourceLoader loader = new ResourceLoader()
  {

    public URL getResource(String resource) throws MalformedURLException
    {
      return servletContext.getResource("/WEB-INF/" + resource);
    }
  };

  ServletContainer(ServletContext servletContext)
  {
    this.servletContext = servletContext;
  }

  public <T> T getSPI(Class<T> spiType)
  {
    if (spiType == ServletContext.class)
    {
      return spiType.cast(servletContext);
    }
    if (spiType.isAssignableFrom(ServletModule.class))
    {
      return spiType.cast(module);
    }
    if (spiType == ResourceLoader.class)
    {
      return spiType.cast(loader);
    }
    return null;
  }
}
