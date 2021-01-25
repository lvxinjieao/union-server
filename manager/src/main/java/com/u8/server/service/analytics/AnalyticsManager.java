package com.u8.server.service.analytics;

import com.u8.server.common.OrderParameter;
import com.u8.server.common.OrderParameters;
import com.u8.server.common.Page;
import com.u8.server.common.UAnalyticsRedisTemplate;
import com.u8.server.constants.GlobalConstants;
import com.u8.server.dao.hibernate.TChannelSummaryDao;
import com.u8.server.dao.hibernate.TRetentionDao;
import com.u8.server.dao.hibernate.TSummaryDao;
import com.u8.server.data.analytics.*;
import com.u8.server.utils.StringUtils;
import com.u8.server.utils.TimeUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * Created by ant on 2016/8/26.
 */
@Service("summaryManager")
public class AnalyticsManager {

    @Autowired
    private TSummaryDao summaryDao;

    @Autowired
    private TChannelSummaryDao channelSummaryDao;

    @Autowired
    private TRetentionDao retentionDao;

    @Autowired
    private UAnalyticsRedisTemplate analyticsRedisTemplate;

    public SummaryInfo getSummaryInfo(int appID, Date from, Date to){

        return summaryDao.querySummaryInfo(appID, from, to);

    }

    public Page<TSummary> getSummaryPage(int currPage, int num, Integer appID, Date beginCreateTime, Date endCreateTime){

        return summaryDao.querySummaryPage(currPage, num, appID, beginCreateTime, endCreateTime);
    }

    public List<TSummary> getSummaryList(Integer appID, Date beginTime, Date endTime){

        return summaryDao.querySummaryData(appID, beginTime, endTime);
    }

    public List<TRetention> getRetentionList(Integer appID, Date beginTime, Date endTime){

        return retentionDao.queryRetentionData(appID, beginTime, endTime);

    }

    public List<TChannelSummary> getChannelSummaryList(Integer appID, Date beginTime, Date endTime){

        return channelSummaryDao.queryChannelSummaryData(appID, beginTime, endTime);
    }


    public String collectTodayInfoForGame(int appID){

        try{

            JSONObject json = new JSONObject();
            json.put("deviceNum", analyticsRedisTemplate.getNum(GlobalConstants.KEY.NEW_DEVICE_NUM_KEY+appID));
            json.put("userNum", analyticsRedisTemplate.scount(GlobalConstants.KEY.KEY_USER_LOGIN+appID));
            json.put("newUserNum", analyticsRedisTemplate.getNum(GlobalConstants.KEY.NEW_USER_NUM_KEY+appID));
            json.put("moneyNum", analyticsRedisTemplate.getNum(GlobalConstants.KEY.PAY_MONEY_COUNT+appID));
            json.put("payedUserNum", analyticsRedisTemplate.scount(GlobalConstants.KEY.KEY_USER_PAYED+appID));
            return json.toString();

        }catch (Exception e){
            e.printStackTrace();
        }

        return "";
    }

    public String collectTodayInfoForChannel(int channelID){
        try{

            JSONObject json = new JSONObject();
            json.put("deviceNum", analyticsRedisTemplate.getNum(GlobalConstants.KEY.NEW_DEVICE_NUM_KEY+channelID));
            json.put("userNum", analyticsRedisTemplate.scount(GlobalConstants.KEY.KEY_USER_LOGIN+channelID));
            json.put("newUserNum", analyticsRedisTemplate.getNum(GlobalConstants.KEY.NEW_USER_NUM_KEY+channelID));
            json.put("moneyNum", analyticsRedisTemplate.getNum(GlobalConstants.KEY.PAY_MONEY_COUNT+channelID));
            json.put("payedUserNum", analyticsRedisTemplate.scount(GlobalConstants.KEY.KEY_USER_PAYED+channelID));
            return json.toString();

        }catch (Exception e){
            e.printStackTrace();
        }

        return "";
    }


