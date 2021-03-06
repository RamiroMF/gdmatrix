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
package org.santfeliu.workflow.web;

import cat.aoc.valid.DocumentToSign;
import cat.aoc.valid.ValidClient;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.codec.binary.Base64;
import org.json.simple.JSONObject;
import org.matrix.signature.DataHash;
import org.matrix.signature.SignatureManagerPort;
import org.matrix.signature.SignatureManagerService;
import org.matrix.util.WSDirectory;
import org.matrix.util.WSEndpoint;
import org.santfeliu.faces.browser.HtmlBrowser;
import org.santfeliu.faces.matrixclient.model.SignatureMatrixClientModel;
import org.santfeliu.util.MatrixConfig;
import org.santfeliu.util.Properties;
import org.santfeliu.web.UserSessionBean;
import org.santfeliu.workflow.form.Form;


/**
 *
 * @author realor
 */
public class SignatureFormBean extends FormBean
{
  public static final String ERROR_PREFIX = "ERROR: ";

  transient HtmlBrowser browser;
  private String message;
  private String sigId; // sigId of document to sign
  private String result;
  private boolean IFrame;
  
  private SignatureMatrixClientModel model;

  public SignatureFormBean()
  {
    model = new SignatureMatrixClientModel();
  }

  public void setBrowser(HtmlBrowser browser)
  {
    this.browser = browser;
  }

  public HtmlBrowser getBrowser()
  {
    return browser;
  }

  public void setMessage(String message)
  {
    this.message = message;
  }

  public String getMessage()
  {
    return message;
  }

  public boolean isIFrame()
  {
    return IFrame;
  }

  public void setIFrame(boolean IFrame)
  {
    this.IFrame = IFrame;
  }

  public void setDocument(String document)
  {
    this.sigId = document;
  }

  public String getDocument()
  {
    return sigId;
  }

  public String getSigId()
  {
    return sigId;
  }

  public void setSigId(String sigId)
  {
    this.sigId = sigId;
  }

  public void setResult(String result)
  {
    this.result = result;
  }

  public String getResult()
  {
    return result;
  }

  @Override
  public String show(Form form)
  {
    Properties parameters = form.getParameters();

    InstanceBean instanceBean = (InstanceBean)getBean("instanceBean");
    instanceBean.setForwardEnabled(false);
    instanceBean.setBackwardEnabled(false);

    browser = new HtmlBrowser();
    browser.setUrl(null);
    
    Object value;
    value = parameters.get("message");
    if (value != null) message = String.valueOf(value);
    value = parameters.get("document");
    if (value != null) sigId = String.valueOf(value);
    value = parameters.get("url");
    if (value != null) browser.setUrl(String.valueOf(value));
    value = parameters.get("iframe");
    if (value != null) IFrame = Boolean.parseBoolean(String.valueOf(value));

    return "signature_form";
  }

  @Override
  public Map submit()
  {
    HashMap variables = new HashMap();
    variables.put("result", result);
    return variables;
  }

  // Applet method
  public String sign()
  {
    if (result.indexOf(ERROR_PREFIX) == -1) // NO ERROR: OK or CANCEL
    {
      InstanceBean instanceBean = (InstanceBean)getBean("instanceBean");
      return instanceBean.forward();    
    }
    else
    {
      String message = result.substring(ERROR_PREFIX.length());
      error(message);
    }
    return null;
  }
    
  // MatrixClient methods
  public SignatureMatrixClientModel getModel()
  {
    return model;
  }

  public void setModel(SignatureMatrixClientModel model)
  {
    this.model = model;
  }
  
  public String documentSigned()
  {
    try
    {
      result = (String)model.parseResult();
      if (result != null)
      {
        InstanceBean instanceBean = (InstanceBean)getBean("instanceBean");
        return instanceBean.forward();         
      }
    }
    catch (Exception ex)
    {
      error(ex);
    }
    return null;
  }

  /* cancel signature */ 
  public String cancelSignature()
  {
    result = "CANCEL";
    InstanceBean instanceBean = (InstanceBean)getBean("instanceBean");
    return instanceBean.forward(); 
  }
  
  // VALid methods
  public String signValid()
  {
    try
    {
      // get VALid accessToken from session
      UserSessionBean userSessionBean = UserSessionBean.getCurrentInstance();
      String accessToken = (String)userSessionBean.getAttribute("accessToken");
      if (accessToken == null)
        throw new Exception("NOT_LOGGED_WITH_VALID");

      // gets hashes of docsToSign
      ArrayList<DocumentToSign> docsToSign = new ArrayList<DocumentToSign>();
      SignatureManagerPort port = getSignatureManagerPort();
      List<DataHash> dataHashes = port.digestData(sigId);
      for (DataHash dataHash : dataHashes)
      {
        DocumentToSign docToSign = new DocumentToSign();
        docToSign.setName(dataHash.getName());
        docToSign.setHash(new String(Base64.encodeBase64(dataHash.getHash())));
        docToSign.setAlgorithm(dataHash.getAlgorithm());
        docsToSign.add(docToSign);
      }
      // create basic signature of docsToSign with VALid
      ValidClient client = new ValidClient();
      client.setBaseUrl(MatrixConfig.getProperty("valid.baseUrl"));
      client.setClientId(MatrixConfig.getProperty("valid.clientId"));
      client.setClientSecret(MatrixConfig.getProperty("valid.clientSecret"));
      client.setRedirectUrl(MatrixConfig.getProperty("valid.redirectUrl"));
      JSONObject signResult = client.getBasicSignature(accessToken, docsToSign);
      String status = (String)signResult.get("status");
      if ("ko".equals(status))
        throw new Exception((String)signResult.get("error"));

      String evidence = (String)signResult.get("evidence");
      byte[] bytes = Base64.decodeBase64(evidence.getBytes());
      port.addExternalSignature(sigId, bytes);

      result = "OK";
      InstanceBean instanceBean = (InstanceBean)getBean("instanceBean");
      return instanceBean.forward();
    }
    catch (Exception ex)
    {
      error(ex);
    }
    return null;
  }
  
  private SignatureManagerPort getSignatureManagerPort()
  {
    WSDirectory dir = WSDirectory.getInstance();
    WSEndpoint endpoint = dir.getEndpoint(SignatureManagerService.class);
    return endpoint.getPort(SignatureManagerPort.class);
  }  
}
