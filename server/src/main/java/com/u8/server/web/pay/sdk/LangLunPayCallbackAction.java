package com.u8.server.web.pay.sdk;

import com.u8.server.common.UActionSupport;
import com.u8.server.constants.PayState;
import com.u8.server.data.UChannel;
import com.u8.server.data.UOrder;
import com.u8.server.log.Log;
import com.u8.server.service.UOrderManager;
import com.u8.server.utils.EncryptUtils;
import com.u8.server.web.pay.SendAgent;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

@Controller
@Namespace("/pay/langlun")
public class LangLunPayCallbackAction extends UActionSupport {

	private static final long serialVersionUID = 1L;
	private String out_trade_no;
	private String price;
	private String pay_status;
	private String extend;
	private String signType;
	private String sign;

	public String getOut_trade_no() {
		return out_trade_no;
	}

	public void setOut_trade_no(String out_trade_no) {
		this.out_trade_no = out_trade_no;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getPay_status() {
		return pay_status;
	}

	public void setPay_status(String pay_status) {
		this.pay_status = pay_status;
	}

	public String getExtend() {
		return extend;
	}

	public void setExtend(String extend) {
		this.extend = extend;
	}

	public String getSignType() {
		return signType;
	}

	public void setSignType(String signType) {
		this.signType = signType;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	@Autowired
	private UOrderManager orderManager;

	@Action("payCallback")
	public void payCallback() {
		
		Log.i("【########## out_trade_no : " + out_trade_no);
		Log.i("【##########  price : " + price);
		Log.i("【########## pay_status : " + pay_status);
		Log.i("【########## extend : " + extend);
		Log.i("【########## signType : " + signType);
		Log.i("【########## sign : " + sign);

		Long orderID = Long.parseLong(extend);
		UOrder order = orderManager.getOrder(orderID);

		if (order == null || order.getChannel() == null) {
			Log.d("【The order is null or the channel is null】");
			this.renderState(false);
			return;
		}

		if (order.getState() > 1) {
			Log.d("The state of the order is complete. The state is " + order.getState());
			this.renderState(false);
			return;
		}

		UChannel channel = order.getChannel();
		if (channel == null) {
			this.renderState(false);
			return;
		}

		if (verifyPay(channel, out_trade_no, price, pay_status, extend, sign)) {
			order.setRealMoney((int) priceToFloat(price));
			order.setCompleteTime(new Date());
			order.setChannelOrderID(out_trade_no);
			order.setState(PayState.STATE_SUC);
			orderManager.saveOrder(order);
			SendAgent.sendCallbackToServer(this.orderManager, order);
			this.renderState(true);
		} else {
			order.setChannelOrderID(out_trade_no);
			order.setState(PayState.STATE_FAILED);
			orderManager.saveOrder(order);
		}

	}

	public float priceToFloat(String price) {
		float p = 0;
		try {
			p = Float.parseFloat(price);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		return p;
	}

	public boolean verifyPay(UChannel channel, String out_trade_no, String price, String pay_status, String extend,String sign) {
		StringBuilder builder = new StringBuilder();
		builder.append(out_trade_no);
		builder.append(price);
		builder.append(pay_status);
		builder.append(extend);
		builder.append(channel.getCpAppSecret());// 游戏key

		String trim = builder.toString().trim();
		Log.i("【########## md5  前 : " + trim);
		String md5 = EncryptUtils.md5(trim);
		Log.i("【########## md5  后 : " + md5);
		Log.i("【########## sign 值 : " + sign);
		return sign.equals(md5);
	}

	private void renderState(boolean suc) {
		String res = "success";
		PrintWriter out = null;
		if (suc) {
			try {
				out = this.response.getWriter();
				out.write(res);
				out.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
