package com.model2.mvc.service.purchase.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.model2.mvc.common.SearchVO;
import com.model2.mvc.common.util.DBUtil;
import com.model2.mvc.service.product.dao.ProductDAO;
import com.model2.mvc.service.product.vo.ProductVO;
import com.model2.mvc.service.purchase.vo.PurchaseVO;
import com.model2.mvc.service.user.dao.UserDAO;

public class PurchaseDAO {

	public PurchaseDAO() {
	}

	/// ��ǰ���
	public void insertPurchase(PurchaseVO purchaseVO) throws Exception {

		Connection con = DBUtil.getConnection();
		
		
		/*
		 * ~.NEXTVAL : �ش� �������� ���� ����
		 * ~.CURRVAL : ���� �������� �˰� ����
		 * 
		 * ��� ����
		 * 		1) ���������� �ƴ� SELECT��
		 * 		2) INSERT ���� SELECT��
		 * 		3) INSERT���� VALUE��
		 * 		4) UPDATE���� SET��
		 * 
		 * ��� �Ұ�
		 * 		1) VIEW�� SELECT��
		 * 		2) DISTINCTŰ���尡 �ִ� SELECT��
		 * 		3) GROUP BY, HAVING, ORDER BY���� �ִ� SELECT��
		 * 		4) SELECT, DELETE, UPDATE�� ��������
		 */
		
		/*������ ����ؼ� ������Ű��
		 * SEQUENCE seq_transaction_tran_no;
		 * ������ ���� ���� (insert) => NEXTVAL
		 * SYSDATE ����ؼ� �ð�����(now�� �ִµ� �̰� MYSQL���� �ִµ�..)
		 */
		String sql = "INSERT INTO transaction VALUES "
				+ "(seq_transaction_tran_no.NEXTVAL,?,?,?,?,?,?,?,1,SYSDATE,?)";

		PreparedStatement stmt = con.prepareStatement(sql);
		stmt.setInt(1, purchaseVO.getPurchaseProd().getProdNo());
		stmt.setString(2, purchaseVO.getBuyer().getUserId());
		stmt.setString(3, purchaseVO.getPaymentOption());
		stmt.setString(4, purchaseVO.getReceiverName());
		stmt.setString(5, purchaseVO.getReceiverPhone());
		stmt.setString(6, purchaseVO.getDivyAddr());
		stmt.setString(7, purchaseVO.getDivyRequest());
		stmt.setString(8, purchaseVO.getDivyDate());

		stmt.executeUpdate();

		//System.out.println("�ŷ� ��� �Ϸ�");
		con.close();
	}

	public PurchaseVO findPurchase(int tranNo) throws Exception {

		Connection con = DBUtil.getConnection();

		String sql = "SELECT * FROM transaction WHERE tran_no = ?";

		PreparedStatement stmt = con.prepareStatement(sql);
		stmt.setInt(1, tranNo);

		ResultSet rs = stmt.executeQuery();

		rs.next();
		PurchaseVO purchaseVO = new PurchaseVO();
		purchaseVO.setTranNo(rs.getInt("tran_no"));
		purchaseVO.setPurchaseProd(new ProductDAO().findProduct(rs.getInt("prod_no")));
		purchaseVO.setBuyer(new UserDAO().findUser(rs.getString("buyer_id")));
		purchaseVO.setPaymentOption(rs.getString("payment_option").trim());
		purchaseVO.setReceiverName(rs.getString("receiver_name"));
		purchaseVO.setReceiverPhone(rs.getString("receiver_phone"));
		purchaseVO.setDivyAddr(rs.getString("demailaddr"));
		purchaseVO.setDivyRequest(rs.getString("dlvy_request"));
		purchaseVO.setTranCode(rs.getString("tran_status_code").trim());
		purchaseVO.setOrderDate(rs.getDate("order_data"));
		purchaseVO.setDivyDate(rs.getString("dlvy_date"));

		//System.out.println("db���� ������ ������ : " + purchaseVO);

		return purchaseVO;
	}

