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
package org.santfeliu.job.store.cases;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.activation.DataHandler;
import org.matrix.cases.Case;
import org.matrix.cases.CaseConstants;
import org.matrix.cases.CaseDocument;
import org.matrix.cases.CaseFilter;
import org.matrix.cases.CaseManagerPort;
import org.matrix.cases.CaseManagerService;
import org.matrix.cases.Intervention;
import org.matrix.doc.Document;
import org.matrix.job.Job;
import org.matrix.job.JobFilter;
import org.matrix.util.WSDirectory;
import org.matrix.util.WSEndpoint;
import org.santfeliu.dic.Type;
import org.santfeliu.dic.TypeCache;
import org.santfeliu.dic.util.DictionaryUtils;
import org.santfeliu.doc.client.DocumentManagerClient;
import org.santfeliu.doc.util.DocumentUtils;
import org.santfeliu.job.service.JobException;
import org.santfeliu.job.service.JobResponse;
import org.santfeliu.job.service.JobResponse.ResponseType;
import org.santfeliu.job.store.JobStore;
import org.santfeliu.util.FileDataSource;
import org.santfeliu.util.MatrixConfig;
import org.santfeliu.util.TextUtils;

/**
 *
 * @author blanquepa
 */
public class CasesJobStore implements JobStore
{
  public static final String JOB_TYPE = "JobCase";
  public static final String SUCCESS_INTERVENTION = "SuccessJobIntervention";
  public static final String ERROR_INTERVENTION = "ErrorJobIntervention";
  public static final String JOB_CASE_DOCUMENT = "JobCaseDocument";
  
  @Override
  public Job storeJob(Job job) throws JobException
  {
    try
    {
      if (job != null)
      {
        Case cas = JobCaseConverter.jobToCase(job);
        String typeId = cas.getCaseTypeId();
        String prefix = "";
        if (typeId.contains(":"))
          prefix = typeId.substring(0, typeId.indexOf(":") + 1);
        Type type = TypeCache.getInstance().getType(typeId);
        if (type != null && type.isDerivedFrom(prefix + JOB_TYPE))
        {
          cas = getCaseManagerPort().storeCase(cas);
          if (job.getJobId() == null)
          {
            job.setJobId(cas.getCaseId());
          }
        } 
        else
          throw new JobException("INVALID_JOB_TYPE", job);
      }
      return job;
    } 
    catch (Exception ex)
    {
      throw new JobException(ex, job);
    }
  }
  
  @Override
  public Job loadJob(String jobId) throws JobException
  {
    try
    {    
      Case cas = getCaseManagerPort().loadCase(jobId);
      String typeId = cas.getCaseTypeId();
      String prefix = "";
      if (typeId.contains(":"))
        prefix = typeId.substring(0, typeId.indexOf(":") + 1);
      Type type = TypeCache.getInstance().getType(typeId);
      if (type.isDerivedFrom(prefix + JOB_TYPE))
      {
        Job job = JobCaseConverter.caseToJob(cas);
        return job;
      } 
      else
      {
        return null;
      }
    }
    catch (Exception ex)
    {
      throw new JobException(ex);
    }
  }

  @Override
  public boolean removeJob(String jobId) throws JobException
  {
    try
    {
      if (jobId != null)
      {
        Job job = loadJob(jobId);
        if (job != null)
        {
          return getCaseManagerPort().removeCase(jobId);
        }
      }
      return false;
    }
    catch (Exception ex)
    {
      throw new JobException(ex);
    }    
  }

  @Override
  public List<Job> findJobs(JobFilter jobFilter) throws JobException
  {
    try
    {
      List<Job> results = new ArrayList();
      
      CaseFilter caseFilter = new CaseFilter();
      String jobTypeId = jobFilter.getJobTypeId() != null ?
        jobFilter.getJobTypeId() : JOB_TYPE;
      caseFilter.setCaseTypeId(jobTypeId);
      caseFilter.setFromDate(jobFilter.getFromDate());
      caseFilter.setToDate(jobFilter.getToDate());
      caseFilter.setDateComparator(CaseConstants.ACTIVE_DATE_COMPARATOR);
      caseFilter.setFirstResult(jobFilter.getFirstResult());
      caseFilter.setMaxResults(jobFilter.getMaxResults());
      
      List<Case> cases = getCaseManagerPort().findCases(caseFilter);
      for (Case cas : cases)
      {
        cas = getCaseManagerPort().loadCase(cas.getCaseId());
        results.add(JobCaseConverter.caseToJob(cas));
      }
      
      return results;        
    }
    catch (Exception ex)
    {
      throw new JobException(ex);
    }
  }

