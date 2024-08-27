package com.model2.mvc.service.product;

import java.util.HashMap;

import com.model2.mvc.common.SearchVO;
//import com.model2.mvc.common.SearchVO;
import com.model2.mvc.service.product.vo.ProductVO;


public interface ProductService{
	
	//��ǰ ���� ��ȸ
	//public void findProduct(ProductVO productVO) throws Exception;
	
	public void addProduct(ProductVO productVO) throws Exception;
	
	public ProductVO getProduct(int productVO) throws Exception;
	
	//��ǰ ��� ��ȸ
	//ArrayList�� HashMap
	//public ArrayList<Product>getProductList(UserVO userVO) throws Exception;
	public HashMap<String, Object>getProductList(SearchVO searchVO) throws Exception;
	
	
	//��ǰ���
	//public  void insertProduct(ProductVO productVO)throws Exception;
	
	//��ǰ ������
	public void updateProduct(ProductVO productVO)throws Exception;
	
}