	public PurchaseVO findPurchase2(int prodNo) throws Exception {

		Connection con = DBUtil.getConnection();

		String sql = "SELECT tran_no, tran_status_code FROM transaction WHERE prod_no=?";

		PreparedStatement stmt = con.prepareStatement(sql);
		stmt.setInt(1, prodNo);

		System.out.println("������ ��ǰ��ȣ " + prodNo);
		ResultSet rs = stmt.executeQuery();

		rs.next();
		
		PurchaseVO purchaseVO = new PurchaseVO();
		purchaseVO.setTranNo(rs.getInt("tran_No"));
		purchaseVO.setTranCode(rs.getString("tran_status_code"));
		
		System.out.println("���޹��� "+prodNo+"������ tranCode �˻��Ϸ�");
		
		con.close();

		return purchaseVO;
	}

	public HashMap<String, Object> getSaleList(SearchVO searchVO) throws Exception {

		Connection con = DBUtil.getConnection();

		PreparedStatement stmt = con.prepareStatement("SELECT * FROM transaction", 
										ResultSet.TYPE_SCROLL_INSENSITIVE,
										ResultSet.CONCUR_UPDATABLE);

		ResultSet rs = stmt.executeQuery();

		rs.last();
		int total = rs.getRow();

		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("count", new Integer(total));

		//rs.absolute
		rs.absolute(searchVO.getPage() * searchVO.getPageUnit() - searchVO.getPageUnit() + 1);
		List<PurchaseVO> list = new ArrayList<PurchaseVO>();

		if (total > 0) {
			for (int i = 0; i < searchVO.getPageUnit(); i++) {
				PurchaseVO vo = new PurchaseVO();

				/*
				 * ���� �˾ƾ��ϴ� �� ������(buyer)�� ���̵��ε�, buyer���ϸ� UserVO�� ��� ������ �ش�ȴ�
				 * (UerVO)�� casting �ϸ� �ȵ��ư�
				 * UserDAO�� �ִ� id�� �������� // USER_ID
				 */
				vo.setTranNo(rs.getInt("tran_no"));
				vo.setBuyer(new UserDAO().findUser(rs.getString("user_id"))); //USER_ID, buyer_id??
				vo.setDivyAddr(rs.getString("demailaddr"));
				vo.setDivyDate(rs.getString("dlvy_date"));
				vo.setDivyRequest(rs.getString("dlvy_request"));
				vo.setOrderDate(rs.getDate("order_date"));
				vo.setPaymentOption(rs.getString("payment_option"));
				vo.setPurchaseProd(new ProductDAO().findProduct(rs.getInt("prod_no")));
				vo.setReceiverName(rs.getString("receiver_name"));
				vo.setReceiverPhone(rs.getString("receiver_phone"));
				vo.setTranCode(rs.getString("tran_status_code"));

				list.add(vo);

				if (!rs.next()) {
					break;
				}
					
			}
		}

		map.put("list", list);
		con.close();

		return map;
	}

	/*���Ÿ�� 
	 * 
	 * append
	 * COUNT ���Ŵϱ� OVER() ����غ��� => GROUP BY ��������ʾƵ� ��.
	 * 
	 * ����� �����͸� ���������� ���� 
	*/
	
