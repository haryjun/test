package com.model2.mvc.service.product.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.model2.mvc.common.SearchVO;
import com.model2.mvc.common.util.DBUtil;
import com.model2.mvc.service.product.vo.ProductVO;

public class ProductDAO {
	
	
	//Ʋ�� �� ������� �ּ�ó���ϱ�!
	
	public ProductDAO(){
	}

	public ProductVO findProduct(int prodNo) throws Exception {
		//������ȸ
		Connection con = DBUtil.getConnection();
		
		String sql = "select*from PRODUCT where PROD_NO = ?";
		
		PreparedStatement stmt = con.prepareStatement(sql);	
		stmt.setInt(1, prodNo);

		ResultSet rs = stmt.executeQuery();

		ProductVO productVO = null;
		while(rs.next()) {
			productVO = new ProductVO();
			productVO.setProdNo(rs.getInt("prod_no"));
			productVO.setProdName(rs.getString("prod_name"));
			productVO.setFileName(rs.getString("image_file"));
			productVO.setProdDetail(rs.getString("prod_detail"));
			productVO.setManuDate(rs.getString("manufacture_day"));
			productVO.setPrice(rs.getInt("price"));
			productVO.setRegDate(rs.getDate("reg_date"));
			
		}
		
		con.close();
		return productVO;
		
	}
	
	
	public HashMap<String, Object> getProductList(SearchVO searchVO) throws Exception {
		
	    Connection con = DBUtil.getConnection();
	    StringBuilder sql2 = new StringBuilder();
	    sql2.append("SELECT ptj.rn, prod.prod_no, prod.prod_name, prod.price, prod.reg_date, NVL(trans.tran_status_code,0) tran_code, ptj.count ");
	    sql2.append("FROM product prod, transaction trans, (SELECT prod_no, ROWNUM rn, COUNT(*)OVER() count FROM product) ptj ");
	    sql2.append("WHERE ptj.prod_no = prod.prod_no AND prod.prod_no=trans.prod_no(+) AND ptj.rn BETWEEN ? AND ? ");
	    
	    //serchVO
	    if (searchVO.getSearchCondition() != null) {
	        switch(searchVO.getSearchCondition()) {
	        
	            case "0" :
	                sql2.append(" AND prod_no='");
	                sql2.append(searchVO.getSearchKeyword());
	                sql2.append("'");
	                
	                break;
	                
	            case "1" :
	                sql2.append(" AND pd.prod_name LIKE '%");
	                sql2.append(searchVO.getSearchKeyword());
	                sql2.append("%'");
	                
	                break;
	                
	            case "2" :
	                sql2.append(" AND pd.price=");
	                sql2.append(searchVO.getSearchKeyword());
	                
	                break;
	        }
	    }
	    sql2.append(" ORDER BY 1 DESC");
	    

	    PreparedStatement stmt = con.prepareStatement(	sql2.toString(),
	                                                    ResultSet.TYPE_SCROLL_INSENSITIVE,
	                                                    ResultSet.CONCUR_UPDATABLE);
	    
	    int searchRowStart = searchVO.getPage() * searchVO.getPageUnit() - searchVO.getPageUnit()+1;
	    System.out.println("searchVO.getPageUnit() : "+searchVO.getPageUnit());
	    
	    int searchRowEnd = searchRowStart + searchVO.getPageUnit()-1;
	    System.out.println("searchRowStart : " + searchRowStart);
	    System.out.println("searchRowEnd : " + searchRowEnd);
	    
	    stmt.setInt(1, searchRowStart);
	    stmt.setInt(2, searchRowEnd);
	    
	    
	    ResultSet rs = stmt.executeQuery();
	    
	    if(rs.next()) {
	        System.out.println("���������");
	    } else {
	        System.out.println("��");
	    }

	    int total = rs.getInt("count");
	    System.out.println("�� ���� :" + total);

	    HashMap<String,Object> map = new HashMap<String,Object>();
	    map.put("count", new Integer(total));
	    
	    List<ProductVO> list = new ArrayList<ProductVO>();
	    if (total > 0) {
	        for (int i = 0; i < searchVO.getPageUnit(); i++) {
	            ProductVO vo = new ProductVO();
	            vo.setProdNo(rs.getInt("prod_no"));
	            vo.setProdName(rs.getString("prod_name"));
	            vo.setPrice(rs.getInt("price"));
	            vo.setRegDate(rs.getDate("reg_date"));
	            vo.setProTranCode(rs.getString("tran_code").trim());
	            
	            list.add(vo);
	            if(!rs.next()) {
	                break;
	            }
	        }
	    }
	    
	    System.out.println("list.size() : "+ list.size());
	    map.put("list", list);
	    System.out.println("map().size() : "+ map.size());

	    con.close();
	        
	    return map;
	    
	}

	

