package com.mikealbert.data.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the PRODUCT_TYPE_CODES database table.
 * 
 */
@Entity
@Table(name="PRODUCT_TYPE_CODES")
public class ProductTypeCode  extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="PRODUCT_TYPE")
	private String productType;

	private String description;

	@Column(name="DOC_FEE_FIN_PARAM")
	private String docFeeFinParam;

	@Column(name="FINANCE_FLAG")
	private String financeFlag;

    public ProductTypeCode() {
    }

	public String getProductType() {
		return this.productType;
	}

	public void setProductType(String productType) {
		this.productType = productType;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDocFeeFinParam() {
		return this.docFeeFinParam;
	}

	public void setDocFeeFinParam(String docFeeFinParam) {
		this.docFeeFinParam = docFeeFinParam;
	}

	public String getFinanceFlag() {
		return this.financeFlag;
	}

	public void setFinanceFlag(String financeFlag) {
		this.financeFlag = financeFlag;
	}
	
	@Override
    public String toString() {
        return "com.mikealbert.vision.entity.ProductTypeCode[ productType=" + productType + " ]";
    }

}