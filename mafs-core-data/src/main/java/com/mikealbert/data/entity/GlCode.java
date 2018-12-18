package com.mikealbert.data.entity;
import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;


/**
 * The persistent class for the GL_CODE database table.
 * 
 */
@Entity
@Table(name="GL_CODE")
public class GlCode implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private GlCodePK id;

	@Column(name="ALTERNATE_KEY")
	private String alternateKey;

	@Column(name="CAT_ID_1")
	private Long catId1;

	@Column(name="CAT_ID_10")
	private Long catId10;

	@Column(name="CAT_ID_2")
	private Long catId2;

	@Column(name="CAT_ID_3")
	private Long catId3;

	@Column(name="CAT_ID_4")
	private Long catId4;

	@Column(name="CAT_ID_5")
	private Long catId5;

	@Column(name="CAT_ID_6")
	private Long catId6;

	@Column(name="CAT_ID_7")
	private Long catId7;

	@Column(name="CAT_ID_8")
	private Long catId8;

	@Column(name="CAT_ID_9")
	private Long catId9;

	@Column(name="DEFAULT_SIGN")
	private String defaultSign;

	private String description;

	@Column(name="GL_TYPE")
	private String glType;

	@Column(name="GL_VALIDATE")
	private String glValidate;

	@Column(name="HISTORY_YEARS")
	private Long historyYears;

	@Column(name="SEGMENT_1")
	private String segment1;

	@Column(name="SEGMENT_10")
	private String segment10;

	@Column(name="SEGMENT_2")
	private String segment2;

	@Column(name="SEGMENT_3")
	private String segment3;

	@Column(name="SEGMENT_4")
	private String segment4;

	@Column(name="SEGMENT_5")
	private String segment5;

	@Column(name="SEGMENT_6")
	private String segment6;

	@Column(name="SEGMENT_7")
	private String segment7;

	@Column(name="SEGMENT_8")
	private String segment8;

	@Column(name="SEGMENT_9")
	private String segment9;

	private String status;

    public GlCode() {
    }

	public GlCodePK getId() {
		return this.id;
	}

	public void setId(GlCodePK id) {
		this.id = id;
	}
	
	public String getAlternateKey() {
		return this.alternateKey;
	}

	public void setAlternateKey(String alternateKey) {
		this.alternateKey = alternateKey;
	}

	public Long getCatId1() {
		return this.catId1;
	}

	public void setCatId1(Long catId1) {
		this.catId1 = catId1;
	}

	public Long getCatId10() {
		return this.catId10;
	}

	public void setCatId10(Long catId10) {
		this.catId10 = catId10;
	}

	public Long getCatId2() {
		return this.catId2;
	}

	public void setCatId2(Long catId2) {
		this.catId2 = catId2;
	}

	public Long getCatId3() {
		return this.catId3;
	}

	public void setCatId3(Long catId3) {
		this.catId3 = catId3;
	}

	public Long getCatId4() {
		return this.catId4;
	}

	public void setCatId4(Long catId4) {
		this.catId4 = catId4;
	}

	public Long getCatId5() {
		return this.catId5;
	}

	public void setCatId5(Long catId5) {
		this.catId5 = catId5;
	}

	public Long getCatId6() {
		return this.catId6;
	}

	public void setCatId6(Long catId6) {
		this.catId6 = catId6;
	}

	public Long getCatId7() {
		return this.catId7;
	}

	public void setCatId7(Long catId7) {
		this.catId7 = catId7;
	}

	public Long getCatId8() {
		return this.catId8;
	}

	public void setCatId8(Long catId8) {
		this.catId8 = catId8;
	}

	public Long getCatId9() {
		return this.catId9;
	}

	public void setCatId9(Long catId9) {
		this.catId9 = catId9;
	}

	public String getDefaultSign() {
		return this.defaultSign;
	}

	public void setDefaultSign(String defaultSign) {
		this.defaultSign = defaultSign;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getGlType() {
		return this.glType;
	}

	public void setGlType(String glType) {
		this.glType = glType;
	}

	public String getGlValidate() {
		return this.glValidate;
	}

	public void setGlValidate(String glValidate) {
		this.glValidate = glValidate;
	}

	public Long getHistoryYears() {
		return this.historyYears;
	}

	public void setHistoryYears(Long historyYears) {
		this.historyYears = historyYears;
	}

	public String getSegment1() {
		return this.segment1;
	}

	public void setSegment1(String segment1) {
		this.segment1 = segment1;
	}

	public String getSegment10() {
		return this.segment10;
	}

	public void setSegment10(String segment10) {
		this.segment10 = segment10;
	}

	public String getSegment2() {
		return this.segment2;
	}

	public void setSegment2(String segment2) {
		this.segment2 = segment2;
	}

	public String getSegment3() {
		return this.segment3;
	}

	public void setSegment3(String segment3) {
		this.segment3 = segment3;
	}

	public String getSegment4() {
		return this.segment4;
	}

	public void setSegment4(String segment4) {
		this.segment4 = segment4;
	}

	public String getSegment5() {
		return this.segment5;
	}

	public void setSegment5(String segment5) {
		this.segment5 = segment5;
	}

	public String getSegment6() {
		return this.segment6;
	}

	public void setSegment6(String segment6) {
		this.segment6 = segment6;
	}

	public String getSegment7() {
		return this.segment7;
	}

	public void setSegment7(String segment7) {
		this.segment7 = segment7;
	}

	public String getSegment8() {
		return this.segment8;
	}

	public void setSegment8(String segment8) {
		this.segment8 = segment8;
	}

	public String getSegment9() {
		return this.segment9;
	}

	public void setSegment9(String segment9) {
		this.segment9 = segment9;
	}

	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}