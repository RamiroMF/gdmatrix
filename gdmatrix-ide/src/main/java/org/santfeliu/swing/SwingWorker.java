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
package org.santfeliu.swing;

import javax.swing.SwingUtilities;

/**
 *
 * @author realor
 */
public class SwingWorker
{
  public final void construct()
  {
    Thread thread = new Thread(new Runnable()
    {
      @Override
      public void run()
      {
        try
        {
          doWork();
        }
        finally
        {
          finished();
        }
      }
    });
    thread.start();
  }

  protected final void notify(final int code, final Object data)
  {
    if (SwingUtilities.isEventDispatchThread())
    {
      doNotify(code, data);
    }
    else
    {
      SwingUtilities.invokeLater(new Runnable()
      {
        @Override
        public void run()
        {
          doNotify(code, data);
        }
      });
    }
  }

  protected final void finished()
  {
    SwingUtilities.invokeLater(new Runnable()
    {
      @Override
      public void run()
      {
        doFinished();
      }
    });
  }

  // working thread
  protected void doWork()
  {
  }
  
  // Event dispatch thread
  protected void doNotify(int code, Object data)
  {
  }
  
  // Event dispatch thread
  protected void doFinished()
  {
  }
}
