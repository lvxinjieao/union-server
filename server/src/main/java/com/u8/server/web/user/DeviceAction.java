package com.u8.server.web.user;

import com.opensymphony.xwork2.ModelDriven;
import com.u8.server.common.UActionSupport;
import com.u8.server.common.UAnalyticsRedisTemplate;
import com.u8.server.constants.GlobalConstants;
import com.u8.server.data.UDevice;
import com.u8.server.data.UGame;
import com.u8.server.log.Log;
import com.u8.server.service.UDeviceManager;
import com.u8.server.service.UGameManager;
import com.u8.server.utils.EncryptUtils;
import net.sf.json.JSONObject;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.Date;

/**
 * Created by ant on 2016/8/12.
 */
@Controller
@Namespace("/user")
public class DeviceAction extends UActionSupport implements ModelDriven<UDevice> {

    private UDevice device;

    private String sign;

    @Autowired
    private UDeviceManager deviceManager;

    @Autowired
    private UGameManager gameManager;

    @Action("addDevice")
    public void addDevice(){
        try{

            UGame game = gameManager.getGame(device.getAppID());
            if(game == null){
                renderState(false, "游戏不存在");
                return;
            }

            StringBuilder sb = new StringBuilder();
            sb.append("appID=").append(device.getAppID()+"")
                    .append("channelID=").append(device.getChannelID())
                    .append("deviceDpi=").append(device.getDeviceDpi())
                    .append("deviceID=").append(device.getDeviceID())
                    .append("deviceOS=").append(device.getDeviceOS())
                    .append("deviceType=").append(device.getDeviceType())
                    .append("mac=").append(device.getMac())
                    .append(game.getAppkey());

            Log.d("sign str:%s", sb.toString());

            String sign = EncryptUtils.md5(sb.toString()).toLowerCase();
            if(!sign.equals(this.sign)){
                renderState(false, "验证失败");
                return;
            }

            UDevice exist = deviceManager.getByDeviceID(device.getDeviceID(), device.getAppID());
            if(exist == null){
                this.device.setCreateTime(new Date());
                this.device.setIp(request.getRemoteAddr());
                this.device.setArea("");
                if(this.device.getSubChannelID() == null){
                    this.device.setSubChannelID(0);
                }
                deviceManager.saveDevice(this.device);
                deviceManager.addDeviceCount(device);
            }

            renderState(true, "上传成功");

        }catch(Exception e){
            Log.e(e.getMessage(), e);
            renderState(false, "上传失败");
        }
    }

    private void renderState(boolean suc, String msg){
        JSONObject json = new JSONObject();
        json.put("state", suc? 1 : 0);
        json.put("msg", msg);
        renderText(json.toString());
    }


    @Override
    public UDevice getModel() {
        if(device == null){
            device = new UDevice();
        }
        return device;
    }

    public UDevice getDevice() {
        return device;
    }

    public void setDevice(UDevice device) {
        this.device = device;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }
}
