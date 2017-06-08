/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.apertum.qsky.servlets;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.net.BCodec;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import ru.apertum.qsky.common.HibernateUtil;
import ru.apertum.qsky.common.Uses;
import ru.apertum.qsky.controller.PagerAlreadyDone;
import ru.apertum.qsky.ejb.IHibernateEJBLocal;
import ru.apertum.qsky.model.pager.PagerData;
import ru.apertum.qsky.model.pager.PagerQuizItems;
import ru.apertum.qsky.model.pager.PagerResults;

/**
 *
 * @author Evgeniy Egorov
 */
public class SetPagerData extends HttpServlet {


    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        final String plg = request.getParameter("qplugins");
        String m = "no", t = "no";
        if (plg != null) {
            String[] pls = plg.split("-");
            if (pls.length == 2) {
                m = pls[0];
                t = pls[1];
            } else {
                m = "err";
                t = plg.length() < 45 ? plg : (plg.substring(0, 40) + "...");
            }
        }

        final PagerResults pagerResults = new PagerResults(request.getRemoteAddr(), Uses.getNow(), request.getParameter("qsysver"), m, t, 0, 0 ,0, "");
        PagerAlreadyDone.getInstance().add(request.getRemoteAddr(), Long.parseLong(request.getParameter("dataid")));
        try {
            if (request.getParameter("inputdata") != null) {
                pagerResults.setInputData(URLDecoder.decode(new BCodec().decode(request.getParameter("inputdata")), "utf-8"));
            }
        } catch (DecoderException | UnsupportedEncodingException ex) {
        }

        final Session ses = HibernateUtil.getSessionFactory().openSession();
        try {
            ses.beginTransaction();
            final Query query = ses.getNamedQuery("PagerData.findById");
            query.setLong("id", Long.parseLong(request.getParameter("dataid")));
            List data = query.list();
            if (data.size() == 1) {
                PagerData pd = (PagerData) data.get(0);
                pagerResults.setPagerDataId((PagerData) data.get(0));
                if (request.getParameter("quizid") != null) {
                    long qid = Long.parseLong(request.getParameter("quizid"));
                    for (PagerQuizItems qitem : pd.getPagerQuizItemsList()) {
                        if (qitem.getId() == qid) {
                            pagerResults.setQuizId(qitem);
                            break;
                        }
                    }
                }
            }

            ses.save(pagerResults);
            ses.getTransaction().commit();
        } catch (HibernateException ex) {
            ses.getTransaction().rollback();
        } finally {
            ses.close();
        }

        response.setStatus(HttpServletResponse.SC_OK);
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
}