	public HashMap<String, Object> getPurchaseList(SearchVO searchVO, String buyerId) throws Exception {

		Connection con = DBUtil.getConnection();
		StringBuilder temp = new StringBuilder();
		temp.append("SELECT purchase.* ");
		temp.append("FROM(SELECT ROW_NUMBER() OVER(order by user_id) rn , ");
		temp.append("trans.tran_no, u.user_id, NVL(trans.tran_status_code,0) tran_code, COUNT(*)OVER() count ");
		temp.append("FROM users u, transaction trans ");
		temp.append("WHERE u.user_id = trans.buyer_id AND u.user_id = ? )purchase ");
		temp.append("WHERE rn BETWEEN ? AND ? ");

		PreparedStatement stmt = con.prepareStatement(temp.toString(), 
										ResultSet.TYPE_SCROLL_INSENSITIVE,
										ResultSet.CONCUR_UPDATABLE);

		int searchRowStart = searchVO.getPage() * searchVO.getPageUnit() - searchVO.getPageUnit() + 1;
		System.out.println("searchVO.getPageUnit() : " + searchVO.getPageUnit());

		int searchRowEnd = searchRowStart + searchVO.getPageUnit() - 1;
		System.out.println("searchRowStart : " + searchRowStart);
		System.out.println("searchRowEnd : " + searchRowEnd);

		stmt.setString(1, buyerId);
		stmt.setInt(2, searchRowStart);
		stmt.setInt(3, searchRowEnd);

		ResultSet rs = stmt.executeQuery();

		HashMap<String, Object> map = new HashMap<String, Object>();
		if (rs.next()) {
			System.out.println("�����������");
		} else {
			System.out.println("����");
			map.put("count", new Integer(0));
			return map;
		}

		int total = rs.getInt("count");
		System.out.println("�� ���� :" + total);

		map.put("count", new Integer(total));
		int max = searchVO.getPageUnit();
		List<PurchaseVO> list = new ArrayList<PurchaseVO>();
		if (total > 0) {
			if (total < max) {
				max = total;
			} else {
				total = max;
			}
			rs.first();
			System.out.println("for�ݺ� Ƚ��:" + total);
			for (int i = 0; i < total; i++) {
				System.out.println((i + 1) + "��°");
				PurchaseVO vo = new PurchaseVO();
				vo.setTranNo(rs.getInt("tran_no"));
				vo.setBuyer(new UserDAO().findUser(rs.getString("user_id")));
				vo.setTranCode(rs.getString("tran_code").trim());
				System.out.println(vo);
				list.add(vo);
				System.out.println("����Ʈ �߰� " + (i + 1) + "�� �Դϴ�");
				if (!rs.next()) {
					break;
				}
			}
		}

		System.out.println("list.size() : " + list.size());
		map.put("list", list);
		System.out.println("map().size() : " + map.size());

		con.close();

		return map;
	}

	public void updatePurchase(PurchaseVO purchaseVO) throws Exception {
		Connection con = DBUtil.getConnection();

		StringBuffer temp = new StringBuffer();
		temp.append("UPDATE transaction SET payment_option=?, ");
		temp.append("receiver_name=?,receiver_phone=?,");
		temp.append("demailAddr=?,dlvy_request=?,");
		temp.append("dlvy_date=? WHERE tran_no=?");
		PreparedStatement stmt = con.prepareStatement(temp.toString());

		System.out.println("update Query = " + temp);
		stmt.setString(1, purchaseVO.getPaymentOption());
		stmt.setString(2, purchaseVO.getReceiverName());
		stmt.setString(3, purchaseVO.getReceiverPhone());
		stmt.setString(4, purchaseVO.getDivyAddr());
		stmt.setString(5, purchaseVO.getDivyRequest());
		stmt.setString(6, purchaseVO.getDivyDate());
		stmt.setInt(7, purchaseVO.getTranNo());

		stmt.executeUpdate();

		System.out.println("DB ���� �Ϸ�");

		con.close();
	}

	public void updateTranCode(PurchaseVO purchaseVO) throws Exception {
		Connection con = DBUtil.getConnection();

		String sql = "UPDATE transaction SET tran_status_code=? WHERE tran_no=?";

		PreparedStatement stmt = con.prepareStatement(sql);

		stmt.setString(1, purchaseVO.getTranCode());
		stmt.setInt(2, purchaseVO.getTranNo());

		System.out.println("��ȯ�� ��ۻ��� ��ȣ : " + purchaseVO.getTranCode());

		stmt.executeUpdate();

		con.close();

	}

}