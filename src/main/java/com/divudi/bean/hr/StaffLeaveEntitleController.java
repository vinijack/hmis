/*
 * MSc(Biomedical Informatics) Project
 *
 * Development and Implementation of a Web-based Combined Data Repository of
 Genealogical, Clinical, Laboratory and Genetic Data
 * and
 * a Set of Related Tools
 */
package com.divudi.bean.hr;

import com.divudi.bean.common.UtilityController;
import com.divudi.bean.common.SessionController;
import com.divudi.data.hr.LeaveType;
import com.divudi.ejb.CommonFunctions;
import com.divudi.entity.Staff;
import com.divudi.facade.StaffLeaveEntitleFacade;
import com.divudi.entity.hr.StaffLeaveEntitle;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;
import javax.inject.Named;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

/**
 *
 * @author Dr. M. H. B. Ariyaratne, MBBS, PGIM Trainee for MSc(Biomedical
 * Informatics)
 */
@Named
@SessionScoped
public class StaffLeaveEntitleController implements Serializable {

    private static final long serialVersionUID = 1L;
    @Inject
    SessionController sessionController;
    @EJB
    private StaffLeaveEntitleFacade ejbFacade;
    List<StaffLeaveEntitle> selectedItems;
    List<StaffLeaveEntitle> selectedAllItems;
    private StaffLeaveEntitle current;
    private List<StaffLeaveEntitle> items = null;
    String selectText = "";

    public List<StaffLeaveEntitle> getSelectedItems() {
//        selectedItems = getFacade().findBySQL("select c from StaffLeaveEntitle c where c.retired=false and upper(c.name) like '%" + getSelectText().toUpperCase() + "%' order by c.name");
        return selectedItems;
    }

    public List<StaffLeaveEntitle> completeStaffLeaveEntitle(String qry) {
        List<StaffLeaveEntitle> a = null;
        if (qry != null) {
            a = getFacade().findBySQL("select c from StaffLeaveEntitle c where c.retired=false and upper(c.name) like '%" + qry.toUpperCase() + "%' order by c.name");
        }
        if (a == null) {
            a = new ArrayList<>();
        }
        return a;
    }

    public void prepareAdd() {
        current = new StaffLeaveEntitle();
    }

    public void setSelectedItems(List<StaffLeaveEntitle> selectedItems) {
        this.selectedItems = selectedItems;
    }

    public String getSelectText() {
        return selectText;
    }

    private void recreateModel() {
        items = null;
        current = null;
    }

    @EJB
    CommonFunctions commonFunctions;

    public void saveSelected() {

        if (getCurrent().getFromDate() == null) {
            UtilityController.addErrorMessage("Please Select From Date");
            return;
        }

        if (getCurrent().getToDate() == null) {
            UtilityController.addErrorMessage("Please Select To Date");
            return;
        }

//        current.setFromDate(fromDate);
//        current.setToDate(toDate);

        if (getCurrent().getId() != null && getCurrent().getId() > 0) {
            getFacade().edit(current);
            UtilityController.addSuccessMessage("savedOldSuccessfully");
        } else {
            current.setCreatedAt(Calendar.getInstance(TimeZone.getTimeZone("IST")).getTime());
            current.setCreater(getSessionController().getLoggedUser());
            getFacade().create(current);
            UtilityController.addSuccessMessage("savedNewSuccessfully");
        }
        recreateModel();
//        getItems();
    }

    public void setSelectText(String selectText) {
        this.selectText = selectText;
    }

    public StaffLeaveEntitleFacade getEjbFacade() {
        return ejbFacade;
    }

    public void setEjbFacade(StaffLeaveEntitleFacade ejbFacade) {
        this.ejbFacade = ejbFacade;
    }

    public SessionController getSessionController() {
        return sessionController;
    }

    public void setSessionController(SessionController sessionController) {
        this.sessionController = sessionController;
    }

    public StaffLeaveEntitleController() {
    }

    public StaffLeaveEntitle getCurrent() {
        if (current == null) {
            current = new StaffLeaveEntitle();
        }
        return current;
    }

    public void setCurrent(StaffLeaveEntitle current) {
        this.current = current;
    }

