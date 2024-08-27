package com.model2.mvc.view.product;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.model2.mvc.common.SearchVO;
import com.model2.mvc.framework.Action;
import com.model2.mvc.service.product.impl.ProductServiceImpl;

public class ListProductAction extends Action {

	@Override
	public String execute(	HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		//SearchVO searchVO=new SearchVO();


//		int page=1;
//		if(request.getParameter("page") != null)
//			page=Integer.parseInt(request.getParameter("page"));
//		
//		searchVO.setPage(page);
//		searchVO.setSearchCondition(request.getParameter("searchCondition"));
//		searchVO.setSearchKeyword(request.getParameter("searchKeyword"));
//		
//		String pageUnit=getServletContext().getInitParameter("pageSize");
//		searchVO.setPageUnit(Integer.parseInt(pageUnit));
//		
//		UserService service=new UserServiceImpl();
//		HashMap<String,Object> map=service.getUserList(searchVO);
//
//		request.setAttribute("map", map);
//		request.setAttribute("searchVO", searchVO);
//		
//		
//		return "forward:/product/listProduct.jsp";
		
		String authorization = "";
		
		SearchVO searchVO = new SearchVO();
		HttpSession session = request.getSession();
		
		int page = 1;
		if(request.getParameter("page") != null) {
			page = Integer.parseInt(request.getParameter("page"));
			if(request.getParameter("menu")==null){
				session.getAttribute("menuType");
			}
		} else {
			if(request.getParameter("menu")!=null){
				if(request.getParameter("menu").equals("manage")){
					authorization = "manage";
					System.out.println("��ǰ���� (�������ɸ��)");
					
				} else {
					authorization = "search";
					System.out.println("��ǰ�˻� (���� �Ұ���)");
				}
				session.setAttribute("menuType", authorization);
			} else {
				System.out.println("��ǰ�޴� parameter ���̴�");
			}
			
		}
		
		
		searchVO.setPage(page);
		searchVO.setSearchCondition(request.getParameter("searchCondition"));
		searchVO.setSearchKeyword(request.getParameter("searchKeyword"));
		searchVO.setPageUnit(Integer.parseInt(getServletContext().getInitParameter("pageSize")));
		
		ProductServiceImpl service = new ProductServiceImpl();
		Map<String, Object> map = service.getProductList(searchVO);
		
		
		request.setAttribute("map", map);
		request.setAttribute("searchVO", searchVO);
		
		
		return "forward:/product/listProduct.jsp";
	
	}

}
