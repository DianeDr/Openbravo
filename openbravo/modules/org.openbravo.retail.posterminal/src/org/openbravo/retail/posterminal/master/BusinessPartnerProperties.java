/*
 ************************************************************************************
 * Copyright (C) 2015 Openbravo S.L.U.
 * Licensed under the Openbravo Commercial License version 1.0
 * You may obtain a copy of the License at http://www.openbravo.com/legal/obcl.html
 * or in the legal folder of this module distribution.
 ************************************************************************************
 */
package org.openbravo.retail.posterminal.master;

import java.util.ArrayList;
import java.util.List;

import org.openbravo.client.kernel.ComponentProvider.Qualifier;
import org.openbravo.mobile.core.model.HQLProperty;
import org.openbravo.mobile.core.model.ModelExtension;

@Qualifier(BusinessPartner.businessPartnerPropertyExtension)
public class BusinessPartnerProperties extends ModelExtension {

  @Override
  public List<HQLProperty> getHQLProperties(Object params) {
    ArrayList<HQLProperty> list = new ArrayList<HQLProperty>() {
      private static final long serialVersionUID = 1L;
      {
        add(new HQLProperty("bpl.businessPartner.id", "id"));
        add(new HQLProperty("bpl.businessPartner.organization.id", "organization"));
        add(new HQLProperty("bpl.businessPartner.name", "name"));
        add(new HQLProperty("bpl.businessPartner.name", "_identifier"));
        add(new HQLProperty("bpl.businessPartner.searchKey", "searchKey"));
        add(new HQLProperty("bpl.businessPartner.description", "description"));
        add(new HQLProperty("bpl.businessPartner.taxID", "taxID"));
        add(new HQLProperty("bpl.businessPartner.sOBPTaxCategory.id", "taxCategory"));
        add(new HQLProperty("bpl.businessPartner.priceList.id", "priceList"));
        add(new HQLProperty("bpl.businessPartner.paymentMethod.id", "paymentMethod"));
        add(new HQLProperty("bpl.businessPartner.paymentTerms.id", "paymentTerms"));
        add(new HQLProperty("bpl.businessPartner.invoiceTerms", "invoiceTerms"));
        add(new HQLProperty("bpl.id", "locId"));
        add(new HQLProperty(
            "COALESCE(bpl.locationAddress.addressLine1, bpl.locationAddress.addressLine2, bpl.locationAddress.postalCode, bpl.locationAddress.cityName)",
            "locName", false));
        add(new HQLProperty("ulist.email", "email"));
        add(new HQLProperty("ulist.id", "contactId"));
        add(new HQLProperty("ulist.phone", "phone"));
        add(new HQLProperty("ulist.firstName", "firstName"));
        add(new HQLProperty("ulist.lastName", "lastName"));
        add(new HQLProperty("plist.priceIncludesTax", "priceIncludesTax"));
        add(new HQLProperty("plist.name", "priceListName"));
        add(new HQLProperty("bpl.locationAddress.cityName", "cityName"));
        add(new HQLProperty("bpl.locationAddress.country.name", "countryName"));
        add(new HQLProperty("bpl.locationAddress.postalCode", "postalCode"));
        add(new HQLProperty("bpl.businessPartner.businessPartnerCategory.id",
            "businessPartnerCategory"));
        add(new HQLProperty("bpl.businessPartner.businessPartnerCategory.name",
            "businessPartnerCategory_name"));
        add(new HQLProperty("bpl.businessPartner.creditLimit", "creditLimit"));
        add(new HQLProperty("bpl.businessPartner.creditUsed", "creditUsed"));
        add(new HQLProperty("bpl.businessPartner.taxExempt", "taxExempt"));
        add(new HQLProperty("bpl.businessPartner.customerBlocking", "customerBlocking"));
        add(new HQLProperty("bpl.businessPartner.salesOrder", "salesOrderBlocking"));
        add(new HQLProperty(
            "(case when bpl.active = 'Y' and bpl.businessPartner.active = 'Y' then true else false end)",
            "active"));
      }
    };
    return list;
  }

}