package com.unimate.servlet;

import com.unimate.dao.MarketDAO;
import com.unimate.dao.OpportunityDAO;
import com.unimate.dao.UserDAO;
import com.unimate.model.Opportunity;
import com.unimate.model.User;
import com.unimate.util.Constants;
import com.unimate.util.JsonUtil;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/admin")
public class AdminServlet extends BaseServlet {

    private final UserDAO userDAO = new UserDAO();
    private final MarketDAO marketDAO = new MarketDAO();
    private final OpportunityDAO opportunityDAO = new OpportunityDAO();

    private boolean isAdmin(User u) {
        return u != null && Constants.ROLE_ADMIN.equals(u.getRole());
    }

    /** Everything the admin dashboard needs, in one call. */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        if (!isAdmin(currentUser(req))) { sendError(res, 403, "Admins only"); return; }

        try {
            Map<String, Object> data = new HashMap<>();
            data.put("pendingUsers", userDAO.findPending());
            data.put("pendingItems", marketDAO.findByStatus(Constants.ITEM_PENDING));
            data.put("totalUsers", userDAO.findAll().size());
            sendJson(res, data);
        } catch (Exception e) {
            sendError(res, 500, e.getMessage());
        }
    }

    /** action=approveUser | rejectUser | approveItem | removeItem */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        if (!isAdmin(currentUser(req))) { sendError(res, 403, "Admins only"); return; }

        String action = req.getParameter("action");
        int id = intParam(req, "id");

        try {
            switch (action) {
                case "approveUser":
                    userDAO.updateStatus(id, Constants.STATUS_ACTIVE); break;
                case "approveItem":
                    marketDAO.updateStatus(id, Constants.ITEM_LIVE); break;
                case "removeItem":
                    marketDAO.updateStatus(id, "REMOVED"); break;
                case "addOpportunity":
                    Opportunity o = new Opportunity();
                    o.setTitle(req.getParameter("title"));
                    o.setType(req.getParameter("type"));
                    o.setCompany(req.getParameter("company"));
                    o.setDescription(req.getParameter("description"));
                    o.setStipend(req.getParameter("stipend"));
                    o.setDeadline(req.getParameter("deadline"));
                    o.setApplyLink(req.getParameter("applyLink"));
                    opportunityDAO.insert(o); break;
                default:
                    sendError(res, 400, "Unknown action"); return;
            }
            sendJson(res, JsonUtil.message("Done"));
        } catch (Exception e) {
            sendError(res, 500, e.getMessage());
        }
    }
}
