package com.mikealbert.vision.view.bean.lov;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.primefaces.component.datatable.DataTable;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.data.SortEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import com.mikealbert.common.MalLogger;
import com.mikealbert.common.MalLoggerFactory;
import com.mikealbert.data.dao.ContactDAO;
import com.mikealbert.data.entity.Contact;
import com.mikealbert.data.vo.StockUnitsLovVO;
import com.mikealbert.exception.MalException;
import com.mikealbert.service.FleetMasterService;
import com.mikealbert.service.QuotationService;
import com.mikealbert.service.QuoteRequestService;
import com.mikealbert.util.MALUtilities;
import com.mikealbert.vision.view.ViewConstants;
import com.mikealbert.vision.view.bean.BaseBean;

@Component("contactsLovBean")
@Scope("view")
public class ContactsLovBean extends BaseBean  {

	MalLogger logger = MalLoggerFactory.getLogger(this.getClass());

	private static final long serialVersionUID = 1L;
	
	@Resource
	QuotationService quotationService;
	@Resource
	ContactDAO contactDAO;
	
	private Contact selectedContact;

	private List<Contact> contacts = new ArrayList<Contact>();
	
	@PostConstruct
	public void init() {
		selectedContact = null;
	}		
		
	
	public void fetchContacts() {
		Long clientCID = Long.parseLong(getRequestParameter("CLIENT_CID_LOV_INPUT"));
		String clientAccountType = (String) getRequestParameter("CLIENT_TYPE_LOV_INPUT");
		String clientAccountCode = (String) getRequestParameter("CLIENT_ACCOUNT_LOV_INPUT");

		selectedContact = null;
		contacts = contactDAO.getContactsByAccountInfo(clientCID, clientAccountType, clientAccountCode);

	}
	

	public void onRowSelect(SelectEvent event) {
		Contact selectedContact = (Contact) event.getObject();
		setSelectedContact(selectedContact);
	}
	

	public Contact getSelectedContact() {
		return selectedContact;
	}

	public void setSelectedContact(Contact selectedContact) {
		this.selectedContact = selectedContact;
	}


	public List<Contact> getContacts() {
		return contacts;
	}


	public void setContacts(List<Contact> contacts) {
		this.contacts = contacts;
	}

	
	
}
