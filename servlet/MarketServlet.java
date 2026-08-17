package com.unimate.servlet;

import com.unimate.dao.MarketDAO;
import com.unimate.model.MarketItem;
import com.unimate.model.User;
import com.unimate.util.JsonUtil;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/marketplace")
public class MarketServlet extends BaseServlet {

    private final MarketDAO marketDAO = new MarketDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        User user = currentUser(req);
        if (user == null) { sendError(res, 401, "Please log in"); return; }

        try {
            if ("detail".equals(req.getParameter("action"))) {
                sendJson(res, marketDAO.findById(intParam(req, "id")));
            } else {
                sendJson(res, marketDAO.findLive());    // students never see PENDING items
            }
        } catch (Exception e) {
            sendError(res, 500, e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        User user = currentUser(req);
        if (user == null) { sendError(res, 401, "Please log in"); return; }

        try {
            MarketItem i = new MarketItem();
            i.setTitle(req.getParameter("title"));
            i.setDescription(req.getParameter("description"));
            i.setCategory(req.getParameter("category"));
            i.setPrice(Double.parseDouble(req.getParameter("price")));
            i.setItemCondition(req.getParameter("condition"));
            i.setPickupPoint(req.getParameter("pickupPoint"));
            i.setSellerId(user.getUserId());

            sendJson(res, JsonUtil.message(
                marketDAO.insert(i) ? "Sent for admin review" : "Could not post the item"));
        } catch (Exception e) {
            sendError(res, 500, e.getMessage());
        }
    }
}