        /**
         * 收集分析统计数量，生成展示需要的json格式
         * @return
         */
    public String collectSummaryInfo(int appID, Date from, Date to){

        SummaryInfo summary = summaryDao.querySummaryInfo(appID, from, to);

        JSONObject json = new JSONObject();
        json.put("deviceNum", summary.getDeviceNum());
        json.put("userNum", summary.getUserNum());
        json.put("payUserNum", summary.getPayUserNum());
        json.put("payMoney", summary.getPayMoney());

        List<TSummary> data = summary.getSummaryData();
        if(data != null && data.size() > 0){

            TSummary first = data.get(0);
            TSummary last = data.get(data.size()-1);
            String format = TimeUtils.FORMATER_5;
            if(TimeUtils.sameYear(first.getCurrTime(), last.getCurrTime())){
                format = TimeUtils.FORMATER_6;
            }

            JSONArray category = new JSONArray();
            for(TSummary s :data) {
                category.add(TimeUtils.format(s.getCurrTime(), format));
            }

            json.put("keyCategory", category);


            JSONArray deviceNums = new JSONArray();
            for(TSummary s : data){
                deviceNums.add(s.getDeviceNum());
            }
            json.put("deviceData", deviceNums);

            JSONArray userNums = new JSONArray();
            for(TSummary s :data){
                userNums.add(s.getUserNum());
            }
            json.put("newUserData", userNums);


            JSONArray dauArray = new JSONArray();
            for(TSummary s : data){
                dauArray.add(s.getDau());
            }
            json.put("allDaudata", dauArray);

            JSONArray ndauArray = new JSONArray();
            for(TSummary s : data){
                ndauArray.add(s.getNdau());
            }
            json.put("newDaudata", ndauArray);


            JSONArray payUserArray = new JSONArray();
            for(TSummary s : data){
                payUserArray.add(s.getPayUserNum());
            }
            json.put("allPayData", payUserArray);

            JSONArray newPayUserArray = new JSONArray();
            for(TSummary s : data) {
                newPayUserArray.add(s.getNewPayUserNum());
            }
            json.put("newPayData", newPayUserArray);


            JSONArray moneyArray = new JSONArray();
            for(TSummary s : data){
                moneyArray.add(s.getMoney() / 100);
            }
            json.put("moneyData", moneyArray);

        }

        return json.toString();
    }

    /**
     * 收集新增玩家统计信息
     * @param appID
     * @param from
     * @param to
     * @return
     */
    public String collectNewUserInfo(int appID, Date from, Date to){

        JSONObject json = new JSONObject();
        List<TSummary> data = summaryDao.querySummaryData(appID, from, to);

        if(data != null && data.size() > 0){

            TSummary first = data.get(0);
            TSummary last = data.get(data.size()-1);
            String format = TimeUtils.FORMATER_5;
            if(TimeUtils.sameYear(first.getCurrTime(), last.getCurrTime())){
                format = TimeUtils.FORMATER_6;
            }

            JSONArray category = new JSONArray();
            for(TSummary s :data) {
                category.add(TimeUtils.format(s.getCurrTime(), format));
            }

            json.put("keyCategory", category);

            JSONArray deviceNums = new JSONArray();
            int totalDevice = 0;
            for(TSummary s : data){
                deviceNums.add(s.getDeviceNum());
                totalDevice += s.getDeviceNum();
            }
            json.put("deviceData", deviceNums);
            json.put("avgDevice", totalDevice/data.size());

            JSONArray userNums = new JSONArray();
            int userTotal = 0;
            for(TSummary s :data){
                userNums.add(s.getUserNum());
                userTotal += s.getUserNum();
            }
            json.put("newUserData", userNums);
            json.put("avgUser", userTotal/data.size());

            float ratioTotal = 0;
            JSONArray array = new JSONArray();
            for(TSummary s : data){
                float ratio = (s.getDeviceNum() == null || s.getDeviceNum().equals(0)) ? 0 : ((float)s.getUniUserNum() / (float)s.getDeviceNum() * 100f);
                array.add(ratio);
                ratioTotal += ratio;
            }
            json.put("ratioData", array);
            json.put("avgRatio", ratioTotal/data.size());

        }

        OrderParameters orderBy = new OrderParameters();
        orderBy.add("userNum", OrderParameter.OrderType.DESC);

        List<ChannelGroupInfo> groups = channelSummaryDao.queryChannelGroups(appID, from, to, orderBy);
        if(groups != null && groups.size() > 0){
            long totalUser = 0;
            JSONArray category = new JSONArray();
            for(ChannelGroupInfo s :groups) {
                category.add(String.format("%s[%s]", s.getChannelName(), s.getChannelID()));
                totalUser += s.getUserNum();
            }

            json.put("channelCategory", category);

            JSONArray userData = new JSONArray();
            JSONArray userRatio = new JSONArray();
            for(ChannelGroupInfo s : groups){
                userData.add(s.getUserNum());
                userRatio.add((int)(totalUser == 0 ? 0 : (float)s.getUserNum()/(float)totalUser * 100f));
            }
            json.put("userData", userData);
            json.put("userRatio", userRatio);
        }

        return json.toString();

    }

