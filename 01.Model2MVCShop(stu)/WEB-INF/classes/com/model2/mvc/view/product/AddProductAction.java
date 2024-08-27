package com.model2.mvc.view.product;

import java.text.DecimalFormat;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.model2.mvc.framework.Action;
import com.model2.mvc.service.product.ProductService;
import com.model2.mvc.service.product.impl.ProductServiceImpl;
import com.model2.mvc.service.product.vo.ProductVO;

public class AddProductAction extends Action {

	public String execute(	HttpServletRequest request, HttpServletResponse response) throws Exception{
		
		ProductVO productVO = new ProductVO();
		
		//String productPrice = String.format("%d", "price");
		
		productVO.setProdName(request.getParameter("prodName"));
		productVO.setProdDetail(request.getParameter("prodDetail"));
		productVO.setManuDate(request.getParameter("manuDate"));
		productVO.setPrice(request.getParameter("price"));
		productVO.setProdName(request.getParameter("fileName"));
		
		System.out.println(productVO);
		
		ProductService service = new ProductServiceImpl();
		service.addProduct(productVO);

	
		return "forward:/product/addProductView.jsp";
	}
}
