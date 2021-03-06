package com.audifilm.matrix.dic.service.types;

import com.audifilm.matrix.dic.service.DBTaulaConfig;
import com.audifilm.matrix.dic.service.DictionaryManager;
import com.audifilm.matrix.util.TextUtil;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;
import javax.xml.ws.WebServiceException;
import org.matrix.dic.PropertyDefinition;
import org.matrix.dic.Type;
import org.matrix.dic.TypeFilter;
import org.matrix.util.WSEndpoint;

/**
 *
 * @author comasfc
 */
public abstract class DicTypeTaulaConfig<P extends DicTypeInterface> extends DicTypeLeave<P>
{

  public DicTypeTaulaConfig(DicTypeInterface parent)
  {
    super(parent);
  }

  abstract public String getTCCODI0();

  public String getTypeIdPrefix()
  {
    return "TC" + getTCCODI0() + DicType.PREFIX_SEPARATOR;
  }

  public Type loadType(DictionaryManager dictionaryManager, String composedIdType)
  {
    String typeId = toLocalId(dictionaryManager.getEndpoint(), composedIdType);

    Query query = dictionaryManager.entityManager.createNamedQuery("loadTaulaConfig");
    query.setParameter("tccodi0", getTCCODI0());
    query.setParameter("tccodi1", typeId);

    query.setFirstResult(0);
    query.setMaxResults(1);

    List<DBTaulaConfig> dbTypeList = query.getResultList();

    if (dbTypeList == null || dbTypeList.isEmpty())
    {
      throw new WebServiceException("dic:TYPE_NOT_FOUND");
    }
    Type type = new Type();
    copyTo(dictionaryManager.getEndpoint(), dbTypeList.get(0), type);

    //PropertyDefinition
    //Les propietats son els camps fixos d'un expedient
    PropertyDefinition prop = new PropertyDefinition();
    type.getPropertyDefinition().add(prop);
    type.getAccessControl().addAll(getAccessControlList(dictionaryManager, composedIdType));
    return type;
  }

  public Type storeType(DictionaryManager dictionaryManager, Type type)
  {
    if (type.getTypeId() == null)
    {
      throw new WebServiceException("Not supported");
    }
    //NO VULL FER UN STORE PER TANT FAIG UN LOAD I RETORNO EL TROBAT
    return loadType(dictionaryManager, type.getTypeId());
  }

  public List<Type> findTypes(DictionaryManager dictionaryManager, TypeFilter filter)
  {
    String typeId = toLocalId(dictionaryManager.getEndpoint(), filter.getTypeId());
    String description = filter.getDescription();

    Query query = dictionaryManager.entityManager.createNamedQuery("findTaulaConfig");
    query.setParameter("tccodi0", getTCCODI0());
    query.setParameter("tccodi1", (typeId == null || typeId.equals("") ? null : typeId));
    query.setParameter("tccodi2", null);
    query.setParameter("description", TextUtil.likePattern(filter.getDescription()));

    query.setFirstResult(filter.getFirstResult());
    query.setMaxResults(filter.getMaxResults());

    List<DBTaulaConfig> dbCaseTypeList = query.getResultList();

    List<Type> typesList = new ArrayList<Type>();
    for (DBTaulaConfig dbTaulaConfig : dbCaseTypeList)
    {
      Type type = new Type();
      copyTo(dictionaryManager.getEndpoint(), dbTaulaConfig, type);

      typesList.add(type);
    }
    return typesList;
  }

  public int countTypes(DictionaryManager dictionaryManager, TypeFilter filter)
  {
    String typeId = toLocalId(dictionaryManager.getEndpoint(), filter.getTypeId());
    String description = filter.getDescription();


    Query query = dictionaryManager.entityManager.createNamedQuery("countTaulaConfig");
    query.setParameter("tccodi0", getTCCODI0());
    query.setParameter("tccodi1", (typeId == null || typeId.equals("") ? null : typeId));
    query.setParameter("tccodi2", null);
    query.setParameter("description", TextUtil.likePattern(filter.getDescription()));

    Number count = (Number) query.getSingleResult();
    return count.intValue();
  }

  @Override
  public List<String> listModifiedTypes(DictionaryManager dictionaryManager, String dateTime1, String dateTime2)
  {
    Query query = dictionaryManager.entityManager.createNamedQuery("listModifiedTaulaConfig");
    query.setParameter("tccodi0", getTCCODI0());
    query.setParameter("tccodi1", null);
    query.setParameter("dateTime1", dateTime1);
    query.setParameter("dateTime2", dateTime2);
    List<String> dbTaulaConfigIdList = query.getResultList();

    List<String> globalTypeIdList = new ArrayList<String>();
    for (String taulaConfigId : dbTaulaConfigIdList)
    {
      globalTypeIdList.add(toGlobalId(dictionaryManager.getEndpoint(), taulaConfigId));
    }
    return globalTypeIdList;
  }

  public void copyTo(WSEndpoint endpoint, DBTaulaConfig dbTaulaType, Type type)
  {

    type.setTypeId(toGlobalId(endpoint, dbTaulaType.getTccodi1()));
    type.setSuperTypeId(getParentDicType().toGlobalId(endpoint, null));
    type.setDescription(dbTaulaType.getDescription());
    type.setTypePath(getGlobalTypePath(endpoint, dbTaulaType.getTccodi1()));

    type.setInstantiable(true);

    type.setChangeDateTime(dbTaulaType.getChangeDateTime());
    type.setChangeUserId(dbTaulaType.getStdumod());
    type.setCreationDateTime(dbTaulaType.getCreationDateTime());
    type.setCreationUserId(dbTaulaType.getStdugr());

  }
}

 


