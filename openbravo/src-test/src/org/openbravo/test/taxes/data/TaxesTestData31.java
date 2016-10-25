/*
 *************************************************************************
 * The contents of this file are subject to the Openbravo  Public  License
 * Version  1.1  (the  "License"),  being   the  Mozilla   Public  License
 * Version 1.1  with a permitted attribution clause; you may not  use this
 * file except in compliance with the License. You  may  obtain  a copy of
 * the License at http://www.openbravo.com/legal/license.html 
 * Software distributed under the License  is  distributed  on  an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific  language  governing  rights  and  limitations
 * under the License. 
 * The Original Code is Openbravo ERP. 
 * The Initial Developer of the Original Code is Openbravo SLU 
 * All portions are Copyright (C) 2015 Openbravo SLU 
 * All Rights Reserved. 
 * Contributor(s):  ______________________________________.
 ************************************************************************
 */

package org.openbravo.test.taxes.data;

import java.math.BigDecimal;
import java.util.HashMap;

public class TaxesTestData31 extends TaxesTestData {

  @Override
  public void initialize() {
    setTestNumber("31");
    setTestDescription("Price including taxes 29: Purchase BOM Taxes positive");
    setSalesTest(false);
    setPriceIncludingTaxes(true);
    // This info will be set in header
    setBpartnerId(BPartnerDataConstants.VENDOR_A);
    // This info will be used in line
    setProductId(ProductDataConstants.DISTRIBUTION_GOOD_D);
    setTaxid(TaxDataConstants.TAX_AS_PER_BOM);
    // This info is used for inserting the line
    setQuantity(BigDecimal.ONE);
    setPrice(new BigDecimal("3"));
    setLineNet(new BigDecimal("2.85"));
    // This info is used to update the line
    setQuantityUpdated(new BigDecimal("2"));
    setPriceUpdated(new BigDecimal("3"));
    setLineNetUpdated(new BigDecimal("5.68"));

    // These are the expected results
    // Each line contains the taxID - {taxableAmtAfterInsert, taxAmtAfterInsert,
    // taxableAmtAfterUpdate, taxAmtAfterUpdate}
    // Exempt tax positive amount
    HashMap<String, String[]> lineTaxes = new HashMap<String, String[]>();
    lineTaxes.put(TaxDataConstants.TAX_VAT_10, new String[] { "1.03", "0.10", "2.04", "0.21" });
    lineTaxes.put(TaxDataConstants.TAX_VAT_3, new String[] { "1.82", "0.05", "3.64", "0.11" });
    // Both taxes for line level and for document level are provided
    setLinetaxes(lineTaxes);
    setDoctaxes(lineTaxes);

  }

}