    /**
     * 收集玩家活跃信息
     * @param appID
     * @param from
     * @param to
     * @return
     */
    public String collectDAUInfo(int appID, Date from, Date to){
        JSONObject json = new JSONObject();
        List<TSummary> data = summaryDao.querySummaryData(appID, from, to);
        if(data != null && data.size() > 0){

            TSummary first = data.get(0);
            TSummary last = data.get(data.size()-1);
            String format = TimeUtils.FORMATER_5;
            if(TimeUtils.sameYear(first.getCurrTime(), last.getCurrTime())){
                format = TimeUtils.FORMATER_6;
            }

            JSONArray category = new JSONArray();
            for(TSummary s :data) {
                category.add(TimeUtils.format(s.getCurrTime(), format));
            }

            json.put("keyCategory", category);


            JSONArray dauArray = new JSONArray();
            JSONArray ndauArray = new JSONArray();
            JSONArray wauArray = new JSONArray();
            JSONArray mauArray = new JSONArray();
            JSONArray dauMauArray = new JSONArray();
            JSONArray avgTimeArray = new JSONArray();

            float dauTotal = 0;
            float wauTotal = 0;
            float mauTotal = 0;
            float timeTotal = 0;

            for(TSummary s : data){
                dauArray.add(s.getDau());
                ndauArray.add(s.getNdau());
                wauArray.add(s.getWau());
                mauArray.add(s.getMau());
                dauMauArray.add((s.getMau() == null || s.getMau().equals(0)) ? 0 : (float)s.getDau()/(float)s.getMau());
                avgTimeArray.add(s.getAvg() == null ? 0 : Math.ceil(s.getAvg()/60f));

                dauTotal += s.getDau();
                wauTotal += s.getWau();
                mauTotal += s.getMau();
                timeTotal += (s.getAvg() == null ? 0 : Math.ceil(s.getAvg()/60f));

            }
            json.put("dauData", dauArray);
            json.put("ndauData", ndauArray);
            json.put("wauData", wauArray);
            json.put("mauData", mauArray);
            json.put("dauMauData", dauMauArray);
            json.put("avgData", avgTimeArray);

            int size = data.size();
            json.put("dauAvg", dauTotal/size);
            json.put("wauAvg", wauTotal/size);
            json.put("mauAvg", mauTotal/size);
            json.put("dauMauAvg",  mauTotal > 0 ? dauTotal/mauTotal : 0);
            json.put("timeAvg", timeTotal/size);

        }

        return json.toString();
    }


