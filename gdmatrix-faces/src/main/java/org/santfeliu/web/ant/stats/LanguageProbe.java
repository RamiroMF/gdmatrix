package org.santfeliu.web.ant.stats;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author ruizsj
 */
public class LanguageProbe extends CounterProbe
{
  Map<String, String> languageMap;

  @Override
  public void init()
  {
    languageMap = new HashMap();
    languageMap.put("ar", "�rab");
    languageMap.put("bg", "b�lgar");
    languageMap.put("ca", "catal�");
    languageMap.put("de", "alemany");
    languageMap.put("en", "angl�s");
    languageMap.put("es", "espanyol");
    languageMap.put("fr", "franc�s");
    languageMap.put("it", "itali�");
    languageMap.put("pt", "portugu�s");
    languageMap.put("ro", "roman�s");
    languageMap.put("ru", "rus");
    languageMap.put("zh", "xin�s");
  }

  @Override
  public void processLine(Line line)
  {
    increment(line.getLanguage(), languageMap.get(line.getLanguage()));
  }
}
