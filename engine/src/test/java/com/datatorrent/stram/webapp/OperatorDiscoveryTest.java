/**
 * Copyright (c) 2012-2015 DataTorrent, Inc.
 * All rights reserved.
 */
package com.datatorrent.stram.webapp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.codehaus.jackson.annotate.JsonTypeInfo.As;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

import com.datatorrent.api.BaseOperator;
import com.google.common.collect.Lists;

public class OperatorDiscoveryTest
{
  @Test
  public void testPropertyDiscovery() throws Exception
  {
    OperatorDiscoverer od = new OperatorDiscoverer();
    Assert.assertNotNull(od.getOperatorClass(BaseOperator.class.getName()));

    JSONObject desc = od.describeClass(CustomBean.class);
    System.out.println(desc.toString(2));

    JSONArray props = desc.getJSONArray("properties");
    Assert.assertNotNull("properties", props);
    JSONObject mapProperty = props.getJSONObject(2);
    Assert.assertEquals("name " + mapProperty, "map", mapProperty.get("name"));
    Assert.assertEquals("canGet " + mapProperty, true, mapProperty.get("canGet"));
    Assert.assertEquals("canSet " + mapProperty, true, mapProperty.get("canSet"));
    Assert.assertEquals("type " + mapProperty, java.util.Map.class.toString(), mapProperty.get("type"));

    JSONArray typeArgs = mapProperty.getJSONArray("typeArgs");
    Assert.assertNotNull("typeArgs", typeArgs);
    Assert.assertEquals("typeArgs " + typeArgs, 2, typeArgs.length());
    Assert.assertEquals("", String.class.toString(), typeArgs.getJSONObject(0).get("type"));
    Assert.assertEquals("", CustomBean.Nested.class.toString(), typeArgs.getJSONObject(1).get("type"));


    JSONObject enumDesc = od.describeClass(CustomBean.Color.class);
    JSONArray enumNames = enumDesc.getJSONArray("enum");
    Assert.assertNotNull("enumNames", enumNames);
    Assert.assertEquals("", CustomBean.Color.BLUE.name(), enumNames.get(0));

  }

  @Test
  public void testValueSerialization() throws Exception
  {
    CustomBean bean = new CustomBean();
    bean.map.put("key1", new CustomBean.Nested());
    bean.stringArray = new String[] { "one", "two", "three" };
    bean.stringList = Lists.newArrayList("four", "five");

    ObjectMapper mapper = new ObjectMapper();
    mapper.configure(org.codehaus.jackson.map.DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    //mapper.enableDefaultTypingAsProperty(ObjectMapper.DefaultTyping.NON_FINAL, Id.CLASS.getDefaultPropertyName());
    mapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL, As.WRAPPER_OBJECT);
    String s = mapper.writeValueAsString(bean);
    System.out.println(new JSONObject(s).toString(2));
  }

  public static class CustomBean
  {
    private int count;
    private List<String> stringList;
    private Properties props;
    private Nested nested;
    private Map<String, Nested> map = new HashMap<String, CustomBean.Nested>();
    private String[] stringArray;
    private Color color = Color.BLUE;

    public static class Nested
    {
      private int size;
      private String name;
      private ArrayList<String> list;

      public int getSize()
      {
        return size;
      }

      public void setSize(int size)
      {
        this.size = size;
      }

      public String getName()
      {
        return name;
      }

      public void setName(String name)
      {
        this.name = name;
      }

      public ArrayList<String> getList()
      {
        return list;
      }

      public void setList(ArrayList<String> list)
      {
        this.list = list;
      }

    }

    public enum Color
    {
      BLUE,
      RED,
      WHITE
    }

    public int getCount()
    {
      return count;
    }

    public void setCount(int count)
    {
      this.count = count;
    }

    public List<String> getStringList()
    {
      return stringList;
    }

    public void setStringList(List<String> stringList)
    {
      this.stringList = stringList;
    }

    public Properties getProps()
    {
      return props;
    }

    public void setProps(Properties props)
    {
      this.props = props;
    }

    public Nested getNested()
    {
      return nested;
    }

    public void setNested(Nested n)
    {
      this.nested = n;
    }

    public Map<String, Nested> getMap()
    {
      return map;
    }

    public void setMap(Map<String, Nested> m)
    {
      this.map = m;
    }

    public Color getColor()
    {
      return color;
    }

    public void setColor(Color color)
    {
      this.color = color;
    }

    public String[] getStringArray()
    {
      return stringArray;
    }


  }

}