    /**
     * 收集玩家留存信息
     * @param appID
     * @param from
     * @param to
     * @return
     */
    public String collectRetentionInfo(int appID, Date from, Date to){

        JSONObject json = new JSONObject();
        List<TRetention> data = retentionDao.queryRetentionData(appID, from, to);
        if(data != null && data.size() > 0){

            TRetention first = data.get(0);
            TRetention last = data.get(data.size()-1);
            String format = TimeUtils.FORMATER_5;
            if(TimeUtils.sameYear(first.getStatTime(), last.getStatTime())){
                format = TimeUtils.FORMATER_6;
            }

            JSONArray category = new JSONArray();
            for(TRetention s : data) {
                category.add(TimeUtils.format(s.getStatTime(), format));
            }

            json.put("keyCategory", category);

            JSONArray dayArray = new JSONArray();
            JSONArray tdayArray = new JSONArray();
            JSONArray wdayArray = new JSONArray();
            JSONArray mdayArray = new JSONArray();

            float daySum = 0;
            int dayCount =0;
            float tdaySum = 0;
            int tdayCount = 0;
            float wdaySum = 0;
            int wdayCount = 0;
            float mdaySum = 0;
            int mdayCount = 0;

            for(TRetention s : data){

                if(s.getDayRetention() == null){
                    s.setDayRetention(",");
                }

                String rs = s.getDayRetention();
                if(rs.startsWith(",")){
                    rs = rs.substring(1);
                }
                rs = rs.trim();

                String[] userNums = rs.split(",");
                if(userNums.length >= 1){
                    String[] vals = userNums[0].split(":");
                    float v = (vals[0].length() <= 1 || Integer.valueOf(vals[1]) <= 0) ? 0 : Float.valueOf(vals[0])/Float.valueOf(vals[1]) * 100f;
                    daySum += v;
                    dayArray.add(v);
                    dayCount++;
                }else{
                    dayArray.add(0);
                }

                if(userNums.length >= 3){
                    String[] vals = userNums[2].split(":");

                    float v = (vals[0].length() <=1 || Integer.valueOf(vals[1]) <= 0) ? 0 : Float.valueOf(vals[0])/Float.valueOf(vals[1]) * 100f;
                    tdaySum += v;
                    tdayArray.add(v);
                    tdayCount++;
                }else{
                    tdayArray.add(0);
                }

                if(userNums.length >= 7){
                    String[] vals = userNums[6].split(":");

                    float v = (vals[0].length() <=1 || Integer.valueOf(vals[1]) <= 0) ? 0 : Float.valueOf(vals[0])/Float.valueOf(vals[1]) * 100f;
                    wdaySum += v;
                    wdayArray.add(v);
                    wdayCount++;
                }else{
                    wdayArray.add(0);
                }

                if(userNums.length >= 30){
                    String[] vals = userNums[29].split(":");

                    float v = (vals[0].length() <=1 || Integer.valueOf(vals[1]) <= 0) ? 0 : Float.valueOf(vals[0])/Float.valueOf(vals[1]) * 100f;
                    mdaySum += v;
                    mdayArray.add(v);
                    mdayCount++;
                }else{
                    mdayArray.add(0);
                }
            }

            json.put("dayArray", dayArray);
            json.put("tdayArray", tdayArray);
            json.put("wdayArray", wdayArray);
            json.put("mdayArray", mdayArray);

            json.put("dayAvg", daySum/(dayCount==0?1:dayCount));
            json.put("tdayAvg", tdaySum/(tdayCount==0?1:tdayCount));
            json.put("wdayAvg", wdaySum/(wdayCount==0?1:wdayCount));
            json.put("mdayAvg", mdaySum/(mdayCount==0?1:mdayCount));

        }

        return json.toString();
    }


