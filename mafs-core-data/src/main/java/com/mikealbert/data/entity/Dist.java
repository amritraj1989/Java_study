package com.mikealbert.data.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;


@Entity
@Table(name = "DIST")
public class Dist  extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="DIS_ID")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DIS_ID_SEQ")
    @SequenceGenerator(name = "DIS_ID_SEQ", sequenceName = "DIS_ID_SEQ", allocationSize = 1)
	private long disId;

	private BigDecimal amount;

	@NotNull
	@Column(name="C_ID")
	private Long corpId;

	@Column(name="CAT_ID_1")
	private BigDecimal catId1;

	@Column(name="CAT_ID_10")
	private BigDecimal catId10;

	@Column(name="CAT_ID_2")
	private BigDecimal catId2;

	@Column(name="CAT_ID_3")
	private BigDecimal catId3;

	@Column(name="CAT_ID_4")
	private BigDecimal catId4;

	@Column(name="CAT_ID_5")
	private BigDecimal catId5;

	@Column(name="CAT_ID_6")
	private BigDecimal catId6;

	@Column(name="CAT_ID_7")
	private BigDecimal catId7;

	@Column(name="CAT_ID_8")
	private BigDecimal catId8;

	@Column(name="CAT_ID_9")
	private BigDecimal catId9;

	@Column(name="CDB_CODE_1")
	private String cdbCode1;

	@Column(name="CDB_CODE_10")
	private String cdbCode10;

	@Column(name="CDB_CODE_2")
	private String cdbCode2;

	@Column(name="CDB_CODE_3")
	private String cdbCode3;

	@Column(name="CDB_CODE_4")
	private String cdbCode4;

	@Column(name="CDB_CODE_5")
	private String cdbCode5;

	@Column(name="CDB_CODE_6")
	private String cdbCode6;

	@Column(name="CDB_CODE_7")
	private String cdbCode7;

	@Column(name="CDB_CODE_8")
	private String cdbCode8;

	@Column(name="CDB_CODE_9")
	private String cdbCode9;

	private String description;

	@Column(name="GL_CODE")
	private String glCode;

	@Column(name="ICA_C_ID")
	private BigDecimal icaCId;

	private BigDecimal quantity;	

	@Transient
	private String glCodeDescription;
	
	//bi-directional many-to-one association to Doc
	@ManyToOne
	@JoinColumn(name="DOC_ID")
	private Doc doc;

	//bi-directional many-to-one association to Docl
	@ManyToOne
	@JoinColumns({
		@JoinColumn(name="DOCL_DOC_ID", referencedColumnName="DOC_ID"),
		@JoinColumn(name="DOCL_LINE_ID", referencedColumnName="LINE_ID")
		})
	private Docl docl;

	public Dist() {
	}

	public long getDisId() {
		return this.disId;
	}
	
	public Long getCorpId() {
		return corpId;
	}

	public void setCorpId(Long corpId) {
		this.corpId = corpId;
	}

	public void setDisId(long disId) {
		this.disId = disId;
	}

	public BigDecimal getAmount() {
		return this.amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	
	public BigDecimal getCatId1() {
		return this.catId1;
	}

	public void setCatId1(BigDecimal catId1) {
		this.catId1 = catId1;
	}

	public BigDecimal getCatId10() {
		return this.catId10;
	}

	public void setCatId10(BigDecimal catId10) {
		this.catId10 = catId10;
	}

	public BigDecimal getCatId2() {
		return this.catId2;
	}

	public void setCatId2(BigDecimal catId2) {
		this.catId2 = catId2;
	}

	public BigDecimal getCatId3() {
		return this.catId3;
	}

	public void setCatId3(BigDecimal catId3) {
		this.catId3 = catId3;
	}

	public BigDecimal getCatId4() {
		return this.catId4;
	}

	public void setCatId4(BigDecimal catId4) {
		this.catId4 = catId4;
	}

	public BigDecimal getCatId5() {
		return this.catId5;
	}

	public void setCatId5(BigDecimal catId5) {
		this.catId5 = catId5;
	}

	public BigDecimal getCatId6() {
		return this.catId6;
	}

	public void setCatId6(BigDecimal catId6) {
		this.catId6 = catId6;
	}

	public BigDecimal getCatId7() {
		return this.catId7;
	}

	public void setCatId7(BigDecimal catId7) {
		this.catId7 = catId7;
	}

	public BigDecimal getCatId8() {
		return this.catId8;
	}

	public void setCatId8(BigDecimal catId8) {
		this.catId8 = catId8;
	}

	public BigDecimal getCatId9() {
		return this.catId9;
	}

	public void setCatId9(BigDecimal catId9) {
		this.catId9 = catId9;
	}

	public String getCdbCode1() {
		return this.cdbCode1;
	}

	public void setCdbCode1(String cdbCode1) {
		this.cdbCode1 = cdbCode1;
	}

	public String getCdbCode10() {
		return this.cdbCode10;
	}

	public void setCdbCode10(String cdbCode10) {
		this.cdbCode10 = cdbCode10;
	}

	public String getCdbCode2() {
		return this.cdbCode2;
	}

	public void setCdbCode2(String cdbCode2) {
		this.cdbCode2 = cdbCode2;
	}

	public String getCdbCode3() {
		return this.cdbCode3;
	}

	public void setCdbCode3(String cdbCode3) {
		this.cdbCode3 = cdbCode3;
	}

	public String getCdbCode4() {
		return this.cdbCode4;
	}

	public void setCdbCode4(String cdbCode4) {
		this.cdbCode4 = cdbCode4;
	}

	public String getCdbCode5() {
		return this.cdbCode5;
	}

	public void setCdbCode5(String cdbCode5) {
		this.cdbCode5 = cdbCode5;
	}

	public String getCdbCode6() {
		return this.cdbCode6;
	}

	public void setCdbCode6(String cdbCode6) {
		this.cdbCode6 = cdbCode6;
	}

	public String getCdbCode7() {
		return this.cdbCode7;
	}

	public void setCdbCode7(String cdbCode7) {
		this.cdbCode7 = cdbCode7;
	}

	public String getCdbCode8() {
		return this.cdbCode8;
	}

	public void setCdbCode8(String cdbCode8) {
		this.cdbCode8 = cdbCode8;
	}

	public String getCdbCode9() {
		return this.cdbCode9;
	}

	public void setCdbCode9(String cdbCode9) {
		this.cdbCode9 = cdbCode9;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getGlCode() {
		return this.glCode;
	}

	public void setGlCode(String glCode) {
		this.glCode = glCode;
	}

	public BigDecimal getIcaCId() {
		return this.icaCId;
	}

	public void setIcaCId(BigDecimal icaCId) {
		this.icaCId = icaCId;
	}

	public BigDecimal getQuantity() {
		return this.quantity;
	}

	public void setQuantity(BigDecimal quantity) {
		this.quantity = quantity;
	}

	public Date getVersionts() {
		return this.versionts;
	}

	public Doc getDoc() {
		return this.doc;
	}

	public void setDoc(Doc doc) {
		this.doc = doc;
	}

	public Docl getDocl() {
		return this.docl;
	}

	public void setDocl(Docl docl) {
		this.docl = docl;
	}
	
	public String getGlCodeDescription() {
		return glCodeDescription;
	}

	public void setGlCodeDescription(String glCodeDescription) {
		this.glCodeDescription = glCodeDescription;
	}
	
}