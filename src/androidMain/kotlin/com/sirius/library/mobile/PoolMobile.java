package com.sirius.library.mobile;

import org.hyperledger.indy.sdk.pool.Pool;
import org.hyperledger.indy.sdk.pool.PoolJSONParameters;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class PoolMobile {
    public static Map<String, Pool> openedPoolRegistry = new ConcurrentHashMap<>();

    public static void registerPool(String name, String genesisFilePath) {
        try {
            File file = new File(genesisFilePath);
        //  String content =  PoolUtils.getStringFromFile(genesisFilePath);

         //   File file =
                    PoolUtils.createPoolLedgerConfig(name,genesisFilePath);
          //  String content =  PoolUtils.getStringFromFile(genesisFilePath);
           // new File(genesisFilePath);
           // boolean isExist =  file.exists();

          //  genesisFilePath = Paths.get(genesisFilePath).toAbsolutePath().toString();

       /*    File file2 =  new File(genesisFilePath);
            file2.setExecutable(true);
            file2.setWritable(true);
            file2.setReadable(true);
            String path = file2.getAbsolutePath();
            Boolean path22 = file2.canRead();
            Boolean path223 = file2.canExecute();

            String path22333 = file2.getCanonicalPath();
            boolean isExist2 =  file2.exists();
           // file.toURI()
            PoolJSONParameters.CreatePoolLedgerConfigJSONParameter createPoolLedgerConfigJSONParameter
                    = new PoolJSONParameters.CreatePoolLedgerConfigJSONParameter(path22333);
           String config =  createPoolLedgerConfigJSONParameter.toJson();
            Pool.createPoolLedgerConfig(name,config ).get(60, TimeUnit.SECONDS);*/
        } catch (Exception e) {
            if(e.getMessage()!=null){
                if (!e.getMessage().contains("PoolLedgerConfigExists")){
                    e.printStackTrace();
                }
            }else{
                e.printStackTrace();
            }
        }
    }

    /*public static void registerPool(String name, File genesisFile) {
        try {
            genesisFilePath = Paths.get(genesisFilePath).toAbsolutePath().toString();
            PoolJSONParameters.CreatePoolLedgerConfigJSONParameter createPoolLedgerConfigJSONParameter
                    = new PoolJSONParameters.CreatePoolLedgerConfigJSONParameter(genesisFilePath);
            Pool.createPoolLedgerConfig(name, createPoolLedgerConfigJSONParameter.toJson()).get(60, TimeUnit.SECONDS);
        } catch (Exception e) {
            if(e.getMessage()!=null){
                if (!e.getMessage().contains("PoolLedgerConfigExists")){
                    e.printStackTrace();
                }
            }else{
                e.printStackTrace();
            }
        }
    }*/


    public Pool getPoolHandle(String name) {
        if (openedPoolRegistry.containsKey(name))
            return openedPoolRegistry.get(name);
        try {
            Pool pool = Pool.openPoolLedger(name, null).get();
            openedPoolRegistry.put(name, pool);
            return pool;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
