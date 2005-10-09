/**
 * ===================================================================
 *
 * Copyright (c) 2003,2004 Ludovic Dubost, All rights reserved.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details, published at 
 * http://www.gnu.org/copyleft/lesser.html or in lesser.txt in the
 * root folder of this distribution.

 * Created by
 * User: Ludovic Dubost
 * Date: 5 ao�t 2004
 * Time: 11:45:11
 */
package com.xpn.xwiki.stats.impl;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Element;
import org.dom4j.dom.DOMElement;

import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.objects.BaseCollection;
import com.xpn.xwiki.objects.BaseObject;
import com.xpn.xwiki.objects.BaseProperty;
import com.xpn.xwiki.objects.PropertyInterface;
import com.xpn.xwiki.objects.classes.BaseClass;
import com.xpn.xwiki.objects.classes.PropertyClass;

public class XWikiStats extends BaseCollection {
    public static int PERIOD_MONTH = 0;
    public static int PERIOD_DAY = 1;

    public XWikiStats() {
        super();
    }

    public XWikiStats(Date period, int periodtype) {
        setPeriod(getPeriodAsInt(period, periodtype));
    }

    public int getPeriod() {
        return getIntValue("period");
    }

    public void setPeriod(int period) {
        setIntValue("period", period);
    }

    public int getPeriodAsInt(Date date, int type) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        if (type == PERIOD_MONTH)
         return cal.get(Calendar.YEAR) * 100 + (cal.get(Calendar.MONTH)+1);
       else
         return cal.get(Calendar.YEAR) * 10000 + (cal.get(Calendar.MONTH)+1) * 100 + (cal.get(Calendar.DAY_OF_MONTH)+1);
    }

    public int getPageViews() {
        return getIntValue("pageViews");
    }

    public void  setPageViews(int pageViews) {
        setIntValue("pageViews", pageViews);
    }

    public void  incPageViews() {
        setIntValue("pageViews", getPageViews() + 1);
    }

    public int hashCode() {
        String str = getName()+getClassName();
        int nb = getNumber();
        if (nb>0)
            str += "_" + nb;
        return str.hashCode();
    }

    public void setId(int id) {
    }

    public Object clone() {
        BaseCollection object = (BaseCollection) super.clone();
        return object;
    }

    public boolean equals(Object obj) {
        if (!super.equals(obj))
            return false;

        if (getNumber()!=((BaseObject)obj).getNumber())
            return false;

        return true;
    }

    public Element toXML(BaseClass bclass) {
        Element oel = new DOMElement("object");

        // Add Class
        if (bclass!=null) {
        Collection fields = bclass.getFieldList();
        if (fields.size()>0) {
            oel.add(bclass.toXML());
          }
        }

        Element el = new DOMElement("name");
        el.addText(getName());
        oel.add(el);

        el = new DOMElement("number");
        el.addText(getNumber() + "");
        oel.add(el);

        el = new DOMElement("className");
        el.addText(getClassName());
        oel.add(el);

        Iterator it = getFieldList().iterator();
        while (it.hasNext()) {
            Element pel = new DOMElement("property");
            PropertyInterface bprop = (PropertyInterface)it.next();
            pel.add(bprop.toXML());
            oel.add(pel);
        }
        return oel;
    }

    public void fromXML(Element oel) throws XWikiException {
        Element cel = oel.element("class");
        BaseClass bclass = new BaseClass();
        if (cel!=null) {
            bclass.fromXML(cel);
            setClassName(bclass.getName());
        }

        setName(oel.element("name").getText());
        List list = oel.elements("property");
        for (int i=0;i<list.size();i++) {
            Element pcel = (Element)((Element) list.get(i)).elements().get(0);
            String name = pcel.getName();
            PropertyClass pclass = (PropertyClass) bclass.get(name);
            if (pclass!=null) {
                BaseProperty property = pclass.newPropertyfromXML(pcel);
                property.setName(name);
                property.setObject(this);
                safeput(name, property);
            }
        }
    }
}