    /**
     * 收集玩家流失信息
     * @param appID
     * @param from
     * @param to
     * @param timeType 1:7日算流失;2:14日算流失;3:30日算流失
     * @return
     */
    public String collectFlowInfo(int appID, Date from, Date to, int timeType){

        if(timeType <= 0 || timeType > 3){
            throw new RuntimeException("timeType must between 1 and 3");
        }

        JSONObject json = new JSONObject();
        List<TRetention> data = retentionDao.queryRetentionData(appID, from, to);
        if(data != null && data.size() > 0){

            TRetention first = data.get(0);
            TRetention last = data.get(data.size()-1);
            String format = TimeUtils.FORMATER_5;
            if(TimeUtils.sameYear(first.getStatTime(), last.getStatTime())){
                format = TimeUtils.FORMATER_6;
            }

            JSONArray category = new JSONArray();
            for(TRetention s : data) {
                category.add(TimeUtils.format(s.getStatTime(), format));
            }

            json.put("keyCategory", category);

            JSONArray wdayArray = new JSONArray();
            JSONArray wdayCountArray = new JSONArray();


            float wdaySum = 0;
            int wdayCountSum = 0;
            int wdayCount = 0;

            for(TRetention s : data){

                if(StringUtils.isEmpty(s.getDayFlowRatio())){
                    s.setDayFlowRatio(",");
                }

                String rs = s.getDayFlowRatio();
                if(rs.startsWith(",")){
                    rs = rs.substring(1);
                }


                String[] userNums = rs.split(",");

                if(userNums.length >= timeType){
                    String[] vals = userNums[timeType-1].split(":");
                    float v = (vals[0].length() <=1 || Integer.valueOf(vals[1]) <= 0) ? 0 : Float.valueOf(vals[0])/Float.valueOf(vals[1]) * 100f;
                    wdaySum += v;
                    wdayCountSum += (vals[0].length() == 0 ? 0 : Integer.valueOf(vals[0]));
                    wdayArray.add(v);
                    wdayCountArray.add((vals[0].length() == 0 ? 0 : Integer.valueOf(vals[0])));
                    wdayCount++;
                }else{
                    wdayArray.add(0);
                    wdayCountArray.add(0);
                }
            }

            json.put("wdayData", wdayArray);
            json.put("wdayCountData", wdayCountArray);
            json.put("wdayAvg", wdaySum/wdayCount);
            json.put("wdayCountAvg", wdayCountSum/wdayCount);
            json.put("wdayCountTotal", wdayCountSum);


            JSONArray backArray = new JSONArray();
            JSONArray backCountArray = new JSONArray();


            float backSum = 0;
            int backCount = 0;
            int backCountSum = 0;

            for(TRetention s : data){

                if(s.getDayBackRatio() == null){
                    s.setDayBackRatio(",");
                }

                String rs = s.getDayBackRatio();
                if(rs.startsWith(",")){
                    rs = rs.substring(1);
                }

                String[] userNums = rs.split(",");

                if(userNums.length >= timeType){
                    String[] vals = userNums[timeType-1].split(":");
                    float v = (vals[0].length() <= 1 || Integer.valueOf(vals[1]) <= 0) ? 0 : Float.valueOf(vals[0])/Float.valueOf(vals[1]) * 100f;
                    backSum += v;
                    backCountSum += (vals[0].length() == 0 ? 0 : Integer.valueOf(vals[0]));
                    backArray.add(v);
                    backCountArray.add((vals[0].length() == 0 ? 0 : Integer.valueOf(vals[0])));
                    backCount++;
                }else{
                    backArray.add(0);
                    backCountArray.add(0);
                }
            }

            json.put("backData", backArray);
            json.put("backCountData", backCountArray);
            json.put("backAvg", backSum/backCount);
            json.put("backCountAvg", backCountSum/backCount);
            json.put("backCountTotal", backCountSum);

        }


        return json.toString();
    }


    /**
     * 收集收入数据
     * @param appID
     * @param from
     * @param to
     * @return
     */
    public String collectMoneyInfo(int appID, Date from, Date to){
        JSONObject json = new JSONObject();
        List<TSummary> data = summaryDao.querySummaryData(appID, from, to);
        if(data != null && data.size() > 0){

            TSummary first = data.get(0);
            TSummary last = data.get(data.size()-1);
            String format = TimeUtils.FORMATER_5;
            if(TimeUtils.sameYear(first.getCurrTime(), last.getCurrTime())){
                format = TimeUtils.FORMATER_6;
            }

            JSONArray category = new JSONArray();
            for(TSummary s :data) {
                category.add(TimeUtils.format(s.getCurrTime(), format));
            }

            json.put("keyCategory", category);


            JSONArray moneyArray = new JSONArray();

            float total = 0;
            for(TSummary s : data){
                moneyArray.add(s.getMoney()/100f);
                total += s.getMoney()/100f;
            }
            json.put("moneyData", moneyArray);

            json.put("totalMoney", total);
            json.put("avgMoney", total/data.size());

        }

        List<ChannelGroupInfo> groups = channelSummaryDao.queryChannelGroups(appID, from, to, null);
        if(groups != null){

            Collections.sort(groups, new Comparator<ChannelGroupInfo>() {
                @Override
                public int compare(ChannelGroupInfo o1, ChannelGroupInfo o2) {
                    return (int)Math.ceil(o2.getPayMoney() - o1.getPayMoney());
                }
            });

            JSONArray category = new JSONArray();
            float total = 0;
            for(ChannelGroupInfo s :groups) {
                category.add(String.format("%s[%s]", s.getChannelName(), s.getChannelID()));
                total += s.getPayMoney();
            }

            json.put("channelCategory", category);

            JSONArray moneyArray = new JSONArray();
            JSONArray moneyRatioArray = new JSONArray();
            for(ChannelGroupInfo s : groups){
                moneyArray.add(s.getPayMoney());
                moneyRatioArray.add(total <= 0 ? 0f : (s.getPayMoney()/total*100f));
            }
            json.put("channelMoneyData", moneyArray);
            json.put("channelMoneyRatio", moneyRatioArray);
        }

        return json.toString();
    }

