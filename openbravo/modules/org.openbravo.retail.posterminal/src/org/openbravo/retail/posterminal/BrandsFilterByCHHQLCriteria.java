/*
 ************************************************************************************
 * Copyright (C) 2016 Openbravo S.L.U.
 * Licensed under the Openbravo Commercial License version 1.0
 * You may obtain a copy of the License at http://www.openbravo.com/legal/obcl.html
 * or in the legal folder of this module distribution.
 ************************************************************************************
 */
package org.openbravo.retail.posterminal;

import javax.enterprise.context.ApplicationScoped;

import org.openbravo.client.kernel.ComponentProvider.Qualifier;
import org.openbravo.mobile.core.process.HQLCriteriaProcess;

@ApplicationScoped
@Qualifier("BFilterByCH_Filter")
public class BrandsFilterByCHHQLCriteria extends HQLCriteriaProcess {

  @Override
  public String getHQLFilter(String params) {
    return "exists (select 1 from ProductCharacteristicValue as chv inner join chv.product as product  "
        + " where product.brand.id = brand.id and chv.characteristicValue.id in ($1)) ";

  }
}