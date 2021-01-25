package com.u8.server.service;

import com.alibaba.fastjson.JSON;
import com.u8.server.common.Page;
import com.u8.server.dao.cache.UChannelProductCacheDao;
import com.u8.server.dao.hibernate.UChannelProductDao;
import com.u8.server.data.UChannelProduct;
import com.u8.server.log.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by ant on 2018/5/15.
 */
@Service("channelProductManager")
public class UChannelProductManager  {

    @Autowired
    private UChannelProductDao channelProductDao;

    @Autowired
    private UChannelProductCacheDao channelProductCacheDao;


    public void saveMultiProductsFromJson(int channelID, String productJsonArray){

        try{

            List<UChannelProduct> currProducts = getProducts(channelID);

            List<UChannelProduct> products = JSON.parseArray(productJsonArray, UChannelProduct.class);


            for(UChannelProduct currP : currProducts){
                boolean found = false;
                for(UChannelProduct p : products){
                    if(p.getGameProductID().equals(currP.getGameProductID())){
                        found = true;
                    }
                }
                if(!found){
                    //delete
                    deleteChannelProduct(currP);
                }
            }


            for(UChannelProduct p : products){

                boolean found = false;
                boolean update = false;
                for(UChannelProduct currP : currProducts){
                    if(p.getGameProductID().equals(currP.getGameProductID())){
                        found = true;
                        if(!p.getChannelProductID().equals(currP.getChannelProductID())){
                            currP.setChannelProductID(p.getChannelProductID());
                            p = currP;
                            update = true;
                        }
                    }
                }

                if(!found){
                    //insert
                    p.setChannelID(channelID);
                    saveChannelProduct(p);

                }else if(update){
                    //update
                    saveChannelProduct(p);
                }

            }


        }catch (Exception e){
            Log.e(e.getMessage());
            e.printStackTrace();
        }

    }

    private void saveChannelProductInCache(UChannelProduct product){
        channelProductCacheDao.saveInLock(getLockKey(product), product);
    }

    private UChannelProduct getChannelProductInCache(int channelID, String gameProductID){
        return channelProductCacheDao.get("channel"+channelID, gameProductID);
    }

    private void deleteChannelProductInCache(int channelID, String gameProductID){
        channelProductCacheDao.delete("channel"+channelID, gameProductID);
    }


    public void saveChannelProduct(final UChannelProduct product){
        channelProductCacheDao.doInLockWithRetry(product, new Runnable() {
            @Override
            public void run() {
                channelProductDao.save(product);
                saveChannelProductInCache(product);
            }
        });

    }

    public UChannelProduct getChannelProduct(int channelID, String gameProductID){
        UChannelProduct product = getChannelProductInCache(channelID, gameProductID);
        if(product == null){
            product = channelProductDao.getChannelProduct(channelID, gameProductID);
            if(product != null){
                saveChannelProductInCache(product);
            }
        }
        return product;

    }

    public UChannelProduct getProductByID(int productID){

        return channelProductDao.get(productID);
    }

    public Page<UChannelProduct> queryPage(int currPage, int num, Integer channelID) {

        return channelProductDao.queryPage(currPage, num, channelID);
    }

    public List<UChannelProduct> getProducts(int channelID) {

        return channelProductDao.getProductsByChannelID(channelID);
    }

    public void deleteChannelProduct(final UChannelProduct product){
        channelProductCacheDao.doInLockWithRetry(getLockKey(product), new Runnable() {
            @Override
            public void run() {
                channelProductDao.delete(product);
                deleteChannelProductInCache(product.getChannelID(), product.getGameProductID());
            }
        });

    }

    private String getLockKey(UChannelProduct product){
        String lockKey = "channel"+product.getChannelID()+product.getGameProductID();
        return lockKey;
    }
}