    /**
     * 收集付费统计数据
     * @param appID
     * @param from
     * @param to
     * @return
     */
    public String collectPayInfo(int appID, Date from, Date to){

        JSONObject json = new JSONObject();
        List<TSummary> data = summaryDao.querySummaryData(appID, from, to);
        if(data != null && data.size() > 0){

            TSummary first = data.get(0);
            TSummary last = data.get(data.size()-1);
            String format = TimeUtils.FORMATER_5;
            if(TimeUtils.sameYear(first.getCurrTime(), last.getCurrTime())){
                format = TimeUtils.FORMATER_6;
            }

            JSONArray category = new JSONArray();
            for(TSummary s :data) {
                category.add(TimeUtils.format(s.getCurrTime(), format));
            }

            json.put("keyCategory", category);


            JSONArray payArray = new JSONArray();
            JSONArray payCountArray = new JSONArray();
            JSONArray arpuArray = new JSONArray();
            JSONArray arppuArray = new JSONArray();
            float total = 0;
            float arpuTotal = 0;
            float arppuTotal = 0;
            float payCountTotal = 0;
            float payTotal = 0;
            for(TSummary s : data){
                payArray.add(s.getDau() <= 0 ? 0f : (float)s.getPayUserNum()/(float)s.getDau()*100f);
                payTotal += (s.getDau() <= 0 ? 0f : (float)s.getPayUserNum()/(float)s.getDau()*100f);
                payCountArray.add(s.getPayUserNum());
                payCountTotal += s.getPayUserNum();
                arpuArray.add(s.getDau() <= 0 ? 0f : ((float)s.getMoney()/100f)/(float)s.getDau());
                arpuTotal += (s.getDau() <= 0 ? 0f : ((float)s.getMoney()/100f)/(float)s.getDau());
                arppuArray.add(s.getPayUserNum() <= 0 ? 0f : ((float)s.getMoney()/100f)/(float)s.getPayUserNum());
                arppuTotal += (s.getPayUserNum() <= 0 ? 0f : ((float)s.getMoney()/100f)/(float)s.getPayUserNum());
                total += s.getPayUserNum();
            }
            json.put("payRatioData", payArray);
            json.put("payCountData", payCountArray);
            json.put("arpuData", arpuArray);
            json.put("arppuData", arppuArray);

            json.put("totalCount", total);
            json.put("arpuAvg", arpuTotal/data.size());
            json.put("arppuAvg",arppuTotal/data.size());
            json.put("payRatioAvg", payTotal/data.size());
            json.put("payCountAvg", payCountTotal/data.size());

        }


        return json.toString();
    }


