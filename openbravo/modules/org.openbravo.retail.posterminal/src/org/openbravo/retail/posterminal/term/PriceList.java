/*
 ************************************************************************************
 * Copyright (C) 2012-2015 Openbravo S.L.U.
 * Licensed under the Openbravo Commercial License version 1.0
 * You may obtain a copy of the License at http://www.openbravo.com/legal/obcl.html
 * or in the legal folder of this module distribution.
 ************************************************************************************
 */
package org.openbravo.retail.posterminal.term;

import java.util.Arrays;
import java.util.List;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.openbravo.client.kernel.RequestContext;
import org.openbravo.retail.posterminal.POSUtils;

public class PriceList extends QueryTerminalProperty {

  @Override
  protected boolean isAdminMode() {
    return true;
  }

  @Override
  protected List<String> getQuery(JSONObject jsonsent) throws JSONException {
    String priceListId = POSUtils.getPriceListByTerminalId(
        RequestContext.get().getSessionAttribute("POSTerminal").toString()).getId();
    return Arrays.asList(new String[] { "from PricingPriceList where id ='" + priceListId
        + "' and $readableSimpleCriteria and $activeCriteria" });
  }

  @Override
  protected boolean bypassPreferenceCheck() {
    return true;
  }

  @Override
  public String getProperty() {
    return "pricelist";
  }

  @Override
  public boolean returnList() {
    return true;
  }
}