    public void delete() {

        if (current != null) {
            current.setRetired(true);
            current.setRetiredAt(Calendar.getInstance(TimeZone.getTimeZone("IST")).getTime());
            current.setRetirer(getSessionController().getLoggedUser());
            getFacade().edit(current);
            UtilityController.addSuccessMessage("DeleteSuccessfull");
        } else {
            UtilityController.addSuccessMessage("NothingToDelete");
        }
        recreateModel();
        getItems();
        current = null;
        getCurrent();
    }

    private StaffLeaveEntitleFacade getFacade() {
        return ejbFacade;
    }

    Staff staff;

    public Staff getStaff() {
        return staff;
    }

    public void setStaff(Staff staff) {
        this.staff = staff;
    }

    LeaveType leaveType;

    public LeaveType getLeaveType() {
        return leaveType;
    }

    public void setLeaveType(LeaveType leaveType) {
        this.leaveType = leaveType;
    }

    Date date;
    Date fromDate;
    Date toDate;

    public CommonFunctions getCommonFunctions() {
        return commonFunctions;
    }

    public void setCommonFunctions(CommonFunctions commonFunctions) {
        this.commonFunctions = commonFunctions;
    }

    public Date getFromDate() {
        return fromDate;
    }

    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    public Date getToDate() {
        return toDate;
    }

    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void createItems() {
        HashMap hm = new HashMap();
        String sql = "select c from StaffLeaveEntitle c "
                + " where c.retired=false ";
//
//        if (date != null) {
//            sql += " and c.fromDate=:fd "
//                    + " and c.toDate=:td ";
//        }

        if (staff != null) {
            sql += " and c.staff= :stf ";
            hm.put("stf", staff);
        }

        if (leaveType != null) {
            sql += " and c.leaveType= :ltp ";
            hm.put("ltp", leaveType);
        }

        sql += "  order by c.staff.code";

        selectedItems = getFacade().findBySQL(sql, hm);
    }

    public void resetDate() {
        if (selectedItems == null) {
            return;
        }
        if (fromDate == null) {
            return;
        }
        
        if (toDate == null) {
            return;
        }

        for (StaffLeaveEntitle s : selectedItems) {
            s.setFromDate(commonFunctions.getFirstDayOfYear(fromDate));
            s.setToDate(commonFunctions.getLastDayOfYear(toDate));
            ejbFacade.edit(s);
        }
    }

    public void createAllItems() {
        HashMap hm = new HashMap();
        String sql = "select c from StaffLeaveEntitle c "
                + " where c.retired=false "
                + " order by c.staff.codeInterger ";

        selectedAllItems = getFacade().findBySQL(sql, hm);
    }

    public List<StaffLeaveEntitle> getItems() {
//        items = getFacade().findAll("name", true);
        return items;
    }

    public List<StaffLeaveEntitle> getSelectedAllItems() {
        return selectedAllItems;
    }

    public void setSelectedAllItems(List<StaffLeaveEntitle> selectedAllItems) {
        this.selectedAllItems = selectedAllItems;
    }

    /**
     *
     */
    @FacesConverter(forClass = StaffLeaveEntitle.class)
    public static class StaffLeaveEntitleConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            StaffLeaveEntitleController controller = (StaffLeaveEntitleController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "staffCategoryController");
            return controller.getEjbFacade().find(getKey(value));
        }

        java.lang.Long getKey(String value) {
            java.lang.Long key;
            key = Long.valueOf(value);
            return key;
        }

        String getStringKey(java.lang.Long value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value);
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof StaffLeaveEntitle) {
                StaffLeaveEntitle o = (StaffLeaveEntitle) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type "
                        + object.getClass().getName() + "; expected type: " + StaffLeaveEntitleController.class.getName());
            }
        }
    }

    @FacesConverter("staffCategoryCon")
    public static class StaffLeaveEntitleControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            StaffLeaveEntitleController controller = (StaffLeaveEntitleController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "staffCategoryController");
            return controller.getEjbFacade().find(getKey(value));
        }

        java.lang.Long getKey(String value) {
            java.lang.Long key;
            key = Long.valueOf(value);
            return key;
        }

        String getStringKey(java.lang.Long value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value);
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof StaffLeaveEntitle) {
                StaffLeaveEntitle o = (StaffLeaveEntitle) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type "
                        + object.getClass().getName() + "; expected type: " + StaffLeaveEntitleController.class.getName());
            }
        }
    }
}