    /**
     * 收集付费转化率数据
     * @param appID
     * @param from
     * @param to
     * @return
     */
    public String collectPayRatioInfo(int appID, Date from, Date to){

        JSONObject json = new JSONObject();

        List<TSummary> data = summaryDao.querySummaryData(appID, from, to);
        if(data != null && data.size() > 0){

            TSummary first = data.get(0);
            TSummary last = data.get(data.size()-1);
            String format = TimeUtils.FORMATER_5;
            if(TimeUtils.sameYear(first.getCurrTime(), last.getCurrTime())){
                format = TimeUtils.FORMATER_6;
            }

            JSONArray category = new JSONArray();
            for(TSummary s :data) {
                category.add(TimeUtils.format(s.getCurrTime(), format));
            }

            json.put("payCategory", category);


            JSONArray payArray = new JSONArray();
            JSONArray totalPayArray = new JSONArray();
            JSONArray totalRatioArray = new JSONArray();

            float ratioTotal = 0;
            for(TSummary s : data){
                payArray.add(s.getNewPayUserNum());
                totalPayArray.add(s.getTotalPayUserNum());
                totalRatioArray.add( s.getTotalUserNum () > 0 ? ((float)s.getTotalPayUserNum()/(float)s.getTotalUserNum() * 100f) : 0);
                ratioTotal += s.getTotalUserNum() == 0 ? 0 : ((float)s.getTotalPayUserNum()/(float)s.getTotalUserNum() * 100f);
            }
            json.put("payData", payArray);
            json.put("payTotalData", totalPayArray);
            json.put("payRatioData", totalRatioArray);

            json.put("ratioAvg", ratioTotal/data.size());

        }



        List<TRetention> rData = retentionDao.queryRetentionData(appID, from, to);
        if(rData != null && rData.size() > 0) {

            TRetention first = rData.get(0);
            TRetention last = rData.get(rData.size() - 1);
            String format = TimeUtils.FORMATER_5;
            if (TimeUtils.sameYear(first.getStatTime(), last.getStatTime())) {
                format = TimeUtils.FORMATER_6;
            }

            JSONArray category = new JSONArray();
            for (TRetention s : rData) {
                category.add(TimeUtils.format(s.getStatTime(), format));
            }

            json.put("keyCategory", category);

            JSONArray dayArray = new JSONArray();
            JSONArray tdayArray = new JSONArray();
            JSONArray wdayArray = new JSONArray();
            JSONArray mdayArray = new JSONArray();

            float daySum = 0;
            int dayCount = 0;
            float tdaySum = 0;
            int tdayCount = 0;
            float wdaySum = 0;
            int wdayCount = 0;
            float mdaySum = 0;
            int mdayCount = 0;

            for (TRetention s : rData) {

                if (s.getDayPayRatio() == null) {
                    s.setDayPayRatio(",");
                }

                String rs = s.getDayPayRatio();
                if (rs.startsWith(",")) {
                    rs = rs.substring(1);
                }


                String[] userNums = rs.split(",");
                if (userNums.length >= 3) {
                    String[] vals = userNums[0].split(":");

                    float v = (vals[0].length() <=1 || Integer.valueOf(vals[1]) <= 0) ? 0 : Float.valueOf(vals[0]) / Float.valueOf(vals[1]) * 100f;
                    daySum += v;
                    dayArray.add(v);
                    dayCount++;
                } else {
                    dayArray.add(0);
                }

                if (userNums.length >= 7) {
                    String[] vals = userNums[2].split(":");
                    float v = (vals[0].length() <=1 || Integer.valueOf(vals[1]) <= 0) ? 0 : Float.valueOf(vals[0]) / Float.valueOf(vals[1]) * 100f;
                    tdaySum += v;
                    tdayArray.add(v);
                    tdayCount++;
                } else {
                    tdayArray.add(0);
                }

                if (userNums.length >= 14) {
                    String[] vals = userNums[6].split(":");
                    float v = (vals[0].length() <=1 || Integer.valueOf(vals[1]) <= 0) ? 0 : Float.valueOf(vals[0]) / Float.valueOf(vals[1]) * 100f;
                    wdaySum += v;
                    wdayArray.add(v);
                    wdayCount++;
                } else {
                    wdayArray.add(0);
                }

                if (userNums.length >= 30) {
                    String[] vals = userNums[29].split(":");
                    float v = (vals[0].length() <=1 || Integer.valueOf(vals[1]) <= 0) ? 0 : Float.valueOf(vals[0]) / Float.valueOf(vals[1]) * 100f;
                    mdaySum += v;
                    mdayArray.add(v);
                    mdayCount++;
                } else {
                    mdayArray.add(0);
                }
            }

            json.put("dayArray", dayArray);
            json.put("tdayArray", tdayArray);
            json.put("wdayArray", wdayArray);
            json.put("mdayArray", mdayArray);

            json.put("dayAvg", daySum / (dayCount == 0 ? 1 : dayCount));
            json.put("tdayAvg", tdaySum / (tdayCount == 0 ? 1 : tdayCount));
            json.put("wdayAvg", wdaySum / (wdayCount == 0 ? 1 : wdayCount));
            json.put("mdayAvg", mdaySum / (mdayCount == 0 ? 1 : mdayCount));
        }

        return json.toString();
    }
}