  @Override
  public void storeJobResponse(JobResponse jobResponse) 
    throws JobException
  {
    try
    {
      Intervention intervention = new Intervention();
      
      ResponseType type = jobResponse.getType();
      if (type == ResponseType.ERROR)
        intervention.setIntTypeId(ERROR_INTERVENTION);
      else
        intervention.setIntTypeId(SUCCESS_INTERVENTION);
      
      intervention.setCaseId(jobResponse.getJobId());
      String startDateTime = jobResponse.getStartDateTime();
      intervention.setStartDate(
        TextUtils.formatInternalDate(startDateTime, "yyyyMMdd"));
      intervention.setStartTime(
        TextUtils.formatInternalDate(startDateTime, "HHmmss"));
      String endDateTime = jobResponse.getEndDateTime();
      intervention.setEndDate(
        TextUtils.formatInternalDate(endDateTime, "yyyyMMdd"));
      intervention.setEndTime(
        TextUtils.formatInternalDate(endDateTime, "HHmmss"));
             
      Document document = null;      
      CaseManagerPort port = getCaseManagerPort();
      File logFile = jobResponse.getLogFile();
      if (logFile != null)
      {
        document = new Document();
        document.setDocTypeId("Document");
        String title = "Job " + jobResponse.getJobId() + "_" 
          + startDateTime + " log";
        document.setTitle(title);
        
        FileDataSource fds = new FileDataSource(logFile, "text/plain");
        
        DocumentUtils.setContentData(document, new DataHandler(fds));
        document = getDocumentManagerClient().storeDocument(document);
                
        CaseDocument caseDocument = new CaseDocument();
        caseDocument.setCaseId(jobResponse.getJobId());
        caseDocument.setDocId(document.getDocId());
        caseDocument.setCaseDocTypeId(JOB_CASE_DOCUMENT); 
        port.storeCaseDocument(caseDocument);
      }
      
      String message = jobResponse.getMessage();
      if (message != null)
      {
        intervention.setComments(message);
        
        if (document != null)
        {
          DictionaryUtils.setProperty(intervention, "logDocId", 
            document.getDocId());
          DictionaryUtils.setProperty(intervention, "logTitle",
            document.getTitle());
        }
        port.storeIntervention(intervention);
      }      
    }
    catch (Exception ex)
    {
      throw new JobException(ex);
    }
  }
  
  private DocumentManagerClient getDocumentManagerClient() 
    throws MalformedURLException
  {
    DocumentManagerClient client = null;

    String wsDirectoryURL =
      MatrixConfig.getClassProperty(getClass(), "wsDirectoryURL");

    if (wsDirectoryURL == null)
    {
      String contextPath = MatrixConfig.getProperty("contextPath");
      wsDirectoryURL = "http://localhost" + contextPath + "/wsdirectory";
    }    

    client = new DocumentManagerClient(new URL(wsDirectoryURL), 
      MatrixConfig.getProperty("adminCredentials.userId"), 
      MatrixConfig.getProperty("adminCredentials.password"));
    
    return client;    
  }
  
  private CaseManagerPort getCaseManagerPort() throws MalformedURLException
  {
    CaseManagerPort port = null;

    String wsDirectoryURL =
      MatrixConfig.getClassProperty(getClass(), "wsDirectoryURL");

    if (wsDirectoryURL == null)
    {
      String contextPath = MatrixConfig.getProperty("contextPath");
      wsDirectoryURL = "http://localhost" + contextPath + "/wsdirectory";
    }

    WSDirectory wsDirectory = 
      WSDirectory.getInstance(new URL(wsDirectoryURL));      
    WSEndpoint endpoint = wsDirectory.getEndpoint(CaseManagerService.class);
    port = endpoint.getPort(CaseManagerPort.class,
      MatrixConfig.getProperty("adminCredentials.userId"),
      MatrixConfig.getProperty("adminCredentials.password"));      

    return port;
  }

}
