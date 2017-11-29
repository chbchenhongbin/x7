package x7.repository;


import java.io.Serializable;
import java.util.Date;

import x7.core.repository.Persistence;


/**
 * 
 * 团购成员
 * @author Sim
 *
 */
public class GroupBuyingMember implements Serializable{

	private static final long serialVersionUID = 1776369120695946858L;

	@Persistence(key = Persistence.KEY_ONE, isNotAutoIncrement = true)
	private long groupBuyingId; //
	@Persistence(key = Persistence.KEY_TWO)
	private long buyerId;
	private long sharerId;

	private String orderStatus; //OrderStatus.java
	private String paymentStatus; //PaymentStatus.java
	private String backStatus;//退款(中途退款) BackStatus.java
	private String payWay;// PayWay.java
	private String payTrxId;// 支付交易号
	private long orderId;//订单编号
	private String user3Type;// User3Type.java
	private Boolean isDeleted;//已删除
	private Date orderTime;
	private Date receiveGoodsTime;
	private Date createTime;
	private Date refreshTime;

	public long getGroupBuyingId() {
		return groupBuyingId;
	}
	public void setGroupBuyingId(long groupBuyingId) {
		this.groupBuyingId = groupBuyingId;
	}
	public long getBuyerId() {
		return buyerId;
	}
	public void setBuyerId(long buyerId) {
		this.buyerId = buyerId;
	}
	public long getSharerId() {
		return sharerId;
	}
	public void setSharerId(long sharerId) {
		this.sharerId = sharerId;
	}

	public String getOrderStatus() {
		return orderStatus;
	}
	public void setOrderStatus(String orderStatus) {
		this.orderStatus = orderStatus;
	}
	public String getPaymentStatus() {
		return paymentStatus;
	}
	public void setPaymentStatus(String paymentStatus) {
		this.paymentStatus = paymentStatus;
	}
	public String getBackStatus() {
		return backStatus;
	}
	public void setBackStatus(String backStatus) {
		this.backStatus = backStatus;
	}
	public long getOrderId() {
		return orderId;
	}
	public void setOrderId(long orderId) {
		this.orderId = orderId;
	}
	public String getUser3Type() {
		return user3Type;
	}
	public void setUser3Type(String user3Type) {
		this.user3Type = user3Type;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public Date getRefreshTime() {
		return refreshTime;
	}
	public void setRefreshTime(Date refreshTime) {
		this.refreshTime = refreshTime;
	}
	public String getPayWay() {
		return payWay;
	}
	public void setPayWay(String payWay) {
		this.payWay = payWay;
	}
	public String getPayTrxId() {
		return payTrxId;
	}
	public void setPayTrxId(String payTrxId) {
		this.payTrxId = payTrxId;
	}
	public Date getOrderTime() {
		return orderTime;
	}
	public void setOrderTime(Date orderTime) {
		this.orderTime = orderTime;
	}
	public Date getReceiveGoodsTime() {
		return receiveGoodsTime;
	}
	public void setReceiveGoodsTime(Date receiveGoodsTime) {
		this.receiveGoodsTime = receiveGoodsTime;
	}
	public Boolean isDeleted() {
		return isDeleted;
	}
	public void setDeleted(Boolean isDeleted) {
		this.isDeleted = isDeleted;
	}
	@Override
	public String toString() {
		return "GroupBuyingMember [groupBuyingId=" + groupBuyingId + ", buyerId=" + buyerId + ", sharerId=" + sharerId
				+ ", orderStatus=" + orderStatus + ", paymentStatus=" + paymentStatus + ", backStatus=" + backStatus
				+ ", payWay=" + payWay + ", payTrxId=" + payTrxId + ", orderId=" + orderId + ", user3Type=" + user3Type
				+ ", isDeleted=" + isDeleted + ", orderTime=" + orderTime
				+ ", receiveGoodsTime=" + receiveGoodsTime + ", createTime=" + createTime + ", refreshTime="
				+ refreshTime + "]";
	}
}