	public void insertProduct(ProductVO productVO)throws Exception{
		//��ǰ���
			
		Connection con = DBUtil.getConnection();
		String sql = "INSERT INTO product (prodName, prodDetail, manuDate, price, fileName) VALUES (?, ?, ?, ?, ?)";
		
		//String sql = "INSERT INTO product (prodNo, prodName, price, manuDate, prodDetail, fileName, proTranCode, regDate) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
		//String sql = "INSERT INTO product VALUES (seq_product_prod_no.nextval,?,?,?,?,?,SYSDATE)";

		//prod_num�� �⺻���� �����ְ� ����(insert���� �ƴ϶�!)
		//sql += ", prod_name, prod_detail, manufacture_day, price, image_file, reg_date";
		
		
		PreparedStatement stmt = con.prepareStatement(sql);

    	stmt.setString(1, productVO.getProdName());
		stmt.setString(2, productVO.getProdDetail());
		stmt.setString(3, productVO.getManuDate());
		stmt.setInt(4, productVO.getPrice());
		stmt.setString(5, productVO.getFileName()); 
		
//		pstmt.setInt(1, productVO.getProdNo());
//        pstmt.setString(2, productVO.getProdName());
//        pstmt.setInt(3, productVO.getPrice());
//        pstmt.setString(4, productVO.getManuDate());
//        pstmt.setString(5, productVO.getProdDetail());
//        pstmt.setString(6, productVO.getFileName());
//        pstmt.setString(7, productVO.getProTranCode());
//        pstmt.setDate(8, productVO.getRegDate());
		
		stmt.executeUpdate();
		
		con.close();
		}
		
	
	public void updateProduct(ProductVO productVO)throws Exception{
		//��ǰ���
		Connection con = DBUtil.getConnection();

		String sql = "UPDATE product SET prod_name=?,prod_detail=?,price=?,manufacture_day=?, image_file =? where prod_no=?";
//		String sql = "update PRODUCT set  PROD_NO=?,PROD_NAME=?,PRICE=?,RED_DATE=?"; 
		//"UPDATE product SET prodName = ?, price = ?, manuDate = ?, prodDetail = ?, fileName = ?, proTranCode = ? WHERE prodNo = ?";
		//������°��������� ���� ����. ���߿� �߰��ϱ�
		
		PreparedStatement stmt = con.prepareStatement(sql);
		
		stmt.setString(1, productVO.getProdName());
		stmt.setString(2, productVO.getProdDetail());
		stmt.setString(3, productVO.getManuDate());
		stmt.setInt(4, productVO.getPrice());
		stmt.setString(5, productVO.getFileName());
//		stmt.setInt(6, productVO.getProdNo());
		stmt.executeUpdate();
		
		//System.out.println("DB�� ������Ʈ : "+productVO);
		con.close();
		
		//String sql = "insert into PRODUCTS"
		
		//prod_num�� �⺻���� �����ְ� ����(insert���� �ƴ϶�!)
		//sql += ", prod_name, prod_detail, manufacture_day, price, image_file, reg_date";
		
		
	//	
//		PreparedStatement stmt = con.prepareStatement(sql);
	//	
//		stmt.setString(1, productVO.prodName());
//		stmt.setString(2, productVO.prodDetail());
//		stmt.setString(3, productVO.manuDate());
//		stmt.setString(4, productVO.price());
//		stmt.setString(5, productVO.proTranCode()); //�̰� �����ؾ���.
	//	
//		con.close();
		}
		
	

	
	
	
	
	
	
	
	
	
}