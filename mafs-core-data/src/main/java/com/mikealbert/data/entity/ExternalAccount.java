package com.mikealbert.data.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.mikealbert.data.beanvalidation.MASize;

// TODO: discover whether there are separate fields used only for customers, suppliers and financial accounts and re-factor this stuff to use
// an externalAccount (base class) entity and separate entities inheriting from the base for customer, supplier and financial accounts
// similar to the re-factor work we've done for addresses 

/**
 * Mapped to EXTERNAL_ACCOUNTS Table
 * @author sibley
 */
@Entity
@Table(name = "EXTERNAL_ACCOUNTS")
public class ExternalAccount extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @EmbeddedId
    protected ExternalAccountPK externalAccountPK;
    
    @Basic(optional = false)
    @MASize(label = "Account Name", min = 1, max = 80)
    @Column(name = "ACCOUNT_NAME")
    private String accountName;
    
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 25)
    @Column(name = "SHORT_NAME")
    private String shortName;
    
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 1)
    @Column(name = "ACC_STATUS")
    private String accStatus;
    
    @Size(max = 80)
    @Column(name = "PAYEE_NAME")
    private String payeeName;
    
    @Size(max = 80)
    @Column(name = "REG_NAME")
    private String regName;
    
    @Size(max = 25)
    @Column(name = "COMP_REG_NO")
    private String compRegNo;
    
    @Column(name = "COMP_REG_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date compRegDate;
    
    @Size(max = 25)
    @Column(name = "TAX_REG_NO")
    private String taxRegNo;
    
    @Size(max = 25)
    @Column(name = "TAX_EXEMPTION_NO")
    private String taxExemptionNo;
    
    @Size(max = 1)
    @Column(name = "TAX_IND")
    private String taxInd;
    
    @Size(max = 1)
    @Column(name = "TAX_HOLDING_IND")
    private String taxHoldingInd;
    
    @Size(max = 25)
    @Column(name = "TELEPHONE_NUMBER")
    private String telephoneNumber;
    
    @Size(max = 25)
    @Column(name = "FAX_CODE")
    private String faxCode;
    
    @Size(max = 25)
    @Column(name = "TELEX_CODE")
    private String telexCode;
    
    @Size(max = 80)
    @Column(name = "MAIN_ACTIVITY")
    private String mainActivity;
    
    @Size(max = 80)
    @Column(name = "BUSINESS_SECTOR")
    private String businessSector;
    
    @Size(max = 10)
    @Column(name = "LANGUAGE")
    private String language;
    
    @Size(max = 25)
    @Column(name = "EXT_REF")
    private String extRef;
    
    @Size(max = 25)
    @Column(name = "CREDIT_REF")
    private String creditRef;
    
    @Size(max = 25)
    @Column(name = "BUILDING_SOCIETY_CODE")
    private String buildingSocietyCode;
    
    @Size(max = 80)
    @Column(name = "BANK_NAME")
    private String bankName;
    
    @Size(max = 25)
    @Column(name = "BANK_ACCOUNT_NUMBER")
    private String bankAccountNumber;
    
    @Size(max = 80)
    @Column(name = "BANK_ACCOUNT_NAME")
    private String bankAccountName;
    
    @Size(max = 25)
    @Column(name = "BANK_SORT_CODE")
    private String bankSortCode;
    
    @Size(max = 80)
    @Column(name = "BANK_ADD_LINE_1")
    private String bankAddLine1;
    
    @Size(max = 80)
    @Column(name = "BANK_ADD_LINE_2")
    private String bankAddLine2;
    
    @Size(max = 80)
    @Column(name = "BANK_ADD_LINE_3")
    private String bankAddLine3;
    
    @Size(max = 80)
    @Column(name = "BANK_ADD_LINE_4")
    private String bankAddLine4;
    
    @Size(max = 25)
    @Column(name = "BANK_POST_CODE")
    private String bankPostCode;
    
    @Column(name = "DATE_LAST_PURCHASE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateLastPurchase;
    
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "AMOUNT_LAST_PURCHASE")
    private BigDecimal amountLastPurchase;
    
    @Column(name = "DATE_LAST_RECEIPT")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateLastReceipt;
    
    @Column(name = "AMOUNT_LAST_RECEIPT")
    private BigDecimal amountLastReceipt;
    
    @Column(name = "DATE_LAST_STATEMENT")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateLastStatement;
    
    @Column(name = "DATE_LAST_CREDIT_CHECK")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateLastCreditCheck;
    
    @Column(name = "DATE_NEXT_CREDIT_CHECK")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateNextCreditCheck;
    
    @Column(name = "DATE_FIN_YEAR_END")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateFinYearEnd;
    
    @Column(name = "DATE_OPENED")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateOpened;
    
    @Column(name = "DATE_EXPIRED")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateExpired;
    
    @Column(name = "OUTSTANDING_ORDERS")
    private BigDecimal outstandingOrders;
    
    @Column(name = "OUTSTANDING_DELNOTES")
    private BigDecimal outstandingDelnotes;
    
    @Column(name = "CREDIT_LIMIT")
    private BigDecimal creditLimit;
    
    @Column(name = "CREDIT_UNIT_1")
    private Integer creditUnit1;
    
    @Column(name = "CREDIT_LIMIT_2")
    private BigDecimal creditLimit2;
    
    @Column(name = "CREDIT_UNIT_2")
    private Integer creditUnit2;
    
    @Column(name = "CAPITAL_LIMIT_1")
    private BigDecimal capitalLimit1;
    
    @Column(name = "CAPITAL_LIMIT_2")
    private BigDecimal capitalLimit2;
    
    @Column(name = "PAYMENT_LIMIT")
    private BigDecimal paymentLimit;
    
    @Column(name = "CG_AMOUNT")
    private BigDecimal cgAmount;
    
    @Column(name = "CG_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date cgDate;
    
    @Size(max = 25)
    @Column(name = "CG_ANNEXURE")
    private String cgAnnexure;
    
    @Size(max = 25)
    @Column(name = "CG_FILE")
    private String cgFile;
    
    @Size(max = 1)
    @Column(name = "LAST_AUDIT_FLAG")
    private String lastAuditFlag;
    
    @Column(name = "LAST_AUDIT_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastAuditDate;
    
    @Size(max = 1)
    @Column(name = "STOPPED_IND")
    private String stoppedInd;
    
    @Size(max = 1)
    @Column(name = "AGE_BREAK_IND")
    private String ageBreakInd;
    
    @Size(max = 1)
    @Column(name = "DOCUMENT_IND")
    private String documentInd;
    
    @Size(max = 1)
    @Column(name = "BACK_ORDERS_ALLOW_IND")
    private String backOrdersAllowInd;
    
    @Column(name = "FIXED_DISC")
    private BigDecimal fixedDisc;
    
    @Size(max = 1)
    @Column(name = "CRED_APPR_STATUS")
    private String credApprStatus;
    
    @Column(name = "QUOTE_CORE_PROFIT_PERC")
    private BigDecimal quoteCoreProfitPerc;
    
    @Column(name = "QUOTE_CORE_PROFIT_VALUE")
    private BigDecimal quoteCoreProfitValue;
    
    @Column(name = "QUOTE_MODEL_PROFIT_PERC")
    private BigDecimal quoteModelProfitPerc;
    
    @Column(name = "QUOTE_MODEL_PROFIT_VALUE")
    private BigDecimal quoteModelProfitValue;
    
    @Column(name = "COMP_ESTABLISH_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date compEstablishDate;
    
    @Column(name = "CREDIT_REF_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date creditRefDate;
    
    @Column(name = "NO_EMPLOYEES")
    private BigDecimal noEmployees;
    
    @Size(max = 1)
    @Column(name = "FOREX_VARIANCE_IND")
    private String forexVarianceInd;
    
    @Size(max = 1)
    @Column(name = "BUSINESS_PERSONAL_IND")
    private String businessPersonalInd;
    
    @Size(max = 1)
    @Column(name = "THIRD_PARTY_FUND_IND")
    private String thirdPartyFundInd;
    
    @Size(max = 1)
    @Column(name = "THIRD_PARTY_SELF_BILL_IND")
    private String thirdPartySelfBillInd;
    
    @Size(max = 25)
    @Column(name = "FIRST_NAME")
    private String firstName;
    
    @Size(max = 25)
    @Column(name = "LAST_NAME")
    private String lastName;
    
    @Column(name = "DATE_OF_BIRTH")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateOfBirth;
    
    @Size(max = 80)
    @Column(name = "OCCUPATION")
    private String occupation;
    
    // @Pattern(regexp="[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?", message="Invalid email")//if the field contains email address consider using this annotation to enforce field validation
    @Size(max = 80)
    @Column(name = "EMAIL")
    private String email;
    
    @Column(name = "RISK_DEPOSIT_AMT")
    private BigDecimal riskDepositAmt;
    
    @Column(name = "RISK_DEPOSIT_RENTAL_UNITS")
    private BigDecimal riskDepositRentalUnits;
    
    @Column(name = "RISK_DEPOSIT_REBATE_PERC")
    private BigDecimal riskDepositRebatePerc;
    
    @Size(max = 1)
    @Column(name = "DISCLOSED_IND")
    private String disclosedInd;
    
    @Size(max = 1)
    @Column(name = "REGULATED_IND")
    private String regulatedInd;
    
    @Column(name = "WITHHOLD_LIMIT")
    private BigDecimal withholdLimit;
    
    @Column(name = "WITHHOLD_PERC")
    private BigDecimal withholdPerc;
    
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 1)
    @Column(name = "INTERNATIONAL_IND")
    private String internationalInd;
    
    @Size(max = 1)
    @Column(name = "CREDIT_MANAGEMENT_TYPE")
    private String creditManagementType;
    
    @Size(max = 1)
    @Column(name = "INVITE_METHOD_IND")
    private String inviteMethodInd;
    
    @Size(max = 3)
    @Column(name = "QM_KEY")
    private String qmKey;
    
    @Column(name = "STOPPED_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date stoppedDate;
    
    @Size(max = 1)
    @Column(name = "PRINT_STATEMENT")
    private String printStatement;
    
    @Size(max = 25)
    @Column(name = "CREDIT_SCORE")
    private String creditScore;
    
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 1)
    @Column(name = "PAYMENT_IND")
    private String paymentInd;
    
    @Size(max = 1)
    @Column(name = "BANKRUPT_IND")
    private String bankruptInd;
    
    @Size(max = 1)
    @Column(name = "SUMMARY_IND")
    private String summaryInd;
    
    @Size(max = 25)
    @Column(name = "RIN_NO")
    private String rinNo;
    
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 1)
    @Column(name = "WEB_QUOTES_REQ_CC_APPROVAL")
    private String webQuotesReqCcApproval;
    
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 1)
    @Column(name = "WEB_QUOTES_REQ_FA_APPROVAL")
    private String webQuotesReqFaApproval;
    
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 1)
    @Column(name = "UPFIT_IND")
    private String upfitInd;
    
    @Column(name = "CRT_EXT_ACC_TYPE")
    private String crtExtAccType;
    
    @Column(name = "CRT_C_ID ")
    private Long crtCId;
    
    @Column(name= "CREDIT_TERMS_CODE")
    private String	creditTermsCode;
    
    @Column(name= "payment_method")
    private String	paymentMethod;
    
        
    @OneToMany( mappedBy = "externalAccount", fetch = FetchType.LAZY)
    private List<CostCentreCode> costCentreCodesList;
        
    @OneToMany(mappedBy = "externalAccount", fetch = FetchType.LAZY)
    private List<Driver> driversList;
    
    @OneToMany(mappedBy = "externalAccount1", fetch = FetchType.LAZY)
    private List<Driver> driversList1;
    
    @OneToMany(mappedBy = "externalAccounts", fetch = FetchType.LAZY)
    private List<ExternalAccountGradeGroup> externalAccountGradeGroupsList;
    
    @JoinColumn(name = "TITLE", referencedColumnName = "TITLE_CODE")
    @ManyToOne(fetch = FetchType.LAZY)
    private TitleCode title;
    
    @OneToMany(mappedBy = "externalAccounts", fetch = FetchType.LAZY)
    private List<ExternalAccount> externalAccountsList;

    @OneToMany(mappedBy = "externalAccount", fetch = FetchType.LAZY)
    private List<Contact> contacts;
    
    @OneToMany(mappedBy = "externalAccount", fetch = FetchType.LAZY)
    private List<WebsiteUser> websiteUsers;
    
    @OneToMany(mappedBy = "externalAccount", fetch = FetchType.LAZY)
    private List<FleetMaster> fleetMaster;
    
    @OneToMany(mappedBy = "externalAccount", fetch = FetchType.LAZY)
    private List<Contract> contract;    
    
    @OneToMany(mappedBy = "externalAccount", fetch = FetchType.LAZY)
    private List<ExtAccAffiliate> extAccAffiliates;
    
    @JoinColumns({
        @JoinColumn(name = "GROUP_EA_C_ID", referencedColumnName = "C_ID"),
        @JoinColumn(name = "GROUP_EA_TYPE", referencedColumnName = "ACCOUNT_TYPE"),
        @JoinColumn(name = "GROUP_EA_ACCOUNT", referencedColumnName = "ACCOUNT_CODE")})
    @ManyToOne(fetch = FetchType.LAZY)
    private ExternalAccount externalAccounts;
    
    @OneToMany(mappedBy = "externalAccounts1", fetch = FetchType.LAZY)
    private List<ExternalAccount> externalAccountsList1;
    
    @JoinColumns({
        @JoinColumn(name = "VRB_EA_C_ID", referencedColumnName = "C_ID"),
        @JoinColumn(name = "VRB_EA_TYPE", referencedColumnName = "ACCOUNT_TYPE"),
        @JoinColumn(name = "VRB_EA_ACCOUNT", referencedColumnName = "ACCOUNT_CODE")})
    @ManyToOne(fetch = FetchType.LAZY)
    private ExternalAccount externalAccounts1;
    
    @OneToMany(mappedBy = "externalAccounts2", fetch = FetchType.LAZY)
    private List<ExternalAccount> externalAccountsList2;
    
    @JoinColumns({
        @JoinColumn(name = "PROSPECT_ENTITY", referencedColumnName = "C_ID"),
        @JoinColumn(name = "PROSPECT_ACCOUNT_TYPE", referencedColumnName = "ACCOUNT_TYPE"),
        @JoinColumn(name = "PROSPECT_ACCOUNT", referencedColumnName = "ACCOUNT_CODE")})
    @ManyToOne(fetch = FetchType.LAZY)
    private ExternalAccount externalAccounts2;
    
    @OneToMany(mappedBy = "externalAccounts3", fetch = FetchType.LAZY)
    private List<ExternalAccount> externalAccountsList3;
    
    @JoinColumns({
        @JoinColumn(name = "LINK_ENTITY", referencedColumnName = "C_ID"),
        @JoinColumn(name = "LINK_ACCOUNT_TYPE", referencedColumnName = "ACCOUNT_TYPE"),
        @JoinColumn(name = "LINK_ACCOUNT", referencedColumnName = "ACCOUNT_CODE")})
    @ManyToOne(fetch = FetchType.LAZY)
    private ExternalAccount externalAccounts3;
    
    @OneToMany(mappedBy = "externalAccounts4", fetch = FetchType.LAZY)
    private List<ExternalAccount> externalAccountsList4;
    
    @JoinColumns({
        @JoinColumn(name = "BROKER_ENTITY", referencedColumnName = "C_ID"),
        @JoinColumn(name = "BROKER_ACCOUNT_TYPE", referencedColumnName = "ACCOUNT_TYPE"),
        @JoinColumn(name = "BROKER_ACCOUNT", referencedColumnName = "ACCOUNT_CODE")})
    @ManyToOne(fetch = FetchType.LAZY)
    private ExternalAccount externalAccounts4;
    
    @OneToMany(mappedBy = "parentExternalAccount", fetch = FetchType.LAZY)
    private List<ExternalAccount> childExternalAccounts;
    
    @JoinColumns({
        @JoinColumn(name = "PARENT_ACCOUNT_ENTITY", referencedColumnName = "C_ID"),
        @JoinColumn(name = "PARENT_ACCOUNT_TYPE", referencedColumnName = "ACCOUNT_TYPE"),
        @JoinColumn(name = "PARENT_ACCOUNT", referencedColumnName = "ACCOUNT_CODE")})
    @ManyToOne(fetch = FetchType.EAGER)
    private ExternalAccount parentExternalAccount;
    
    @OneToMany(mappedBy = "externalAccounts6", fetch = FetchType.LAZY)
    private List<ExternalAccount> externalAccountsList6;
    
    @JoinColumns({
        @JoinColumn(name = "STATEMENT_TO_ENTITY", referencedColumnName = "C_ID"),
        @JoinColumn(name = "STATEMENT_TO_TYPE", referencedColumnName = "ACCOUNT_TYPE"),
        @JoinColumn(name = "STATEMENT_TO", referencedColumnName = "ACCOUNT_CODE")})
    @ManyToOne(fetch = FetchType.LAZY)
    private ExternalAccount externalAccounts6;
    
    @OneToMany(mappedBy = "externalAccounts7", fetch = FetchType.LAZY)
    private List<ExternalAccount> externalAccountsList7;
    
    @JoinColumns({
        @JoinColumn(name = "INVOICE_TO_ENTITY", referencedColumnName = "C_ID"),
        @JoinColumn(name = "INVOICE_TO_TYPE", referencedColumnName = "ACCOUNT_TYPE"),
        @JoinColumn(name = "INVOICE_TO", referencedColumnName = "ACCOUNT_CODE")})
    @ManyToOne(fetch = FetchType.LAZY)
    private ExternalAccount externalAccounts7;
    
    @OneToMany(mappedBy = "externalAccounts8", fetch = FetchType.LAZY)
    private List<ExternalAccount> externalAccountsList8;
    
    @JoinColumns({
        @JoinColumn(name = "CG_EA_ENTITY", referencedColumnName = "C_ID"),
        @JoinColumn(name = "CG_EA_TYPE", referencedColumnName = "ACCOUNT_TYPE"),
        @JoinColumn(name = "CG_EA_ACCOUNT", referencedColumnName = "ACCOUNT_CODE")})
    @ManyToOne(fetch = FetchType.LAZY)
    private ExternalAccount externalAccounts8;
    
    @OneToMany(mappedBy = "payeeAccount")
    private List<MaintenanceRequest> maintenanceRequests;    
    
    @OneToMany(mappedBy = "payeeAccount")
    private List<MaintenanceRequest> serviceProvider; 
    
    @OneToMany(mappedBy = "externalAccount", orphanRemoval=true)
    private List<ClientPointAccount> clientPointAccount;       
    
    @OneToMany(mappedBy = "payeeAccount")
    private List<DealerAccessoryPrice> dealerAccessoryPrices;  
    
    @OneToMany(mappedBy = "externalAccount")
    private List<UpfitterQuote> upfitterQuotes;
    
    @OneToMany(mappedBy = "externalAccount")
    private List<ExtAccAddress> externalAccountAddresses;
    
	@OneToMany(mappedBy = "upfitter")
    private List<UpfitterProgress> upfitterProgress;
    
    public ExternalAccount() {}

    public ExternalAccount(ExternalAccountPK externalAccountPK) {
        this.externalAccountPK = externalAccountPK;
    }

    public ExternalAccount(ExternalAccountPK externalAccountPK, String accountName, String shortName, String accStatus, String internationalInd, String paymentInd, String webQuotesReqCcApproval, String webQuotesReqFaApproval, String upfitInd) {
        this.externalAccountPK = externalAccountPK;
        this.accountName = accountName;
        this.shortName = shortName;
        this.accStatus = accStatus;
        this.internationalInd = internationalInd;
        this.paymentInd = paymentInd;
        this.webQuotesReqCcApproval = webQuotesReqCcApproval;
        this.webQuotesReqFaApproval = webQuotesReqFaApproval;
        this.upfitInd = upfitInd;
    }

    public ExternalAccount(long cId, String accountType, String accountCode) {
        this.externalAccountPK = new ExternalAccountPK(cId, accountType, accountCode);
    }

    public ExternalAccountPK getExternalAccountPK() {
        return externalAccountPK;
    }

    public void setExternalAccountPK(ExternalAccountPK externalAccountPK) {
        this.externalAccountPK = externalAccountPK;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getAccStatus() {
        return accStatus;
    }

    public void setAccStatus(String accStatus) {
        this.accStatus = accStatus;
    }

    public String getPayeeName() {
        return payeeName;
    }

    public void setPayeeName(String payeeName) {
        this.payeeName = payeeName;
    }

    public String getRegName() {
        return regName;
    }

    public void setRegName(String regName) {
        this.regName = regName;
    }

    public String getCompRegNo() {
        return compRegNo;
    }

    public void setCompRegNo(String compRegNo) {
        this.compRegNo = compRegNo;
    }

    public Date getCompRegDate() {
        return compRegDate;
    }

    public void setCompRegDate(Date compRegDate) {
        this.compRegDate = compRegDate;
    }

    public String getTaxRegNo() {
        return taxRegNo;
    }

    public void setTaxRegNo(String taxRegNo) {
        this.taxRegNo = taxRegNo;
    }

    public String getTaxExemptionNo() {
        return taxExemptionNo;
    }

    public void setTaxExemptionNo(String taxExemptionNo) {
        this.taxExemptionNo = taxExemptionNo;
    }

    public String getTaxInd() {
        return taxInd;
    }

    public void setTaxInd(String taxInd) {
        this.taxInd = taxInd;
    }

    public String getTaxHoldingInd() {
        return taxHoldingInd;
    }

    public void setTaxHoldingInd(String taxHoldingInd) {
        this.taxHoldingInd = taxHoldingInd;
    }

    public String getTelephoneNumber() {
        return telephoneNumber;
    }

    public void setTelephoneNumber(String telephoneNumber) {
        this.telephoneNumber = telephoneNumber;
    }

    public String getFaxCode() {
        return faxCode;
    }

    public void setFaxCode(String faxCode) {
        this.faxCode = faxCode;
    }

    public String getTelexCode() {
        return telexCode;
    }

    public void setTelexCode(String telexCode) {
        this.telexCode = telexCode;
    }

    public String getMainActivity() {
        return mainActivity;
    }

    public void setMainActivity(String mainActivity) {
        this.mainActivity = mainActivity;
    }

    public String getBusinessSector() {
        return businessSector;
    }

    public void setBusinessSector(String businessSector) {
        this.businessSector = businessSector;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getExtRef() {
        return extRef;
    }

    public void setExtRef(String extRef) {
        this.extRef = extRef;
    }

    public String getCreditRef() {
        return creditRef;
    }

    public void setCreditRef(String creditRef) {
        this.creditRef = creditRef;
    }

    public String getBuildingSocietyCode() {
        return buildingSocietyCode;
    }

    public void setBuildingSocietyCode(String buildingSocietyCode) {
        this.buildingSocietyCode = buildingSocietyCode;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getBankAccountNumber() {
        return bankAccountNumber;
    }

    public void setBankAccountNumber(String bankAccountNumber) {
        this.bankAccountNumber = bankAccountNumber;
    }

    public String getBankAccountName() {
        return bankAccountName;
    }

    public void setBankAccountName(String bankAccountName) {
        this.bankAccountName = bankAccountName;
    }

    public String getBankSortCode() {
        return bankSortCode;
    }

    public void setBankSortCode(String bankSortCode) {
        this.bankSortCode = bankSortCode;
    }

    public String getBankAddLine1() {
        return bankAddLine1;
    }

    public void setBankAddLine1(String bankAddLine1) {
        this.bankAddLine1 = bankAddLine1;
    }

    public String getBankAddLine2() {
        return bankAddLine2;
    }

    public void setBankAddLine2(String bankAddLine2) {
        this.bankAddLine2 = bankAddLine2;
    }

    public String getBankAddLine3() {
        return bankAddLine3;
    }

    public void setBankAddLine3(String bankAddLine3) {
        this.bankAddLine3 = bankAddLine3;
    }

    public String getBankAddLine4() {
        return bankAddLine4;
    }

    public void setBankAddLine4(String bankAddLine4) {
        this.bankAddLine4 = bankAddLine4;
    }

    public String getBankPostCode() {
        return bankPostCode;
    }

    public void setBankPostCode(String bankPostCode) {
        this.bankPostCode = bankPostCode;
    }

    public Date getDateLastPurchase() {
        return dateLastPurchase;
    }

    public void setDateLastPurchase(Date dateLastPurchase) {
        this.dateLastPurchase = dateLastPurchase;
    }

    public BigDecimal getAmountLastPurchase() {
        return amountLastPurchase;
    }

    public void setAmountLastPurchase(BigDecimal amountLastPurchase) {
        this.amountLastPurchase = amountLastPurchase;
    }

    public Date getDateLastReceipt() {
        return dateLastReceipt;
    }

    public void setDateLastReceipt(Date dateLastReceipt) {
        this.dateLastReceipt = dateLastReceipt;
    }

    public BigDecimal getAmountLastReceipt() {
        return amountLastReceipt;
    }

    public void setAmountLastReceipt(BigDecimal amountLastReceipt) {
        this.amountLastReceipt = amountLastReceipt;
    }

    public Date getDateLastStatement() {
        return dateLastStatement;
    }

    public void setDateLastStatement(Date dateLastStatement) {
        this.dateLastStatement = dateLastStatement;
    }

    public Date getDateLastCreditCheck() {
        return dateLastCreditCheck;
    }

    public void setDateLastCreditCheck(Date dateLastCreditCheck) {
        this.dateLastCreditCheck = dateLastCreditCheck;
    }

    public Date getDateNextCreditCheck() {
        return dateNextCreditCheck;
    }

    public void setDateNextCreditCheck(Date dateNextCreditCheck) {
        this.dateNextCreditCheck = dateNextCreditCheck;
    }

    public Date getDateFinYearEnd() {
        return dateFinYearEnd;
    }

    public void setDateFinYearEnd(Date dateFinYearEnd) {
        this.dateFinYearEnd = dateFinYearEnd;
    }

    public Date getDateOpened() {
        return dateOpened;
    }

    public void setDateOpened(Date dateOpened) {
        this.dateOpened = dateOpened;
    }

    public Date getDateExpired() {
        return dateExpired;
    }

    public void setDateExpired(Date dateExpired) {
        this.dateExpired = dateExpired;
    }

    public BigDecimal getOutstandingOrders() {
        return outstandingOrders;
    }

    public void setOutstandingOrders(BigDecimal outstandingOrders) {
        this.outstandingOrders = outstandingOrders;
    }

    public BigDecimal getOutstandingDelnotes() {
        return outstandingDelnotes;
    }

    public void setOutstandingDelnotes(BigDecimal outstandingDelnotes) {
        this.outstandingDelnotes = outstandingDelnotes;
    }

    public BigDecimal getCreditLimit() {
        return creditLimit;
    }

    public void setCreditLimit(BigDecimal creditLimit) {
        this.creditLimit = creditLimit;
    }

    public Integer getCreditUnit1() {
        return creditUnit1;
    }

    public void setCreditUnit1(Integer creditUnit1) {
        this.creditUnit1 = creditUnit1;
    }

    public BigDecimal getCreditLimit2() {
        return creditLimit2;
    }

    public void setCreditLimit2(BigDecimal creditLimit2) {
        this.creditLimit2 = creditLimit2;
    }

    public Integer getCreditUnit2() {
        return creditUnit2;
    }

    public void setCreditUnit2(Integer creditUnit2) {
        this.creditUnit2 = creditUnit2;
    }

    public BigDecimal getCapitalLimit1() {
        return capitalLimit1;
    }

    public void setCapitalLimit1(BigDecimal capitalLimit1) {
        this.capitalLimit1 = capitalLimit1;
    }

    public BigDecimal getCapitalLimit2() {
        return capitalLimit2;
    }

    public void setCapitalLimit2(BigDecimal capitalLimit2) {
        this.capitalLimit2 = capitalLimit2;
    }

    public BigDecimal getPaymentLimit() {
        return paymentLimit;
    }

    public void setPaymentLimit(BigDecimal paymentLimit) {
        this.paymentLimit = paymentLimit;
    }

    public BigDecimal getCgAmount() {
        return cgAmount;
    }

    public void setCgAmount(BigDecimal cgAmount) {
        this.cgAmount = cgAmount;
    }

    public Date getCgDate() {
        return cgDate;
    }

    public void setCgDate(Date cgDate) {
        this.cgDate = cgDate;
    }

    public String getCgAnnexure() {
        return cgAnnexure;
    }

    public void setCgAnnexure(String cgAnnexure) {
        this.cgAnnexure = cgAnnexure;
    }

    public String getCgFile() {
        return cgFile;
    }

    public void setCgFile(String cgFile) {
        this.cgFile = cgFile;
    }

    public String getLastAuditFlag() {
        return lastAuditFlag;
    }

    public void setLastAuditFlag(String lastAuditFlag) {
        this.lastAuditFlag = lastAuditFlag;
    }

    public Date getLastAuditDate() {
        return lastAuditDate;
    }

    public void setLastAuditDate(Date lastAuditDate) {
        this.lastAuditDate = lastAuditDate;
    }

    public String getStoppedInd() {
        return stoppedInd;
    }

    public void setStoppedInd(String stoppedInd) {
        this.stoppedInd = stoppedInd;
    }

    public String getAgeBreakInd() {
        return ageBreakInd;
    }

    public void setAgeBreakInd(String ageBreakInd) {
        this.ageBreakInd = ageBreakInd;
    }

    public String getDocumentInd() {
        return documentInd;
    }

    public void setDocumentInd(String documentInd) {
        this.documentInd = documentInd;
    }

    public String getBackOrdersAllowInd() {
        return backOrdersAllowInd;
    }

    public void setBackOrdersAllowInd(String backOrdersAllowInd) {
        this.backOrdersAllowInd = backOrdersAllowInd;
    }

    public BigDecimal getFixedDisc() {
        return fixedDisc;
    }

    public void setFixedDisc(BigDecimal fixedDisc) {
        this.fixedDisc = fixedDisc;
    }

    public String getCredApprStatus() {
        return credApprStatus;
    }

    public void setCredApprStatus(String credApprStatus) {
        this.credApprStatus = credApprStatus;
    }

    public BigDecimal getQuoteCoreProfitPerc() {
        return quoteCoreProfitPerc;
    }

    public void setQuoteCoreProfitPerc(BigDecimal quoteCoreProfitPerc) {
        this.quoteCoreProfitPerc = quoteCoreProfitPerc;
    }

    public BigDecimal getQuoteCoreProfitValue() {
        return quoteCoreProfitValue;
    }

    public void setQuoteCoreProfitValue(BigDecimal quoteCoreProfitValue) {
        this.quoteCoreProfitValue = quoteCoreProfitValue;
    }

    public BigDecimal getQuoteModelProfitPerc() {
        return quoteModelProfitPerc;
    }

    public void setQuoteModelProfitPerc(BigDecimal quoteModelProfitPerc) {
        this.quoteModelProfitPerc = quoteModelProfitPerc;
    }

    public BigDecimal getQuoteModelProfitValue() {
        return quoteModelProfitValue;
    }

    public void setQuoteModelProfitValue(BigDecimal quoteModelProfitValue) {
        this.quoteModelProfitValue = quoteModelProfitValue;
    }

    public Date getCompEstablishDate() {
        return compEstablishDate;
    }

    public void setCompEstablishDate(Date compEstablishDate) {
        this.compEstablishDate = compEstablishDate;
    }

    public Date getCreditRefDate() {
        return creditRefDate;
    }

    public void setCreditRefDate(Date creditRefDate) {
        this.creditRefDate = creditRefDate;
    }

    public BigDecimal getNoEmployees() {
        return noEmployees;
    }

    public void setNoEmployees(BigDecimal noEmployees) {
        this.noEmployees = noEmployees;
    }

    public String getForexVarianceInd() {
        return forexVarianceInd;
    }

    public void setForexVarianceInd(String forexVarianceInd) {
        this.forexVarianceInd = forexVarianceInd;
    }

    public String getBusinessPersonalInd() {
        return businessPersonalInd;
    }

    public void setBusinessPersonalInd(String businessPersonalInd) {
        this.businessPersonalInd = businessPersonalInd;
    }

    public String getThirdPartyFundInd() {
        return thirdPartyFundInd;
    }

    public void setThirdPartyFundInd(String thirdPartyFundInd) {
        this.thirdPartyFundInd = thirdPartyFundInd;
    }

    public String getThirdPartySelfBillInd() {
        return thirdPartySelfBillInd;
    }

    public void setThirdPartySelfBillInd(String thirdPartySelfBillInd) {
        this.thirdPartySelfBillInd = thirdPartySelfBillInd;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public BigDecimal getRiskDepositAmt() {
        return riskDepositAmt;
    }

    public void setRiskDepositAmt(BigDecimal riskDepositAmt) {
        this.riskDepositAmt = riskDepositAmt;
    }

    public BigDecimal getRiskDepositRentalUnits() {
        return riskDepositRentalUnits;
    }

    public void setRiskDepositRentalUnits(BigDecimal riskDepositRentalUnits) {
        this.riskDepositRentalUnits = riskDepositRentalUnits;
    }

    public BigDecimal getRiskDepositRebatePerc() {
        return riskDepositRebatePerc;
    }

    public void setRiskDepositRebatePerc(BigDecimal riskDepositRebatePerc) {
        this.riskDepositRebatePerc = riskDepositRebatePerc;
    }

    public String getDisclosedInd() {
        return disclosedInd;
    }

    public void setDisclosedInd(String disclosedInd) {
        this.disclosedInd = disclosedInd;
    }

    public String getRegulatedInd() {
        return regulatedInd;
    }

    public void setRegulatedInd(String regulatedInd) {
        this.regulatedInd = regulatedInd;
    }

    public BigDecimal getWithholdLimit() {
        return withholdLimit;
    }

    public void setWithholdLimit(BigDecimal withholdLimit) {
        this.withholdLimit = withholdLimit;
    }

    public BigDecimal getWithholdPerc() {
        return withholdPerc;
    }

    public void setWithholdPerc(BigDecimal withholdPerc) {
        this.withholdPerc = withholdPerc;
    }

    public String getInternationalInd() {
        return internationalInd;
    }

    public void setInternationalInd(String internationalInd) {
        this.internationalInd = internationalInd;
    }

    public String getCreditManagementType() {
        return creditManagementType;
    }

    public void setCreditManagementType(String creditManagementType) {
        this.creditManagementType = creditManagementType;
    }

    public String getInviteMethodInd() {
        return inviteMethodInd;
    }

    public void setInviteMethodInd(String inviteMethodInd) {
        this.inviteMethodInd = inviteMethodInd;
    }

    public String getQmKey() {
        return qmKey;
    }

    public void setQmKey(String qmKey) {
        this.qmKey = qmKey;
    }

    public Date getStoppedDate() {
        return stoppedDate;
    }

    public void setStoppedDate(Date stoppedDate) {
        this.stoppedDate = stoppedDate;
    }

    public String getPrintStatement() {
        return printStatement;
    }

    public void setPrintStatement(String printStatement) {
        this.printStatement = printStatement;
    }

    public String getCreditScore() {
        return creditScore;
    }

    public void setCreditScore(String creditScore) {
        this.creditScore = creditScore;
    }

    public String getPaymentInd() {
        return paymentInd;
    }

    public void setPaymentInd(String paymentInd) {
        this.paymentInd = paymentInd;
    }

    public String getBankruptInd() {
        return bankruptInd;
    }

    public void setBankruptInd(String bankruptInd) {
        this.bankruptInd = bankruptInd;
    }

    public String getSummaryInd() {
        return summaryInd;
    }

    public void setSummaryInd(String summaryInd) {
        this.summaryInd = summaryInd;
    }

    public String getRinNo() {
        return rinNo;
    }

    public void setRinNo(String rinNo) {
        this.rinNo = rinNo;
    }

    public String getWebQuotesReqCcApproval() {
        return webQuotesReqCcApproval;
    }

    public void setWebQuotesReqCcApproval(String webQuotesReqCcApproval) {
        this.webQuotesReqCcApproval = webQuotesReqCcApproval;
    }

    public String getWebQuotesReqFaApproval() {
        return webQuotesReqFaApproval;
    }

    public void setWebQuotesReqFaApproval(String webQuotesReqFaApproval) {
        this.webQuotesReqFaApproval = webQuotesReqFaApproval;
    }

    public String getUpfitInd() {
        return upfitInd;
    }

    public void setUpfitInd(String upfitInd) {
        this.upfitInd = upfitInd;
    }

    public List<CostCentreCode> getCostCentreCodesList() {
        return costCentreCodesList;
    }

    public void setCostCentreCodesList(List<CostCentreCode> costCentreCodesList) {
        this.costCentreCodesList = costCentreCodesList;
    }

    public List<Driver> getDriversList() {
        return driversList;
    }

    public void setDriversList(List<Driver> driversList) {
        this.driversList = driversList;
    }

    public List<Driver> getDriversList1() {
        return driversList1;
    }

    public void setDriversList1(List<Driver> driversList1) {
        this.driversList1 = driversList1;
    }

    public List<ExternalAccountGradeGroup> getExternalAccountGradeGroupsList() {
        return externalAccountGradeGroupsList;
    }

    public void setExternalAccountGradeGroupsList(List<ExternalAccountGradeGroup> externalAccountGradeGroupsList) {
        this.externalAccountGradeGroupsList = externalAccountGradeGroupsList;
    }

    public TitleCode getTitle() {
        return title;
    }

    public void setTitle(TitleCode title) {
        this.title = title;
    }

    public List<ExternalAccount> getExternalAccountsList() {
        return externalAccountsList;
    }

    public void setExternalAccountsList(List<ExternalAccount> externalAccountsList) {
        this.externalAccountsList = externalAccountsList;
    }

    public ExternalAccount getExternalAccounts() {
        return externalAccounts;
    }

    public void setExternalAccounts(ExternalAccount externalAccounts) {
        this.externalAccounts = externalAccounts;
    }

    public List<ExternalAccount> getExternalAccountsList1() {
        return externalAccountsList1;
    }

    public void setExternalAccountsList1(List<ExternalAccount> externalAccountsList1) {
        this.externalAccountsList1 = externalAccountsList1;
    }

    public ExternalAccount getExternalAccounts1() {
        return externalAccounts1;
    }

    public void setExternalAccounts1(ExternalAccount externalAccounts1) {
        this.externalAccounts1 = externalAccounts1;
    }

    public List<ExternalAccount> getExternalAccountsList2() {
        return externalAccountsList2;
    }

    public void setExternalAccountsList2(List<ExternalAccount> externalAccountsList2) {
        this.externalAccountsList2 = externalAccountsList2;
    }

    public ExternalAccount getExternalAccounts2() {
        return externalAccounts2;
    }

    public void setExternalAccounts2(ExternalAccount externalAccounts2) {
        this.externalAccounts2 = externalAccounts2;
    }

    public List<ExternalAccount> getExternalAccountsList3() {
        return externalAccountsList3;
    }

    public void setExternalAccountsList3(List<ExternalAccount> externalAccountsList3) {
        this.externalAccountsList3 = externalAccountsList3;
    }

    public ExternalAccount getExternalAccounts3() {
        return externalAccounts3;
    }

    public void setExternalAccounts3(ExternalAccount externalAccounts3) {
        this.externalAccounts3 = externalAccounts3;
    }

    public List<ExternalAccount> getExternalAccountsList4() {
        return externalAccountsList4;
    }

    public void setExternalAccountsList4(List<ExternalAccount> externalAccountsList4) {
        this.externalAccountsList4 = externalAccountsList4;
    }

    public ExternalAccount getExternalAccounts4() {
        return externalAccounts4;
    }

    public void setExternalAccounts4(ExternalAccount externalAccounts4) {
        this.externalAccounts4 = externalAccounts4;
    }

    public List<ExternalAccount> getChildExternalAccounts() {
        return childExternalAccounts;
    }

    public void setchildExternalAccounts(List<ExternalAccount> childExternalAccounts) {
        this.childExternalAccounts = childExternalAccounts;
    }

    public ExternalAccount getParentExternalAccount() {
        return parentExternalAccount;
    }

    public void setParentExternalAccount(ExternalAccount parentExternalAccount) {
        this.parentExternalAccount = parentExternalAccount;
    }

    public List<ExternalAccount> getExternalAccountsList6() {
        return externalAccountsList6;
    }

    public void setExternalAccountsList6(List<ExternalAccount> externalAccountsList6) {
        this.externalAccountsList6 = externalAccountsList6;
    }

    public ExternalAccount getExternalAccounts6() {
        return externalAccounts6;
    }

    public void setExternalAccounts6(ExternalAccount externalAccounts6) {
        this.externalAccounts6 = externalAccounts6;
    }

    public List<ExternalAccount> getExternalAccountsList7() {
        return externalAccountsList7;
    }

    public void setExternalAccountsList7(List<ExternalAccount> externalAccountsList7) {
        this.externalAccountsList7 = externalAccountsList7;
    }

    public ExternalAccount getExternalAccounts7() {
        return externalAccounts7;
    }

    public void setExternalAccounts7(ExternalAccount externalAccounts7) {
        this.externalAccounts7 = externalAccounts7;
    }

    public List<ExternalAccount> getExternalAccountsList8() {
        return externalAccountsList8;
    }

    public void setExternalAccountsList8(List<ExternalAccount> externalAccountsList8) {
        this.externalAccountsList8 = externalAccountsList8;
    }

    public ExternalAccount getExternalAccounts8() {
        return externalAccounts8;
    }

    public void setExternalAccounts8(ExternalAccount externalAccounts8) {
        this.externalAccounts8 = externalAccounts8;
    }

    public List<FleetMaster> getFleetMaster() {
		return fleetMaster;
	}

	public void setFleetMaster(List<FleetMaster> fleetMaster) {
		this.fleetMaster = fleetMaster;
	}

	public List<Contact> getContacts() {
		return contacts;
	}

	public void setContacts(List<Contact> contacts) {
		this.contacts = contacts;
	}

	public List<WebsiteUser> getWebsiteUsers() {
		return websiteUsers;
	}

	public void setWebsiteUsers(List<WebsiteUser> websiteUsers) {
		this.websiteUsers = websiteUsers;
	}
	
	public List<Contract> getContract() {
		return contract;
	}

	public void setContract(List<Contract> contract) {
		this.contract = contract;
	}

	public List<MaintenanceRequest> getMaintenanceRequests() {
		return maintenanceRequests;
	}

	public void setMaintenanceRequests(List<MaintenanceRequest> maintenanceRequests) {
		this.maintenanceRequests = maintenanceRequests;
	}
	

	public String getCrtExtAccType() {
		return crtExtAccType;
	}

	public void setCrtExtAccType(String crtExtAccType) {
		this.crtExtAccType = crtExtAccType;
	}

	public Long getCrtCId() {
		return crtCId;
	}

	public void setCrtCId(Long crtCId) {
		this.crtCId = crtCId;
	}

	
	public String getCreditTermsCode() {
		return creditTermsCode;
	}

	public void setCreditTermsCode(String creditTermsCode) {
		this.creditTermsCode = creditTermsCode;
	}

	@Override
    public int hashCode() {
        int hash = 0;
        hash += (externalAccountPK != null ? externalAccountPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ExternalAccount)) {
            return false;
        }
        ExternalAccount other = (ExternalAccount) object;
        if ((this.getExternalAccountPK() == null && other.getExternalAccountPK() != null) || (this.getExternalAccountPK() != null && !this.getExternalAccountPK().equals(other.getExternalAccountPK()))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.mikealbert.entity.ExternalAccount[ externalAccountPK=" + externalAccountPK + " ]";
    }

	public List<MaintenanceRequest> getServiceProvider() {
		return serviceProvider;
	}

	public void setServiceProvider(List<MaintenanceRequest> serviceProvider) {
		this.serviceProvider = serviceProvider;
	}

	public List<DealerAccessoryPrice> getDealerAccessoryPrices() {
		return dealerAccessoryPrices;
	}

	public void setDealerAccessoryPrices(List<DealerAccessoryPrice> dealerAccessoryPrices) {
		this.dealerAccessoryPrices = dealerAccessoryPrices;
	}

	public String getPaymentMethod() {
		return paymentMethod;
	}

	public void setPaymentMethod(String paymentMethod) {
		this.paymentMethod = paymentMethod;
	}
	

	public List<ClientPointAccount> getClientPointAccount() {
		return clientPointAccount;
	}

	public void setClientPointAccount(List<ClientPointAccount> clientPointAccount) {
		this.clientPointAccount = clientPointAccount;
	}

	public List<UpfitterQuote> getUpfitterQuotes() {
		return upfitterQuotes;
	}

	public void setUpfitterQuotes(List<UpfitterQuote> upfitterQuotes) {
		this.upfitterQuotes = upfitterQuotes;
	}

	public List<ExtAccAddress> getExternalAccountAddresses() {
		return externalAccountAddresses;
	}

	public void setExternalAccountAddresses(List<ExtAccAddress> externalAccountAddresses) {
		this.externalAccountAddresses = externalAccountAddresses;
	}

	public List<UpfitterProgress> getUpfitterProgress() {
		return upfitterProgress;
	}

	public void setUpfitterProgress(List<UpfitterProgress> upfitterProgress) {
		this.upfitterProgress = upfitterProgress;
	}

	public List<ExtAccAffiliate> getExtAccAffiliates() {
		return extAccAffiliates;
	}

	public void setExtAccAffiliates(List<ExtAccAffiliate> extAccAffiliates) {
		this.extAccAffiliates = extAccAffiliates;
	}
    
}
