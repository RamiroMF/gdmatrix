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
package org.santfeliu.web.servlet;

import com.google.zxing.qrcode.encoder.ByteMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.google.zxing.qrcode.encoder.Encoder;
import com.google.zxing.qrcode.encoder.QRCode;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Hashtable;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author ruizsj
 */
public class QRCodeServlet extends HttpServlet
{
  public static final int DEFAULT_SIZE = 200;
  public static final ErrorCorrectionLevel DEFAULT_LEVEL =
    ErrorCorrectionLevel.L;

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response)
    throws IOException
  {
    try
    {
      String url = request.getParameter("url");
      if (url != null)
      {
        String levelParam = request.getParameter("level");
        ErrorCorrectionLevel level = getLevel(levelParam);

        String sizeParam = request.getParameter("size");
        int size = (sizeParam == null) ?
          DEFAULT_SIZE : Integer.parseInt(sizeParam);

        response.setContentType("image/png");
        BufferedImage image = new BufferedImage(size, size,
          BufferedImage.TYPE_INT_ARGB);
        Graphics g = image.getGraphics();

        g.setColor(Color.WHITE);
        g.fillRect(0, 0, size, size);

        Hashtable hints = new Hashtable();
        QRCode qrcode = Encoder.encode(url, level, hints);

        ByteMatrix matrix = qrcode.getMatrix();
        int matrixWidth = matrix.getWidth();

        int pixelWidth = size / matrix.getHeight();

        g.setColor(Color.black);
        for (int x = 0; x < matrixWidth; x++)
        {
          for (int y = 0; y < matrixWidth; y++)
          {
            byte c = matrix.get(x, y);
            if (c == 1)
            {
              g.fillRect(x * pixelWidth, y * pixelWidth, pixelWidth, pixelWidth);
            }
          }
        }
        g.dispose();
        ImageIO.write(image, "png", response.getOutputStream());
      }
      else
      {
        response.setContentType("text/html");
        PrintWriter writer = response.getWriter();
        writer.println("<HTML><BODY>");
        writer.println("QRCodeServlet 1.0");
        writer.println("</BODY></HTML>");
      }
    }
    catch (Exception ex)
    {
      response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
  }

  private ErrorCorrectionLevel getLevel(String levelParam)
  {
    ErrorCorrectionLevel level;

    if ("L".equals(levelParam))
    {
      level = ErrorCorrectionLevel.L;
    }
    else if ("M".equals(levelParam))
    {
      level = ErrorCorrectionLevel.M;
    }
    else if ("Q".equals(levelParam))
    {
      level = ErrorCorrectionLevel.Q;
    }
    else if ("H".equals(levelParam))
    {
      level = ErrorCorrectionLevel.H;
    }
    else
    {
      level = DEFAULT_LEVEL;
    }
    return level;
  }
}
