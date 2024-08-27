package com.model2.mvc.service.product;

import java.util.HashMap;

import com.model2.mvc.common.SearchVO;
//import com.model2.mvc.common.SearchVO;
import com.model2.mvc.service.product.vo.ProductVO;


public interface ProductService{
	
	//상품 정보 조회
	//public void findProduct(ProductVO productVO) throws Exception;
	
	public void addProduct(ProductVO productVO) throws Exception;
	
	public ProductVO getProduct(int productVO) throws Exception;
	
	//상품 목록 조회
	//ArrayList와 HashMap
	//public ArrayList<Product>getProductList(UserVO userVO) throws Exception;
	public HashMap<String, Object>getProductList(SearchVO searchVO) throws Exception;
	
	
	//상품등록
	//public  void insertProduct(ProductVO productVO)throws Exception;
	
	//상품 정보수
	public void updateProduct(ProductVO productVO)throws Exception;
	
}
